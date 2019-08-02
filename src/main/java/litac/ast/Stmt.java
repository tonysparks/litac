/*
 * see license.txt
 */
package litac.ast;

import java.util.ArrayList;
import java.util.List;

import litac.ast.Decl.*;
import litac.checker.*;
import litac.checker.TypeInfo.FieldInfo;
import litac.parser.ErrorCode;
import litac.parser.ParseException;
import litac.parser.tokens.Token;

/**
 * @author Tony
 *
 */
public abstract class Stmt extends Node {

    public static List<FieldInfo> fromFieldStmt(Token token, List<FieldStmt> fields) {
        List<FieldInfo> result = new ArrayList<>();
        
        for(FieldStmt s : fields) {
            FieldInfo fieldInfo = null;
            if(s instanceof VarFieldStmt) {
                VarFieldStmt var = (VarFieldStmt)s;
                fieldInfo = new FieldInfo(var.type, var.name, var.attributes, null);
            }
            else if(s instanceof StructFieldStmt) {
                StructFieldStmt struct = (StructFieldStmt)s;
                fieldInfo = new FieldInfo(struct.decl.type, struct.decl.name, struct.decl.attributes, null);
            }
            else if(s instanceof UnionFieldStmt) {
                UnionFieldStmt union = (UnionFieldStmt)s;
                fieldInfo = new FieldInfo(union.decl.type, union.decl.name, union.decl.attributes, null);
            }
            else if(s instanceof EnumFieldStmt) {
                EnumFieldStmt enm = (EnumFieldStmt)s;
                fieldInfo = new FieldInfo(enm.decl.type, enm.decl.name, enm.decl.attributes, null);
            }
            else {
                throw new ParseException(ErrorCode.INVALID_FIELD, token);
            }
            
            result.add(fieldInfo);
        }
        
        return result;
    }
    
    public static class ModuleStmt extends Stmt {

        public String name;
        public List<ImportStmt> imports;
        public List<NoteStmt> notes;
        public List<Decl> declarations;
        
        
        public ModuleStmt(String name, List<ImportStmt> imports, List<NoteStmt> notes, List<Decl> declarations) {
            this.name = name;
            this.imports = becomeParentOf(imports);
            this.notes = becomeParentOf(notes);
            this.declarations = becomeParentOf(declarations);
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }

        @Override
        protected Node doCopy() {            
            return new ModuleStmt(this.name, copy(this.imports), copy(this.notes), copy(this.declarations));
        }
    }

    
    public static class ImportStmt extends Stmt {
        public String moduleName;
        public String alias;
        
        public ImportStmt(String moduleName, String alias) {
            this.moduleName = moduleName;
            this.alias = alias;
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new ImportStmt(this.moduleName, this.alias);
        }
    }
    

    public static class NoteStmt extends Stmt {
        public final String name;
        public final List<String> attributes;
        
        public NoteStmt(String name, List<String> attributes) {
            this.name = name;
            this.attributes = attributes;
        }
        
        public String getAttr(int index, String defaultValue) {
            if(this.attributes == null || this.attributes.size() <= index) {
                return defaultValue;
            }
            
            return this.attributes.get(index);
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);            
        } 
        
