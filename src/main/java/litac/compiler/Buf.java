/*
 * see license.txt
 */
package litac.compiler;

/**
 * @author Tony
 *
 */
public class Buf {

    private StringBuilder sb;
    private int indent;
    private final String tabSpaces;
    private final boolean useTabs;
    
    public Buf(String tabSpaces, boolean useTabs) {
        this.tabSpaces = tabSpaces;
        this.useTabs = useTabs;
        
        this.sb = new StringBuilder();
        this.indent = 0;
    }
    
    @Override
    public String toString() {
        return this.sb.toString();
    }
    
    public Buf appendRaw(String str) {
        this.sb.append(str);
        return this;
    }
    
    public Buf outln() {
        this.sb.append("\n");
        for(int i = 0; i < this.indent; i++) {
            if(this.useTabs) {
                this.sb.append("\t");
            }
            else {
                this.sb.append(this.tabSpaces);
            }
        }
        
        return this;
    }
    
    public Buf out(String message, Object ... args) {
        String str = String.format(message, args);
        for(int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if(c == '\n') {
                outln();
            }
            else if(c == '{') {
                sb.append(c);
                indent();
                outln();
            }
            else if(c == '}') {
                unindent();
                outln();
                sb.append(c);
            }
            else {
                sb.append(c);
            }           
        }
        
        return this;
    }
    
    public Buf indent() {
        this.indent++;
        return this;
    }
    
    public Buf unindent() {
        this.indent--;
        return this;
    }

}
