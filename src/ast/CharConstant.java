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
public class CharConstant implements Expression {

  private final char value;

  public CharConstant(char value) {
    this.value = value;
  }

  public void toCminus(StringBuilder builder, final String prefix) {
    builder.append("'").append(value).append("'");
  }

  @Override
  public EvalResult toMIPS(StringBuilder code, StringBuilder data,
          SymbolTable symbolTable, RegisterAllocator regAllocator) {
    final String register = regAllocator.getAny();
    code.append("\tli\t").append(register).append(", ").append((int) value).append("\n");
    return EvalResult.createRegisterResult(register, VarType.CHAR);
  }

}
