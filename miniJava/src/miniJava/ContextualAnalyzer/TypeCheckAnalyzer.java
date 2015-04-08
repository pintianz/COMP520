/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.ContextualAnalyzer;

import java.util.Iterator;

import miniJava.SyntacticAnalyzer.SourcePosition;
import miniJava.SyntacticAnalyzer.Token;
import miniJava.SyntacticAnalyzer.TokenKind;
import miniJava.ErrorReporter;
import miniJava.AbstractSyntaxTrees.*;
import miniJava.AbstractSyntaxTrees.Package;
/*
 * 
 */
public class TypeCheckAnalyzer implements Visitor<String,Type> {
	
	private ErrorReporter reporter;
	private static SourcePosition dummyPos = new SourcePosition();
	private static Type dummyErrorType = new BaseType(TypeKind.ERROR,dummyPos);
	private static Type dummyUnsupportedType = new BaseType(TypeKind.UNSUPPORTED,dummyPos);
	private static Type dummyBooleanType = new BaseType(TypeKind.BOOLEAN,dummyPos);
	private static Type dummyIntType = new BaseType(TypeKind.INT,dummyPos);
	private static Type dummyVoidType = new BaseType(TypeKind.VOID,dummyPos);
	private static Type dummyArrayIntType = new ArrayType(dummyIntType,dummyPos);
	private static Type dummyNullType = new NullType(TypeKind.NULL,dummyPos);
	
	private Type curClass;
	
	public TypeCheckAnalyzer (ErrorReporter reporter) {
		this.reporter = reporter;
	}
	
	// Reports that the identifier or operator used at a leaf of the AST
	// has not been declared.

	public static boolean showPosition = false;
    
    /**
     * print text representation of AST to stdout
     * @param ast root node of AST 
     */
    public void check(AST ast){
        ast.visit(this, "");
    }   
   
	///////////////////////////////////////////////////////////////////////////////
	//
	// PACKAGE
	//
	/////////////////////////////////////////////////////////////////////////////// 

    public Type visitPackage(Package prog, String arg){
        for (ClassDecl c: prog.classDeclList){
        	c.visit(this, "");
        }
        return null;
    }
    
    
	///////////////////////////////////////////////////////////////////////////////
	//
	// DECLARATIONS
	//
	///////////////////////////////////////////////////////////////////////////////
    
    public Type visitClassDecl(ClassDecl clas, String arg){
    	curClass = new ClassType(new Identifier(new Token(TokenKind.ID, clas.name, dummyPos)), dummyPos);
        for (MethodDecl m: clas.methodDeclList){
        	m.visit(this, "");
        }
        return null;
    }
    
    public Type visitFieldDecl(FieldDecl f, String arg){
    	f.type.visit(this, "");
        return null;
    }
    
    public Type visitMethodDecl(MethodDecl m, String arg){
    	m.type.visit(this, "");
    	
        ParameterDeclList pdl = m.parameterDeclList;
        for (ParameterDecl pd: pdl) {
        	pd.visit(this, "");
        }
        StatementList sl = m.statementList;
        for (Statement s: sl) {
            s.visit(this, "");
        }
        if(m.type.equals(dummyVoidType)){
        	if (m.returnExp != null){
            	TypeCheckError("Cannot return value in void method "+m.name+" at "+m.returnExp.posn.toString());
        	}
        } else {
        	if (m.returnExp == null) {
        		TypeCheckError("Return expr value missing in "+m.name+" at "+m.posn.toString());
        	} else {
        		if(m.returnExp.visit(this, "")!=null && !m.type.equals(m.returnExp.visit(this, ""))){
        			TypeCheckError("Return expression value does not match method declaration in "+m.name+" at "+m.returnExp.posn.toString());
        		}
        	}
        }
        
        return null;
    }
    
    public Type visitParameterDecl(ParameterDecl pd, String arg){
        pd.type.visit(this, "");
        return null;
    } 
    
    public Type visitVarDecl(VarDecl vd, String arg){ //local variable decl
        vd.type.visit(this, "");
        return null;
    }
 
	
	///////////////////////////////////////////////////////////////////////////////
	//
	// TYPES
	//
	///////////////////////////////////////////////////////////////////////////////
    
