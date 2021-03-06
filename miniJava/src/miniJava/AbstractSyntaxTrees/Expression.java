/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import  miniJava.SyntacticAnalyzer.SourcePosition;

public abstract class Expression extends AST {

  public Expression(SourcePosition posn) {
    super (posn);
  }
  public abstract String getName();
  public String getNamePos(){
	  return getName() + posn.toString();
  }
}
