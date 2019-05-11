/*
 * see license.txt
 */
package litac.compiler.c;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import litac.ast.Decl;
import litac.ast.Decl.FuncDecl;
import litac.ast.Decl.ParameterDecl;
import litac.ast.Decl.VarDecl;
import litac.ast.Expr;
import litac.ast.Stmt;
import litac.ast.Expr.FuncCallExpr;
import litac.ast.Expr.FuncIdentifierExpr;
import litac.ast.Node;
import litac.ast.NodeVisitor.AbstractNodeVisitor;
import litac.ast.Stmt.BlockStmt;
import litac.ast.Stmt.ImportStmt;
import litac.ast.Stmt.ModuleStmt;
import litac.checker.GenericParam;
import litac.checker.Module;
import litac.checker.TypeInfo;
import litac.checker.TypeResolver;
import litac.checker.TypeInfo.FuncTypeInfo;
import litac.compiler.CompilationUnit;
import litac.compiler.c.Scope.ScopeType;
import litac.util.Stack;
import litac.util.Tuple;

/**
 * @author Tony
 *
 */
public class GenericsNodeVisitor extends AbstractNodeVisitor {

    static class GenericDecl {
        ModuleStmt module;
        Decl originalDecl;
        
        public GenericDecl(ModuleStmt module,
                           Decl originalDecl) {
            this.module = module;
            this.originalDecl = originalDecl;
        }
    }
    
   // private NameCache names;
    private CompilationUnit unit;
    private Set<String> visitedModules;
    private Scope scope;    
    
    private Map<String, Map<String,GenericDecl>> genericDecls;
    private Stack<ModuleStmt> modules;
    
    private List<Decl> newDeclarations;
    private TypeResolver resolver;
    private Module module;
    
    public GenericsNodeVisitor(TypeResolver resolver, Module module, CompilationUnit unit/*, NameCache names*/) {
        this.resolver = resolver;
        this.unit = unit;
        this.module = module;
        
        //this.names = names;
        this.visitedModules = new HashSet<>();
        this.scope = new Scope(null, "", ScopeType.MODULE);
        this.genericDecls = new HashMap<>();
        this.genericDecls.put("", new HashMap<>());
        
        this.modules = new Stack<>();
        this.newDeclarations = new ArrayList<>();
    }
        
    @Override
    public void visit(ModuleStmt stmt) {
        if(this.visitedModules.contains(stmt.name)) {
            return;
        }
        
        this.visitedModules.add(stmt.name);
        this.modules.add(stmt);
        
        this.scope = this.scope.pushScope(stmt.name, ScopeType.MODULE);
                
        for(ImportStmt i : stmt.imports) {
            i.visit(this);
        }
                
        for(Decl d : stmt.declarations) {
            d.visit(this);
        }        
        
        stmt.declarations.addAll(this.newDeclarations);
        this.newDeclarations.clear();
        
        this.modules.pop();
        this.scope = this.scope.popScope();
    }
    
    @Override
    public void visit(ImportStmt stmt) {
        ModuleStmt module = this.unit.getModule(stmt.moduleName);
        module.visit(this);
    }

    @Override
    public void visit(VarDecl d) {
        if(d.expr != null) {
            d.expr.visit(this);
        }
    }
    
    @Override
    public void visit(FuncDecl d) {        
        FuncTypeInfo funcInfo = d.type.as();
        
        if(!funcInfo.genericParams.isEmpty()) {
            this.genericDecls.get("").put(d.name, new GenericDecl(this.modules.peek(), d));
        }
        
        if(d.bodyStmt != null) {
            d.bodyStmt.visit(this);
        }
    }
    
    @Override
    public void visit(BlockStmt stmt) {
        this.scope = this.scope.pushLocalScope();
        for(Stmt s : stmt.stmts) {
            s.visit(this);
        }
        this.scope = this.scope.popScope();
    }
    
