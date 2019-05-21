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
}
