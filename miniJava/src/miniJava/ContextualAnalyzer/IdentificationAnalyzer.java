/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.ContextualAnalyzer;

import miniJava.ErrorReporter;
import miniJava.ContextualAnalyzer.IdentificationTable;
import miniJava.SyntacticAnalyzer.SourcePosition;
import miniJava.AbstractSyntaxTrees.*;
import miniJava.AbstractSyntaxTrees.Package;
/*
 * 
 */
public class IdentificationAnalyzer implements Visitor<String,Object> {
	
	private IdentificationTable idTable;
	private ErrorReporter reporter;
	private Declaration curClass;
	private AST astII; //implicit import ASK root
	
	private boolean inStaticMethod;

	private static SourcePosition dummyPos = new SourcePosition();
	private static Type dummyVoidType = new BaseType(TypeKind.VOID,dummyPos);
	private static Type dummyIntType = new BaseType(TypeKind.INT,dummyPos);
	
	public IdentificationAnalyzer (ErrorReporter reporter) {
		this.reporter = reporter;
		this.idTable = new IdentificationTable (reporter);
	}
	
	// Reports that the identifier or operator used at a leaf of the AST
	// has not been declared.

	public static boolean showPosition = false;
    
    /**
     * print text representation of AST to stdout
     * @param ast root node of AST 
     */
    public void check(AST ast, AST astii){
    	astII = astii;
        ast.visit(this, "");
    }   
    
    private void mergeImplicitImport(Package prog){
    	for(ClassDecl cd: ((Package)astII).classDeclList){
    		prog.classDeclList.add(cd);
    	}
    }
   
	///////////////////////////////////////////////////////////////////////////////
	//
	// PACKAGE
	//
	/////////////////////////////////////////////////////////////////////////////// 

    public Object visitPackage(Package prog, String arg){
    	//load implicit imports
    	mergeImplicitImport(prog);
    	
    	
    	//ID Table populate level 1 decl
        for (ClassDecl c: prog.classDeclList){
        	idTable.enter(c.name, c); //level 1 class name
        }
        
        //populate classmember
        for (ClassDecl c: prog.classDeclList){
        	for (FieldDecl f: c.fieldDeclList){
        		idTable.enterClass(c.name, f.name, f);
        	}
        	for (MethodDecl m: c.methodDeclList){
        		idTable.enterClass(c.name, m.name, m);
        	}
        }
        
        //check for suitable mian class
        Boolean mainClassFound = false;
        for (ClassDecl c: prog.classDeclList){
        	Declaration potentialMain = idTable.retrieveMember(c.name, "main");
        	if(potentialMain != null){
        		if(potentialMain.checkStatic()==true && potentialMain.checkPrivate()==false&&potentialMain.type.equals(dummyVoidType)){
        			if(potentialMain instanceof MethodDecl){
        				ParameterDeclList pl = ((MethodDecl)potentialMain).parameterDeclList;
        				if(pl.size()==1 && pl.get(0).type.typeKind == TypeKind.ARRAY){
        					ArrayType at = (ArrayType)pl.get(0).type;
        					if(at.eltType.typeKind == TypeKind.CLASS && ((ClassType)at.eltType).className.spelling.equals("String")){
        						if(mainClassFound){
        							IdentificationError("Deplicate main class found at "+c.posn.toString());
        						}
        						mainClassFound = true;
        						((MethodDecl)potentialMain).isMain = true;
        					}
        				}
        			}
        		}
        	}
        }
        
        if(!mainClassFound){
        	IdentificationError("Suitable main class not found");
        }
        
        for (ClassDecl c: prog.classDeclList){
        	//open level 2 class member
        	idTable.openScope();
        	c.visit(this, "");
        	idTable.closeScope();
        }
        return null;
    }
    
    
	///////////////////////////////////////////////////////////////////////////////
	//
	// DECLARATIONS
	//
	///////////////////////////////////////////////////////////////////////////////
    
    public Object visitClassDecl(ClassDecl clas, String arg){
    	curClass = idTable.retrieveClass(clas.name);
    	//populate current class: class member
    	for (FieldDecl f: clas.fieldDeclList){
    		f.visit(this, "");
    		idTable.enter(f.name, f); //level 2 class member
    	}
    	for (MethodDecl m: clas.methodDeclList){
    		idTable.enter(m.name, m); //level 2 class member
    	}
        
        //open level 3 method parameters
        for (MethodDecl m: clas.methodDeclList){
        	idTable.openScope();
        	m.visit(this, "");
        	idTable.closeScope();
        }
        if(clas.name.equals("_PrintStream")){
        	clas.methodDeclList.get(0).isPrintln = true;
        }
        return null;
    }
    
