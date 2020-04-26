/*
 * see license.txt
 */
package litac.lsp;

import java.util.*;

import litac.ast.Node.SrcPos;
import litac.compiler.Symbol;
import litac.lsp.JsonRpc.Position;

/**
 * @author Tony
 *
 */
public class IntellisenseScope {

    public static enum ScopeType {
        MODULE,
        FUNC,
    }
    
    private SrcPos startPosition;
    private SrcPos endPosition;
    
    private List<Symbol> symbols;
    private ScopeType type;
    
    private IntellisenseScope parentScope;
        
    
    public IntellisenseScope(IntellisenseScope parentScope, SrcPos start, SrcPos end, ScopeType type) {
        this.parentScope = parentScope;
        this.startPosition = start;
        this.endPosition = end;
        this.symbols = new ArrayList<>();
        this.type = type;
    }
    
    /**
     * @return the parentScope
     */
    public IntellisenseScope getParentScope() {
        return parentScope;
    }
    
    /**
     * @return the type
     */
    public ScopeType getScopeType() {
        return type;
    }
    
    /**
     * @return the startPosition
     */
    public SrcPos getStartPosition() {
        return startPosition;
    }
    
    /**
     * @return the endPosition
     */
    public SrcPos getEndPosition() {
        return endPosition;
    }
    
    public void addSymbol(Symbol sym) {
        this.symbols.add(sym);
    }
    
    public Symbol findSymbol(String name) {
        int size = this.symbols.size();
        for(int i = 0; i < size; i++) {
            Symbol sym = this.symbols.get(i);
            if(sym.name.equals(name)) {
                return sym;
            }
        }
        
        if(this.parentScope != null) {
            return this.parentScope.findSymbol(name);
        }
        
        return null;
    }
    
    /**
     * @return the symbols
     */
    public List<Symbol> getSymbols() {
        return symbols;
    }
    
    /**
     * @return the all the scopes and parent scopes symbols
     */
    public List<Symbol> getAllSymbols() {
        List<Symbol> allSymbols = new ArrayList<>(this.symbols);
        if(this.parentScope != null) {
            allSymbols.addAll(this.parentScope.getAllSymbols());
        }
        
        return allSymbols;
    }
    
    public int compareTo(Position pos) {
        if(pos.line < this.startPosition.lineNumber) {
            return -1;
        }
        if(pos.line > this.endPosition.lineNumber) {
            return 1;
        }
        
        
        if(pos.line == this.startPosition.lineNumber && pos.character < this.startPosition.position) {
            return -1;
        }
        
        if(pos.line == this.endPosition.lineNumber && pos.character > this.endPosition.position) {
            return 1;
        }
        
        return 0;
    }

}
