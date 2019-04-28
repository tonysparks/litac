/*
 * see license.txt
 */
package litac.compiler.c;

/**
 * @author Tony
 *
 */
public class NameCoord {

    public final String parentModuleName;
    public final String moduleName;
    public final String moduleAlias;
    
    public NameCoord(String parentModuleName, String name, String alias) {
        this.parentModuleName = parentModuleName;
        this.moduleName = name;
        this.moduleAlias = alias;
    }
}