    public Object visitFieldDecl(FieldDecl f, String arg){
    	f.type.visit(this, "");
        return null;
    }
    
    public Object visitMethodDecl(MethodDecl m, String arg){
    	inStaticMethod = m.checkStatic();
    	
    	m.type.visit(this, "");
    	
        ParameterDeclList pdl = m.parameterDeclList;
        for (ParameterDecl pd: pdl) {
        	if(m.isStatic){
        		pd.visit(this, "isStatic");
        	} else {
        		pd.visit(this, "");
        	}
        }
        StatementList sl = m.statementList;
        
        //open level 4 local variable
        idTable.openScope();
        for (Statement s: sl) {
            s.visit(this, "");
        }
        if (m.returnExp != null) {
            m.returnExp.visit(this, "");
        }
        idTable.closeScope();
        
        return null;
    }
    
    public Object visitParameterDecl(ParameterDecl pd, String arg){
    	//populate level 3 method parameter
    	if(arg.equals("isStatic")){
    		pd.isStatic = true;
    	}
    	idTable.enter(pd.name, pd);
        pd.type.visit(this, "");
        return null;
    } 
    
    public Object visitVarDecl(VarDecl vd, String arg){ //local variable decl
    	//populate level 4+ decl
    	if(inStaticMethod){
    		vd.isStatic=true;
    	}
    	idTable.enter(vd.name, vd);
        vd.type.visit(this, "");
        return null;
    }
 
	
	///////////////////////////////////////////////////////////////////////////////
	//
	// TYPES
	//
	///////////////////////////////////////////////////////////////////////////////
    public Object visitNullType(NullType type, String arg) {
		// TODO Auto-generated method stub
		return null;
	}
    
    public Object visitBaseType(BaseType type, String arg){
        return null;
    }
    
    public Object visitClassType(ClassType type, String arg){
        //check if class id declared
    	Declaration decl =  idTable.retrieveClass(type.className.spelling);
        if(decl==null){
        	IdentificationError("Reference to class type: " + type.className.spelling + " is not declared in "+type.posn.toString());
        } else {
        	type.className.decl = decl;
        }
        return null;
    }
    
    public Object visitArrayType(ArrayType type, String arg){
    	//check if class id declared
    	if(type.eltType.typeKind == TypeKind.CLASS){
    		Declaration decl =  idTable.retrieveClass(((ClassType)type.eltType).className.spelling);
    		if(decl==null){
            	IdentificationError("Reference to class type " + ((ClassType)type.eltType).className.spelling + " is not declared in array at"+type.posn.toString());
            } else {
            	((ClassType)type.eltType).className.decl = decl;
            }
    	}
        return null;
    }
    
	
	///////////////////////////////////////////////////////////////////////////////
	//
	// STATEMENTS
	//
	///////////////////////////////////////////////////////////////////////////////

    public Object visitBlockStmt(BlockStmt stmt, String arg){
        StatementList sl = stmt.sl;
        //open level 4+ local nested scope
        idTable.openScope();
        for (Statement s: sl) {
        	s.visit(this, "");
        }
        idTable.closeScope();
        return null;
    }
    
    public Object visitVardeclStmt(VarDeclStmt stmt, String arg){
        stmt.varDecl.visit(this, "");
        //flag the variable being declared to prevent access while initializing the variable
        idTable.setBeingDeclared(stmt.varDecl.name);
        stmt.initExp.visit(this, "checkNotStaticClassName");
        idTable.removeBeingDeclared(stmt.varDecl.name);
        return null;
    }
    
    public Object visitAssignStmt(AssignStmt stmt, String arg){
        stmt.ref.visit(this, "");
        if(stmt.ref.decl instanceof MethodDecl){
        	IdentificationError("Assign to method declaration at" + stmt.getPos());
        }
        stmt.val.visit(this, "");
        return null;
    }
    
    public Object visitCallStmt(CallStmt stmt, String arg){
        stmt.methodRef.visit(this, "checkFunction");
        ExprList al = stmt.argList;
        for (Expression e: al) {
            e.visit(this, "");
        }
        return null;
    }
    
