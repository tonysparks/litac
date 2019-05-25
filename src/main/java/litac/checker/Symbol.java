/*
 * see license.txt
 */
package litac.checker;

import litac.ast.Decl;

/**
 * @author Tony
 *
 */
public class Symbol {

    public static final int IS_LOCAL    = (1<<1);
    public static final int IS_FOREIGN  = (1<<2);
    public static final int IS_CONSTANT = (1<<3);
    
    public final Decl decl;
    public final String name;
    public final TypeInfo type;
    public final Module declared;
    //public final boolean isLocal;
    private final int flags;
    
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
}
