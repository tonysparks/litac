/*
 * see license.txt
 */
package litac.compiler;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import litac.ast.*;
import litac.ast.Decl.*;
import litac.ast.Stmt.*;
import litac.checker.TypeInfo;
import litac.checker.TypeInfo.*;
import litac.compiler.Scope.ScopeType;
import litac.compiler.Symbol.ResolveState;
import litac.util.Names;

/**
 * Keeps track of a modules defined types.
 * 
 * @author Tony
 *
 */
public class Module {

    private Module root;
    private final ModuleId id;
    private final String simpleName;
    private Scope currentScope;
    private Scope moduleScope;
    
    private Map<String, Module> imports;
    private ModuleStmt moduleStmt;
    private List<Module> usingImports;
    
    private Map<String, Symbol> funcTypes;
    private Map<String, Symbol> structTypes;
    private Map<String, Symbol> unionTypes;
    private Map<String, Symbol> enumTypes;
    private Map<String, Symbol> typedefTypes;
    
    private Map<String, Symbol> publicFuncTypes;
    private Map<String, Symbol> publicTypes;
    
    private Map<String, Symbol> importedFuncTypes;
    private Map<String, Symbol> importedAggregateTypes;
    
    private Map<String, Symbol> foreignTypes;
    private Map<String, Symbol> builtins;
    
    private List<NoteStmt> notes;
    
    private Map<String, Symbol> genericTypes;
    
    private PhaseResult result;
    private List<Symbol> symbols;
        
    public Module(Module root,
                  List<Symbol> programSymbols,
                  Map<String, Symbol> genericTypes,
                  PhaseResult result, 
                  ModuleStmt moduleStmt) {
        this.root = root;
        this.result = result;
        this.moduleStmt = moduleStmt;
        this.id = moduleStmt.id;
        
        // normalize the name
        this.simpleName = this.id.fullModuleName
                                    .replaceAll("\\\\", "/")
                                    .replaceAll("/", "__");
        
        this.imports = new HashMap<>();         
        this.funcTypes = new HashMap<>();
        this.structTypes = new HashMap<>();
        this.unionTypes = new HashMap<>();
        this.enumTypes = new HashMap<>();
        this.typedefTypes = new HashMap<>();
        
        this.publicFuncTypes = new HashMap<>();
        this.publicTypes = new HashMap<>();
        
        this.foreignTypes = new HashMap<>();
        this.builtins = new HashMap<>();
        
        this.importedFuncTypes = new HashMap<>();
        this.importedAggregateTypes = new HashMap<>();
        
        this.usingImports = new ArrayList<>();
        
        this.genericTypes = genericTypes;
        this.notes = new ArrayList<>();
        
        this.symbols = programSymbols;
        
        this.moduleScope = new Scope(result, ScopeType.MODULE);
        this.currentScope = this.moduleScope;
    }
    
    private void addSymbol(Symbol sym) {        
        if(!this.symbols.stream()
                        .anyMatch(s -> {                            
                            return sym.name.equals(s.name) && (sym.decl == s.decl || s.isBuiltin());
                        })) {
            this.symbols.add(sym);
        }
    }

    @Override
    public String toString() {
        return this.id.fullModuleName;
    }
    
    public Module getRoot() {
        return root != null ? root : this;
    }
    
    /**
     * @return the symbols
     */
    public List<Symbol> getSymbols() {        
        return this.symbols;
    }
    
    /**
     * @return the moduleScope
     */
    public Scope getModuleScope() {
        return moduleScope;
    }
    
    public PhaseResult getPhaseResult() {
        return result;
    }
    
    public ModuleStmt getModuleStmt() {
        return moduleStmt;
    }
    
    public Collection<Module> getImports() {
        return imports.values();
    }
    
    public Collection<Symbol> getDeclaredFuncs() {
        return funcTypes.values();
    }
    