    public Object visitIfStmt(IfStmt stmt, String arg){
        if(stmt.thenStmt.isVarDeclStmt()){
        	IdentificationError("Cannot have a solidary varDecl Stmt within if statment branches at " + stmt.thenStmt.getPos());
        } else if(stmt.elseStmt != null && stmt.elseStmt.isVarDeclStmt()){
        	IdentificationError("Cannot have a solidary varDecl Stmt within if statment branches at " + stmt.elseStmt.getPos());
        } else {
	    	stmt.cond.visit(this, "");
	        stmt.thenStmt.visit(this, "");
	        if (stmt.elseStmt != null)
	            stmt.elseStmt.visit(this, "");
        }
        return null;
    }
    
    public Object visitWhileStmt(WhileStmt stmt, String arg){
    	if(stmt.body.isVarDeclStmt()){
        	IdentificationError("Cannot have a solidary varDecl Stmt within while statment branches at " + stmt.body.getPos());
        } else {
	        stmt.cond.visit(this, "");
	        stmt.body.visit(this, "");
        }
        return null;
    }
    

	///////////////////////////////////////////////////////////////////////////////
	//
	// EXPRESSIONS
	//
	///////////////////////////////////////////////////////////////////////////////

    public Object visitUnaryExpr(UnaryExpr expr, String arg){
        expr.operator.visit(this, "");
        expr.expr.visit(this, "");
        return null;
    }
    
    public Object visitBinaryExpr(BinaryExpr expr, String arg){
        expr.operator.visit(this, "");
        expr.left.visit(this, "");
        expr.right.visit(this, "");
        return null;
    }
    
    public Object visitRefExpr(RefExpr expr, String arg){
        expr.ref.visit(this, arg);
        return null;
    }
    
    public Object visitCallExpr(CallExpr expr, String arg){
        expr.functionRef.visit(this, "checkFunction");
        ExprList al = expr.argList;
        for (Expression e: al) {
            e.visit(this, "");
        }
        return null;
    }
    
    public Object visitLiteralExpr(LiteralExpr expr, String arg){
        expr.lit.visit(this, "");
        return null;
    }
 
    public Object visitNewArrayExpr(NewArrayExpr expr, String arg){
        expr.eltType.visit(this, "");
        expr.sizeExpr.visit(this, "");
        return null;
    }
    
    public Object visitNewObjectExpr(NewObjectExpr expr, String arg){
        expr.classtype.visit(this, "");
        return null;
    }
    

	///////////////////////////////////////////////////////////////////////////////
	//
	// REFERENCES
	//
	///////////////////////////////////////////////////////////////////////////////
	
    //ALL REFERENCE VISITOR RETURN ITS OWN CONTEXT: StaticClass, InstanceClass, ThisClass
    public Object visitQualifiedRef(QualifiedRef qr, String arg) {
    	RefContext surroundingContext;
    	
    	//visit reference to obtain context
    	surroundingContext = (RefContext)qr.ref.visit(this, "");
    	if(qr.ref.decl != null){
    		if(qr.ref.decl.type instanceof BaseType){
    			IdentificationError("qualification of reference " + qr.id.spelling + " at "+qr.id.posn.toString() + " is not referenced from a class instance");
    		} else if(qr.ref.decl.type instanceof ArrayType){
    			if(!qr.id.spelling.equals("length")){
        			IdentificationError("Unable to access array member " + qr.id.spelling + " at "+qr.id.posn.toString() + ", only length is accessible");
    			}
    			FieldDecl tempfd = new FieldDecl(false, false, dummyIntType, "length", dummyPos);
    			tempfd.isArrayLength = true;
    			qr.id.decl=tempfd;
    			return RefContext.ArrayLength;
    		}else{
		    	switch(surroundingContext){
		    		case StaticClass:
		    			qr.id.visit(this, qr.ref.decl.name);
		    			if(qr.id.decl != null){
		    				if(!qr.id.decl.checkStatic()){
		        				IdentificationError("Unable to access class member " + qr.id.spelling + " at "+qr.id.posn.toString() + " from class "+ qr.ref.decl.name +" because it is not static");
		        			}
		        			if(qr.id.decl.checkPrivate()){
		        				IdentificationError("Unable to access class member " + qr.id.spelling + " at "+qr.id.posn.toString() + " from class "+ qr.ref.decl.name +" because it is private");
		        			}
		    			}
		    			break;
		    		case InstanceClass:
		    			qr.id.visit(this, ((ClassType)qr.ref.decl.type).className.spelling);
		    			if(qr.id.decl != null){
		    				ClassDecl cd = (ClassDecl)idTable.retrieveClass(qr.ref.decl.name);
			    			if(qr.id.decl.checkPrivate() && !curClass.name.equals(((ClassType)qr.ref.decl.type).className.spelling)){
			    				IdentificationError("Unable to access class member " + qr.id.spelling + " at "+qr.id.posn.toString() + " from class "+ qr.ref.decl.name +" because it is private");
			    			}
		    			}
		    			break;
		    		case ThisClass:
		    			qr.id.visit(this, ((ClassDecl)curClass).name);
		    			break;
		    		case ArrayLength:
		        		IdentificationError(qr.id.spelling + " at "+qr.id.posn.toString() + " cannot be accessed or derived from array.length field");
		    			return RefContext.ArrayLength;
		    		default: //this class
		    			IdentificationError("Unknown case reached while analyzing qualified reference " + qr.id.spelling + " at "+qr.id.posn.toString() + " from class "+ qr.ref.decl.name);
		    			break;
		    	}
		    	if(arg.equals("checkFunction")){
		    		if(!(qr.id.decl instanceof MethodDecl)){
		    			IdentificationError("reference " + qr.id.spelling + " at "+qr.id.posn.toString() + " is not a method");
		    		}
		    	} else {
		    		if((qr.id.decl instanceof MethodDecl)){
		    			IdentificationError("method reference " + qr.id.spelling + " at "+qr.id.posn.toString() + " without an invocation");
		    		}
		    	}
    		}
    	}
    	qr.decl = qr.id.decl;
	    return RefContext.InstanceClass;
    }
    
