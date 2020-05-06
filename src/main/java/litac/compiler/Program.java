/*
 * see license.txt
 */
package litac.compiler;

import java.util.*;

import litac.ast.*;
import litac.checker.TypeInfo;
import litac.compiler.Module;

/**
 * Represents the program in Modules/Symbols
 */
public class Program {

    private Module main;
    private Map<ModuleId, Module> modules;
    private List<Symbol> symbols;
    private Map<TypeSpec, TypeInfo> resolvedTypeMap;

    public Program(Module main, 
                   Map<ModuleId, Module> modules, 
                   List<Symbol> symbols,
                   Map<TypeSpec, TypeInfo> resolvedTypeMap) {
        this.main = main;
        this.modules = modules;
        this.symbols = symbols;
        this.resolvedTypeMap = resolvedTypeMap;
    }
    
    public boolean isMainModule(Module module) {
        return module.getId().equals(this.main.getId());
    }
    
    public Module getModule(ModuleId module) {
        return this.modules.get(module);
    }
    
    public List<Module> getModules() {
        return new ArrayList<>(this.modules.values());
    }
    
    public Module getMainModule() {
        return this.main;
    }
    
    public List<Symbol> getSymbols() {
        return symbols;
    }
    
    public Map<TypeSpec, TypeInfo> getResolvedTypeMap() {
        return resolvedTypeMap;
    }
}
