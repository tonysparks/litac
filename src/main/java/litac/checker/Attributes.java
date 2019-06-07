/*
 * see license.txt
 */
package litac.checker;

import java.util.List;

import litac.ast.Stmt.NoteStmt;

/**
 * @author Tony
 *
 */
public class Attributes {
    
    /**
     * Visibility (should be exported)
     */
    public boolean isPublic;    
    
    /**
     * Global variable
     */
    public boolean isGlobal;
    
    
    /**
     * Any notes about a declaration
     */
    public List<NoteStmt> notes;
    
    public static final int USING_MODIFIER = (1 << 1);
    public static final int CONST_MODIFIER = (1 << 2);
    
    public int modifiers;
    
    public boolean isConst() {
        return (this.modifiers & CONST_MODIFIER) > 0;
    }
    
    public boolean isUsing() {
        return (this.modifiers & USING_MODIFIER) > 0;
    }
    
    public boolean isForeign() {
        return this.notes != null && this.notes.stream().anyMatch(n -> n.note.name.equals("foreign"));
    }
}
