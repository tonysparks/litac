/*
 * see license.txt
 */
package litac.checker;

import java.util.List;

/**
 * @author Tony
 *
 */
public class Note {

    public final String name;
    public final List<String> attributes;
    
    public Note(String name, List<String> attributes) {
        this.name = name;
        this.attributes = attributes;
    }
    
    public String getAttr(int index, String defaultValue) {
        if(this.attributes == null || this.attributes.size() <= index) {
            return defaultValue;
        }
        
        return this.attributes.get(index);
    }
}
