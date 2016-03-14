/*
 * Code formatter project
 * CS 4481
 */
package ast;

import formatter.RegisterAllocator;
import formatter.SymbolTable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author edwajohn
 */
public class Program {

  private ArrayList<Declaration> declarations;

  public Program(List<Declaration> declarations) {
    this.declarations = new ArrayList<>(declarations);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (Declaration declaration : declarations) {
      declaration.toCminus(builder, "");
    }
    return builder.toString();
  }

  public String toMIPS(SymbolTable symbolTable, RegisterAllocator regAllocator) {
    StringBuilder code = new StringBuilder();
    StringBuilder data = new StringBuilder();

    code.append("\t# All program code is placed after the\n"
            + "\t# .text assembler directive\n"
            + "\t.text\n"
            + "\n"
            + "\t# Declare main as a global function\n"
            + "\t.globl	main\n"
            + "\n");

    data.append("\t# All memory structures are placed after the\n"
            + "\t# .data assembler directive\n"
            + "\t.data\n"
            + "\n");

    data.append("newline:\t.asciiz \"\\n\"\n");

    for (Declaration declaration : declarations) {
      declaration.toMIPS(code, data, symbolTable, null);
    }

    code.append("\t# exit\n");
    code.append("\tli\t$v0, 10\n");
    code.append("\tsyscall");

    return code.toString() + "\n\n" + data.toString();
  }

}
