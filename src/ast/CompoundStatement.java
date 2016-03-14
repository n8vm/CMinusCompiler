/*
 * Code formatter project
 * CS 4481
 */
package ast;

import formatter.RegisterAllocator;
import formatter.SymbolTable;
import java.util.List;

public class CompoundStatement implements Statement {

  private final List<Statement> statements;
  private final SymbolTable symbolTable;

  public CompoundStatement(List<Statement> statements, SymbolTable symbolTable) {
    this.statements = statements;
    this.symbolTable = symbolTable;
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    builder.append(prefix).append("{\n");
    for (Statement s : statements) {
      s.toCminus(builder, prefix + "  ");
    }
    builder.append(prefix).append("}\n");
  }

  @Override
  public EvalResult toMIPS(StringBuilder code, StringBuilder data,
          SymbolTable parentSymbolTable, RegisterAllocator regAllocator) {
    //Find the AR size from the parent table.
    int parameterShiftAmount = -parentSymbolTable.getARSize();

//    if (parentSymbolTable.returnType == null) {
//      parameterShiftAmount -= 4;
//    }
    //code.append(parameterShiftAmount + "TEST\n");
    //Shift the stack pointer to avoid writing over parameters.
    code.append("\taddi ").append("$sp, ").append("$sp, ").append(parameterShiftAmount).append("\t\t#Entering parameter scope...\n");

    for (Statement s : statements) {
      EvalResult result = s.toMIPS(code, data, this.symbolTable, regAllocator);
      if (result.getRegister() != null) {
        regAllocator.clear(result.getRegister());
      }
    }

    //Shift the stack pointer to it's original location.
    code.append("\taddi ").append("$sp, ").append("$sp, ").append(-parameterShiftAmount).append("\t\t#Reverting parameter scope...\n");

    return EvalResult.createVoidResult();
  }

}
