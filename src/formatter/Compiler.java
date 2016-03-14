/*
 * Code formatter project
 * CS 4481
 */
package formatter;

import ast.Program;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import parser.CminusLexer;
import parser.CminusParser;
import parser.CminusParser.ProgramContext;

/**
 *
 * @author edwajohn
 */
public class Compiler {

  private static Logger LOGGER;

  public Compiler() throws IOException {
  }

  public void format(String filename) throws IOException {
    ANTLRInputStream chars = new ANTLRInputStream(new FileReader(filename));
    CminusLexer lexer = new CminusLexer(chars);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    CminusParser parser = new CminusParser(tokens);
    parser.setBuildParseTree(true);
    ProgramContext programCtx = parser.program();
    parser.getErrorHandler().toString();

    // Add print and println to the symbol table. These are built-in functions.
    ASTBuilder builder = new ASTBuilder();
    SymbolTable symbolTable = new SymbolTable(false);
    symbolTable.addSymbol("print", new SymbolInfo("print", null, -1, true));
    symbolTable.addSymbol("println", new SymbolInfo("println", null, -1, true));
    Program program = builder.buildProgram(programCtx, symbolTable);
    RegisterAllocator regAllocator = new RegisterAllocator();

    LOGGER.info("");
    LOGGER.info("Formatted code:");
    LOGGER.info("");
    LOGGER.info(program.toMIPS(symbolTable, regAllocator));

    PrintWriter writer = new PrintWriter("Cminus.asm");
    regAllocator.clearAll();
    writer.append(program.toMIPS(symbolTable, regAllocator));
    writer.close();
  }

  public static String getFormatted(String code) throws IOException {
    ANTLRInputStream chars = new ANTLRInputStream(code);
    CminusLexer lexer = new CminusLexer(chars);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    CminusParser parser = new CminusParser(tokens);
    parser.setBuildParseTree(true);
    ProgramContext programCtx = parser.program();

    // Add print and println to the symbol table. These are built-in functions.
    ASTBuilder builder = new ASTBuilder();
    SymbolTable symbolTable = new SymbolTable(false);
    symbolTable.addSymbol("print", new SymbolInfo("print", null, -1, true));
    symbolTable.addSymbol("println", new SymbolInfo("println", null, -1, true));
    Program program = builder.buildProgram(programCtx, symbolTable);
    RegisterAllocator regAllocator = new RegisterAllocator();

    return (program.toMIPS(symbolTable, regAllocator));
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

    Compiler formatter = new Compiler();
    LOGGER.info("Parsing " + args[0] + "\n");
    formatter.format(args[0]);
  }

}
