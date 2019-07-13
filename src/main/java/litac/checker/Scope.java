/*
 * see license.txt
 */
package litac.checker;

import java.util.*;

import litac.ast.Decl;
import litac.ast.Decl.*;
import litac.checker.TypeInfo.TypeKind;
import litac.util.Names;

/**
 * @author Tony
 *
 */
public class Scope {
    
    public static enum ScopeType {
        MODULE,
        LOCAL
    }
        
    private Scope parent;
    private ScopeType type;
    private PhaseResult result;
    
    private Map<String, Symbol> symbols;    
    
    public Scope(PhaseResult result, ScopeType type) {
        this(result, type, null);
    }
    
    public Scope(PhaseResult result, ScopeType type, Scope parent) {
        this.result = result;
        this.type = type;
        this.parent = parent;
        
        this.symbols = new HashMap<>();
    }
    
    public Scope getParent() {
        return this.parent;
    }
    
    public Symbol addSymbol(Module module, Decl decl, String symbolName, TypeInfo type) {
        return addSymbol(module, decl, symbolName, type, false);
    }
    
    public Symbol addSymbol(Module module, Decl decl, String symbolName, TypeInfo type, boolean isConstant) {
        int flags = 0;
        if(isConstant) {
            flags |= Symbol.IS_CONSTANT;
        }
        return addSymbol(module, decl, symbolName, type, flags);
    }
    
    public Symbol addSymbol(Module module, Decl decl, String symbolName, TypeInfo type, int flags) {
        if(this.symbols.containsKey(symbolName)) {
            this.result.addError(decl, "symbol '%s' already defined", symbolName);
        }
        
        boolean isNewType = !(decl instanceof VarDecl) && 
                            !(decl instanceof ConstDecl) &&
                            !(decl instanceof TypedefDecl) &&
                            !(decl instanceof ParameterDecl); 
        
        if(this.type.equals(ScopeType.LOCAL)) {
            if(!isNewType) {
                flags |= Symbol.IS_LOCAL;
            }
        }
        
        if(isForeign(decl)) {
            flags |= Symbol.IS_FOREIGN;
        }
        
        if(isNewType) {
            flags |= Symbol.IS_TYPE;
        }
        
        Symbol sym = new Symbol(decl, symbolName, type, module, flags);
        decl.sym = sym;
        
        if(isNewType) {
            type.sym = sym;            
        }
        
        if(decl instanceof ParameterDecl && type.isKind(TypeKind.FuncPtr)) {
            type.name = symbolName;
        }
        
        this.symbols.put(symbolName, sym);
        
        return sym;
    }
        

    private boolean isForeign(Decl d) {
        return d.attributes.isForeign();
    }
    
    
    public Symbol getSymbol(String varName) {
        if(this.symbols.containsKey(varName)) {
            return this.symbols.get(varName);
        }
        
        if(this.parent != null) {
            return this.parent.getSymbol(varName);
        }
        
        return null;
    }
    
    public void addSymbol(String moduleAlias, Symbol sym) {
        String symbolName = Names.litaName(moduleAlias, sym.name);
        if(this.symbols.containsKey(symbolName)) {
            this.result.addError(sym.decl, "symbol '%s' already defined", symbolName);
            return;            
        }
        
        this.symbols.put(symbolName, sym);
    }
    
    public void addSymbol(Symbol sym) {
        String symbolName = sym.name;
        if(this.symbols.containsKey(symbolName)) {
            this.result.addError(sym.decl, "symbol '%s' already defined", symbolName);
            return;            
        }
        
        this.symbols.put(sym.name, sym);
    }
    
    public Collection<Symbol> getSymbols() {
        return this.symbols.values();
    }
        
    public Scope pushLocalScope() {
        return new Scope(this.result, ScopeType.LOCAL, this);
    }
      
}
