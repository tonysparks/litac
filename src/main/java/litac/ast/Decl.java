/*
 * see license.txt
 */
package litac.ast;

import java.util.List;

import litac.checker.Attributes;
import litac.checker.TypeInfo;
import litac.checker.TypeInfo.EnumFieldInfo;

/**
 * @author Tony
 *
 */
public abstract class Decl extends Stmt {

    public static enum DeclKind {
        VAR,
        PARAM,
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
    public Attributes attributes;
    
    public Decl(DeclKind kind, TypeInfo type, String name) {
        this.kind = kind;
        this.type = type;
        this.name = name;
        this.attributes = new Attributes();
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
        
        @Override
        protected Node doCopy() {            
            return new VarDecl(this.name, this.type.copy(), copy(this.expr));
        }
    }
    
    public static class ParameterDecl extends Decl {
        
        /**
         * @param type
         * @param name
         */
        public ParameterDecl(TypeInfo type, String name) {
            super(DeclKind.PARAM, type, name);
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        public String toString() {    
            return this.name + ": " + this.type;
        }
        
        @Override
        protected Node doCopy() {            
            return new ParameterDecl(this.type.copy(), this.name);
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
        
        @Override
        protected Node doCopy() {            
            return new ConstDecl(this.name, this.type.copy(), this.expr.copy());
        }
    }
    
    public static class FuncDecl extends Decl {
        public ParametersStmt params;
        public Stmt bodyStmt;
        public TypeInfo returnType;
                
        /**
         * @param name
         * @param type
         */
        public FuncDecl(String name, TypeInfo type, ParametersStmt params, Stmt body, TypeInfo returnType) {
            super(DeclKind.FUNC, type, name);
            this.params = becomeParentOf(params);
            this.bodyStmt = becomeParentOf(body);
            this.returnType = returnType;
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);  
        }
        
        @Override
        protected Node doCopy() {            
            return new FuncDecl(this.name, this.type.copy(), copy(params), copy(this.bodyStmt), this.returnType.copy());
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
        
        public void updateName(String name) {
            this.name = name;
        }
        
        @Override
        protected Node doCopy() {            
            return new StructDecl(this.name, this.type.copy(), copy(this.fields));
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
        
        @Override
        protected Node doCopy() {            
            return new UnionDecl(this.name, this.type.copy(), copy(this.fields));
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
        
        @Override
        protected Node doCopy() {            
            return new EnumDecl(this.name, this.type.copy(), this.fields);
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
        
        @Override
        protected Node doCopy() {            
            return new TypedefDecl(this.name, this.type.copy(), this.typeToAlias, this.alias);
        }
    }
}
