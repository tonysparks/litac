/*
 * see license.txt
 */
package litac.checker;

import java.util.HashMap;
import java.util.Map;

import litac.ast.Stmt;
import litac.ast.TypeInfo;
import litac.ast.TypeInfo.EnumTypeInfo;
import litac.ast.TypeInfo.FuncTypeInfo;
import litac.ast.TypeInfo.StructTypeInfo;
import litac.ast.TypeInfo.UnionTypeInfo;

/**
 * @author Tony
 *
 */
public class Module {

    public String name;        
    private Map<String, Module> imports;
    
    private Map<String, FuncTypeInfo> funcTypes;
    private Map<String, StructTypeInfo> structTypes;
    private Map<String, UnionTypeInfo> unionTypes;
    private Map<String, EnumTypeInfo> enumTypes;
    private TypeCheckResult result;
    
    public Scope currentScope;
    
    public Module(TypeCheckResult result, String name) {
        this.result = result;
        this.name = name;
        
        this.imports = new HashMap<>();         
        this.funcTypes = new HashMap<>();
        this.structTypes = new HashMap<>();
        this.unionTypes = new HashMap<>();
        this.enumTypes = new HashMap<>();
        
        this.currentScope = new Scope(result);
    }
    
    public void importModule(Stmt stmt, Module module, String alias) {
        if(module == null) {
            this.result.addError(stmt, "unable to import module '%s' ", alias);
            return;
        }
        
        this.imports.put(alias, module);    
    }
    
    public Module getModule(String name) {
        return this.imports.get(name);
    }
    
    public void declareFunc(Stmt stmt, String funcName, FuncTypeInfo type) {
        if(this.funcTypes.containsKey(funcName)) {
            this.result.addError(stmt, "%s function is already defined", funcName);
            return;
        }
        
        this.funcTypes.put(funcName, type);
    }
    
    public void declareStruct(Stmt stmt, String structName, StructTypeInfo type) {
        if(this.structTypes.containsKey(structName)) {
            this.result.addError(stmt, "%s struct is already defined", structName);
            return;
        }
        
        if(this.unionTypes.containsKey(structName)) {
            this.result.addError(stmt, "%s union is already defined with the same name", structName);
            return;
        }
        
        if(this.enumTypes.containsKey(structName)) {
            this.result.addError(stmt, "%s enum is already defined with the same name", structName);
            return;
        }
        
        
        this.structTypes.put(structName, type);
    }
    
    public void declareUnion(Stmt stmt, String unionName, UnionTypeInfo type) {
        if(this.unionTypes.containsKey(unionName)) {
            this.result.addError(stmt, "%s union is already defined", unionName);
            return;
        }
        
        if(this.structTypes.containsKey(unionName)) {
            this.result.addError(stmt, "%s struct is already defined with the same name", unionName);
            return;
        }
        
        
        if(this.enumTypes.containsKey(unionName)) {
            this.result.addError(stmt, "%s enum is already defined with the same name", unionName);
            return;
        }
        
        this.unionTypes.put(unionName, type);
    }
    
    public void declareEnum(Stmt stmt, String enumName, EnumTypeInfo type) {
        if(this.enumTypes.containsKey(enumName)) {
            this.result.addError(stmt, "%s enum is already defined", enumName);
            return;
        }
        
        if(this.unionTypes.containsKey(enumName)) {
            this.result.addError(stmt, "%s union is already defined with the same name", enumName);
            return;
        }
        
        if(this.structTypes.containsKey(enumName)) {
            this.result.addError(stmt, "%s struct is already defined with the same name", enumName);
            return;
        }
        
        
        this.enumTypes.put(enumName, type);
    }
    
    public FuncTypeInfo getFuncType(String funcName) {
        return this.funcTypes.get(funcName);
    }
    
    public TypeInfo getType(String typeName) {
        if(this.structTypes.containsKey(typeName)) {
            return this.structTypes.get(typeName);
        }
        
        if(this.unionTypes.containsKey(typeName)) {
            return this.unionTypes.get(typeName);
        }
        
        if(this.enumTypes.containsKey(typeName)) {
            return this.enumTypes.get(typeName);
        }
        
        return null;
    }
    
    public Scope pushScope() {
        this.currentScope = this.currentScope.pushScope();
        return this.currentScope;
    }
    
    public Scope popScope() {
        this.currentScope = this.currentScope.getParent();
        return this.currentScope;
    }
}
