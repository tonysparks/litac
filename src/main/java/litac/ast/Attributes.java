/*
 * see license.txt
 */
package litac.ast;

import java.util.ArrayList;
import java.util.List;

import litac.ast.Node.SrcPos;
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
    
    public int modifiers;
    public SrcPos srcPos;
        
    public boolean isUsing() {
        return (this.modifiers & USING_MODIFIER) > 0;
    }
    
    public boolean isForeign() {
        return hasNote("foreign");
    }
    
    public boolean isTest() {
        return hasNote("test");
    }
    
    public boolean hasNote(String note) {
        return this.notes != null && this.notes.stream().anyMatch(n -> n.name.equals(note));
    }
    
    public NoteStmt getNote(String name) {
        if(this.notes == null) {
            return null;
        }
        
        return this.notes.stream()
                    .filter(n -> n.name.equals(name))
                    .findFirst()
                    .orElse(null);
    }
    
    
    public void addNote(NoteStmt note) {
        if(this.notes == null) {
            this.notes = new ArrayList<>();
        }
        
        this.notes.add(note);
    }
    
    public void addNotes(List<NoteStmt> notes) {
        if(notes != null) {
            if(this.notes == null) {
                this.notes = new ArrayList<>();
            }
            this.notes.addAll(notes);
        }
    }
}