    public Object visitIndexedRef(IndexedRef ir, String arg) {
    	ir.indexExpr.visit(this, "");
    	ir.ref.visit(this, "");
    	ir.decl = ir.ref.decl;
    	return RefContext.InstanceClass;
    }
    
    public Object visitIdRef(IdRef ref, String arg) {
    	ref.id.visit(this, "");
    	ref.decl = ref.id.decl;
//    	if(inStaticMethod && !ref.id.decl.checkStatic()){
//    		IdentificationError("cannot reference non-static symbol "+ ref.id.spelling +" in static context at" + ref.id.posn.toString());
//		}
    	Declaration tempClass = idTable.retrieveClass(ref.id.spelling);
    	if(tempClass!= null && tempClass==ref.id.decl){
    		if(arg.equals("checkNotStaticClassName")){
    			IdentificationError("Incomplete reference to "+ ref.id.spelling +" in variable declaration at" + ref.id.posn.toString());
    		}
    		return RefContext.StaticClass;
    	} else{
    		if(inStaticMethod && !ref.id.decl.checkStatic()){
        		IdentificationError("cannot reference non-static symbol "+ ref.id.spelling +" in static context at" + ref.id.posn.toString());
    		}
    		return RefContext.InstanceClass;
    	}
    }
   
    public Object visitThisRef(ThisRef ref, String arg) {
    	ref.decl = curClass;
    	return RefContext.ThisClass;
    }
    
    
	///////////////////////////////////////////////////////////////////////////////
	//
	// TERMINALS
	//
	///////////////////////////////////////////////////////////////////////////////
    
	public Object visitNullLiteral(NullLiteral nl, String arg) {
		// TODO Auto-generated method stub
		return null;
	}
    
    //arg is the name of the class if the identifier is a class member
    public Object visitIdentifier(Identifier id, String arg){
    	if(arg!=null&&arg.length()>0){ //specified class name
    		Declaration decl =  idTable.retrieveMember(arg, id.spelling);
	        if(decl==null){
	        	IdentificationError("Reference to class member: " + id.spelling + " in "+id.posn.toString()+" is not declared in class "+arg);
	        } else {
	        	id.decl = decl;
	        }
	        return null;
    	} else {
	    	Declaration decl =  idTable.retrieve(id.spelling);
	        if(decl==null){
	        	IdentificationError("Reference to id: " + id.spelling + " is not declared in "+id.posn.toString());
	        } else {
	        	id.decl = decl;
	        }
	        return null;
    	}
    }
    
    public Object visitOperator(Operator op, String arg){
        return null;
    }
    
    public Object visitIntLiteral(IntLiteral num, String arg){
        return null;
    }
    
    public Object visitBooleanLiteral(BooleanLiteral bool, String arg){
        return null;
    }
    
	///////////////////////////////////////////////////////////////////////////////
	//
	// DISPLAY ERROR
	//
	///////////////////////////////////////////////////////////////////////////////

    private void IdentificationError(String e) {
		reporter.reportError("***Idenficiation error: " + e);
	}
}
