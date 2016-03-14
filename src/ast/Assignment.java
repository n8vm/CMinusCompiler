/*
 * Code formatter project
 * CS 4481
 */
package ast;

import formatter.RegisterAllocator;
import formatter.SymbolInfo;
import formatter.SymbolTable;

/**
 *
 * @author edwajohn
 */
public class Assignment implements Expression, CminusElement {

  private final Mutable mutable;
  private final AssignmentType type;
  private final Expression rhs;

  public Assignment(Mutable mutable, String assign, Expression rhs) {
    this.mutable = mutable;
    this.type = AssignmentType.fromString(assign);
    this.rhs = rhs;
  }

  public void toCminus(StringBuilder builder, final String prefix) {
    mutable.toCminus(builder, prefix);
    if (rhs != null) {
      builder.append(" ").append(type.toString()).append(" ");
      rhs.toCminus(builder, prefix);
    } else {
      builder.append(type.toString());

    }
  }

  /**
   * Handles all Expression assignment productions.
   */
  @Override
  public EvalResult toMIPS(StringBuilder code, StringBuilder data,
          SymbolTable symbolTable, RegisterAllocator regAllocator) {
    switch (type) {
      case EQUALS:
        return emitEquals(code, data, symbolTable, regAllocator);
      case PLUS:
        return emitPlus(code, data, symbolTable, regAllocator);
      case MINUS:
        return emitMinus(code, data, symbolTable, regAllocator);
      case TIMES:
        return emitTimes(code, data, symbolTable, regAllocator);
      case DIVIDE:
        return emitDivide(code, data, symbolTable, regAllocator);
      case INC:
        return emitInc(code, data, symbolTable, regAllocator);
      case DEC:
        return emitDec(code, data, symbolTable, regAllocator);
      default:
        System.out.println("Unsupported assignment operator type: " + type.toString());
        break;
    }
    return EvalResult.createVoidResult();
  }

  /**
   * Handles =
   *
   * "sw rhsRegister stackoffset($sp)"
   */
  private EvalResult emitEquals(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    String rhsRegister = rhs.toMIPS(code, data, symbolTable, regAllocator).getRegister();
    int stackOffset = symbolTable.find(mutable.getId()).getAddressOffset();
    code.append("\tsw").append(" ").append(rhsRegister).append(", ").append(stackOffset).append("($sp)");
    code.append("\t\t#Assigning to " + mutable.getId() + ".\n");
    regAllocator.clear(rhsRegister);
    return EvalResult.createVoidResult();
  }

  /**
   * Handles +=
   *
   * "lw $LHS, offset($sp)" -> "add $LHS, $LHS, $RHS" -> "sw $$LHS, offset($sp)"
   */
  private EvalResult emitPlus(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    String rhsRegister = rhs.toMIPS(code, data, symbolTable, regAllocator).getRegister();
    String lhsRegister = regAllocator.getAny();
    int stackOffset = symbolTable.find(mutable.getId()).getAddressOffset();
    code.append("\tlw").append(" ").append(lhsRegister).append(", ").append(stackOffset).append("($sp)").append("\n");
    code.append("\tadd").append(" ").append(lhsRegister).append(", ").append(lhsRegister).append(", ").append(rhsRegister);
    code.append("\t#Adding to " + mutable.getId() + ".\n");
    code.append("\tsw").append(" ").append(lhsRegister).append(", ").append(stackOffset).append("($sp)\n");
    regAllocator.clear(rhsRegister);
    return EvalResult.createRegisterResult(lhsRegister, VarType.INT);
  }

  /**
   * Handles -=
   *
   * "lw $LHS, offset($sp)" -> "add $LHS, $LHS, $RHS" -> "sw $$LHS, offset($sp)"
   */
  private EvalResult emitMinus(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    String rhsRegister = rhs.toMIPS(code, data, symbolTable, regAllocator).getRegister();
    String lhsRegister = regAllocator.getAny();
    int stackOffset = symbolTable.find(mutable.getId()).getAddressOffset();
    code.append("\tlw").append(" ").append(lhsRegister).append(", ").append(stackOffset).append("($sp)").append("\n");
    code.append("\tsub").append(" ").append(lhsRegister).append(", ").append(lhsRegister).append(", ").append(rhsRegister);
    code.append("\t#Subtracting from " + mutable.getId() + ".\n");
    code.append("\tsw").append(" ").append(lhsRegister).append(", ").append(stackOffset).append("($sp)\n");
    regAllocator.clear(rhsRegister);
    return EvalResult.createRegisterResult(lhsRegister, VarType.INT);
  }

