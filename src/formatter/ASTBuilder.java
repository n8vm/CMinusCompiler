/*
 * Code formatter project
 * CS 4481
 */
package formatter;

import ast.Program;
import ast.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import parser.CminusParser.*;

/**
 *
 * @author edwajohn
 */
public class ASTBuilder {

  private static Logger LOGGER = Logger.getLogger(ASTBuilder.class.getName());

  private SymbolTable symbolTable = null;

  public ASTBuilder() {
  }

  public Program buildProgram(ProgramContext programCtx, SymbolTable symbolTable) {
    this.symbolTable = symbolTable;
    List<Declaration> declarations = new ArrayList<>();
    for (DeclarationContext declarationCtx : programCtx.declaration()) {
      if (declarationCtx.varDeclaration() != null) {
        VarDeclarationContext vdc = declarationCtx.varDeclaration();
        declarations.add(getVarDeclaration(vdc, false));
      } else if (declarationCtx.funDeclaration() != null) {
        FunDeclarationContext fdc = declarationCtx.funDeclaration();
        declarations.add(getFunDeclaration(fdc));
      }
    }
    Program program = new Program(declarations);
    return program;
  }

  private VarDeclaration getVarDeclaration(VarDeclarationContext varDecl, boolean isStatic) {
    VarType type = VarType.fromString(varDecl.typeSpecifier().getText());
    List<String> ids = new ArrayList<>();
    List<Integer> arraySizes = new ArrayList<>();
    for (VarDeclIdContext idCtx : varDecl.varDeclId()) {
      if (idCtx.NUMCONST() != null) {
        ids.add(idCtx.ID().getText());
        arraySizes.add(Integer.parseInt(idCtx.NUMCONST().getText()));
      } else {
        ids.add(idCtx.ID().getText());
        arraySizes.add(-1);
      }
    }
    VarDeclaration vd = new VarDeclaration(type, ids, arraySizes, isStatic);
    for (int i = 0; i < ids.size(); ++i) {
//    for (String id : ids) {
      final String id = ids.get(i);
      final int size = arraySizes.get(i);
      //this.symbolTable.addSymbol(id, new SymbolInfo(id, type, false));
      this.symbolTable.addLocalVariable(id, new SymbolInfo(id, type, size, false));
    }
    return vd;
  }

  private FunDeclaration getFunDeclaration(FunDeclarationContext funDecl) {
    VarType returnType = null;
    if (funDecl.typeSpecifier() != null) {
      returnType = VarType.fromString(funDecl.typeSpecifier().getText());
    }
    final String id = funDecl.ID().getText();
    List<Param> params = new ArrayList<>();
    for (ParamContext paramCtx : funDecl.param()) {
      VarType type = VarType.fromString(paramCtx.typeSpecifier().getText());
      ParamIdContext paramIdCtx = paramCtx.paramId();
      String paramId = paramIdCtx.ID().getText();
      final boolean array = paramIdCtx.getChildCount() > 1;
      params.add(new Param(type, paramId, array));
    }

    symbolTable = symbolTable.createChildFunction(returnType);
    for (Param param : params) {
      symbolTable.addLocalVariable(param.getId(),
              new SymbolInfo(param.getId(), param.getType(), -1, false));
    }
    Statement statement = getStatement(funDecl.statement());
    FunDeclaration fd = new FunDeclaration(returnType, id, params, statement, symbolTable);

    symbolTable = symbolTable.getParent();
    symbolTable.addSymbol(id, new SymbolInfo(id, returnType, -1, true));

    return fd;
  }

