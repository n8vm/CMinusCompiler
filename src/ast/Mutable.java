
/*
 * Code formatter project
 * CS 4481
 */
package ast;

import formatter.RegisterAllocator;
import formatter.SymbolInfo;
import formatter.SymbolTable;

/**
 *
 * @author edwajohn
 */
public class Mutable implements Expression, CminusElement {

  private final String id;
  private final Expression index;

  public Mutable(String id, Expression index) {
    this.id = id;
    this.index = index;
  }

  public String getId() {
    return id;
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    builder.append(id);
    if (index != null) {
      builder.append("[");
      index.toCminus(builder, prefix);
      builder.append("]");
    }
  }

  @Override
  public EvalResult toMIPS(StringBuilder code, StringBuilder data,
          SymbolTable symbolTable, RegisterAllocator regAllocator) {
    //find the symbol, and store into a register.
    SymbolInfo mutableInfo = symbolTable.find(id);
    String currentRegister = regAllocator.getAny();
    code.append("\tlw ").append(currentRegister).append(", ").append(mutableInfo.getAddressOffset()).append("($sp)").append("\t\t#Loading mutable.\n");
    code.append("#Symbol table used by previous mutable: ").append(symbolTable.toString() + "\n");

    //return that register.
    return EvalResult.createRegisterResult(currentRegister, mutableInfo.getType());
  }
}
