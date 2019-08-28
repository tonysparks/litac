/*
 * see license.txt
 */
package litac.ast;

import java.util.List;

import litac.checker.TypeInfo.EnumFieldInfo;
import litac.compiler.Symbol;
import litac.generics.GenericParam;

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
    
    public Symbol sym;
    public DeclKind kind;
    public String name;
    public Attributes attributes;
    
    public Decl(DeclKind kind, String name) {
        this.kind = kind;        
        this.name = name;
        this.attributes = new Attributes();
    }
    
    public static class VarDecl extends Decl {
        public TypeSpec type;
        public Expr expr;
        
        public VarDecl(String name, TypeSpec type, Expr expr, int modifiers) {
            super(DeclKind.VAR, name);
            this.type = type;
            this.expr = becomeParentOf(expr);
            this.attributes.modifiers = modifiers;
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new VarDecl(this.name, TypeSpec.copy(this.type), copy(this.expr), this.attributes.modifiers);
        }
    }
    
    public static class ParameterDecl extends Decl {
        public TypeSpec type;        
        public Expr defaultValue;

        public ParameterDecl(TypeSpec type, String name, Expr defaultValue, int modifiers) {
            super(DeclKind.PARAM, name);
            this.type = type;
            this.defaultValue = becomeParentOf(defaultValue);
            this.attributes.modifiers = modifiers;
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
            return new ParameterDecl(TypeSpec.copy(this.type), this.name, copy(this.defaultValue), this.attributes.modifiers);
        }
    }
        
    public static class ConstDecl extends Decl {
        public TypeSpec type;
        public Expr expr;
        
        public ConstDecl(String name, TypeSpec type, Expr expr, int modifiers) {
            super(DeclKind.CONST, name);
            this.type = type;
            this.expr = becomeParentOf(expr);
            this.attributes.modifiers = modifiers;
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new ConstDecl(this.name, TypeSpec.copy(this.type), copy(this.expr), this.attributes.modifiers);
        }
    }
    
    public static class FuncDecl extends Decl {
        public ParametersStmt params;
        public Stmt bodyStmt;
        public TypeSpec returnType;
        public List<GenericParam> genericParams;
        public int flags;        
        
        public FuncDecl(String name, ParametersStmt params, Stmt body, TypeSpec returnType, List<GenericParam> genericParams, int flags) {
            super(DeclKind.FUNC, name);
            this.params = becomeParentOf(params);
            this.bodyStmt = becomeParentOf(body);
            this.returnType = returnType;
            this.genericParams = genericParams;
            this.flags = flags;
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);  
        }
        
        @Override
        protected Node doCopy() {            
            return new FuncDecl(this.name, 
                                copy(params), 
                                copy(this.bodyStmt), 
                                TypeSpec.copy(this.returnType), 
                                genericParams, 
                                flags);
        }
    }
    
    
    
    public static class StructDecl extends Decl {
        public List<FieldStmt> fields;
        public List<GenericParam> genericParams;
        public int flags;
        
        public StructDecl(String name, List<FieldStmt> fields, List<GenericParam> genericParams, int flags) {
            super(DeclKind.STRUCT, name);
            this.fields = becomeParentOf(fields);
            this.genericParams = genericParams;
            this.flags = flags;
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
            return new StructDecl(this.name, copy(this.fields), this.genericParams, this.flags);
        }
    }       
    
    public static class UnionDecl extends Decl {
        public List<FieldStmt> fields;
        public List<GenericParam> genericParams;
        public int flags;
        
        public UnionDecl(String name, List<FieldStmt> fields, List<GenericParam> genericParams, int flags) {
            super(DeclKind.UNION, name);
            this.fields = becomeParentOf(fields);
            this.genericParams = genericParams;
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new UnionDecl(this.name, copy(this.fields), this.genericParams, this.flags);
        }
    }
    
    public static class EnumDecl extends Decl {
        public List<EnumFieldInfo> fields;
        
        public EnumDecl(String name, List<EnumFieldInfo> fields) {
            super(DeclKind.ENUM, name);            
            this.fields = fields;
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new EnumDecl(this.name, this.fields);
        }
    }
    
    public static class TypedefDecl extends Decl {
        public TypeSpec type;
        public String alias;
        
        public TypedefDecl(String name, TypeSpec type, String alias) {
            super(DeclKind.TYPEDEF, name);
            this.type = type;
            this.alias = alias;
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new TypedefDecl(this.name, TypeSpec.copy(this.type), this.alias);
        }
    }
}
