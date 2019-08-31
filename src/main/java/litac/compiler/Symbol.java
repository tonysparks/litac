/*
 * see license.txt
 */
package litac.compiler;

import litac.ast.Decl;
import litac.checker.TypeInfo;
import litac.checker.TypeInfo.TypeKind;

/**
 * @author Tony
 *
 */
public class Symbol {

    public static enum SymbolKind {
        TYPE,
        VAR,
        CONST,
        FUNC,
    }
    
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
    public static final int IS_GENERIC_TEMPLATE = (1<<7);
    
    public SymbolKind kind;
    public ResolveState state;
    public Decl decl;
    public final String name;
    
    /** the module in which this symbol is defined in */
    public Module declared;
    
    /** if this symbol is from a generic type, this module is where the generic
     *  type is defined
     */
    public Module genericDeclaration;
    
    public TypeInfo type;
    private      int flags;
    
    public Symbol(SymbolKind kind, 
                  Decl decl, 
                  String name,
                  Module declared, 
                  int flags) {
        
        this.kind = kind;
        this.decl = decl;
        this.name = name;
        this.declared = declared;
        this.flags = flags;
        this.state = ResolveState.UNRESOLVED;
    }
    
    public boolean isKind(TypeKind kind) {
        if(this.type == null) {
            return false;
        }
        
        return this.type.isKind(kind);
    }
    
    public TypeKind getKind() {
        if(this.type == null) {
            return TypeKind.Void;
        }
        
        return this.type.getKind();
    }
    
    /**
     * @return the type
     */
    public TypeInfo getType() {
        return type;
    }
    
    /**
     * @return true if this symbol is a generic template
     */
    public boolean isGenericTemplate() {
        return (this.flags & IS_GENERIC_TEMPLATE) > 0;
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
    
    public void markAsGenericTemplate() {
        this.flags |= IS_GENERIC_TEMPLATE;
    }
    
    /**
     * Marks the symbol as completed by definition (incomplete types are
     * defined as globals that need to be eventually resolved)
     */
    public void markComplete(TypeInfo type) {        
        this.flags &= ~IS_INCOMPLETE;
        this.type = type;
    }
    
    public Module getDeclaredModule() {
        if(this.genericDeclaration != null) {
            return this.genericDeclaration;
        }
        
        return this.declared;
    }
}
