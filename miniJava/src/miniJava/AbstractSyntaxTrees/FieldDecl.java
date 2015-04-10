/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class FieldDecl extends MemberDecl {
	
	public FieldDecl(boolean isPrivate, boolean isStatic, Type t, String name, SourcePosition posn){
    super(isPrivate, isStatic, t, name, posn);
    isArrayLength = false;
	}
	
	public FieldDecl(MemberDecl md, SourcePosition posn) {
		super(md,posn);
		isArrayLength = false;
	}
	
	public <A, R> R visit(Visitor<A, R> v, A o) {
        return v.visitFieldDecl(this, o);
    }
	public boolean isArrayLength;
}

