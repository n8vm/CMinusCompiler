package formatter;

import ast.VarType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 * Code formatter project
 * CS 4481
 */
/**
 *
 * @author edwajohn
 */
public class SymbolTable {

  private final HashMap<String, SymbolInfo> table;
  private SymbolTable parent;
  private final List<SymbolTable> children;
  private final boolean function;
  public VarType returnType = null;
  private int localVarOffset = 0;
  private List<String> storedRegisters = new ArrayList<String>();

  public static int counter = 0;

  public SymbolTable(boolean function) {
    table = new HashMap<>();
    parent = null;
    children = new ArrayList<>();
    this.function = function;
  }

  public void addSymbol(String id, SymbolInfo symbol) {
    table.put(id, symbol);
  }

  public void addLocalVariable(String id, SymbolInfo symbol) {
    localVarOffset -= symbol.getSize();
    symbol.setAddressOffset(localVarOffset);
    table.put(id, symbol);
  }

  public void setRegistersToStore(List<String> registers) {
    storedRegisters = new ArrayList<>(registers);
    localVarOffset -= 4 * registers.size();
  }

  public void clearRegistersToStore() {
    localVarOffset += 4 * storedRegisters.size();
    storedRegisters = new ArrayList<>();
  }

  /**
   * Emits code to store registers onto the stack. The AR size is NOT updated -
   * that is done in setRegistersToStore.
   *
   * @param code
   */
  public void storeRegisters(StringBuilder code) {
    for (int i = 0; i < storedRegisters.size(); ++i) {
      code.append("\tsw ").append(storedRegisters.get(i));
      code.append(", ").append(localVarOffset + (i * 4)).append("($sp)\n");
    }
  }

  /**
   * Emits code to restore registers from the stack. The AR size is NOT updated
   * - that is done in clearRegistersToStore.
   */
  public void restoreRegisters(StringBuilder code) {
    if (function) {
      for (int i = 0; i < storedRegisters.size(); ++i) {
        code.append("\tlw ").append(storedRegisters.get(i));
        code.append(", ").append(localVarOffset + (i * 4)).append("($sp)\n");
      }
    } else {
      parent.restoreRegisters(code);
    }
  }

  /**
   * Returns the size of the activation record.
   *
   * @return
   */
  public int getARSize() {
    return -localVarOffset;
  }

  public SymbolInfo find(String id) {
    if (table.containsKey(id)) {
      return table.get(id);
    }
    if (parent != null) {
      SymbolInfo info = parent.find(id);
      return new SymbolInfo(info, parent.getARSize());
    }
    return null;
  }

  public int returnValueOffset() {
    if (function) {
      if (returnType != null) {
        return -returnType.getSize();
      }
      return 0;
    }
    if (parent == null) {
      throw new RuntimeException("returnValueOffset not a function without a parent");
    }
    return parent.getARSize() + parent.returnValueOffset();
  }

  /**
   * Upon a return statement, returns the offset to the returning stack pointer.
   *
   * @return
   */
  public int getReturnStackPointerOffset() {
    if (function) {
      return 0;
    }
    if (parent == null) {
      throw new RuntimeException("returnValueOffset not a function without a parent");
    }
    return parent.getARSize() + parent.getReturnStackPointerOffset();
  }

  public SymbolTable getFunctionAncestor() {
    if (function) {
      return this;
    }
    if (parent == null) {
      throw new RuntimeException("getFunctionAncestor not a function without a parent");
    }
    return parent.getFunctionAncestor();
  }

  /**
   * Returns the new child.
   *
   * @return
   */
  public SymbolTable createChildFunction(VarType returnType) {
    SymbolTable child = new SymbolTable(true);
    if (returnType != null) {
      child.localVarOffset = -returnType.getSize();
      child.returnType = returnType;
    }
    children.add(child);
    child.parent = this;
    return child;
  }

  public SymbolTable createChildCompound() {
    SymbolTable child = new SymbolTable(false);
    children.add(child);
    child.parent = this;
    return child;
  }

  public SymbolTable getParent() {
    return parent;
  }

  public static String createUniqueLabel() {
    return "label" + counter++;
  }

  public String toString() {
    return table.keySet().toString();
  }

}
