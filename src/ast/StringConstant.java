/*
 * Code formatter project
 * CS 4481
 */
package ast;

import formatter.RegisterAllocator;
import formatter.SymbolTable;

/**
 *
 * @author edwajohn
 */
public class StringConstant implements Expression {

  private final String value;

  public StringConstant(String value) {
    this.value = value.substring(1, value.length() - 1);
  }

  public void toCminus(StringBuilder builder, final String prefix) {
    builder.append("\"").append(value).append("\"");
  }

  @Override
  public EvalResult toMIPS(StringBuilder code, StringBuilder data,
          SymbolTable symbolTable, RegisterAllocator regAllocator) {
    final String label = SymbolTable.createUniqueLabel();
    data.append(label).append(":\t").append(".asciiz").append("\t").append("\"").append(value).append("\"\n");
    return EvalResult.createAddressResult(label, VarType.STRING);
  }
}
