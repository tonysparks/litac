/*
 * see license.txt
 */
package litac.checker;

import litac.compiler.CompilationUnit;
import litac.compiler.c.GenericsNodeVisitor;

/**
 * @author Tony
 *
 */
public class GenericsResolver {

    private CompilationUnit unit;
    
    /**
     * 
     */
    public GenericsResolver(CompilationUnit unit) {
        this.unit = unit;
    }
    
    public void applyGenerics(TypeResolver resolver, Module main) {
        GenericsNodeVisitor generics = new GenericsNodeVisitor(resolver, main, unit);
        //module.getModuleStmt().visit(generics);
        this.unit.getMain().visit(generics);
    }

}
