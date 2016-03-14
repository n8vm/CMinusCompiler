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
public class UnaryOperator implements Expression {

  private final UnaryOperatorType type;
  private final Expression expression;

  public UnaryOperator(String type, Expression expression) {
    this.type = UnaryOperatorType.fromString(type);
    this.expression = expression;
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    builder.append(type);
    expression.toCminus(builder, prefix);
  }

  @Override
  public EvalResult toMIPS(StringBuilder code, StringBuilder data,
          SymbolTable symbolTable, RegisterAllocator regAllocator) {
    String rhsRegister = expression.toMIPS(code, data, symbolTable, regAllocator).getRegister();
    if (type == type.NOT) {
      code.append("\tnot ").append(rhsRegister).append(", ").append(rhsRegister).append("\n");
      code.append("\taddi ").append(rhsRegister).append(", ").append(rhsRegister).append(", ").append("2").append("\n");
      return EvalResult.createRegisterResult(rhsRegister, VarType.BOOL);
    } else if (type == type.NEG) {
      code.append("\tneg ").append(rhsRegister).append(", ").append(rhsRegister).append("\n");
      return EvalResult.createRegisterResult(rhsRegister, VarType.INT);
    } else {
      throw new RuntimeException("UnaryOperator.mipsEvaluate not supported");
    }
  }
}
