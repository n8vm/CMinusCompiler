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
public class Param implements CminusElement {

  private final VarType type;
  private final String id;
  private final boolean array;

  public Param(VarType type, String id, boolean array) {
    this.type = type;
    this.id = id;
    this.array = array;
  }

  public VarType getType() {
    return type;
  }

  public String getId() {
    return id;
  }

  public boolean isArray() {
    return array;
  }

  public void toCminus(StringBuilder builder, final String prefix) {
    if (isArray()) {
      builder.append(type).append(" ").append(id).append("[]");
    } else {
      builder.append(type).append(" ").append(id);
    }
  }

  @Override
  public EvalResult toMIPS(StringBuilder code, StringBuilder data,
          SymbolTable symbolTable, RegisterAllocator regAllocator) {
    code.append("# Param not supported");
    return EvalResult.createVoidResult();
  }

}
