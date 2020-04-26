/*
 * see license.txt
 */
package litac.ast;

import java.util.*;

import litac.Errors;
import litac.ast.Decl.*;
import litac.compiler.*;

/**
 * @author Tony
 *
 */
public abstract class Stmt extends Node {
    
    public static class ModuleStmt extends Stmt {
        public List<ImportStmt> imports;
        public List<NoteStmt> notes;
        public List<Decl> declarations;
        public ModuleId id;

        public ModuleStmt(ModuleId id, List<ImportStmt> imports, List<NoteStmt> notes, List<Decl> declarations) {
            this.id = id;
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
            return new ModuleStmt(this.id, copy(this.imports), copy(this.notes), copy(this.declarations));
        }
    }

    
    public static class ImportStmt extends Stmt {
        public String moduleName;        
        public String alias;
        public ModuleId moduleId;
        public boolean isUsing;
        
        public ImportStmt(String moduleName,                          
                          String alias,
                          ModuleId moduleId,
                          boolean isUsing) {
            this.moduleName = moduleName;            
            this.alias = alias;
            this.moduleId = moduleId;
            this.isUsing = isUsing;
        }
        
        public String getImportName() {
            String importName = this.alias != null ? this.alias : this.moduleId.fullModuleName;
            return importName;
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new ImportStmt(this.moduleName, this.alias, this.moduleId, this.isUsing);
        }
    }
    

    public static class NoteStmt extends Stmt {
        public final String name;
        public final List<String> attributes;
        
        public NoteStmt(String name) {
            this(name, Collections.emptyList());
        }
        
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
    
    public static class EnumFieldEntryStmt extends Stmt {
        public Identifier fieldName;
        public Expr value;
        public Attributes attributes;
        
        public EnumFieldEntryStmt(Identifier fieldName, Expr value, Attributes attributes) {            
            this.fieldName = becomeParentOf(fieldName);
            this.value = becomeParentOf(value);
            this.attributes = attributes;
        }
                
        @Override
        protected Node doCopy() {        
            return this;
        }
        
        @Override
        public void visit(NodeVisitor v) { 
            v.visit(this);
        }
    }
    
    public static abstract class FieldStmt extends Stmt { 
    }
    
    public static class VarFieldStmt extends FieldStmt {        
        public Identifier fieldName;
        public TypeSpec type;
        public Attributes attributes;
        public Expr defaultExpr;
        
        public VarFieldStmt(Identifier name, TypeSpec type, Attributes attributes, Expr defaultExpr) {
            this.fieldName = becomeParentOf(name);
            this.type = type;
            this.attributes = attributes;
            this.defaultExpr = defaultExpr;
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            VarFieldStmt v = new VarFieldStmt(this.fieldName.copy(), TypeSpec.copy(this.type), this.attributes, copy(this.defaultExpr));            
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
    
    public static class FuncBodyStmt extends Stmt {
        public List<Stmt> stmts;
        
        public FuncBodyStmt(List<Stmt> stmts) {
            this.stmts = becomeParentOf(stmts);
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new FuncBodyStmt(copy(this.stmts));
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
    
    public static class CompStmt extends Stmt {
        public String type;
        public String expr;
        public List<Stmt> body;
        public CompStmt end;
        
        private Stmt evaluatedStmt;
        
        public CompStmt(String type, String expr, List<Stmt> body, CompStmt end) {
            this.type = type;
            this.expr = expr;
            this.body = becomeParentOf(body);
            this.end = becomeParentOf(end);
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            CompStmt s = new CompStmt(this.type, this.expr, copy(body), copy(end));
            s.evaluatedStmt = copy(this.evaluatedStmt);
            return s;
        }
        

        private boolean execute(Preprocessor pp, String expression) {
            try {
                return pp.execute(expression);
            }
            catch(CompileException e) {
                Errors.compileError(getSrcPos(), e.getMessage());
            }
            
            return false;
        }
        
        /**
         * Evaluates the Compile Time Statement in a statement body
         * 
         * @param pp
         * @return the {@link Stmt} that should be used as a replacement
         */
        public Stmt evaluateForBody(Preprocessor pp) {
            if(this.evaluatedStmt != null) {
                return this.evaluatedStmt;
            }
            
            CompStmt compStmt = this;
            switch(compStmt.type) {
                case "if":
                case "elseif":
                case "else": {
                    if(compStmt.type.equals("else") || execute(pp, compStmt.expr)) {
                        List<Stmt> stmts = compStmt.body;
                        this.evaluatedStmt = new BlockStmt(stmts).setSrcPos(getSrcPos(), getEndSrcPos());
                        return this.evaluatedStmt;
                    }
                    else {
                        if(compStmt.end != null) {
                            this.evaluatedStmt = compStmt.end.evaluateForBody(pp).setSrcPos(getSrcPos(), getEndSrcPos());
                            return this.evaluatedStmt;
                        }
                    }
                }
            }            
            this.evaluatedStmt = new EmptyStmt().setSrcPos(getSrcPos(), getEndSrcPos());
            return this.evaluatedStmt;
        }
        
        
        /**
         * Evaluates the Compile Time Statement for a module.
         * 
         * @param pp
         * @param imports
         * @param moduleNotes
         * @param declarations
         */
        public void evaluateForModule(Preprocessor pp, 
                                      List<ImportStmt> imports, 
                                      List<NoteStmt> moduleNotes, 
                                      List<Decl> declarations) {
            
            CompStmt compStmt = this;
            switch(compStmt.type) {
                case "if":
                case "elseif":
                case "else": {
                    if(compStmt.type.equals("else") || execute(pp, compStmt.expr)) {
                        List<Stmt> stmts = compStmt.body;
                        for(Stmt s : stmts) {
                            if(s instanceof Decl) {
                                declarations.add((Decl)s);
                            }
                            else if(s instanceof ImportStmt) {
                                imports.add((ImportStmt)s);
                            }
                            else if(s instanceof NoteStmt) {
                                moduleNotes.add((NoteStmt)s);
                            }
                            else if(s instanceof BlockStmt) {
                                BlockStmt b = (BlockStmt)s;
                                for(Stmt n : b.stmts) {
                                    if(n instanceof NoteStmt) {
                                        moduleNotes.add((NoteStmt)n);
                                    }
                                }
                            }
                        }
                    }                    
                    else {
                        if(compStmt.end != null) {
                            compStmt.end.evaluateForModule(pp, imports, moduleNotes, declarations);
                        }
                    }
                }
            }
        }
    }
}