    @Override
	public Type visitNullType(NullType type, String arg) {
		// TODO Auto-generated method stub
		return null;
	}
    
    public Type visitBaseType(BaseType type, String arg){
        return null;
    }
    
    public Type visitClassType(ClassType type, String arg){
        return null;
    }
    
    public Type visitArrayType(ArrayType type, String arg){
        return null;
    }
    
	
	///////////////////////////////////////////////////////////////////////////////
	//
	// STATEMENTS
	//
	///////////////////////////////////////////////////////////////////////////////

    public Type visitBlockStmt(BlockStmt stmt, String arg){
        StatementList sl = stmt.sl;
        for (Statement s: sl) {
        	s.visit(this, "");
        }
        return null;
    }
    
    public Type visitVardeclStmt(VarDeclStmt stmt, String arg){
        Type eType = stmt.initExp.visit(this, "");
        if(!eType.equals(dummyErrorType) && !eType.equals(dummyUnsupportedType)){
        	if(!stmt.varDecl.type.equals(eType)){
            	TypeCheckError("variable declaration type mismatch at vardecl statement "+stmt.getPos());
            }
        }
        return null;
    }
    
    public Type visitAssignStmt(AssignStmt stmt, String arg){
    	Type rType = stmt.ref.visit(this, "");
        Type vType = stmt.val.visit(this, "");
        if(!vType.equals(dummyErrorType) && !vType.equals(dummyUnsupportedType) && !rType.equals(dummyErrorType) && !rType.equals(dummyUnsupportedType)){
        	if(!vType.equals(rType)){
            	TypeCheckError("assignment type mismatch at "+stmt.getPos());
            }
        }
        return null;
    }
    
    public Type visitCallStmt(CallStmt stmt, String arg){
    	ExprList al = stmt.argList;
        for (Expression e: al) {
            Type eType = e.visit(this, "");
            if(eType.equals(dummyErrorType) || eType.equals(dummyUnsupportedType)) return null;
        }
        Type methodType = stmt.methodRef.visit(this, "");
        if(!methodType.equals(dummyErrorType) && !methodType.equals(dummyUnsupportedType)){
        	MethodDecl oriDecl = (MethodDecl)stmt.methodRef.decl;
        	if(oriDecl.parameterDeclList.size() != al.size()){
        		TypeCheckError("call statement at "+stmt.getPos()+"does not have the right number of argument");
        	} else {
	        	Iterator<Expression> alIter = al.iterator();
	        	Iterator<ParameterDecl> plIter = oriDecl.parameterDeclList.iterator();
	            while (plIter.hasNext()) {
	            	Expression a = alIter.next();
	            	ParameterDecl p= plIter.next();
	                Type argType = a.visit(this, "");
	                if(!argType.equals(p.type)){
	            		TypeCheckError("argument: "+a.getNamePos()+" of call statement at "+stmt.getPos()+" does not match the type in the declaration");            		
	                }
	            }
        	}
        }
        return null;
    }
    
    public Type visitIfStmt(IfStmt stmt, String arg){
        Type cType = stmt.cond.visit(this, "");
        if(!cType.equals(dummyErrorType) && !cType.equals(dummyUnsupportedType)){
        	if(!cType.equals(dummyBooleanType)){
        		TypeCheckError("If statement at "+stmt.getPos()+" condition type not boolean");            		
        	}
	        stmt.thenStmt.visit(this, "");
	        if (stmt.elseStmt != null)
	            stmt.elseStmt.visit(this, "");
        }
        return null;
    }
    
    public Type visitWhileStmt(WhileStmt stmt, String arg){
        Type cType = stmt.cond.visit(this, "");
        if(!cType.equals(dummyErrorType) && !cType.equals(dummyUnsupportedType)){
        	if(!cType.equals(dummyBooleanType)){
        		TypeCheckError("While statement at "+stmt.getPos()+" condition type not boolean");            		
        	}
	        stmt.body.visit(this, "");
        }
        return null;
    }
    

