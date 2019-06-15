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
    private ModuleStmt builtin;
    private Map<String, ModuleStmt> imports;
    
    private CompilationUnit(ModuleStmt builtin, ModuleStmt main) {
        this.builtin = builtin;
        this.main = main;
        this.imports = new HashMap<>();
        this.imports.put("builtins", builtin);
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
     * @return the builtin
     */
    public ModuleStmt getBuiltin() {
        return builtin;
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
        ModuleStmt builtin = readModule(new File(options.libDir, "builtins.lita"));
        ModuleStmt main = readModule(moduleFile);
        main.imports.add(new ImportStmt("builtins", null));
        
        CompilationUnit unit = new CompilationUnit(builtin, main);
        
        CompilationUnitNodeVisitor visitor = new CompilationUnitNodeVisitor(options, unit);
        visitor.visit(main);
        visitor.visit(builtin);
        
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
                ModuleStmt module = loadModule(imp);
                if(module != null) {
                    module.visit(this);
                }
            }
        }
        
        private ModuleStmt loadModule(ImportStmt stmt) {
            String moduleName = stmt.moduleName;
            
            if(this.unit.imports.containsKey(moduleName)) {
                return null;                
            }
            
            File importFile = new File(this.options.srcDir.getAbsolutePath(), moduleName + ".lita");
            if(!importFile.exists()) {
                importFile = new File(this.options.libDir, moduleName + ".lita");
            }
            
            try {
                ModuleStmt module = readModule(importFile);
                this.unit.imports.put(moduleName, module);
                return module;
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
