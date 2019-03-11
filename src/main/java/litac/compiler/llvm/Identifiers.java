/*
 * see license.txt
 */
package litac.compiler.llvm;

import java.util.LinkedList;

/**
 * @author Tony
 *
 */
public class Identifiers {

    public static class Identifier {
        public String name;
        public String llvmName;
        public int index;
        
        /**
         * @param name
         * @param llvmName
         * @param index
         */
        public Identifier(String name, String llvmName, int index) {
            this.name = name;
            this.llvmName = llvmName;
            this.index = index;
        }
        
        
    }
    
    private LinkedList<Identifier> temp;
    private int index;
    
    /**
     * 
     */
    public Identifiers() {
        this.temp = new LinkedList<>();
    }
    
    public Identifier getByIndex(int index) {
        return this.temp.get(index);
    }
    
    public Identifier getByName(String idName) {
        for(Identifier id : this.temp) {
            if(id.name.equals(idName)) {
                return id;
            }
        }
        
        return null;
    }
    
    
    public void alloc(int n) {
        while(n --> 0) {
            alloc();
        }
    }
    
    public Identifier alloc(String idName) {
        String llvmName = "%" + this.index;
        Identifier id = new Identifier(idName, llvmName, this.index);
        
        this.temp.add(id);
        this.index++;
        return id;
    }
    
    public Identifier alloc() {
        return alloc("");
    }
    
    public Identifier peek() {
        return this.temp.peekLast();
    }
    
    public Identifier dealloc() {
        return this.temp.pollLast();
    }

}
