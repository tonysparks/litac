/*
 * see license.txt
 */
package litac.compiler;

import java.util.*;

/**
 * @author Tony
 *
 */
public class Buf {

    private static final Map<Character, String> escapeChars = new HashMap<>();
    static {
        //escapeChars.put('\a', "\\a");
        escapeChars.put('\b', "\\b");
        //escapeChars.put('\e', "\\e");
        escapeChars.put('\f', "\\f");
        escapeChars.put('\n', "\\n");
        escapeChars.put('\r', "\\r");
        escapeChars.put('\t', "\\t");
        //escapeChars.put('\v', "\\v");
        escapeChars.put('\\', "\\\\");
        escapeChars.put('\'', "\\'");
        escapeChars.put('\"', "\\\"");
        //escapeChars.put('\?', "\\?");
        escapeChars.put('\0', "\\0");
    }
    
    private StringBuilder sb;
    private int indent;
    private final String tabSpaces;
    private final boolean useTabs;
    private final int indentWidth;
    
    private StringBuilder formatSb;
    private Formatter formatter;
    
    public Buf(int indentWidth, boolean useTabs) {
        String tabSpaces = "";
        for(int i = 0; i < indentWidth; i++) {
            tabSpaces += " ";
        }
        
        this.tabSpaces = tabSpaces;
        this.useTabs = useTabs;
        this.indentWidth = indentWidth;
        
        this.formatSb = new StringBuilder();
        this.formatter = new Formatter(this.formatSb);
        
        this.sb = new StringBuilder(4096 * 2);
        this.indent = 0;
    }

    public boolean tabs() {
        return this.useTabs;
    }
    
    public int indentWidth() {
        return this.indentWidth;
    }
    
    @Override
    public String toString() {
        return this.sb.toString();
    }
    
    public Buf appendRaw(String str) {
        this.sb.append(str);
        return this;
    }
    public Buf appendRaw(char c) {
        this.sb.append(c);
        return this;
    }
    
    public Buf appendStringEscape(String str) {        
        for(int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if(escapeChars.containsKey(c)) {
                appendRaw(escapeChars.get(c));
            }
            else {
                appendRaw(c);
            }
        }        
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
        this.formatSb.setLength(0);
        this.formatter.format(message, args);
        
        int size = this.formatSb.length();
        for(int i = 0; i < size; i++) {
            char c = formatSb.charAt(i);
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
