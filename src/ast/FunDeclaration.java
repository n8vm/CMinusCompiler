/*
 * Code formatter project
 * CS 4481
 */
package ast;

import ast.Param;
import ast.VarType;
import formatter.RegisterAllocator;
import formatter.SymbolTable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author edwajohn
 */
public class FunDeclaration implements Declaration, CminusElement {

  private final VarType returnType;
  private final String id;
  private final ArrayList<Param> params;
  private final Statement statement;
  private final SymbolTable symbolTable;

  public FunDeclaration(VarType returnType, String id, List<Param> params,
          Statement statement, SymbolTable symbolTable) {
    this.returnType = returnType;
    this.id = id;
    this.params = new ArrayList<>(params);
    this.statement = statement;
    this.symbolTable = symbolTable;
  }

  public void toCminus(StringBuilder builder, final String prefix) {
    String rt = (returnType != null) ? returnType.toString() : "void";
    builder.append("\n").append(rt).append(" ");
    builder.append(id);
    builder.append("(");

    for (Param param : params) {
      param.toCminus(builder, prefix);
      builder.append(", ");
    }
    if (!params.isEmpty()) {
      builder.delete(builder.length() - 2, builder.length());
    }
    builder.append(")\n");
    statement.toCminus(builder, prefix);
  }

  @Override
  public EvalResult toMIPS(StringBuilder code, StringBuilder data,
          SymbolTable parentSymbolTable, RegisterAllocator dummy) {
    if (!id.equals("main")) {
      code.append("fun_");
    }
    code.append(id).append(":\n");

    //Account for the global table
    if (id.equals("main")) {
      code.append("\taddi $sp, $sp, ").append(-parentSymbolTable.getARSize()).append(" #Accounting for the global AR table.\n");
    }

    // Call toMIPS on the statement with dummy code and data builders in
    // order to get the registers that will be used.
    RegisterAllocator regAllocator = new RegisterAllocator();
    statement.toMIPS(new StringBuilder(), new StringBuilder(),
            this.symbolTable, regAllocator);
    final List<String> usedRegisters = regAllocator.getUsed();
    code.append("\t# Registers used in this function: ").append(usedRegisters).append("\n");
    symbolTable.setRegistersToStore(usedRegisters);

    // Store the registers to the stack and update the AR size
    code.append("\t# Store registers\n");
    symbolTable.storeRegisters(code);

    // Emit the function code
    regAllocator = new RegisterAllocator();
    statement.toMIPS(code, data, this.symbolTable, regAllocator);

    if (!id.equals("main")) {
      new Return(null).toMIPS(code, data, symbolTable, regAllocator);
    }
//    code.append("\t# Restore registers\n");
//    symbolTable.restoreRegisters(code);
    symbolTable.clearRegistersToStore();
    return EvalResult.createVoidResult();
  }

}
