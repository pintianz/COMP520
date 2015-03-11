/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.ContextualAnalyzer;

import miniJava.ErrorReporter;
import miniJava.ContextualAnalyzer.IdentificationTable;
import miniJava.AbstractSyntaxTrees.*;
import miniJava.AbstractSyntaxTrees.Package;
/*
 * 
 */
public class IdentificationAnalyzer implements Visitor<String,Object> {
	
	private IdentificationTable idTable;
	private ErrorReporter reporter;
	private Declaration curClass;
	
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
    public void check(AST ast){
        ast.visit(this, "");
    }   
    
    
    
	///////////////////////////////////////////////////////////////////////////////
	//
	// PACKAGE
	//
	/////////////////////////////////////////////////////////////////////////////// 

    public Object visitPackage(Package prog, String arg){
        for (ClassDecl c: prog.classDeclList){
        	idTable.enter(c.name, c); //level 1 class name
        }
        
        //open level 2 class member
        idTable.openScope();
        for (ClassDecl c: prog.classDeclList){
        	c.visit(this, "");
        }
        idTable.closeScope();
        return null;
    }
    
    
	///////////////////////////////////////////////////////////////////////////////
	//
	// DECLARATIONS
	//
	///////////////////////////////////////////////////////////////////////////////
    
    public Object visitClassDecl(ClassDecl clas, String arg){
    	curClass = idTable.retrieveClass(clas.name);
    	
    	for (FieldDecl f: clas.fieldDeclList){
    		idTable.enter(f.name, f); //level 2 class member
    	}
        for (MethodDecl m: clas.methodDeclList){
        	idTable.enter(m.name, m); //level 2 class member
        }
        
        for (FieldDecl f: clas.fieldDeclList){
        	f.visit(this, "");
        }
        
        //open level 3 method parameters
        idTable.openScope();
        for (MethodDecl m: clas.methodDeclList){
        	m.visit(this, "");
        }
        idTable.closeScope();
        return null;
    }
    
    public Object visitFieldDecl(FieldDecl f, String arg){
    	f.type.visit(this, "");
        return null;
    }
    
    public Object visitMethodDecl(MethodDecl m, String arg){
    	m.type.visit(this, "");
    	
        ParameterDeclList pdl = m.parameterDeclList;
        for (ParameterDecl pd: pdl) {
        	pd.visit(this, "");
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
    	idTable.enter(pd.name, pd); //level 3 method parameter
        pd.type.visit(this, "");
        return null;
    } 
    
    public Object visitVarDecl(VarDecl vd, String arg){ //local variable decl
    	idTable.enter(vd.name, vd);
        vd.type.visit(this, "");
        return null;
    }
 
	
	///////////////////////////////////////////////////////////////////////////////
	//
	// TYPES
	//
	///////////////////////////////////////////////////////////////////////////////
    
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
        stmt.initExp.visit(this, "");
        return null;
    }
    
    public Object visitAssignStmt(AssignStmt stmt, String arg){
        stmt.ref.visit(this, "");
        stmt.val.visit(this, "");
        return null;
    }
    
    public Object visitCallStmt(CallStmt stmt, String arg){
        stmt.methodRef.visit(this, "");
        ExprList al = stmt.argList;
        for (Expression e: al) {
            e.visit(this, "");
        }
        return null;
    }
    
    public Object visitIfStmt(IfStmt stmt, String arg){
        stmt.cond.visit(this, "");
        stmt.thenStmt.visit(this, "");
        if (stmt.elseStmt != null)
            stmt.elseStmt.visit(this, "");
        return null;
    }
    
    public Object visitWhileStmt(WhileStmt stmt, String arg){
        stmt.cond.visit(this, "");
        stmt.body.visit(this, "");
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
        expr.ref.visit(this, "");
        return null;
    }
    
    public Object visitCallExpr(CallExpr expr, String arg){
        expr.functionRef.visit(this, "");
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
	
    public Object visitQualifiedRef(QualifiedRef qr, String arg) {
    	qr.id.visit(this, "");
    	qr.decl = qr.id.decl;
    	qr.ref.visit(this, "");
	    return null;
    }
    
    public Object visitIndexedRef(IndexedRef ir, String arg) {
    	ir.indexExpr.visit(this, "");
    	ir.ref.visit(this, "");
    	ir.decl = ir.ref.decl;
    	return null;
    }
    
    public Object visitIdRef(IdRef ref, String arg) {
    	ref.id.visit(this, "");
    	ref.decl = ref.id.decl;
    	return null;
    }
   
    public Object visitThisRef(ThisRef ref, String arg) {
    	ref.decl = curClass;
    	return null;
    }
    
    
	///////////////////////////////////////////////////////////////////////////////
	//
	// TERMINALS
	//
	///////////////////////////////////////////////////////////////////////////////
    
    public Object visitIdentifier(Identifier id, String arg){
    	switch(arg){
    		case("qualifiedRef"):
    	}
    	Declaration decl =  idTable.retrieve(id.spelling);
        if(decl==null){
        	IdentificationError("Reference to id: " + id.spelling + " is not declared in "+id.posn.toString());
        } else {
        	id.decl = decl;
        }
        return null;
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
		reporter.reportError("Idenficiation error: " + e);
	}
}
