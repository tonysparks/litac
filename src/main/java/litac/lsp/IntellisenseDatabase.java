/*
 * see license.txt
 */
package litac.lsp;

import java.util.*;

import litac.ast.*;
import litac.ast.Stmt.ModuleStmt;
import litac.compiler.Symbol;
import litac.lsp.IntellisenseScope.ScopeType;
import litac.lsp.JsonRpc.*;

/**
 * @author Tony
 *
 */
public class IntellisenseDatabase {

    static class ScopeComparator implements Comparator<IntellisenseScope> {
        
        @Override
        public int compare(IntellisenseScope a, IntellisenseScope b) {
            return 0;
        }
    }
    
    private Map<ModuleId, List<IntellisenseScope>> scopes;
    
    private List<IntellisenseScope> currentModuleScopes;
    private IntellisenseScope currentScope;
    private IntellisenseScope globalScope;
    
    public IntellisenseDatabase() {
        this.scopes = new HashMap<>();
    }

    public void clear() {
        this.scopes.clear();
    }
    
    public void beginModule(ModuleStmt module) {
        this.currentModuleScopes = new ArrayList<>();
        this.scopes.put(module.id, this.currentModuleScopes);
        
        this.globalScope = new IntellisenseScope(null, module.getSrcPos(), module.getEndSrcPos(), ScopeType.MODULE);
        this.currentScope = this.globalScope;
    }
    
    public void endModule() {        
    }
    
    public void beginScope(Node node) {
        this.currentScope = new IntellisenseScope(this.currentScope, node.getSrcPos(), node.getEndSrcPos(), ScopeType.FUNC);
        this.currentModuleScopes.add(this.currentScope);
    }
    
    public void addSymbol(Symbol sym) {        
        this.currentScope.addSymbol(sym);        
    }
    
    public void popScope() {
        IntellisenseScope parent = this.currentScope.getParentScope();
        if(parent != null) {
            this.currentScope = parent;
        }
    }
    
    /**
     * @return the scopes
     */
    public Map<ModuleId, List<IntellisenseScope>> getScopes() {
        return scopes;
    }
    
    public IntellisenseScope getScope(ModuleId moduleId, Position pos) {
        IntellisenseScope scope = search(this.scopes.get(moduleId), pos); 
        return scope;
    }
    
    
    private IntellisenseScope search(List<IntellisenseScope> scopes, Position pos) {
        if(scopes == null) return null;
        
        int first = 0;
        int last = scopes.size() - 1;
        int mid = last / 2;
        while(first <= last) {
            IntellisenseScope scope = scopes.get(mid);
            int result = scope.compareTo(pos);
            if(result < 0) {
                last = mid - 1;
            }
            else if(result > 0) {
                first = mid + 1;
            }
            else {
                return scope;
            }
            
            mid = (first + last) / 2;
        }
        
        return null;
    }
    
}
