/*
 * Code formatter project
 * CS 4481
 */
package ast;

/**
 * This class represents the result of emitting code to MIPS. There are various
 * forms this result can take: 1) void, for cases where the calling node doesn't
 * need any information returned, such as a return statement. 2) register, for
 * cases where the called node needs to inform the calling node what register a
 * result is placed in, such as BinaryOperator. 3) address, for cases where the
 * returning result is in memory, such as StringConstant.
 *
 * To instantiate an EvalResult object use the factory methods create...().
 *
 * @author edwajohn
 */
public class EvalResult {

  private final String register;
  private final String address;
  private final VarType type;

  public static EvalResult createVoidResult() {
    return new EvalResult(null, null, null);
  }

  public static EvalResult createRegisterResult(String register, VarType type) {
    return new EvalResult(register, null, type);
  }

  public static EvalResult createAddressResult(String address, VarType type) {
    return new EvalResult(null, address, type);
  }

  private EvalResult(String register, String address, VarType type) {
    this.register = register;
    this.address = address;
    this.type = type;
  }

  /**
   * Anytime you get a register from a result you should seriously consider
   * calling regAllocator.clear(reg) after using the register to minimize
   * register usage.
   *
   * @return
   */
  public String getRegister() {
    return register;
  }

  public String getAddress() {
    return address;
  }

  public VarType getType() {
    return type;
  }

}