  /**
   * Given a statement context, returns a list of AST statements.
   *
   * @param statementCtx
   * @return
   */
  private Statement getStatement(StatementContext statementCtx) {
    Statement statement = null;
    if (statementCtx.expressionStmt() != null) {
      // Expression
      ExpressionContext ec = statementCtx.expressionStmt().expression();
      if (ec != null) {
        statement = new ExpressionStatement(getExpression(ec));
      }
    } else if (statementCtx.compoundStmt() != null) {
      // Compound {}
      symbolTable = symbolTable.createChildCompound();
      statement = unrollCompound(statementCtx.compoundStmt());
      symbolTable = symbolTable.getParent();
    } else if (statementCtx.ifStmt() != null) {
      // if
      IfStmtContext ssc = statementCtx.ifStmt();
      Expression ifExp = getSimpleExpression(ssc.simpleExpression());
      List<StatementContext> ifStmts = new ArrayList<>(ssc.statement());
      Statement trueStatements = getStatement(ifStmts.get(0));
      Statement falseStatements = null;
      if (ifStmts.size() > 1) {
        falseStatements = getStatement(ifStmts.get(1));
      }
      statement = new If(ifExp, trueStatements, falseStatements);
    } else if (statementCtx.whileStmt() != null) {
      // while
      WhileStmtContext ic = statementCtx.whileStmt();
      Expression whileExp = getSimpleExpression(ic.simpleExpression());
      Statement whileStmt = getStatement(ic.statement());
      statement = new While(whileExp, whileStmt);
    } else if (statementCtx.returnStmt() != null) {
      // return
      Expression exp = null;
      if (statementCtx.returnStmt().expression() != null) {
        exp = getExpression(statementCtx.returnStmt().expression());
      }
      statement = new Return(exp);
    } else if (statementCtx.breakStmt() != null) {
      // break
      statement = new Break();
    }
    return statement;
  }

  private CompoundStatement unrollCompound(CompoundStmtContext compoundStmtCtx) {
    List<Statement> statements = new ArrayList<>();
    for (VarDeclarationContext vdc : compoundStmtCtx.varDeclaration()) {
      statements.add(getVarDeclaration(vdc, false));
    }
    for (StatementContext sc : compoundStmtCtx.statement()) {
      statements.add(getStatement(sc));
    }
    return new CompoundStatement(statements, symbolTable);
  }

  private Expression getExpression(ExpressionContext ec) {
    Expression expression = null;
    MutableContext mc = ec.mutable();
    if (mc != null) {
      // assignment
      ExpressionContext rhsCtx = ec.expression();
      Expression rhs = null;
      if (rhsCtx != null) {
        // binary
        rhs = getExpression(rhsCtx);
      }
      expression = new Assignment(getMutable(mc), ec.getChild(1).getText(), rhs);
    } else {
      // simple expression
      expression = getOrExpression(ec.simpleExpression().orExpression());
    }
    return expression;
  }

  private Expression getSimpleExpression(SimpleExpressionContext ctx) {
    return getOrExpression(ctx.orExpression());
  }

  private Expression getOrExpression(OrExpressionContext oec) {
    ArrayList<AndExpressionContext> ands = new ArrayList<>(oec.andExpression());
    assert (!ands.isEmpty());
    if (ands.size() == 1) {
      return getAndExpression(ands.get(0));
    }
    BinaryOperator or = new BinaryOperator(getAndExpression(ands.get(0)), "||", getAndExpression(ands.get(1)));
    for (int i = 2; i < ands.size(); ++i) {
      or = new BinaryOperator(or, "||", getAndExpression(ands.get(i)));
    }
    return or;
  }

  private Expression getAndExpression(AndExpressionContext aec) {
    ArrayList<UnaryRelExpressionContext> rels = new ArrayList<>(aec.unaryRelExpression());
    if (rels.size() == 1) {
      return getUnaryRelExpression(rels.get(0));
    }
    BinaryOperator and = new BinaryOperator(getUnaryRelExpression(rels.get(0)), "&&", getUnaryRelExpression(rels.get(1)));
    for (int i = 2; i < rels.size(); ++i) {
      and = new BinaryOperator(and, "&&", getUnaryRelExpression(rels.get(i)));
    }
    return and;
  }

  private Expression getUnaryRelExpression(UnaryRelExpressionContext aec) {
    Expression expression = getRelExpression(aec.relExpression());
    for (int i = 0; i < aec.BANG().size(); ++i) {
      expression = new UnaryOperator("!", expression);
    }
    return expression;
  }

