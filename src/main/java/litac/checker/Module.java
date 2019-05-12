/*
 * see license.txt
 */
package litac.checker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import litac.ast.Decl;
import litac.ast.Decl.*;
import litac.checker.Scope.ScopeType;
import litac.checker.TypeInfo.*;
import litac.util.Names;
import litac.ast.Stmt;
import litac.ast.Stmt.ModuleStmt;
import litac.ast.Stmt.NoteStmt;

/**
 * Keeps track of a modules defined types.
 * 
 * @author Tony
 *
 */
public class Module {

    private String name;        
    private Scope currentScope;
    
    private Map<String, Module> imports;
    private ModuleStmt moduleStmt;
    
    
    private Map<String, FuncTypeInfo> funcTypes;
    private Map<String, StructTypeInfo> structTypes;
    private Map<String, UnionTypeInfo> unionTypes;
    private Map<String, EnumTypeInfo> enumTypes;
    
    private Map<String, FuncTypeInfo> publicFuncTypes;
    private Map<String, TypeInfo> publicTypes;
    
    private Map<String, FuncTypeInfo> importedFuncTypes;
    private Map<String, TypeInfo> importedAggregateTypes;
    
    private Map<String, TypeInfo> foreignTypes;
    
    private List<NoteStmt> notes;
    
    private PhaseResult result;
    
    
    public Module(PhaseResult result, ModuleStmt moduleStmt, String name) {
        this.result = result;
        this.moduleStmt = moduleStmt;
        this.name = name;
        
        this.imports = new HashMap<>();         
        this.funcTypes = new HashMap<>();
        this.structTypes = new HashMap<>();
        this.unionTypes = new HashMap<>();
        this.enumTypes = new HashMap<>();
        
        this.publicFuncTypes = new HashMap<>();
        this.publicTypes = new HashMap<>();
        
        this.foreignTypes = new HashMap<>();
        
        this.importedFuncTypes = new HashMap<>();
        this.importedAggregateTypes = new HashMap<>();
        
        this.notes = new ArrayList<>();
        
        this.currentScope = new Scope(result, ScopeType.MODULE);
    }
    
    public ModuleStmt getModuleStmt() {
        return moduleStmt;
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
        result.addAll(funcTypes.values());
        
        return result;
    }
    
    public boolean isForeignType(String name) {
        return this.foreignTypes.containsKey(name);
    }
    
    public boolean isImported(String name) {
        return this.importedAggregateTypes.containsKey(name) || 
               this.importedFuncTypes.containsKey(name);
    }
    
    public void importModule(Stmt stmt, Module module, String alias) {
        if(module == null) {
            this.result.addError(stmt, "unable to import module '%s' ", alias);
            return;
        }
                
        this.imports.put(alias, module);
               
        for(Entry<String, FuncTypeInfo> funcType: module.publicFuncTypes.entrySet()) {
            this.importedFuncTypes.put(Names.litaName(alias, funcType.getKey()), funcType.getValue());
        }
        
        for(Entry<String, TypeInfo> aggType: module.publicTypes.entrySet()) {
            this.importedAggregateTypes.put(Names.litaName(alias, aggType.getKey()), aggType.getValue());
        }
        
        
        for(Entry<String, TypeInfo> typeEntry: module.foreignTypes.entrySet()) {
            TypeInfo type = typeEntry.getValue();
            if(type.isKind(TypeKind.Func)) {
                this.importedFuncTypes.put(Names.litaName(alias, typeEntry.getKey()), type.as());
            }
            else {
                this.importedAggregateTypes.put(Names.litaName(alias, typeEntry.getKey()), type);
            }
        }
        
        this.foreignTypes.putAll(module.foreignTypes);
        this.notes.addAll(module.notes);
    }
    
    public List<NoteStmt> getNotes() {
        return notes;
    }
    
    public Module getModule(String name) {
        return this.imports.get(name);
    }
    
