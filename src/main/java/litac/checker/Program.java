/*
 * see license.txt
 */
package litac.checker;

import java.util.*;

/**
 * Represents the program in Modules/Symbols
 */
public class Program {

    private Module main;
    private Map<String, Module> modules;
    private List<Symbol> symbols;

    public Program(Module main, 
                   Map<String, Module> modules, 
                   List<Symbol> symbols) {
        this.main = main;
        this.modules = modules;
        this.symbols = symbols;
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
}
