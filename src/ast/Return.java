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
public class Return implements Statement {

  private final Expression expr;

  public Return(Expression expr) {
    this.expr = expr;
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    builder.append(prefix);
    if (expr == null) {
      builder.append("return;\n");
    } else {
      builder.append("return ");
      expr.toCminus(builder, prefix);
      builder.append(";\n");
    }
  }

  @Override
  public EvalResult toMIPS(StringBuilder code, StringBuilder data,
          SymbolTable symbolTable, RegisterAllocator regAllocator) {

    int parameterShiftAmount = -symbolTable.getParent().getARSize();
    if (symbolTable.getParent().returnType == null) {
      parameterShiftAmount -= 4;
    }

    if (expr != null) {
      EvalResult result = expr.toMIPS(code, data, symbolTable, regAllocator);
      code.append("\tsw ").append(result.getRegister()).append(", ").append(symbolTable.getReturnStackPointerOffset() - 4).append("($sp)\n");
      code.append("\taddi ").append("$sp, ").append("$sp, ").append(-parameterShiftAmount).append("\t\t#Reverting parameter scope...\n");
      code.append("\t# Restore registers\n");
      symbolTable.restoreRegisters(code);
      symbolTable.clearRegistersToStore();
      code.append("\tjr $ra\n");
      return result;
    } else {
      code.append("\tjr $ra\n");
      code.append("\taddi ").append("$sp, ").append("$sp, ").append(-parameterShiftAmount).append("\t\t#Reverting parameter scope...\n");
      code.append("\t# Restore registers\n");
      symbolTable.restoreRegisters(code);
      symbolTable.clearRegistersToStore();
      return EvalResult.createVoidResult();
    }
  }
}
