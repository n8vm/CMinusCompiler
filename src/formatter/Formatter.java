/*
 * Code formatter project
 * CS 4481
 */
package formatter;

import ast.Program;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import parser.CminusLexer;
import parser.CminusParser;
import parser.CminusParser.DeclarationContext;
import parser.CminusParser.ProgramContext;
import parser.CminusParser.VarDeclIdContext;
import parser.CminusParser.VarDeclarationContext;

/**
 *
 * @author edwajohn
 */
public class Formatter {

  private static Logger LOGGER;

  public Formatter() throws IOException {
  }

  public void format(String filename) throws IOException {
    ANTLRInputStream chars = new ANTLRInputStream(new FileReader(filename));
    CminusLexer lexer = new CminusLexer(chars);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    CminusParser parser = new CminusParser(tokens);
    parser.setBuildParseTree(true);
    ProgramContext programCtx = parser.program();

    ASTBuilder builder = new ASTBuilder();
    SymbolTable symbolTable = new SymbolTable(false);
    symbolTable.addSymbol("print", new SymbolInfo("print", null, -1, true));
    symbolTable.addSymbol("println", new SymbolInfo("println", null, -1, true));
    Program program = builder.buildProgram(programCtx, symbolTable);

    LOGGER = Logger.getLogger(Parser.class.getName());
    LOGGER.info("");
    LOGGER.info("Formatted code:");
    LOGGER.info("");
    LOGGER.info(program.toString());
//
//    ASTBuilder builder = new ASTBuilder();
//    SymbolTable symbolTable = new SymbolTable(false);
//    Program program = builder.buildProgram(programCtx, symbolTable);
//
//    LOGGER.info("");
//    LOGGER.info("Formatted code:");
//    LOGGER.info("");
//    LOGGER.info(program.toString());
  }

  public static String getFormatted(String sourceCode) {
    ANTLRInputStream chars = new ANTLRInputStream(sourceCode);
    CminusLexer lexer = new CminusLexer(chars);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    CminusParser parser = new CminusParser(tokens);
    parser.setBuildParseTree(true);
    ProgramContext programCtx = parser.program();

    ASTBuilder builder = new ASTBuilder();
    SymbolTable symbolTable = new SymbolTable(false);
    symbolTable.addSymbol("print", new SymbolInfo("print", null, -1, true));
    symbolTable.addSymbol("println", new SymbolInfo("println", null, -1, true));
    Program program = builder.buildProgram(programCtx, symbolTable);

    return program.toString();
  }

  /**
   * @param args the command line arguments
   * @throws java.io.IOException
   */
  public static void main(String[] args) throws IOException {
    // Logging setup
    Level level = Level.INFO;
    if (args[args.length - 1].equals("-v")) {
      level = Level.ALL;
    }
    Properties props = System.getProperties();
    props.setProperty("java.util.logging.SimpleFormatter.format", "%5$s%6$s%n");
    Logger.getLogger("").setLevel(level);
    for (Handler handler : Logger.getLogger("").getHandlers()) {
      handler.setLevel(level);
    }
    LOGGER = Logger.getLogger(Parser.class.getName());

    Formatter formatter = new Formatter();
    LOGGER.info("Parsing " + args[0] + "\n");
    formatter.format(args[0]);
  }

}
