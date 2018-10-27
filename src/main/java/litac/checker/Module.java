/*
 * see license.txt
 */
package litac.checker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import litac.ast.Decl;
import litac.ast.Decl.*;
import litac.checker.TypeInfo.*;
import litac.ast.Stmt;

/**
 * @author Tony
 *
 */
public class Module {

    public String name;        
    public Scope currentScope;
    
    private Map<String, Module> imports;
    
    private Map<String, FuncTypeInfo> funcTypes;
    private Map<String, StructTypeInfo> structTypes;
    private Map<String, UnionTypeInfo> unionTypes;
    private Map<String, EnumTypeInfo> enumTypes;
    
    private Map<String, FuncTypeInfo> publicFuncTypes;
    private Map<String, TypeInfo> publicTypes;
    
    private Map<String, FuncTypeInfo> importedFuncTypes;
    private Map<String, TypeInfo> importedAggregateTypes;
    
    private TypeCheckResult result;
    
    
    public Module(TypeCheckResult result, String name) {
        this.result = result;
        this.name = name;
        
        this.imports = new HashMap<>();         
        this.funcTypes = new HashMap<>();
        this.structTypes = new HashMap<>();
        this.unionTypes = new HashMap<>();
        this.enumTypes = new HashMap<>();
        
        this.publicFuncTypes = new HashMap<>();
        this.publicTypes = new HashMap<>();
        
        this.importedFuncTypes = new HashMap<>();
        this.importedAggregateTypes = new HashMap<>();
        
        this.currentScope = new Scope(result);
    }
    
    public Collection<Module> getImports() {
        return imports.values();
    }
    
    public Collection<FuncTypeInfo> getDeclaredFuncs() {
        return funcTypes.values();
    }
    
    public Collection<TypeInfo> getDeclaredTypes() {
        Collection<TypeInfo> result = new ArrayList<>(); 
        result.addAll(structTypes.values());
        result.addAll(unionTypes.values());
        result.addAll(enumTypes.values());
        
        return result;
    }
    
    public void importModule(Stmt stmt, Module module, String alias) {
        if(module == null) {
            this.result.addError(stmt, "unable to import module '%s' ", alias);
            return;
        }
        
        this.imports.put(alias, module);    
        
        for(Entry<String, FuncTypeInfo> funcType: module.publicFuncTypes.entrySet()) {
            this.importedFuncTypes.put(String.format("%s::%s", alias, funcType.getKey()), funcType.getValue());
        }
        
        for(Entry<String, TypeInfo> aggType: module.publicTypes.entrySet()) {
            this.importedAggregateTypes.put(String.format("%s::%s", alias, aggType.getKey()), aggType.getValue());
        }
    }
    
    public Module getModule(String name) {
        return this.imports.get(name);
    }
    
    private void addPublicDecl(Decl decl, String name, TypeInfo type) {
        if(decl.isPublic) {
            if(type.isKind(TypeKind.Func)) {
                FuncTypeInfo funcInfo = type.as();
                this.publicFuncTypes.put(name, funcInfo);
            }
            else {
                this.publicTypes.put(name, type);
            }
        }
    }
    
    public void declareFunc(FuncDecl stmt, String funcName, FuncTypeInfo type) {
        if(this.funcTypes.containsKey(funcName)) {
            this.result.addError(stmt, "%s function is already defined", funcName);
            return;
        }
        
        this.funcTypes.put(funcName, type);
        
        addPublicDecl(stmt, funcName, type);
    }
    
    public void declareStruct(StructDecl stmt, String structName, StructTypeInfo type) {
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
        
        addPublicDecl(stmt, structName, type);
    }
    
    public void declareUnion(UnionDecl stmt, String unionName, UnionTypeInfo type) {
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
        
        addPublicDecl(stmt, unionName, type);
    }
    
    public void declareEnum(EnumDecl stmt, String enumName, EnumTypeInfo type) {
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
        
        addPublicDecl(stmt, enumName, type);
    }
    
    public FuncTypeInfo getFuncType(String funcName) {
        if(funcName.contains("::")) {
            return this.importedFuncTypes.get(funcName);
        }
        
        return this.funcTypes.get(funcName);
    }
    
    public TypeInfo getType(String typeName) {
        if(typeName.contains("::")) {
            return this.importedAggregateTypes.get(typeName);
        }
        
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Module other = (Module) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        }
        else if (!name.equals(other.name))
            return false;
        return true;
    }
    
    
}