    @Override
    public void visit(FuncCallExpr expr) {    
        FuncTypeInfo funcInfo = expr.object.getResolvedType().as();
        if(!funcInfo.genericParams.isEmpty()) {
            List<ParameterDecl> newFuncParams = new ArrayList<>();
            StringBuilder funcName = new StringBuilder(funcInfo.name);
            List<Tuple<String, TypeInfo>> replacements = new ArrayList<>();
            
            for(int i = 0; i < expr.genericArgs.size(); i++) {
                TypeInfo argInfo = expr.genericArgs.get(i);
                funcName.append("").append(argInfo.getName().replace("::", ""));
            }
            
            String newFuncName = funcName.toString();
            boolean isAlreadyDefined = (this.module.getFuncType(newFuncName) != null);
            
            for(ParameterDecl paramDecl : funcInfo.parameterDecls) {
                boolean isGenericParam = false;
                for(int i = 0; i < funcInfo.genericParams.size(); i++) {
                    GenericParam p = funcInfo.genericParams.get(i);
                    if(p.name.equals(paramDecl.type.getName())) {
                        // TODO: verify correct number of generic args/params
                        TypeInfo argInfo = expr.genericArgs.get(i);
                        replacements.add(new Tuple<>(p.name, argInfo));
                        newFuncParams.add(new ParameterDecl(argInfo, paramDecl.name));
                        isGenericParam = true;
                        break;
                    }
                }    
                
                if(!isGenericParam) {
                    newFuncParams.add(paramDecl);
                }
            }
            
            TypeInfo newReturnType = funcInfo.returnType;
            for(int i = 0; i < funcInfo.genericParams.size(); i++) {
                GenericParam p = funcInfo.genericParams.get(i);
                if(p.name.equals(funcInfo.returnType.getName())) {
                    // TODO: verify correct number of generic args/params
                    TypeInfo argInfo = expr.genericArgs.get(i);
                    replacements.add(new Tuple<>(p.name, argInfo));
                    newReturnType = argInfo;
                    break;
                }
            }
            

            // Don't create a duplicate if we've already creatd this function
            //boolean isAlreadyDefined = this.names.hasBackendName(this.scope.getName(), newFuncName);
            
                        
            FuncTypeInfo newFuncInfo = new FuncTypeInfo(newFuncName, 
                                                        newReturnType, 
                                                        newFuncParams, 
                                                        funcInfo.isVararg, 
                                                        Collections.emptyList());
            
            
//            if(!isAlreadyDefined) {     
//                // TODO get proper name coordinates
//                this.names.add(new NameCoord("", "", ""), newFuncName, false, true, newFuncInfo);
//            }
            
            expr.object.resolveTo(newFuncInfo);
            Node parent = expr.getParentNode();
            if(parent instanceof Decl) {
                Decl decl = (Decl)parent;
                decl.type = newReturnType;
            }
            else if(parent instanceof Expr) {
                Expr e = (Expr)parent;
                e.resolveTo(newReturnType);
            }
            
            if(expr.object instanceof FuncIdentifierExpr) {
                FuncIdentifierExpr idExpr = (FuncIdentifierExpr)expr.object;
                idExpr.variable = newFuncName;
            }
            
            // TODO: Get correct module
            GenericDecl genDecl = this.genericDecls.get("").get(funcInfo.name);
            TypeReplacerNodeVisitor replacer = new TypeReplacerNodeVisitor(replacements);
            FuncDecl decl = genDecl.originalDecl.copy();
            decl.name = newFuncName;
            decl.type = newFuncInfo;
            decl.returnType = newFuncInfo.returnType;
            decl.params.params = newFuncParams;
            
            decl.visit(replacer);
            
            this.resolver.resolveStmt(this.module, decl);
            
            if(!isAlreadyDefined) {
                this.module.declareFunc(decl, newFuncName, newFuncInfo);
                this.newDeclarations.add(decl);
            }
        }                
    }
}
