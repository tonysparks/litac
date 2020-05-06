/*
 * see license.txt
 */
package litac.compiler;

import java.util.*;

import litac.ast.Decl;
import litac.ast.Decl.*;
import litac.ast.TypeSpec.TypeSpecKind;
import litac.compiler.Symbol.SymbolKind;
import litac.util.Names;
import litac.compiler.Module;

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
    
    public Symbol addSymbol(Module module, Decl decl, String symbolName) {
        return addSymbol(module, decl, symbolName, false);
    }
    
    public Symbol addSymbol(Module module, Decl decl, String symbolName, boolean isConstant) {
        int flags = 0;
        if(isConstant) {
            flags |= Symbol.IS_CONSTANT;
        }
        return addSymbol(module, decl, symbolName, flags);
    }
    
    public Symbol addSymbol(Module module, Decl decl, String symbolName, int flags) {
        if(this.symbols.containsKey(symbolName) && !decl.attributes.hasNote("generated")) {
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
        
        if(decl.attributes.isForeign()) {
            flags |= Symbol.IS_FOREIGN;
            
            // if this is a foreign primitive declaration
            // we want the symbol information associated with this
            // type, so that the CGen knows it is a foreign type
            if(decl.kind == DeclKind.TYPEDEF) {
                TypedefDecl typedefDecl = (TypedefDecl) decl;
                if(typedefDecl.type.kind != TypeSpecKind.FUNC_PTR) {
                    isNewType = true;
                }
            }
        }
        
        if(decl.attributes.isExtern()) {
            flags |= Symbol.IS_EXTERN;
        }
        
        if(isNewType) {
            flags |= Symbol.IS_TYPE;            
        }
        
        SymbolKind kind = SymbolKind.VAR;
        switch(decl.kind) {
            case CONST:
                kind = SymbolKind.CONST;
                flags |= Symbol.IS_CONSTANT;
                break;
            case ENUM:
            case STRUCT:
            case TYPEDEF:
            case UNION:
                kind = SymbolKind.TYPE;
                flags |= Symbol.IS_TYPE;  
                break;
            case FUNC:
                kind = SymbolKind.FUNC;
                break;
            case PARAM:
            case VAR:
            default:
                kind = SymbolKind.VAR;
                break;
        }
                
        Symbol sym = new Symbol(kind, decl, symbolName, module, flags);
        decl.sym = sym;
                
        this.symbols.put(symbolName, sym);
        return sym;
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