	///////////////////////////////////////////////////////////////////////////////
	//
	// EXPRESSIONS
	//
	///////////////////////////////////////////////////////////////////////////////

    public Type visitUnaryExpr(UnaryExpr expr, String arg){
        Type ret = null;
    	expr.operator.visit(this, "");
        Type innerType = expr.expr.visit(this, "");
        if(innerType.equals(dummyUnsupportedType) || innerType.equals(dummyErrorType)){
        	ret = dummyErrorType; 
        } else {
        	switch(expr.operator.kind){
            case OP_MINUS:
            	if(!innerType.equals(dummyIntType)){
            		TypeCheckError("Did not find type int at numerical negate unary expression: "+expr.getNamePos());
            		ret = dummyUnsupportedType;
            	} else {
            		ret = innerType;
            	}
            	break;
            case OP_NEGATE:
            	if(!innerType.equals(dummyBooleanType)){
            		TypeCheckError("Did not find type boolean at logical negate  unary expression: "+expr.getNamePos());
            		ret = dummyUnsupportedType;
            	} else {
            		ret = innerType;
            	}
            	break;
    		default:
    			TypeCheckError("Unsupported unary operator");
    			ret = dummyUnsupportedType;
    			break;
            }
        }
        expr.astType = ret;
        return ret;
    }
    
    public Type visitBinaryExpr(BinaryExpr expr, String arg){
    	Type ret = null;
    	 expr.operator.visit(this, "");
    	 Type leftType = expr.left.visit(this, "");
    	 Type rightType = expr.right.visit(this, "");
    	 
         if(leftType.equals(dummyUnsupportedType) || leftType.equals(dummyErrorType) || rightType.equals(dummyUnsupportedType) || rightType.equals(dummyErrorType)){
         	ret = dummyErrorType; 
         } else {
         	ret = typeCheckBinaryExpr(expr, leftType, rightType, expr.operator);
         }
         expr.astType = ret;
         return ret;
    }
    
