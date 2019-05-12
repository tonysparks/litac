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
     * If the declaration has generic types
     */
    public boolean hasGenerics;
    
    /**
     * Any notes about a declaration
     */
    public List<NoteStmt> notes;
}
