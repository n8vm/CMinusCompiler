/*
 * Code formatter project
 * CS 4481
 */
package ast;

import formatter.RegisterAllocator;
import formatter.SymbolInfo;
import formatter.SymbolTable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author edwajohn
 */
public class Call implements Expression {

  private final String id;
  private final List<Expression> args;

  public Call(String id, List<Expression> args) {
    this.id = id;
    this.args = new ArrayList<>(args);
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    builder.append(id).append("(");
    for (Expression arg : args) {
      arg.toCminus(builder, prefix);
      builder.append(", ");
    }
    if (!args.isEmpty()) {
      builder.setLength(builder.length() - 2);
    }
    builder.append(")");
  }

  @Override
  public EvalResult toMIPS(StringBuilder code, StringBuilder data,
          SymbolTable symbolTable, RegisterAllocator regAllocator) {

    if (id.equals("println")) {
      emitPrintln(code, data, symbolTable, regAllocator);
      regAllocator.clearAll();
    } else {
      ArrayList<EvalResult> results = new ArrayList();
      for (int i = 0; i < args.size(); ++i) {
        results.add(args.get(i).toMIPS(code, data, symbolTable, regAllocator));
      }
      //Add parameters to the stack here.
      for (int i = 0; i < args.size(); ++i) {
        String resultRegister = results.get(i).getRegister();
        code.append("\tsw ").append(resultRegister).append(", ").append(((i + 2) * -4)).append("($sp)\t\t#Setting parameter\n");
        regAllocator.clear(resultRegister);
      }
      code.append("\tjal ").append("fun_").append(id).append("\n");
      String returnRegister = regAllocator.getAny();
      code.append("\tlw ").append(returnRegister).append(", ").append(-4).append("($sp)\n");
      return EvalResult.createRegisterResult(returnRegister, VarType.INT);
    }

    return EvalResult.createVoidResult();
  }

  private EvalResult emitPrintln(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    //handles void arguments
    if (args.size() != 0) {
      EvalResult argument = args.get(0).toMIPS(code, data, symbolTable, regAllocator);
      //int & bools
      if (argument.getType() == VarType.INT || argument.getType() == VarType.BOOL) {
        code.append("\tli $v0, 1\n");
        code.append("\tmove $a0, ").append(argument.getRegister()).append("\n");
        code.append("\tsyscall\n");
      } //strings
      else if (argument.getType() == VarType.STRING) {
        code.append("\tli $v0, 4\n");
        code.append("\tla $a0, ").append(argument.getAddress()).append("\n");
        code.append("\tsyscall\n");
      } else {
        System.out.println("Undefined type in call: " + argument.getType());
      }
    }
    code.append("\tli $v0, 4\n");
    code.append("\tla $a0, ").append("newline").append("\n");
    code.append("\tsyscall\n");
    return EvalResult.createVoidResult();
  }

}
