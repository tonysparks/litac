/*
 * see license.txt
 */
package litac.compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import litac.ast.NodeVisitor.AbstractNodeVisitor;
import litac.ast.Stmt.ImportStmt;
import litac.ast.Stmt.ModuleStmt;
import litac.parser.Parser;
import litac.parser.Scanner;
import litac.parser.Source;

/**
 * @author Tony
 *
 */
public class CompilationUnit {

    private ModuleStmt main;
    private Map<String, ModuleStmt> imports;
    
    private CompilationUnit(ModuleStmt main) {
        this.main = main;
        this.imports = new HashMap<>();
    }
    
    /**
     * @return the imports
     */
    public Map<String, ModuleStmt> getImports() {
        return imports;
    }
    
    public ModuleStmt getModule(String moduleName) {
        return this.imports.get(moduleName);
    }
    
    /**
     * @return the main
     */
    public ModuleStmt getMain() {
        return main;
    }

    /**
     * Loads all {@link ModuleStmt}s.
     * 
     * @param options
     * @param main
     * @return
     */
    public static CompilationUnit modules(BackendOptions options, File moduleFile) throws IOException {
        ModuleStmt main = readModule(moduleFile);
        
        CompilationUnit unit = new CompilationUnit(main);
        
        new CompilationUnitNodeVisitor(options, unit).visit(main);
        
        return unit;
    }
    
    private static ModuleStmt readModule(File moduleFile) throws IOException {            
        if(!moduleFile.exists()) {
            throw new FileNotFoundException();
        }
        
        Source source = new Source(moduleFile.getName(), new FileReader(moduleFile));                                    
        Parser parser = new Parser(new Scanner(source));
        ModuleStmt module = parser.parseModule();
        
        return module;                       
    }
    
    private static class CompilationUnitNodeVisitor extends AbstractNodeVisitor {
        
        BackendOptions options;
        CompilationUnit unit;
        
        CompilationUnitNodeVisitor(BackendOptions options, CompilationUnit unit) {
            this.options = options;
            this.unit = unit;
        }
        
        @Override
        public void visit(ModuleStmt stmt) {
            for(ImportStmt imp : stmt.imports) {
                loadModule(imp);
            }
        }
        
        private void loadModule(ImportStmt stmt) {
            String moduleName = stmt.moduleName;
            
            if(this.unit.imports.containsKey(moduleName)) {
                return;                
            }
            
            File importFile = new File(this.options.srcDir.getAbsolutePath(), moduleName + ".lita");
            try {
                ModuleStmt module = readModule(importFile);
                this.unit.imports.put(moduleName, module);
            }
            catch (FileNotFoundException e) {
                throw Compiler.error(stmt, "could not find module '%s' at '%s'", moduleName, importFile.getAbsolutePath());
            }
            catch (IOException e) {
                throw Compiler.error(stmt, "I/O error '%s'", e.getMessage());
            }
        }
        

    }
}
