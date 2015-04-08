/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.CodeGenerator.RuntimeEntity;
import miniJava.SyntacticAnalyzer.SourcePosition;

public abstract class AST {

  public AST (SourcePosition posn) {
    this.posn = posn;
    astType = null;
  }
  
  public String toString() {
      String fullClassName = this.getClass().getName();
      String cn = fullClassName.substring(1 + fullClassName.lastIndexOf('.'));
      if (ASTDisplay.showPosition)
    	  cn = cn + " " + posn.toString();
      return cn;
  }

  public abstract <A,R> R visit(Visitor<A,R> v, A o);

  public SourcePosition posn;
  public Type astType;
  public RuntimeEntity runtimeEntity;
}
