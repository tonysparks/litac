/*
 * see license.txt
 */
package litac.ast;

import java.util.List;

import litac.ast.TypeInfo.EnumFieldInfo;
import litac.ast.TypeInfo.ParameterInfo;

/**
 * @author Tony
 *
 */
public abstract class Decl extends Stmt {

    public static enum DeclKind {
        VAR,
        CONST,
        FUNC,
        STRUCT,
        ENUM,
        UNION,
        TYPEDEF,
    }

    public TypeInfo type;
    public DeclKind kind;
    public String name;
    
    public Decl(DeclKind kind, TypeInfo type, String name) {
        this.kind = kind;
        this.type = type;
        this.name = name;
    }
    
    public static class VarDecl extends Decl {
        
        public Expr expr;
        
        public VarDecl(String name, TypeInfo type, Expr expr) {
            super(DeclKind.VAR, type, name);            
            this.expr = becomeParentOf(expr);
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
    }
    
    public static class ConstDecl extends Decl {
        public Expr expr;
        
        public ConstDecl(String name, TypeInfo type, Expr expr) {
            super(DeclKind.CONST, type, name);
            this.expr = becomeParentOf(expr);
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
    }
    
    public static class FuncDecl extends Decl {
        public List<ParameterInfo> parameterInfos;
        public Stmt bodyStmt;
        public TypeInfo returnType;
        
        /**
         * @param name
         * @param type
         */
        public FuncDecl(String name, TypeInfo type, List<ParameterInfo> parameterInfos, Stmt body, TypeInfo returnType) {
            super(DeclKind.FUNC, type, name);
            this.parameterInfos = parameterInfos;
            this.bodyStmt = becomeParentOf(body);
            this.returnType = returnType;
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);  
        }
    }
    
    
    
    public static class StructDecl extends Decl {
        public List<FieldStmt> fields;
        
        public StructDecl(String name, TypeInfo type, List<FieldStmt> fields) {
            super(DeclKind.STRUCT, type, name);
            this.fields = becomeParentOf(fields);
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
    }       
    
    public static class UnionDecl extends Decl {
        public List<FieldStmt> fields;
        
        public UnionDecl(String name, TypeInfo type, List<FieldStmt> fields) {
            super(DeclKind.UNION, type, name);
            this.fields = becomeParentOf(fields);
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
    }
    
    public static class EnumDecl extends Decl {
        public List<EnumFieldInfo> fields;
        
        public EnumDecl(String name, TypeInfo type, List<EnumFieldInfo> fields) {
            super(DeclKind.ENUM, type, name);            
            this.fields = fields;
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
    }
    
    public static class TypedefDecl extends Decl {
        public String typeToAlias;
        public String alias;
        
        public TypedefDecl(String name, TypeInfo type, String typeToAlias, String alias) {
            super(DeclKind.TYPEDEF, type, name);
            this.typeToAlias = typeToAlias;
            this.alias = alias;
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
    }

}