    private Type typeCheckBinaryExpr(BinaryExpr expr, Type leftType, Type rightType, Operator operator){
    	Type ret = null;
    	switch(operator.kind){
    	case OP_PLUS:
    		if(!leftType.equals(dummyIntType) || !rightType.equals(dummyIntType)){
        		TypeCheckError("Type mismatch at ADDITION binary expression: "+expr.getNamePos()+" need 2 int expressions");
        		ret = dummyUnsupportedType;
        	} else {
        		ret = leftType;
        	}
        	break;
        case OP_MINUS:
        	if(!leftType.equals(dummyIntType) || !rightType.equals(dummyIntType)){
        		TypeCheckError("Type mismatch at SUBTRACTION binary expression: "+expr.getNamePos()+" need 2 int expressions");
        		ret = dummyUnsupportedType;
        	} else {
        		ret = leftType;
        	}
        	break;
        case OP_TIMES:
        	if(!leftType.equals(dummyIntType) || !rightType.equals(dummyIntType)){
        		TypeCheckError("Type mismatch at MULTIPLICATION binary expression: "+expr.getNamePos()+" need 2 int expressions");
        		ret = dummyUnsupportedType;
        	} else {
        		ret = leftType;
        	}
        	break;
        case OP_DIVIDE:
        	if(!leftType.equals(dummyIntType) || !rightType.equals(dummyIntType)){
        		TypeCheckError("Type mismatch at DIVISION binary expression: "+expr.getNamePos()+" need 2 int expressions");
        		ret = dummyUnsupportedType;
        	} else {
        		ret = leftType;
        	}
        	break;
        case OP_AND:
        	if(!leftType.equals(dummyBooleanType) || !rightType.equals(dummyBooleanType)){
        		TypeCheckError("Type mismatch at AND binary expression: "+expr.getNamePos()+" need 2 boolean expressions");
        		ret = dummyUnsupportedType;
        	} else {
        		ret = leftType;
        	}
        	break;
        case OP_OR:
        	if(!leftType.equals(dummyBooleanType) || !rightType.equals(dummyBooleanType)){
        		TypeCheckError("Type mismatch at OR binary expression: "+expr.getNamePos()+" need 2 boolean expressions");
        		ret = dummyUnsupportedType;
        	} else {
        		ret = leftType;
        	}
        	break;
        case OP_EQ:
        	if(!leftType.equals(rightType)){
        		TypeCheckError("Type mismatch at EQ binary expression: "+expr.getNamePos());
        		ret = dummyUnsupportedType;
        	} else {
        		ret = dummyBooleanType;
        	}
        	break;
        case OP_NEQ:
        	if(!leftType.equals(rightType)){
        		TypeCheckError("Type mismatch at NEQ binary expression: "+expr.getNamePos());
        		ret = dummyUnsupportedType;
        	} else {
        		ret = dummyBooleanType;
        	}
        	break;
        case OP_GT:
        	if(!leftType.equals(dummyIntType) || !rightType.equals(dummyIntType)){
        		TypeCheckError("Type mismatch at GT binary expression: "+expr.getNamePos());
        		ret = dummyUnsupportedType;
        	} else {
        		ret = dummyBooleanType;
        	}
        	break;
        case OP_GTE:
        	if(!leftType.equals(dummyIntType) || !rightType.equals(dummyIntType)){
        		TypeCheckError("Type mismatch at GTE binary expression: "+expr.getNamePos());
        		ret = dummyUnsupportedType;
        	} else {
        		ret = dummyBooleanType;
        	}
        	break;
        case OP_LT:
        	if(!leftType.equals(dummyIntType) || !rightType.equals(dummyIntType)){
        		System.out.println("THEN");
        		TypeCheckError("Type mismatch at LT binary expression: "+expr.getNamePos());
        		ret = dummyUnsupportedType;
        	} else {
        		System.out.println("ELSE");
        		ret = dummyBooleanType;
        	}
        	break;
        case OP_LTE:
        	if(!leftType.equals(dummyIntType) || !rightType.equals(dummyIntType)){
        		TypeCheckError("Type mismatch at LTE binary expression: "+expr.getNamePos());
        		ret = dummyUnsupportedType;
        	} else {
        		ret = dummyBooleanType;
        	}
        	break;
		default:
			TypeCheckError("unsupported binary expression opertor at: "+expr.getNamePos());
    		ret = dummyUnsupportedType;
			break;
        }
    	return ret;
    }
    
    public Type visitRefExpr(RefExpr expr, String arg){
        
        Type ret = null;
        Type innerType = expr.ref.visit(this, "");
   	 
        if(innerType != null && (innerType.equals(dummyUnsupportedType) || innerType.equals(dummyErrorType))){
        	ret = dummyErrorType; 
        } else {
        	ret = innerType;
        }
        expr.astType = ret;
        return ret;
    }
    
    public Type visitCallExpr(CallExpr expr, String arg){
    	Type ret = null;
        Type funcType = expr.functionRef.visit(this, "");
        if(funcType.equals(dummyUnsupportedType) || funcType.equals(dummyErrorType)){
        	ret = dummyErrorType; 
        } else {
        	ret = funcType;
        	ExprList al = expr.argList;
        	MethodDecl oriDecl = (MethodDecl)expr.functionRef.decl;
        	if(oriDecl.parameterDeclList.size() != al.size()){
        		ret = dummyUnsupportedType;
        		TypeCheckError("function call: "+expr.getNamePos()+"does not have the right number of argument");
        	}
        	Iterator<Expression> alIter = al.iterator();
        	Iterator<ParameterDecl> plIter = oriDecl.parameterDeclList.iterator();
            while (plIter.hasNext()) {
            	Expression a = alIter.next();
            	ParameterDecl p= plIter.next();
                Type argType = a.visit(this, "");
                if(!argType.equals(p.type)){
                	ret = dummyErrorType;
            		TypeCheckError("argument: "+a.getNamePos()+" of function call: "+expr.getNamePos()+" does not match the type in the declaration");            		
                }
            }
        }
        expr.astType = ret;
        return ret;
    }
    
