/*
 * Code formatter project
 * CS 4481
 */
package ast;

/**
 *
 * @author edwajohn
 */
public enum VarType {

  INT("int"), BOOL("bool"), CHAR("char"), STRING("string");

  private final String value;

  private VarType(String value) {
    this.value = value;
  }

  public static VarType fromString(String s) {
    for (VarType vt : VarType.values()) {
      if (vt.value.equals(s)) {
        return vt;
      }
    }
    throw new RuntimeException("Illegal string in VarType.fromString()");
  }

  public int getSize() {
    return 4;
  }

  @Override
  public String toString() {
    return value;
  }

}
