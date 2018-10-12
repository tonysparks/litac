/*
 * see license.txt
 */
package litac.checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import litac.ast.TypeInfo;
import litac.ast.TypeInfo.TypeKind;
import litac.ast.Decl;
import litac.ast.Decl.ConstDecl;
import litac.ast.Decl.FuncDecl;
import litac.ast.Decl.VarDecl;
import litac.ast.Expr;
import litac.ast.Stmt;
import litac.ast.Stmt.VarFieldStmt;
import litac.checker.TypeCheckResult.TypeCheckError;
import litac.util.Stack;

/**
 * @author Tony
 *
 */
public class Scope {

    public static class TypeCheck {
        public Expr expr;
        public TypeInfo type;
        
        public TypeCheck(Expr expr, 
                         TypeInfo type) {
            this.expr = expr;
            this.type = type;            
        }
    }
    
    public Map<String, TypeInfo> types;
    public Map<String, TypeInfo> variables;
    
    // Key=>TypeName, Value=>List of TypeInfo's that need to be resolved
    private Map<String, List<TypeInfo>> usedTypes;
    
    private List<TypeCheck> pendingChecks;
    
    private Stack<FuncDecl> funcDecls;
    private Stack<TypeInfo> typeStack;
    
    private Scope parent;
    
    private TypeCheckResult result;
    
    public Scope(TypeCheckResult result) {
        this(result, null);
    }
    
    public Scope(TypeCheckResult result, Scope parent) {
        this.result = result;
        this.parent = parent;
        
        this.types = new HashMap<>();
        this.variables = new HashMap<>();
        this.pendingChecks = new ArrayList<>();
        
        this.funcDecls = new Stack<>();
        this.typeStack = new Stack<>();
    }
    
    public Scope getParent() {
        return this.parent;
    }
    
    public void pushFuncDecl(FuncDecl func) {
        this.funcDecls.push(func);
    }
    
    public void popsFuncDecl() {
        this.funcDecls.pop();
    }
    
    public FuncDecl peekFuncDecl() {
        return this.funcDecls.peek();
    }
    
    public void pushTypeInfo(TypeInfo type) {
        this.typeStack.push(type);
    }
    
    public TypeInfo popTypeInfo() {
        return this.typeStack.pop();
    }
    
    public TypeInfo peekTypeInfo() {
        return this.typeStack.peek();
    }
    
    
    public void addTypeCheck(Expr expr, TypeInfo type) {
        this.pendingChecks.add(new TypeCheck(expr, type));
    }
    
    public void checkTypes() {        
        for(TypeCheck check : this.pendingChecks) {
            if(!check.expr.isResolved()) {
                result.addError(new TypeCheckError(
                        String.format("unresolved type expression"), check.expr));
            }
            
            if(!check.type.canCastTo(check.expr.getResolvedType())) {
                result.addError(new TypeCheckError(
                        String.format("%s does not match type %s", check.type.name, check.expr.getResolvedType().name), check.expr));
            }
        }
    }
    
    public void addVariable(Stmt stmt, String variableName, TypeInfo type) {
        if(this.variables.containsKey(variableName)) {
            this.result.addError(new TypeCheckError(String.format("variable '%s' already defined", variableName), stmt));
        }
                
        this.variables.put(variableName, type);
    }
        
    public void addType(Stmt stmt, TypeInfo type) {
        if(this.types.containsKey(type.name)) {
            this.result.addError(new TypeCheckError(String.format("type '%s' already defined", type.name), stmt));
        }
        
        this.types.put(type.name, type);
    }
    
    //public TypeCheckError checkType()
    
    public TypeInfo getType(String typeName) {
        if(this.types.containsKey(typeName)) {
            return this.types.get(typeName);
        }
        
        if(this.parent != null) {
            return this.parent.getType(typeName);
        }
        
        return null;
    }
    
    public TypeInfo getVariable(String varName) {
        if(this.variables.containsKey(varName)) {
            return this.variables.get(varName);
        }
        
        if(this.parent != null) {
            return this.parent.getVariable(varName);
        }
        
        return null;
    }
    
    private void setVariableType(String varName, TypeInfo type) {
        if(this.variables.containsKey(varName)) {
            this.variables.put(varName, type);
        }
        else if(this.parent != null) {
            this.parent.setVariableType(varName, type);
        }        
    }
    
    public Scope pushScope() {
        return new Scope(this.result, this);
    }
}
