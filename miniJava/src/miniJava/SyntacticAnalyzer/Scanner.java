/**
 * Name: Pintian Zhang
 * PID:720131743
 * Assignment 1
 * 
 * 	Scanner component for the course compiler
 * 
 *  Scan the input until the end one character at a time and return individual character or string as token for the parser
 *  For the list of token se TokenKing.java
 *  Whitespace, tab, newline, line comment and block comments are ignored
 *
 *  Grammar:
 *   NUM ::= digit digit*
 *   digit ::= '0' | ... | '9'
 *   Keyword:= true | false | int | boolean | this | new | if | else | while | class | return | public | private | static | void'
 *   ID ::= LETTER (LETTER | digit)*
 *   LETTER: a|..|Z
 *   unop: !|-, 
 *   binop: > | < | == | <= | >= | != | && | || | + | - | * | /
 *   misc ::= ( | ) | { | } | [ | ] ; | , | . 
 *   EOT ::= $
 */
package miniJava.SyntacticAnalyzer;

import miniJava.ErrorReporter;

public class Scanner{

	private SourceFile source;
	private ErrorReporter reporter;

	//private char currentChar;
	private int curCharInt;
	private StringBuilder currentSpelling;
	private boolean incorporatePredefined;
	
	private final static char eolUnix = '\n';
	private final static char eolWindows = '\r';

	public Scanner(SourceFile source, ErrorReporter reporter, boolean incorporatePredefined) {
		this.source = source;
		this.reporter = reporter;
		this.incorporatePredefined = incorporatePredefined;

		// initialize scanner state
		readChar();
	}

	/**
	 * skip whitespace, tab, newline, line comment, block comments and scan next token
	 * @return token
	 */
	public Token scan() {

		// skip whitespace, tab, newline, line comment, block comments
		while (getCurChar() == ' ' || getCurChar() == eolUnix || getCurChar() == eolWindows || getCurChar() == '\t' || getCurChar() == '/'){
			if(getCurChar() == '/'){
				if(peekChar()=='/'){ // comment 1 line
					skipIt();
					skipIt();
					while(getCurChar() != eolUnix && getCurChar() !=eolWindows){
						if(getCurCharInt() == -1){
							return new Token(TokenKind.EOT, "", null);
						}
						skipIt();
					}
					skipIt();
					continue;
				} else if(peekChar() =='*'){ // comment block
					skipIt();
					skipIt();
					while(getCurChar() != '*' || peekChar()!='/'){
						if(getCurCharInt() == -1){
							scanError("unterminated block comment");
							return new Token(TokenKind.ERROR, "", new SourcePosition(source.getCurrentLine(), source.getCurrentLine()));
						} else {
							skipIt();
						}
					}
					skipIt();
					skipIt();
					continue;
				} else{
					break;
				}
			}
			skipIt();
		}
		
		//check for --
		while(getCurChar() == '-' && peekChar() =='-'){
			scanError("decrement is not allowed in mini java");
			return new Token(TokenKind.ERROR, "",  new SourcePosition(source.getCurrentLine(), source.getCurrentLine()));
		}
		
		// collect spelling and identify token kind
		currentSpelling = new StringBuilder();
		SourcePosition pos = new SourcePosition();
	    pos.start = source.getCurrentLine();
		TokenKind kind = scanToken();
		pos.finish = source.getCurrentLine();
		// return new token
		return new Token(kind, currentSpelling.toString(), pos);
	}

