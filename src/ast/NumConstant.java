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
public class NumConstant implements Expression, CminusElement {

  private final int value;

  public NumConstant(int value) {
    this.value = value;
  }

  public void toCminus(StringBuilder builder, final String prefix) {
    builder.append(Integer.toString(value));
  }

  @Override
  public EvalResult toMIPS(StringBuilder code, StringBuilder data,
          SymbolTable symbolTable, RegisterAllocator regAllocator) {
    final String register = regAllocator.getAny();
    final int intValue = value;
    code.append("\tli ").append(register).append(", ").append(intValue).append("\n");
    return EvalResult.createRegisterResult(register, VarType.INT);
  }
}
