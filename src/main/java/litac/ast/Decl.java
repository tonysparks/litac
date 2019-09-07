/*
 * see license.txt
 */
package litac.ast;

import java.util.*;

import litac.checker.TypeInfo;
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
        NATIVE,
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
        
        public VarDecl(String name, TypeSpec type) {
            this(name, type, null, 0);
        }
        
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
    
    public static abstract class GenericDecl extends Decl {
        public List<GenericParam> genericParams;
        
        public GenericDecl(DeclKind kind, String name, List<GenericParam> genericParams) {
            super(kind, name);
            this.genericParams = genericParams;
        }
        
        public boolean hasGenericParams() {
            return this.genericParams != null && !this.genericParams.isEmpty();
        }
    }
    
    public static class FuncDecl extends GenericDecl {
        public ParametersStmt params;
        public Stmt bodyStmt;
        public TypeSpec returnType;
        public int flags;        
        
        public FuncDecl(String name, ParametersStmt params, Stmt body, TypeSpec returnType, List<GenericParam> genericParams, int flags) {
            super(DeclKind.FUNC, name, genericParams);
            this.params = becomeParentOf(params);
            this.bodyStmt = becomeParentOf(body);
            this.returnType = returnType;            
            this.flags = flags;
        }
        
        public boolean isMethod() {
            return (this.flags & TypeInfo.FUNC_ISMETHOD_FLAG) != 0;
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
                                new ArrayList<>(this.genericParams), 
                                flags);
        }
    }
    
    public static abstract class AggregateDecl extends GenericDecl {
        public List<FieldStmt> fields;
        public int flags;
        
        
        public AggregateDecl(DeclKind kind, String name, List<FieldStmt> fields, List<GenericParam> genericParams, int flags) {
            super(kind, name, genericParams);
            this.fields = becomeParentOf(fields);
            this.flags = flags;
        }
    }
    
    public static class StructDecl extends AggregateDecl {
        public StructDecl(String name, List<FieldStmt> fields, List<GenericParam> genericParams, int flags) {
            super(DeclKind.STRUCT, name, fields, genericParams, flags);
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
            return new StructDecl(this.name, 
                                  copy(this.fields), 
                                  new ArrayList<>(this.genericParams), 
                                  this.flags);
        }
    }       
    
    public static class UnionDecl extends AggregateDecl {        
        public UnionDecl(String name, List<FieldStmt> fields, List<GenericParam> genericParams, int flags) {
            super(DeclKind.UNION, name, fields, genericParams, flags);
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new UnionDecl(this.name, 
                                 copy(this.fields), 
                                 new ArrayList<>(this.genericParams), 
                                 this.flags);
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
    
    public static class TypedefDecl extends GenericDecl {
        public TypeSpec type;
        public String alias;
        
        public TypedefDecl(String name, TypeSpec type, String alias, List<GenericParam> genericParams) {
            super(DeclKind.TYPEDEF, name, genericParams);
            this.type = type;
            this.alias = alias;
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new TypedefDecl(this.name, 
                                   TypeSpec.copy(this.type), 
                                   this.alias, 
                                   new ArrayList<>(this.genericParams));
        }
    }
    
    public static class NativeDecl extends Decl {
        public TypeInfo type;
        
        public NativeDecl(TypeInfo type) {
            super(DeclKind.NATIVE, type.name);
            this.type = type;
            this.attributes.addNote(new NoteStmt("foreign"));
        }
        
        @Override
        public void visit(NodeVisitor v) {
        }
        
        @Override
        protected Node doCopy() {        
            return this;
        }
    }
}
