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
public class BoolConstant implements Expression {

  private final boolean value;

  public BoolConstant(boolean value) {
    this.value = value;
  }

  public void toCminus(StringBuilder builder, final String prefix) {
    if (value) {
      builder.append("true");
    } else {
      builder.append("false");
    }
  }

  @Override
  public EvalResult toMIPS(StringBuilder code, StringBuilder data,
          SymbolTable symbolTable, RegisterAllocator regAllocator) {
    final String register = regAllocator.getAny();
    final int intValue = value ? 1 : 0;
    code.append("\tli\t").append(register).append(", ").append(intValue).append("\n");
    return EvalResult.createRegisterResult(register, VarType.BOOL);
  }

}
