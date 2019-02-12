/*
 * see license.txt
 */
package litac.compiler.llvm;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Tony
 *
 */
public class LLVMScope {

    private Queue<Identifiers> identifiers;
    private Queue<Labels> labels;
    
    public LLVMScope() {
        this.identifiers = new LinkedList<>();
        this.labels = new LinkedList<>();
    }
    
    public Identifiers peekIdentifiers() {
        return this.identifiers.peek();
    }
    
    public Labels peekLabels() {
        return this.labels.peek();
    }
    
    public void enter() {
        this.identifiers.add(new Identifiers());
        this.labels.add(new Labels());
    }
    
    public void leave() {
        this.identifiers.poll();
        this.labels.poll();
    }

}
