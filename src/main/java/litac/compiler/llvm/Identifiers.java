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
public class Identifiers {

    private LinkedList<String> temp;
    private int index;
    
    /**
     * 
     */
    public Identifiers() {
        this.temp = new LinkedList<>();
    }
    
    public String get(int index) {
        return this.temp.get(index);
    }
    
    public void alloc(int n) {
        while(n --> 0) {
            alloc();
        }
    }
    
    public String alloc() {
        String name = "%" + this.index++;
        this.temp.add(name);
        return name;
    }
    
    public String peek() {
        return this.temp.peekLast();
    }
    
    public String dealloc() {
        return this.temp.pollLast();
    }

}
