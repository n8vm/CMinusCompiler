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
public class BinaryOperator implements Expression {

  private final Expression lhs, rhs;
  private String lhsRegister, rhsRegister;
  private final BinaryOperatorType type;

  public BinaryOperator(Expression lhs, BinaryOperatorType type, Expression rhs) {
    this.lhs = lhs;
    this.type = type;
    this.rhs = rhs;
  }

  public BinaryOperator(Expression lhs, String type, Expression rhs) {
    this.lhs = lhs;
    this.type = BinaryOperatorType.fromString(type);
    this.rhs = rhs;
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    lhs.toCminus(builder, prefix);
    builder.append(" ").append(type).append(" ");
    rhs.toCminus(builder, prefix);
  }

  @Override
  public EvalResult toMIPS(
          StringBuilder code, StringBuilder data,
          SymbolTable symbolTable, RegisterAllocator regAllocator) {
    lhsRegister = lhs.toMIPS(code, data, symbolTable, regAllocator).getRegister();
    rhsRegister = rhs.toMIPS(code, data, symbolTable, regAllocator).getRegister();
    switch (type) {
      case PLUS:
        return emitPlus(code, data, symbolTable, regAllocator);
      case MINUS:
        return emitMinus(code, data, symbolTable, regAllocator);
      case TIMES:
        return emitTimes(code, data, symbolTable, regAllocator);
      case DIVIDE:
        return emitDivide(code, data, symbolTable, regAllocator);
      case MOD:
        return emitMod(code, data, symbolTable, regAllocator);
      case EQ:
        return emitEQ(code, data, symbolTable, regAllocator);
      case NE:
        return emitNE(code, data, symbolTable, regAllocator);
      case GE:
        return emitGE(code, data, symbolTable, regAllocator);
      case GT:
        return emitGT(code, data, symbolTable, regAllocator);
      case LT:
        return emitLT(code, data, symbolTable, regAllocator);
      case LE:
        return emitLE(code, data, symbolTable, regAllocator);
      case AND:
        return emitAnd(code, data, symbolTable, regAllocator);
      case OR:
        return emitOr(code, data, symbolTable, regAllocator);
      default:
        System.out.println("Unsupported binary operator type: " + type.toString());
        break;
    }
    return EvalResult.createVoidResult();
  }

  private EvalResult emitPlus(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    code.append("\tadd ").append(lhsRegister).append(", ").append(lhsRegister).append(", ").append(rhsRegister).append("\n");
    regAllocator.clear(rhsRegister);
    return EvalResult.createRegisterResult(lhsRegister, VarType.INT);
  }

  private EvalResult emitMinus(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    code.append("\tsub ").append(lhsRegister).append(", ").append(lhsRegister).append(", ").append(rhsRegister).append("\n");
    regAllocator.clear(rhsRegister);
    return EvalResult.createRegisterResult(lhsRegister, VarType.INT);
  }

  private EvalResult emitTimes(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    code.append("\tmult ").append(lhsRegister).append(", ").append(rhsRegister).append("\n");
    code.append("\tmflo ").append(lhsRegister).append("\n");
    regAllocator.clear(rhsRegister);
    return EvalResult.createRegisterResult(lhsRegister, VarType.INT);
  }

  private EvalResult emitDivide(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    code.append("\tdiv ").append(lhsRegister).append(", ").append(rhsRegister).append("\n");
    code.append("\tmflo ").append(lhsRegister).append("\n");
    regAllocator.clear(rhsRegister);
    return EvalResult.createRegisterResult(lhsRegister, VarType.INT);
  }

  private EvalResult emitMod(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    code.append("\tdiv ").append(lhsRegister).append(", ").append(rhsRegister).append("\n");
    code.append("\tmfhi ").append(lhsRegister).append("\n");
    regAllocator.clear(rhsRegister);
    return EvalResult.createRegisterResult(lhsRegister, VarType.INT);
  }

  private EvalResult emitOr(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    code.append("\tor ").append(lhsRegister).append(", ").append(lhsRegister).append(", ").append(rhsRegister).append("\n");
    regAllocator.clear(rhsRegister);
    return EvalResult.createRegisterResult(lhsRegister, VarType.BOOL);
  }

  private EvalResult emitAnd(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    code.append("\tand ").append(lhsRegister).append(", ").append(lhsRegister).append(", ").append(rhsRegister).append("\n");
    regAllocator.clear(rhsRegister);
    return EvalResult.createRegisterResult(lhsRegister, VarType.BOOL);
  }

  private EvalResult emitLE(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    code.append("\tsle ").append(lhsRegister).append(", ").append(lhsRegister).append(", ").append(rhsRegister).append("\n");
    regAllocator.clear(rhsRegister);
    return EvalResult.createRegisterResult(lhsRegister, VarType.BOOL);
  }

  private EvalResult emitLT(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    code.append("\tslt ").append(lhsRegister).append(", ").append(lhsRegister).append(", ").append(rhsRegister).append("\n");
    regAllocator.clear(rhsRegister);
    return EvalResult.createRegisterResult(lhsRegister, VarType.BOOL);
  }

  private EvalResult emitGT(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    code.append("\tsgt ").append(lhsRegister).append(", ").append(lhsRegister).append(", ").append(rhsRegister).append("\n");
    regAllocator.clear(rhsRegister);
    return EvalResult.createRegisterResult(lhsRegister, VarType.BOOL);
  }

  private EvalResult emitGE(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    code.append("\tsge ").append(lhsRegister).append(", ").append(lhsRegister).append(", ").append(rhsRegister).append("\n");
    regAllocator.clear(rhsRegister);
    return EvalResult.createRegisterResult(lhsRegister, VarType.BOOL);
  }

  private EvalResult emitEQ(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    code.append("\tseq ").append(lhsRegister).append(", ").append(lhsRegister).append(", ").append(rhsRegister).append("\n");
    regAllocator.clear(rhsRegister);
    return EvalResult.createRegisterResult(lhsRegister, VarType.BOOL);
  }

  private EvalResult emitNE(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    code.append("\tsne ").append(lhsRegister).append(", ").append(lhsRegister).append(", ").append(rhsRegister).append("\n");
    regAllocator.clear(rhsRegister);
    return EvalResult.createRegisterResult(lhsRegister, VarType.BOOL);
  }
}