    public Collection<Symbol> getDeclaredTypes() {
        Collection<Symbol> result = new ArrayList<>(); 
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
    
    public void importModule(ImportStmt stmt, Module module, String alias) {
        if(module == null) {
            this.result.addError(stmt, "unable to import module '%s' ", alias);
            return;
        }
                
        if(alias != null) {
            this.imports.put(alias, module);
                   
            for(Entry<String, Symbol> funcType: module.publicFuncTypes.entrySet()) {
                this.importedFuncTypes.put(Names.litaName(alias, funcType.getKey()), funcType.getValue());
            }
            
            for(Entry<String, Symbol> aggType: module.publicTypes.entrySet()) {
                this.importedAggregateTypes.put(Names.litaName(alias, aggType.getKey()), aggType.getValue());
            }
            
            
            for(Entry<String, Symbol> typeEntry: module.foreignTypes.entrySet()) {
                Symbol sym = typeEntry.getValue();
                if(sym.isTypeKind(TypeKind.Func)) {
                    this.importedFuncTypes.put(Names.litaName(alias, typeEntry.getKey()), sym);
                }
                else {
                    this.importedAggregateTypes.put(Names.litaName(alias, typeEntry.getKey()), sym);
                }
            }
            

            module.currentScope().getSymbols()
                                 .stream()
                                 .filter(s -> s.decl.attributes.isPublic &&
                                              module.isSymbolOwned(s))
                                 .forEach(s -> currentScope().addSymbol(alias, s));
        }
        else {
            this.imports.put(module.id.fullModuleName, module);
            
            for(Entry<String, Symbol> funcType: module.publicFuncTypes.entrySet()) {
                this.funcTypes.put(funcType.getKey(), funcType.getValue());
            }
            
            for(Entry<String, Symbol> aggType: module.publicTypes.entrySet()) {
                Symbol type = aggType.getValue();
                switch(type.getTypeKind()) {
                    case Union: {
                        this.unionTypes.put(aggType.getKey(), type);
                        break;
                    }
                    case Struct: {
                        this.structTypes.put(aggType.getKey(), type);
                        break;
                    }
                    case Enum: {
                        this.enumTypes.put(aggType.getKey(), type);
                        break;
                    }                        
                    default: {
                        this.typedefTypes.put(aggType.getKey(), type);
                    }
                }                
            }
            
            module.currentScope().getSymbols()
                                 .stream()
                                 .filter(s -> s.decl.attributes.isPublic && 
                                              module.isSymbolOwned(s))
                                 .forEach(s -> currentScope().addSymbol(s));
            
            for(Entry<String, Symbol> typeEntry: module.foreignTypes.entrySet()) {
                Symbol type = typeEntry.getValue();
                if(type.isTypeKind(TypeKind.Func)) {
                    this.importedFuncTypes.put(Names.litaName(alias, typeEntry.getKey()), type);
                }
                else {
                    this.importedAggregateTypes.put(Names.litaName(alias, typeEntry.getKey()), type);
                }
            }
        }
        
        if(stmt.isUsing) {
            this.usingImports.add(module);
            
            for(Entry<String, Symbol> funcType: module.publicFuncTypes.entrySet()) {
                this.publicFuncTypes.put(funcType.getKey(), funcType.getValue());
            }
            
            for(Entry<String, Symbol> aggType: module.publicTypes.entrySet()) {
                this.publicTypes.put(aggType.getKey(), aggType.getValue());
            }
        }
        
        this.foreignTypes.putAll(module.foreignTypes);
        this.notes.addAll(module.notes);
    }
    
    
    /**
     * @param sym
     * @return true if the supplied symbol is owned by this module
     */
    public boolean isSymbolOwned(Symbol sym) {
        return sym.declared == this || this.usingImports.stream().anyMatch(m -> m.isSymbolOwned(sym));
    }
    
    public List<NoteStmt> getNotes() {
        return notes;
    }
    
    public Module getModule(String name) {
        return this.imports.get(name);
    }
    
    private boolean isForeign(Decl decl) {
        return decl.attributes.isForeign();
    }
    
    private Symbol addPublicDecl(Decl decl, String name) {
        Symbol sym = this.currentScope.addSymbol(this, decl, name);
        addSymbol(sym);
        
        if(decl.attributes.isPublic) {
            if(decl.kind == DeclKind.FUNC) {                                
                this.publicFuncTypes.put(name, sym);
            }
            else {
                this.publicTypes.put(name, sym);
            }
            
            if(isForeign(decl)) {
                this.foreignTypes.put(name, sym);
            }
        }
        
        return sym;
    }
    
    public void declareNote(NoteStmt stmt) {
        this.notes.add(stmt);
    }
    
    public Symbol declareFunc(FuncDecl stmt, String funcName) {
        if(this.funcTypes.containsKey(funcName)) {
            this.result.addError(stmt, "%s function is already defined", funcName);
            return this.funcTypes.get(funcName);
        }
        
        Symbol sym = addPublicDecl(stmt, funcName);        
        this.funcTypes.put(funcName, sym);        
        
        return sym;
    }
    
    public Symbol declareStruct(StructDecl stmt, String structName) {
        if(this.structTypes.containsKey(structName)) {
            this.result.addError(stmt, "%s struct is already defined", structName);
            return this.structTypes.get(structName);
        }
        
        if(this.unionTypes.containsKey(structName)) {
            this.result.addError(stmt, "%s union is already defined with the same name", structName);
            return this.unionTypes.get(structName);
        }
        
        if(this.enumTypes.containsKey(structName)) {
            this.result.addError(stmt, "%s enum is already defined with the same name", structName);
            return this.enumTypes.get(structName);
        }
        
        
        Symbol sym = addPublicDecl(stmt, structName);
        this.structTypes.put(structName, sym);
        
        return sym;
    }
    
    public Symbol declareUnion(UnionDecl stmt, String unionName) {
        if(this.unionTypes.containsKey(unionName)) {
            this.result.addError(stmt, "%s union is already defined", unionName);
            return this.unionTypes.get(unionName);
        }
        
        if(this.structTypes.containsKey(unionName)) {
            this.result.addError(stmt, "%s struct is already defined with the same name", unionName);
            return this.structTypes.get(unionName);
        }
        
        
        if(this.enumTypes.containsKey(unionName)) {
            this.result.addError(stmt, "%s enum is already defined with the same name", unionName);
            return this.enumTypes.get(unionName);
        }
        
        Symbol sym = addPublicDecl(stmt, unionName);
        this.unionTypes.put(unionName, sym);
        
        return sym;        
    }
    
    public Symbol declareEnum(EnumDecl stmt, String enumName) {
        if(this.enumTypes.containsKey(enumName)) {
            this.result.addError(stmt, "%s enum is already defined", enumName);
            return this.enumTypes.get(enumName);
        }
        
        if(this.unionTypes.containsKey(enumName)) {
            this.result.addError(stmt, "%s union is already defined with the same name", enumName);
            return this.unionTypes.get(enumName);
        }
        
        if(this.structTypes.containsKey(enumName)) {
            this.result.addError(stmt, "%s struct is already defined with the same name", enumName);
            return this.structTypes.get(enumName);
        }
        
        
        Symbol sym = addPublicDecl(stmt, enumName);
        this.enumTypes.put(enumName, sym);
        
        return sym;
    }
    
    public Symbol declareTypedef(TypedefDecl stmt, String alias) {
        Symbol previousType = getType(alias);
        if(previousType != null) {
            this.result.addError(stmt, "%s is already defined", alias);
            return previousType;
        }

        Symbol sym = addPublicDecl(stmt, alias);        
        this.typedefTypes.put(alias, sym);
        
        return sym;
    }
    
    public Symbol addIncomplete(Decl decl) {
        if(!decl.attributes.isGlobal) {
            return null;
        }
        
        Symbol sym = this.currentScope.getSymbol(decl.name); 
        if(sym != null && !decl.attributes.hasNote("generated")) {
            this.result.addError(decl, "%s is already defined", decl.name);
            return null;
        }
        
        Symbol newSym = this.currentScope.addSymbol(this, decl, decl.name, Symbol.IS_INCOMPLETE);
        addSymbol(newSym);
        
        return newSym;
    }
    
    public Symbol addBuiltin(TypeInfo type) {
        Symbol newSym = this.currentScope.addSymbol(this, new NativeDecl(type), type.name);
        newSym.state = ResolveState.RESOLVED;
        newSym.type = type;
        newSym.markAsBuiltin();
        type.sym = newSym;
        
        this.builtins.put(type.name, newSym);
        
        addSymbol(newSym);        
        return newSym;
    }
    
    public Symbol getFuncType(String funcName) {
        if(funcName.contains("::")) {
            return this.importedFuncTypes.get(funcName);
        }
        
        if(this.funcTypes.containsKey(funcName)) {
            return this.funcTypes.get(funcName);
        }
        
        if(this.typedefTypes.containsKey(funcName)) {
            Symbol type = this.typedefTypes.get(funcName);
            if(type.isTypeKind(TypeKind.Func)) {
                return type;
            }
        }
        
        return null;        
    }
    
    public List<Symbol> getMethodsFor(AggregateTypeInfo recv) {
        String methodNamePrefix = recv.name + "_";
        return this.funcTypes.entrySet().stream()
            .filter(e -> e.getKey().startsWith(methodNamePrefix))
            .filter(e -> {
                FuncTypeInfo funcInfo = e.getValue().type.as();
                if(funcInfo.parameterDecls.isEmpty()) {
                    return false;                   
                }
                
                TypeInfo arg = TypeInfo.getBase(funcInfo.parameterDecls.get(0).type);
                return arg.strictEquals(recv);
            })
            .map(e -> e.getValue())
            .collect(Collectors.toList());            
    }
    
    public Symbol getMethodType(AggregateTypeInfo recv, String methodName) {               
        Symbol funcSym = getMethodTypeFromReceiver(recv, methodName);
        
        // if the method wasn't found on the top level, we should look at 
        // any using fields to determine if this a method call on one of the using fields
        if(funcSym == null) {            
            funcSym = getMethodTypeFromUsing(recv, methodName);
        }
        
        return funcSym;
    }
    
    private Symbol getMethodTypeFromUsing(AggregateTypeInfo aggInfo, String methodName) {
        if(!aggInfo.hasUsingFields()) {
            return null;
        }
        
        Symbol funcSym = null;
        for(FieldInfo field : aggInfo.usingInfos) {
            funcSym = getMethodTypeFromReceiver(field.type, methodName);
            
            // we found it!
            if(funcSym != null) {
                return funcSym;
            }
        }
        
        for(FieldInfo field : aggInfo.usingInfos) {
            funcSym = getMethodTypeFromUsing(TypeInfo.getBase(field.type).as(), methodName);
            
            // we found it!
            if(funcSym != null) {
                return funcSym;
            }
        }
        
        return funcSym;
    }
    
    private Symbol getMethodTypeFromReceiver(TypeInfo recv, String methodName) {
        
        String funcName = FuncTypeInfo.getMethodName(recv, methodName);
        Symbol funcSym = null;

        // First try and see if the receiver declaration module has
        // the method
        Symbol sym = recv.sym;
        if(sym != null) {
            funcSym = sym.getDeclaredModule().getFuncType(funcName);
        }
        
        // if the receiver module doesn't have the method, we look
        // in the current scope
        if(funcSym == null) {
            funcSym = getFuncType(funcName);
        }
        
        return funcSym;
    }
    
    public Symbol getType(String typeName) {
        if(typeName.contains("::")) {
            if (this.importedAggregateTypes.containsKey(typeName)) {
                return this.importedAggregateTypes.get(typeName);
            }
            
            if (this.importedFuncTypes.containsKey(typeName)) {
                return this.importedFuncTypes.get(typeName);
            }
        }
        
        if(this.genericTypes.containsKey(typeName)) {
            return this.genericTypes.get(typeName);
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
        
        if(this.funcTypes.containsKey(typeName)) {
            return this.funcTypes.get(typeName);
        }
        
        if(this.typedefTypes.containsKey(typeName)) {
            return this.typedefTypes.get(typeName);
        }
        
        return this.builtins.get(typeName);
    }
    
    public Symbol getAliasedType(String typeName) {
        return this.typedefTypes.get(typeName);
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
    
    /**
     * @return the id
     */
    public ModuleId getId() {
        return id;
    }
    
    public String simpleName() {
        return this.simpleName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        if (id == null) {
            if (other.id != null)
                return false;
        }
        else if (!id.equals(other.id))
            return false;
        return true;
    }
    
}