        @Override
        protected Node doCopy() {            
            return new NoteStmt(this.name, this.attributes);
        }
    }
    
    public static class GotoStmt extends Stmt {
        public String label;
        
        public GotoStmt(String label) {
            this.label = label;
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);            
        } 
        
        @Override
        protected Node doCopy() {            
            return new GotoStmt(this.label);
        }
    }
    
    public static class LabelStmt extends Stmt {
        public String label;
        
        public LabelStmt(String label) {
            this.label = label;
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);            
        } 
        
        @Override
        protected Node doCopy() {            
            return new LabelStmt(this.label);
        }
    }
    
    public static abstract class FieldStmt extends Stmt { 
    }
    
    public static class VarFieldStmt extends FieldStmt {
        public String name;
        public TypeInfo type;
        public Attributes attributes;
        
        public VarFieldStmt(String name, TypeInfo type, int modifiers) {
            this.name = name;
            this.type = type;
            this.attributes = new Attributes();
            this.attributes.modifiers = modifiers;
        }
        
        public FieldStmt addNotes(List<NoteStmt> notes) {
            if(notes != null) {
                if(this.attributes.notes == null) {
                    this.attributes.notes = new ArrayList<>();
                }
                this.attributes.notes.addAll(notes);
            }
            return this;
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            VarFieldStmt v = new VarFieldStmt(this.name, this.type.copy(), this.attributes.modifiers);
            v.attributes = this.attributes;
            return v;
        }
    }
    
    public static class StructFieldStmt extends FieldStmt {
        public StructDecl decl;
        
        public StructFieldStmt(StructDecl decl) {
            this.decl = becomeParentOf(decl);
        }

        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new StructFieldStmt(this.decl.copy());
        }
    }
    
    public static class UnionFieldStmt extends FieldStmt {
        public UnionDecl decl;
        
        public UnionFieldStmt(UnionDecl decl) {
            this.decl = becomeParentOf(decl);
        }


        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new UnionFieldStmt(this.decl.copy());
        }
    }
    
    public static class EnumFieldStmt extends FieldStmt {
        public EnumDecl decl;
        
        public EnumFieldStmt(EnumDecl decl) {
            this.decl = becomeParentOf(decl);
        }


        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new EnumFieldStmt(this.decl.copy());
        }
    }
 
    public static class IfStmt extends Stmt {
        public Expr condExpr;
        public Stmt thenStmt;
        public Stmt elseStmt;
        
        public IfStmt(Expr condExpr, Stmt thenStmt, Stmt elseStmt) {
            this.condExpr = becomeParentOf(condExpr);
            this.thenStmt = becomeParentOf(thenStmt);
            this.elseStmt = becomeParentOf(elseStmt);
        }
        

        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new IfStmt(copy(this.condExpr), copy(this.thenStmt), copy(this.elseStmt));
        }
    }
    
    public static class WhileStmt extends Stmt {
        public Expr condExpr;
        public Stmt bodyStmt;
        
        public WhileStmt(Expr condExpr, Stmt bodyStmt) {
            this.condExpr = becomeParentOf(condExpr);
            this.bodyStmt = becomeParentOf(bodyStmt);
        }
        

        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new WhileStmt(copy(this.condExpr), copy(this.bodyStmt));
        }
    }
    
    public static class EmptyStmt extends Stmt {
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new EmptyStmt();
        }
    }
    
    public static class DoWhileStmt extends Stmt {
        public Expr condExpr;
        public Stmt bodyStmt;
        
        public DoWhileStmt(Expr condExpr, Stmt bodyStmt) {
            this.condExpr = becomeParentOf(condExpr);
            this.bodyStmt = becomeParentOf(bodyStmt);
        }
        

        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new DoWhileStmt(copy(this.condExpr), copy(this.bodyStmt));
        }
    }
    
    public static class ForStmt extends Stmt {
        public Stmt initStmt;
        public Expr condExpr;
        public Stmt postStmt;
        public Stmt bodyStmt;
        
        public ForStmt(Stmt initStmt, Expr condExpr, Stmt postStmt, Stmt bodyStmt) {
            this.initStmt = becomeParentOf(initStmt);
            this.condExpr = becomeParentOf(condExpr);
            this.postStmt = becomeParentOf(postStmt);
            this.bodyStmt = becomeParentOf(bodyStmt);
        }
        

        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new ForStmt(copy(this.initStmt), copy(this.condExpr), copy(this.postStmt), copy(this.bodyStmt));
        }
    }
    
    public static class SwitchCaseStmt extends Stmt {
        public Expr cond;
        public Stmt stmt;
        
        public SwitchCaseStmt(Expr cond, Stmt stmt) {
            this.cond = becomeParentOf(cond);
            this.stmt = becomeParentOf(stmt);
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new SwitchCaseStmt(copy(this.cond), copy(this.stmt));
        }
    }
    
    public static class SwitchStmt extends Stmt {
        public Expr cond;
        public List<SwitchCaseStmt> stmts;
        public Stmt defaultStmt;
        
        public SwitchStmt(Expr cond, List<SwitchCaseStmt> stmts, Stmt defaultStmt) {
            this.cond = becomeParentOf(cond);
            this.stmts = becomeParentOf(stmts);
            this.defaultStmt = becomeParentOf(defaultStmt);
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new SwitchStmt(copy(this.cond), copy(this.stmts), copy(defaultStmt));
        }
    }
    
    public static class BreakStmt extends Stmt {
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new BreakStmt();
        }
    }
        
    public static class ContinueStmt extends Stmt {
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new ContinueStmt();
        }
    }

    public static class ReturnStmt extends Stmt {
        public Expr returnExpr;
        
        public ReturnStmt(Expr returnExpr) {
            this.returnExpr = becomeParentOf(returnExpr);
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new ReturnStmt(copy(this.returnExpr));
        }
    }
    
    public static class BlockStmt extends Stmt {
        public List<Stmt> stmts;
        
        public BlockStmt(List<Stmt> stmts) {
            this.stmts = becomeParentOf(stmts);
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new BlockStmt(copy(this.stmts));
        }
    }
    
    public static class DeferStmt extends Stmt {
        public Stmt stmt;
        
        public DeferStmt(Stmt stmt) {
            this.stmt = stmt;
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);            
        }
        
        @Override
        protected Node doCopy() {            
            return new DeferStmt(copy(this.stmt));
        }
    }
    
    public static class ParametersStmt extends Stmt {
        public List<ParameterDecl> params;
        public boolean isVararg;
        
        public ParametersStmt(List<ParameterDecl> params, boolean isVararg) {
            this.params = becomeParentOf(params);
            this.isVararg = isVararg;
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new ParametersStmt(copy(this.params), this.isVararg);
        }
    }
    
    public static class VarDeclsStmt extends Stmt {
        public List<VarDecl> vars;
        
        public VarDeclsStmt(List<VarDecl> vars) {
            this.vars = becomeParentOf(vars);
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new VarDeclsStmt(copy(this.vars));
        }
    }
    
    public static class ConstDeclsStmt extends Stmt {
        public List<ConstDecl> consts;
        
        public ConstDeclsStmt(List<ConstDecl> consts) {
            this.consts = becomeParentOf(consts);
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new ConstDeclsStmt(copy(this.consts));
        }
    }
    
    public static class CompStmt {
        public String type;
        public String expr;
        public List<Stmt> body;
        public CompStmt end;
        
        public CompStmt(String type, String expr, List<Stmt> body, CompStmt end) {
            this.type = type;
            this.expr = expr;
            this.body = body;
            this.end = end;
            //this.body = becomeParentOf(body);
            //this.end = becomeParentOf(end);
        }
        
//        @Override
//        protected Node doCopy() {            
//            return new CompStmt(this.type, this.expr, copy(body), copy(end));
//        }
    }
}
