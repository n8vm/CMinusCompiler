/*
 * Code formatter project
 * CS 4481
 */
package formatter;

import ast.VarType;

/**
 *
 * @author edwajohn
 */
public class SymbolInfo {

  private final String id;
  // In the case of a function, type is the return type
  private final VarType type;
  // -1 if not an array
  private final int arraySize;
  // True if the symbol is a function
  private final boolean function;
  // This is the offset from the stack pointer of the immediate symbol table.
  private int addressOffset;

  public SymbolInfo(String id, VarType type, int arraySize, boolean function) {
    this.id = id;
    this.type = type;
    this.arraySize = arraySize;
    this.function = function;
    this.addressOffset = 0;
  }

  /**
   * @param copy
   * @param spDifference The copy info object has an address offset relative to
   * it's local symbol table. This copy constructor makes the address offset
   * relative to any containing symbol table. If the symbol table is the local
   * one then spDifference will be zero. Otherwise it will be some positive
   * number.
   */
  public SymbolInfo(SymbolInfo copy, int spDifference) {
    this(copy.id, copy.type, copy.arraySize, copy.function);
    this.addressOffset = copy.addressOffset + spDifference;
  }

  public void setAddressOffset(int offset) {
    addressOffset = offset;
  }

  public VarType getType() {
    return type;
  }

  public int getAddressOffset() {
    return addressOffset;
  }

  public int getSize() {
    if (arraySize > -1) {
      return type.getSize() * arraySize;
    }
    return type.getSize();
  }

  @Override
  public String toString() {
    return "<" + id + ", " + type + '>';
  }

}
