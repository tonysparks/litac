/*
 * see license.txt
 */
package litac.checker;

import java.util.HashMap;
import java.util.Map;

import litac.ast.Decl;
import litac.ast.Decl.ConstDecl;
import litac.ast.Decl.VarDecl;
import litac.ast.Stmt.NoteStmt;

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
        if(this.symbols.containsKey(symbolName)) {
            this.result.addError(decl, "symbol '%s' already defined", symbolName);
        }
                
        int flags = 0;
        if(this.type.equals(ScopeType.LOCAL)) {
            flags |= Symbol.IS_LOCAL;
        }
        
        if(isForeign(decl)) {
            flags |= Symbol.IS_FOREIGN;
        }
        
        Symbol sym = new Symbol(decl, type, module, flags);
        decl.sym = sym;
        
        if(!(decl instanceof VarDecl) && 
           !(decl instanceof ConstDecl)) {
            type.sym = sym;
        }
        
        this.symbols.put(symbolName, sym);
        
        return sym;
    }
        

    private boolean isForeign(Decl d) {
        boolean isForeign = false;
        if(d.attributes.notes != null) {
            for(NoteStmt n : d.attributes.notes) {
                if(n.note.name.equalsIgnoreCase("foreign")) {
                    isForeign = true;
                }
            }
        }
        
        return isForeign;            
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
    
    
    /**
     * Updates an already defined variable, ensures the type match.
     * 
     * @param stmt
     * @param varName
     * @param type
     */
//    public void updateVariable(Stmt stmt, String varName, TypeInfo type) {
//        Symbol definedType = getSymbol(varName);
//        if(definedType == null) {
//            this.result.addError(stmt, "'%s' has not been declared", varName);
//            return;
//        }
//        
//        if(!definedType.type.canCastTo(type)) {
//            this.result.addError(stmt, "'%s' of type '%s' can't be assigned to type '%s'", varName, definedType, type);
//            return;
//        }
//    }
        
    public Scope pushLocalScope() {
        return new Scope(this.result, ScopeType.LOCAL, this);
    }
      
}
