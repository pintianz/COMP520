/**
 *   TokenKind is a simple enumeration of the different kinds of tokens
 *   
 *   NUM: 0|..|9, 
 *   LETTER: a|..|Z
 *   UNOP: !|-, 
 *   BINOP: > | < | == | <= | >= | != | && | || | + | - | * | /, 
 *   VAL_TRUE,
 *   VAL_FALSE, 
 *   TYPE_INT, 
 *   TYPE_BOOLEAN, 
 *   ID: LETTER (NUM|LETTER)*, 
 *   KW_NEW, 
 *   KW_IF, 
 *   KW_ELSE, 
 *   KW_WHILE, 
 *   KW_CLASS, 
 *   KW_RETURN, 
 *   KW_PUBLIC,
 *   KW_PRIVATE, 
 *   KW_STATIC, 
 *   KW_VOID, 
 *   LPAREN, 
 *   RPAREN, 
 *   LBRACKET, 
 *   RBRACKET, 
 *   EOT, 
 *   ERROR
 */
package miniJava.SyntacticAnalyzer;

public enum TokenKind {NUM, OP_EQUAL, OP_NEGATE, OP_GT, OP_LT, OP_EQ, OP_GTE, OP_LTE, OP_NEQ, OP_AND, OP_OR, OP_PLUS, OP_MINUS, OP_TIMES, OP_DIVIDE, KW_TRUE, KW_FALSE, KW_INT, KW_BOOLEAN, KW_THIS, ID, KW_NEW, KW_IF, KW_ELSE, KW_WHILE, KW_CLASS, KW_RETURN, KW_PUBLIC,KW_PRIVATE, KW_STATIC, KW_VOID, LPAREN, RPAREN, LCURLYBRACKET, RCURLYBRACKET, LSQRBRACKET, RSQRBRACKET, SEMICOLON, COMMA, PERIOD, EOT, ERROR}