	/**
	 *   TokenKind is a simple enumeration of the different kinds of tokens
	 *   NUM ::= digit digit*
	 *   digit ::= '0' | ... | '9'
	 *   Keyword:= true | false | int | boolean | this | new | if | else | while | class | return | public | private | static | void'
	 *   	each keyword has its own token denoted by KW_xxx
	 *   ID ::= LETTER (LETTER | digit)*
	 *   LETTER: a|..|Z
	 *   unop: !|-, 
	 *   binop: > | < | == | <= | >= | != | && | || | + | - | * | /, 
	 *   	each operation has its own token denoted by OP_xxx
	 *   	note that '/' is both Binop and Unop
	 *   misc ::= ( | ) | { | } | [ | ] ; | , | . 
	 *   	each misc has its own token denoted by xxx
	 *   EOT ::= $
	 */
	public TokenKind scanToken() {

		// scan Token
		switch (getCurChar()) {
		case '!':
			takeIt();
			if(getCurChar()=='='){
				takeIt();
				return(TokenKind.OP_NEQ);
			}
			return(TokenKind.OP_NEGATE);
		case '=':
			takeIt();
			if(getCurChar()=='='){
				takeIt();
				return(TokenKind.OP_EQ);
			}
			return(TokenKind.OP_EQUAL);
		case '<':
			takeIt();
			if(getCurChar()=='='){
				takeIt();
				return(TokenKind.OP_LTE);
			}
			return(TokenKind.OP_LT);
		case '>':
			takeIt();
			if(getCurChar()=='='){
				takeIt();
				return(TokenKind.OP_GTE);
			}
			return(TokenKind.OP_GT);
		case '&':
			takeIt();
			if(getCurChar()=='&'){
				takeIt();
				return(TokenKind.OP_AND);
			}
			return(TokenKind.ERROR);
		case '|':
			takeIt();
			if(getCurChar()=='|'){
				takeIt();
				return(TokenKind.OP_OR);
			}
			return(TokenKind.ERROR);
		case '+':
			takeIt();
			return(TokenKind.OP_PLUS);

		case '*':
			takeIt();
			return(TokenKind.OP_TIMES);
		
		case '-':
			takeIt();
			return(TokenKind.OP_MINUS);

		case '/':
			takeIt();
			return(TokenKind.OP_DIVIDE);

		case '(': 
			takeIt();
			return(TokenKind.LPAREN);

		case ')':
			takeIt();
			return(TokenKind.RPAREN);
			
		case '[': 
			takeIt();
			return(TokenKind.LSQRBRACKET);

		case ']':
			takeIt();
			return(TokenKind.RSQRBRACKET);
		
		case '{': 
			takeIt();
			return(TokenKind.LCURLYBRACKET);

		case '}':
			takeIt();
			return(TokenKind.RCURLYBRACKET);
			
		case ';':
			takeIt();
			return(TokenKind.SEMICOLON);
			
		case ',':
			takeIt();
			return(TokenKind.COMMA);
			
		case '.':
			takeIt();
			return(TokenKind.PERIOD);

		case '0': case '1': case '2': case '3': case '4':
		case '5': case '6': case '7': case '8': case '9':
			while (isDigit(getCurChar()))
				takeIt();
			return(TokenKind.NUM);
		
		case 'a': case 'b': case 'c': case 'd': case 'e':
		case 'f': case 'g': case 'h': case 'i': case 'j':
		case 'k': case 'l': case 'm': case 'n': case 'o':
		case 'p': case 'q': case 'r': case 's': case 't':
		case 'u': case 'v': case 'w': case 'x': case 'y': case 'z': 
		case 'A': case 'B': case 'C': case 'D': case 'E':
		case 'F': case 'G': case 'H': case 'I': case 'J':
		case 'K': case 'L': case 'M': case 'N': case 'O':
		case 'P': case 'Q': case 'R': case 'S': case 'T':
		case 'U': case 'V': case 'W': case 'X': case 'Y': case 'Z': 
			while (isDigit(getCurChar()) || isLetter(getCurChar()) || getCurChar()=='_'){
				takeIt();
			}
			if(keywordCheck()!=null){
				return(keywordCheck());
			}
			return(TokenKind.ID);

		case '_':
			if(incorporatePredefined && checkPreDefinedKW()){
				while (isDigit(getCurChar()) || isLetter(getCurChar()) || getCurChar()=='_'){
					takeIt();
				}
				return(TokenKind.ID);
			} 
		default:
			if(getCurCharInt() == -1) return(TokenKind.EOT); //EOT
			scanError("Unrecognized character '" + getCurChar() + "' in input");
			return(TokenKind.ERROR);
		}
	}
	
	private boolean checkPreDefinedKW(){
		if((getCurChar()+peekCharStr(11)).equals("_PrintStream")){
			if(peekCharStr(12).charAt(11) == '_' || isDigit(peekCharStr(12).charAt(11)) || isLetter(peekCharStr(12).charAt(11))){
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	private void takeIt() {
		currentSpelling.append(getCurChar());
		nextChar();
	}

	private void skipIt() {
		nextChar();
	}
	
	//check if char is digit
	private boolean isDigit(char c) {
		return (c >= '0') && (c <= '9');
	}
	//check if char is letter
	private boolean isLetter(char c) {
		return ((c >= 'a') && (c <= 'z'))||((c >= 'A') && (c <= 'Z'));
	}
	//check if string is keyword
	private TokenKind keywordCheck() {
		switch (currentSpelling.toString()) {
			case "new":
				return(TokenKind.KW_NEW);
			case "if":
				return(TokenKind.KW_IF);
			case "else":
				return(TokenKind.KW_ELSE);
			case "while":
				return(TokenKind.KW_WHILE);
			case "class":
				return(TokenKind.KW_CLASS);
			case "return":
				return(TokenKind.KW_RETURN);
			case "public":
				return(TokenKind.KW_PUBLIC);
			case "private":
				return(TokenKind.KW_PRIVATE);
			case "static":
				return(TokenKind.KW_STATIC);
			case "void":
				return(TokenKind.KW_VOID);
			case "true":
				return(TokenKind.KW_TRUE);
			case "false":
				return(TokenKind.KW_FALSE);
			case "int":
				return(TokenKind.KW_INT);
			case "boolean":
				return(TokenKind.KW_BOOLEAN);
			case "this":
				return(TokenKind.KW_THIS);
			case "null":
				return(TokenKind.KW_NULL);
		}
		return null;
	}
	
	private void scanError(String m) {
		reporter.reportError("Scan Error:  " + m);
	}

	/**
	 * advance to next char in inputstream
	 * detect end of line or end of file and substitute '$' as distinguished eot terminal
	 */
	private void nextChar() {
		if (getCurCharInt() != -1)
			readChar();
	}

	private void readChar() {
		curCharInt = source.getSourceInt();
	}
	private char peekChar() {
		return source.sourcePeek();
	}
	private String peekCharStr(int len) {
		return source.sourcePeekStr(len);
	}
	
	private int getCurCharInt() {
		return curCharInt;
	}
	private char getCurChar() {
		return (char)curCharInt;
	}
}