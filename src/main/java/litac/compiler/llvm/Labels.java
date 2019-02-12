/*
 * see license.txt
 */
package litac.compiler.llvm;

import java.util.LinkedList;

/**
 * @author Tony
 *
 */
public class Labels {

    private LinkedList<String> labels;
    private int index;
    
    
    public Labels() {
        this.labels = new LinkedList<>();
    }
    
    public String label(String prefix) {
        String name = prefix + (this.index++);
        this.labels.add(name);
        
        return name;
    }
    
    public String label() {
        return label("");
    }

}