    private boolean isForeign(Decl decl) {
        if(decl.attributes.notes != null) {
            for(NoteStmt n : decl.attributes.notes) {
                if(n.note.name.equalsIgnoreCase("foreign")) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private Symbol addPublicDecl(Decl decl, String name, TypeInfo type) {
        if(decl.attributes.isPublic) {
            if(type.isKind(TypeKind.Func)) {                
                FuncTypeInfo funcInfo = type.as();
                this.publicFuncTypes.put(name, funcInfo);
            }
            else {
                this.publicTypes.put(name, type);
            }
            

            if(isForeign(decl)) {
                this.foreignTypes.put(name, type);
            }
        }
        
        return this.currentScope.addSymbol(this, decl, name, type);
    }
    
    public void declareNote(NoteStmt stmt) {
        this.notes.add(stmt);
    }
    
    public Symbol declareFunc(FuncDecl stmt, String funcName, FuncTypeInfo type) {
        if(this.funcTypes.containsKey(funcName)) {
            this.result.addError(stmt, "%s function is already defined", funcName);
            return this.funcTypes.get(funcName).sym;
        }
        
        this.funcTypes.put(funcName, type);
        
        return addPublicDecl(stmt, funcName, type);
    }
    
    public Symbol declareStruct(StructDecl stmt, String structName, StructTypeInfo type) {
        if(this.structTypes.containsKey(structName)) {
            this.result.addError(stmt, "%s struct is already defined", structName);
            return this.structTypes.get(structName).sym;
        }
        
        if(this.unionTypes.containsKey(structName)) {
            this.result.addError(stmt, "%s union is already defined with the same name", structName);
            return this.unionTypes.get(structName).sym;
        }
        
        if(this.enumTypes.containsKey(structName)) {
            this.result.addError(stmt, "%s enum is already defined with the same name", structName);
            return this.enumTypes.get(structName).sym;
        }
        
        
        this.structTypes.put(structName, type);
        
        return addPublicDecl(stmt, structName, type);
    }
    
    public Symbol declareUnion(UnionDecl stmt, String unionName, UnionTypeInfo type) {
        if(this.unionTypes.containsKey(unionName)) {
            this.result.addError(stmt, "%s union is already defined", unionName);
            return this.unionTypes.get(unionName).sym;
        }
        
        if(this.structTypes.containsKey(unionName)) {
            this.result.addError(stmt, "%s struct is already defined with the same name", unionName);
            return this.structTypes.get(unionName).sym;
        }
        
        
        if(this.enumTypes.containsKey(unionName)) {
            this.result.addError(stmt, "%s enum is already defined with the same name", unionName);
            return this.enumTypes.get(unionName).sym;
        }
        
        this.unionTypes.put(unionName, type);
        
        return addPublicDecl(stmt, unionName, type);
    }
    
    public Symbol declareEnum(EnumDecl stmt, String enumName, EnumTypeInfo type) {
        if(this.enumTypes.containsKey(enumName)) {
            this.result.addError(stmt, "%s enum is already defined", enumName);
            return this.enumTypes.get(enumName).sym;
        }
        
        if(this.unionTypes.containsKey(enumName)) {
            this.result.addError(stmt, "%s union is already defined with the same name", enumName);
            return this.unionTypes.get(enumName).sym;
        }
        
        if(this.structTypes.containsKey(enumName)) {
            this.result.addError(stmt, "%s struct is already defined with the same name", enumName);
            return this.structTypes.get(enumName).sym;
        }
        
        
        this.enumTypes.put(enumName, type);
        
        return addPublicDecl(stmt, enumName, type);
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
        this.currentScope = this.currentScope.pushLocalScope();
        return this.currentScope;
    }
    
    public Scope popScope() {
        this.currentScope = this.currentScope.getParent();
        return this.currentScope;
    }
    
    public Scope currentScope() {
        return this.currentScope;
    }
    
    public String name() {
        return this.name;
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
