package miniJava.SyntacticAnalyzer;

import miniJava.SyntacticAnalyzer.SourcePosition;
/**
 *  A token has a kind and a spelling
 *  In a full compiler it would also have a source position 
 */
public class Token {
	public TokenKind kind;
	public String spelling;
	public SourcePosition position;

	public Token(TokenKind kind, String spelling, SourcePosition position) {
		this.kind = kind;
		this.spelling = spelling;
		this.position = position;
	}
}