  private Expression getRelExpression(RelExpressionContext ctx) {
    ArrayList<SumExpressionContext> sums = new ArrayList<>(ctx.sumExpression());
    ArrayList<RelopContext> relops = new ArrayList<>(ctx.relop());
    if (sums.size() == 1) {
      return getSumExpression(sums.get(0));
    }
    BinaryOperator rel = new BinaryOperator(getSumExpression(sums.get(0)), relops.get(0).getText(), getSumExpression(sums.get(1)));
    for (int i = 1; i < relops.size(); ++i) {
      rel = new BinaryOperator(rel, relops.get(i).getText(), getSumExpression(sums.get(i + 1)));
    }
    return rel;
  }

  private Expression getSumExpression(SumExpressionContext ctx) {
    List<TermExpressionContext> terms = new ArrayList<>(ctx.termExpression());
    List<SumopContext> sumops = new ArrayList<>(ctx.sumop());
    if (terms.size() == 1) {
      return getTermExpression(terms.get(0));
    }
    BinaryOperator op = new BinaryOperator(
            getTermExpression(terms.get(0)),
            sumops.get(0).getText(),
            getTermExpression(terms.get(1)));
    for (int i = 1; i < sumops.size(); ++i) {
      op = new BinaryOperator(
              op,
              sumops.get(i).getText(),
              getTermExpression(terms.get(i + 1)));
    }
    return op;
  }

  private Expression getTermExpression(TermExpressionContext ctx) {
    ArrayList<UnaryExpressionContext> unaries = new ArrayList<>(ctx.unaryExpression());
    ArrayList<MulopContext> mulops = new ArrayList<>(ctx.mulop());
    if (unaries.size() == 1) {
      return getUnaryExpression(unaries.get(0));
    }
    BinaryOperator op = new BinaryOperator(getUnaryExpression(unaries.get(0)), mulops.get(0).getText(), getUnaryExpression(unaries.get(1)));
    for (int i = 1; i < mulops.size(); ++i) {
      op = new BinaryOperator(op, mulops.get(i).getText(), getUnaryExpression(unaries.get(i + 1)));
    }
    return op;
  }

  private Expression getUnaryExpression(UnaryExpressionContext ctx) {
    Expression expression = getFactorExpression(ctx.factor());
    ArrayList<UnaryopContext> ops = new ArrayList<>(ctx.unaryop());
    for (int i = ops.size() - 1; i >= 0; --i) {
      expression = new UnaryOperator(ops.get(i).getText(), expression);
    }
    return expression;
  }

  private Expression getFactorExpression(FactorContext ctx) {
    if (ctx.mutable() != null) {
      return getMutable(ctx.mutable());
    }
    return getImmutable(ctx.immutable());
  }

  private Mutable getMutable(MutableContext mc) {
    String id = mc.ID().getText();
    if (symbolTable.find(id) == null) {
      LOGGER.warning("Undefined symbol on line " + mc.getStart().getLine() + ": " + id);
    }
    Expression index = null;
    if (mc.expression() != null) {
      index = getExpression(mc.expression());
    }
    return new Mutable(id, index);
  }

  private Expression getImmutable(ImmutableContext mc) {
    if (mc.expression() != null) {
      return new ParenExpression(getExpression(mc.expression()));
    }
    if (mc.call() != null) {
      return getCall(mc.call());
    }
    return getConstant(mc.constant());
  }

  private Expression getCall(CallContext call) {
    final String id = call.ID().getText();
    ArrayList<Expression> args = new ArrayList<>();
    for (ExpressionContext exprCtx : call.expression()) {
      args.add(getExpression(exprCtx));
    }
    if (this.symbolTable.find(id) == null) {
      LOGGER.warning("Undefined symbol on line " + call.getStart().getLine() + ": " + id);
    }
    return new Call(id, args);
  }

  private Expression getConstant(ConstantContext constant) {
    if (constant.NUMCONST() != null) {
      return new NumConstant(Integer.parseInt(constant.NUMCONST().getText()));
    }
    if (constant.CHARCONST() != null) {
      return new CharConstant(constant.CHARCONST().getText().charAt(0));
    }
    if (constant.STRINGCONST() != null) {
      return new StringConstant(constant.STRINGCONST().getText());
    }
    if (constant.getText().equals("true")) {
      return new BoolConstant(true);
    }
    return new BoolConstant(false);
  }
}