  /**
   * Handles ++
   *
   * "lw $LHS, offset($sp)" -> "addi $LHS, $LHS, 1" -> "sw $$LHS, offset($sp)"
   */
  private EvalResult emitInc(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    String lhsRegister = regAllocator.getAny();
    int stackOffset = symbolTable.find(mutable.getId()).getAddressOffset();
    code.append("\tlw").append(" ").append(lhsRegister).append(", ").append(stackOffset).append("($sp)").append("\n");
    code.append("\taddi").append(" ").append(lhsRegister).append(", ").append(lhsRegister).append(", ").append("1");
    code.append("\t#Incrementing " + mutable.getId() + ".\n");
    code.append("\tsw").append(" ").append(lhsRegister).append(", ").append(stackOffset).append("($sp)\n");
    return EvalResult.createRegisterResult(lhsRegister, VarType.INT);
  }

  /**
   * Handles --
   *
   * "lw $LHS, offset($sp)" -> "addi $LHS, $LHS, -1" -> "sw $$LHS, offset($sp)"
   */
  private EvalResult emitDec(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    String lhsRegister = regAllocator.getAny();
    int stackOffset = symbolTable.find(mutable.getId()).getAddressOffset();
    code.append("\tlw").append(" ").append(lhsRegister).append(", ").append(stackOffset).append("($sp)").append("\n");
    code.append("\taddi").append(" ").append(lhsRegister).append(", ").append(lhsRegister).append(", ").append("-1");
    code.append("\t#Decrementing " + mutable.getId() + ".\n");
    code.append("\tsw").append(" ").append(lhsRegister).append(", ").append(stackOffset).append("($sp)\n");
    regAllocator.clear(lhsRegister);
    return EvalResult.createRegisterResult(lhsRegister, VarType.INT);
  }

  /**
   * Handles *=
   *
   * "lw $LHS, offset($sp)" -> "mult $LHS, $RHS" -> "mflo $LHS" -> "sw $$LHS,
   * offset($sp)"
   */
  private EvalResult emitTimes(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    String rhsRegister = rhs.toMIPS(code, data, symbolTable, regAllocator).getRegister();
    String lhsRegister = regAllocator.getAny();
    int stackOffset = symbolTable.find(mutable.getId()).getAddressOffset();
    code.append("\tlw").append(" ").append(lhsRegister).append(", ").append(stackOffset).append("($sp)").append("\n");
    code.append("\tmult").append(" ").append(lhsRegister).append(", ").append(rhsRegister);
    code.append("\t\t#Multiplying to " + mutable.getId() + ".\n");
    code.append("\tmflo").append(" ").append(lhsRegister).append("\n");
    code.append("\tsw").append(" ").append(lhsRegister).append(", ").append(stackOffset).append("($sp)\n");
    regAllocator.clear(rhsRegister);
    return EvalResult.createRegisterResult(lhsRegister, VarType.INT);
  }

  /**
   * Handles /=
   *
   * "lw $LHS, offset($sp)" -> "div $LHS, $RHS" -> "mflo $LHS" -> "sw $$LHS,
   * offset($sp)"
   */
  private EvalResult emitDivide(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    String rhsRegister = rhs.toMIPS(code, data, symbolTable, regAllocator).getRegister();
    String lhsRegister = regAllocator.getAny();
    int stackOffset = symbolTable.find(mutable.getId()).getAddressOffset();
    code.append("\tlw").append(" ").append(lhsRegister).append(", ").append(stackOffset).append("($sp)").append("\n");
    code.append("\tdiv").append(" ").append(lhsRegister).append(", ").append(rhsRegister);
    code.append("\t\t#Dividing from " + mutable.getId() + ".\n");
    code.append("\tmflo").append(" ").append(lhsRegister).append("\n");
    code.append("\tsw").append(" ").append(lhsRegister).append(", ").append(stackOffset).append("($sp)\n");
    regAllocator.clear(rhsRegister);
    return EvalResult.createRegisterResult(lhsRegister, VarType.INT);
  }
}
