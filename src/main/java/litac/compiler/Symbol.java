/*
 * see license.txt
 */
package litac.compiler;

import litac.ast.Decl;
import litac.checker.TypeInfo;

/**
 * @author Tony
 *
 */
public class Symbol {

    public static enum ResolveState {
        UNRESOLVED,
        RESOLVING,
        RESOLVED,
    }
    
    public static final int IS_LOCAL    = (1<<1);
    public static final int IS_FOREIGN  = (1<<2);
    public static final int IS_CONSTANT = (1<<3);
    public static final int IS_USING    = (1<<4);
    public static final int IS_TYPE     = (1<<5);
    public static final int IS_INCOMPLETE = (1<<6);
    
    public ResolveState state;
    public final Decl decl;
    public final String name;
    
    /** the module in which this symbol is defined in */
    public Module declared;
    
    /** if this symbol is from a generic type, this module is where the generic
     *  type is defined
     */
    public Module genericDeclaration;
    
    private      TypeInfo type;
    private      int flags;
    
    public Symbol(Decl decl, 
                  String name,
                  TypeInfo type, 
                  Module declared, 
                  int flags) {
        
        this.decl = decl;
        this.name = name;
        this.type = type;
        this.declared = declared;
        this.flags = flags;
        this.state = ResolveState.UNRESOLVED;
    }
    
    /**
     * @return the type
     */
    public TypeInfo getType() {
        return type;
    }
    
    /**
     * @return true if this symbol is a local symbol (not module scoped)
     */
    public boolean isLocal() {
        return (this.flags & IS_LOCAL) > 0;
    }
    
    /**
     * @return true if this symbol is a foreign symbol (native C type)
     */
    public boolean isForeign() {
        return (this.flags & IS_FOREIGN) > 0;
    }
    
    /**
     * @return true if this symbol was defined as a constant
     */
    public boolean isConstant() {
        return (this.flags & IS_CONSTANT) > 0;
    }
    
    /**
     * @return true if this symbol was defined from a using modifier
     */
    public boolean isUsing() {
        return (this.flags & IS_USING) > 0;
    }
    
    /**
     * @return if this symbol represents a type
     */
    public boolean isType() {
        return (this.flags & IS_TYPE) > 0;
    }
    
    /**
     * @return if this symbol is incomplete in its definition
     */
    public boolean isIncomplete() {
        return (this.flags & IS_INCOMPLETE) > 0;
    }
    
    /**
     * Removes the foreign designation
     */
    public void removeForeign() {
        this.flags &= ~IS_FOREIGN;
    }
    
    
    /**
     * Marks the symbol as completed by definition (incomplete types are
     * defined as globals that need to be eventually resolved)
     */
    public void markComplete() {        
        this.flags &= ~IS_INCOMPLETE;
        this.type = this.decl.type;
    }
    
    public Module getDeclaredModule() {
        if(this.genericDeclaration != null) {
            return this.genericDeclaration;
        }
        
        return this.declared;
    }
}
