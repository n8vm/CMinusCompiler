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
public interface CminusElement {

  public void toCminus(StringBuilder builder, final String prefix);

  /**
   * Emits MIPS code.
   *
   * @param code Use this builder to emit code to the .text section of the MIPS
   * file.
   * @param data Use this builder to emit code to the .data section of the MIPS
   * file.
   * @param symbolTable Keeps track of variable and function symbols.
   * @param regAllocator Allocates registers on demand. Each C- function should
   * use fresh registers.
   * @return The result of emitting MIPS code. See comments in EvalResult.
   */
  public EvalResult toMIPS(StringBuilder code, StringBuilder data,
          SymbolTable symbolTable, RegisterAllocator regAllocator);
}
