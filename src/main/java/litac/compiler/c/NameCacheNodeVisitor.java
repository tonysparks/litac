/*
 * see license.txt
 */
package litac.compiler.c;

import litac.ast.Decl;
import litac.ast.Decl.ConstDecl;
import litac.ast.Decl.EnumDecl;
import litac.ast.Decl.FuncDecl;
import litac.ast.Decl.StructDecl;
import litac.ast.Decl.TypedefDecl;
import litac.ast.Decl.UnionDecl;
import litac.ast.Decl.VarDecl;
import litac.ast.NodeVisitor.AbstractNodeVisitor;
import litac.ast.Stmt.ImportStmt;
import litac.ast.Stmt.ModuleStmt;
import litac.ast.Stmt.NoteStmt;
import litac.compiler.CompilationUnit;
import litac.util.Stack;

/**
 * @author Tony
 *
 */
public class NameCacheNodeVisitor extends AbstractNodeVisitor {
    private NameCache names;
    private CompilationUnit unit;
    
    private Stack<NameCoord> currentModule;
    private Stack<ModuleStmt> parentModule;
    
    public NameCacheNodeVisitor(NameCache names, CompilationUnit unit) {
        this.names = names;
        this.unit = unit;
        
        this.currentModule = new Stack<>();
        this.currentModule.add(new NameCoord("", unit.getMain().name, unit.getMain().name));
        
        this.parentModule = new Stack<>();
    }
    
    @Override
    public void visit(ModuleStmt stmt) {
        this.parentModule.add(stmt);
        for(ImportStmt i : stmt.imports) {
            i.visit(this);
        }
        
        for(Decl d : stmt.declarations) {
            d.visit(this);
        }
        this.parentModule.pop();
    }
    

    @Override
    public void visit(ImportStmt stmt) {
        ModuleStmt module = this.unit.getModule(stmt.moduleName);
        this.currentModule.push(new NameCoord(this.parentModule.peek().name, stmt.moduleName, stmt.alias));        
        module.visit(this);
        this.currentModule.pop();
    }
    
    private boolean isForeign(Decl d) {
        boolean isForeign = false;
        if(d.attributes.notes != null) {
            for(NoteStmt n : d.attributes.notes) {
                n.visit(this);
                
                if(n.note.name.equalsIgnoreCase("foreign")) {
                    isForeign = true;
                }
            }
        }
        
        return isForeign;            
    }
    
    
    @Override
    public void visit(EnumDecl d) {
        NameCoord name = this.currentModule.peek();
        this.names.add(name, d.name, isForeign(d), true, d.type.getResolvedType());
    }
    
    @Override
    public void visit(FuncDecl d) {
        NameCoord name = this.currentModule.peek();
        this.names.add(name, d.name, isForeign(d), true, d.type.getResolvedType());
    }
        
    @Override
    public void visit(StructDecl d) {
        NameCoord name = this.currentModule.peek();
        this.names.add(name, d.name, isForeign(d), true, d.type.getResolvedType());
    }
    
    @Override
    public void visit(UnionDecl d) {
        NameCoord name = this.currentModule.peek();
        this.names.add(name, d.name, isForeign(d), true, d.type.getResolvedType());
    }
    
    @Override
    public void visit(TypedefDecl d) {
        NameCoord name = this.currentModule.peek();
        this.names.add(name, d.name, isForeign(d), true, d.type.getResolvedType());
    }
    
    @Override
    public void visit(VarDecl d) {
        NameCoord name = this.currentModule.peek();
        this.names.add(name, d.name, isForeign(d), false, d.type.getResolvedType());
    }
    
    @Override
    public void visit(ConstDecl d) {
        NameCoord name = this.currentModule.peek();
        this.names.add(name, d.name, isForeign(d), false, d.type.getResolvedType());
    }

}
