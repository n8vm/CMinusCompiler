grammar Cminus;

//program : declarationList ;
program : declaration+ ;

declarationList : declarationList declaration | declaration ;

declaration : varDeclaration | funDeclaration ;

varDeclaration : typeSpecifier varDeclList ';' ;

scopedVarDeclaration : scopedTypeSpecifier varDeclList ';' ;

varDeclList : varDeclList ',' varDeclId | varDeclId ;
//varDeclList : varDeclList ',' varDeclInitialize | varDeclInitialize ;

//varDeclInitialize : varDeclId ;//| varDeclId ':' simpleExpression ;

varDeclId : ID | ID '[' NUMCONST ']' ;
 
scopedTypeSpecifier : 'static' typeSpecifier | typeSpecifier ;

typeSpecifier : 'int' | 'bool' | 'char' ;

funDeclaration : typeSpecifier ID '(' params ')' statement ;

params : paramList | /* epsilon */ ;

paramList : paramList ',' param | param ;
param : typeSpecifier paramId ;

//paramList : paramList ',' paramTypeList | paramTypeList ;

//paramTypeList : typeSpecifier paramIdList ;

//paramIdList : paramIdList ',' paramId | paramId ;

paramId : ID | ID '[]' ;

statement : expressionStmt | compoundStmt | selectionStmt
          | iterationStmt | returnStmt | breakStmt ;

compoundStmt : '{' localDeclarations statementList '}' ;

localDeclarations : localDeclarations scopedVarDeclaration | /* epsilon */ ;

statementList : statementList statement | /* epsilon */ ;

expressionStmt : expression ';' | ';' ;

selectionStmt : 'if' '(' simpleExpression ')' statement
              | 'if' '(' simpleExpression ')' statement 'else' statement
              ;

iterationStmt : 'while' '(' simpleExpression ')' statement
              | 'foreach' '(' mutable 'in' simpleExpression ')' statement
              ;

returnStmt : 'return' ';' | 'return' expression ';' ;

breakStmt : 'break' ';' ;

expression : mutable '=' expression
           | mutable '+=' expression | mutable '−=' expression
           | mutable '∗=' expression | mutable '/=' expression
           | mutable '++' | mutable '−−' | simpleExpression
           ;

simpleExpression : simpleExpression '|' andExpression | andExpression ;

andExpression : andExpression '&' unaryRelExpression | unaryRelExpression ;

unaryRelExpression : '!' unaryRelExpression | relExpression ;

relExpression : sumExpression relop sumExpression | sumExpression ;

relop : '<=' | '<' | '>' | '>=' | '==' | '!=' ;

sumExpression : sumExpression sumop term | term ;

sumop : '+' | '-' ;

term : term mulop unaryExpression | unaryExpression ;

mulop : '*' | '/' | '%' ;

unaryExpression : unaryop unaryExpression | factor ;

unaryop : '-' | '*' | '?' ;

factor : immutable | mutable ;

mutable : ID | ID '[' expression ']' ;

immutable : '(' expression ')' | call | constant ;

call : ID '(' args ')' ;

args : argList | /* epsilon */ ;

argList : argList ',' expression | expression ;

constant : NUMCONST | CHARCONST | STRINGCONST | 'true' | 'false' ;



ID : LETTER (LETTER | DIGIT)* ;
NUMCONST : DIGIT+ ;
STRINGCONST : '"' ('\\"'|~'"')*? '"' ;
CHARCONST : '"' ('\\"'|~'"') '"' ;

WS :  (' ' | '\t' | '\n' | '\r' | '\f')+ -> skip ;
COMMENT
  :   ( '//' ~[\r\n]* '\r'? '\n'
      | '/*' .*? '*/'
      ) -> skip
  ;

fragment LETTER : ('a'..'z' | 'A'..'Z');
fragment DIGIT  : ('0'..'9');
