/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */

package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class ArrayType extends Type {

	    public ArrayType(Type eltType, SourcePosition posn){
	        super(TypeKind.ARRAY, posn);
	        this.eltType = eltType;
	    }
	        
	    public <A,R> R visit(Visitor<A,R> v, A o) {
	        return v.visitArrayType(this, o);
	    }
	    
	    @Override
	    public boolean equals(Type type){
	    	if(type instanceof NullType) return true;
	    	return (super.equals(type) && eltType.equals(((ArrayType)type).eltType));
	    }
	    private int length;
	    public Type eltType;
	}