    public Type visitLiteralExpr(LiteralExpr expr, String arg){
    	Type ret = null;
        Type litType = expr.lit.visit(this, "");
        if(litType.equals(dummyUnsupportedType) || litType.equals(dummyErrorType)){
        	ret = dummyErrorType; 
        } else {
        	ret = litType;
        }
        expr.astType = ret;
        return ret;
    }
 
    public Type visitNewArrayExpr(NewArrayExpr expr, String arg){
    	Type ret = null;
    	Type sizeType = expr.sizeExpr.visit(this, "");
    	if(sizeType.equals(dummyUnsupportedType) || sizeType.equals(dummyErrorType)){
        	ret = dummyErrorType; 
        } else {
        	Type arrayType = expr.eltType;
        	if(!sizeType.equals(dummyIntType)){
        		ret = dummyErrorType;
        		TypeCheckError("Array: "+expr.getNamePos()+"needs int size expression");            		
        	} else {
        		ret = dummyArrayIntType;
        	}
        }
    	expr.astType = ret;
        return ret;
    }
    
    public Type visitNewObjectExpr(NewObjectExpr expr, String arg){
    	Type ret = null;
    	Type classType = expr.classtype;
    	ret = classType;
        expr.astType = ret;
        return ret;
    }
    

	///////////////////////////////////////////////////////////////////////////////
	//
	// REFERENCES
	//
	///////////////////////////////////////////////////////////////////////////////
	
    //ALL REFERENCE VISITOR RETURN ITS OWN CONTEXT: StaticClass, InstanceClass, ThisClass
    public Type visitQualifiedRef(QualifiedRef qr, String arg) {
    	Type ret = null;
    	Type innerType = qr.id.visit(this, "");
        if(innerType.equals(dummyUnsupportedType) || innerType.equals(dummyErrorType)){
        	ret = dummyErrorType; 
        } else {
        	ret = innerType;
        }
        qr.astType = ret;
        return ret;
    }
    
    public Type visitIndexedRef(IndexedRef ir, String arg) {
    	Type ret = null;
    	Type exprType = ir.indexExpr.visit(this, "");
    	Type refType = ir.ref.visit(this, "");
    	if(exprType.equals(dummyUnsupportedType) || exprType.equals(dummyErrorType) || refType.equals(dummyUnsupportedType) || refType.equals(dummyErrorType)){
        	ret = dummyErrorType; 
        } else if(!exprType.equals(dummyIntType)){
        	ret = dummyErrorType;
    		TypeCheckError("Expression within index reference: "+ir.getNamePos()+" does not derive INT");
        } else {
        	ret = ((ArrayType)refType).eltType;
        }
    	ir.astType = ret;
        return ret;
    }
    
    public Type visitIdRef(IdRef ref, String arg) {
    	Type ret = null;
    	Type idType = ref.id.visit(this, "");
    	if(idType.equals(dummyUnsupportedType) || idType.equals(dummyErrorType)){
        	ret = dummyErrorType; 
        } else {
        	ret = idType;
        }
    	ref.astType = ret;
        return ret;
    }
   
    public Type visitThisRef(ThisRef ref, String arg) {
    	return curClass;
    }
    
    
	///////////////////////////////////////////////////////////////////////////////
	//
	// TERMINALS
	//
	///////////////////////////////////////////////////////////////////////////////
    
    @Override
	public Type visitNullLiteral(NullLiteral nl, String arg) {
		// TODO Auto-generated method stub
		return dummyNullType;
	}
    
    //arg is the name of the class if the identifier is a class member
    public Type visitIdentifier(Identifier id, String arg){
    	return id.decl.type;
    }
    
    public Type visitOperator(Operator op, String arg){
        return null;
    }
    
    public Type visitIntLiteral(IntLiteral num, String arg){
        return dummyIntType;
    }
    
    public Type visitBooleanLiteral(BooleanLiteral bool, String arg){
        return dummyBooleanType;
    }
    
	///////////////////////////////////////////////////////////////////////////////
	//
	// DISPLAY ERROR
	//
	///////////////////////////////////////////////////////////////////////////////

    private void TypeCheckError(String e) {
		reporter.reportError("***Type Check error: " + e);
	}
}
