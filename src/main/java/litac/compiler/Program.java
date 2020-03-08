/*
 * see license.txt
 */
package litac.compiler;

import java.util.*;

import litac.ast.TypeSpec;
import litac.checker.TypeInfo;
import litac.compiler.Module;

/**
 * Represents the program in Modules/Symbols
 */
public class Program {

    private Module main;
    private Map<String, Module> modules;
    private List<Symbol> symbols;
    private Map<TypeSpec, TypeInfo> resolvedTypeMap;

    public Program(Module main, 
                   Map<String, Module> modules, 
                   List<Symbol> symbols,
                   Map<TypeSpec, TypeInfo> resolvedTypeMap) {
        this.main = main;
        this.modules = modules;
        this.symbols = symbols;
        this.resolvedTypeMap = resolvedTypeMap;
    }
    
    public Module getModule(String name) {
        return this.modules.get(name);
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
