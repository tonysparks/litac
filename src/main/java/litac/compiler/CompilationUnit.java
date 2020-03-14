/*
 * see license.txt
 */
package litac.compiler;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import litac.ast.NodeVisitor.AbstractNodeVisitor;
import litac.ast.Stmt.ImportStmt;
import litac.ast.Stmt.ModuleStmt;
import litac.compiler.BackendOptions.OutputType;
import litac.parser.*;

/**
 * @author Tony
 *
 */
public class CompilationUnit {

    private ModuleStmt main;
    private ModuleStmt builtin;
    private Map<String, ModuleStmt> imports;
    
    public CompilationUnit(ModuleStmt builtin, ModuleStmt main) {
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
    public static CompilationUnit modules(BackendOptions options, File moduleFile, PhaseResult result) throws IOException {   
        ModuleStmt builtin = readModule(options.preprocessor(), options.findModule("builtins.lita"), result);
        ModuleStmt main = readModule(options.preprocessor(), moduleFile, result);
        main.imports.add(new ImportStmt("builtins", null, false));
     
        if(options.outputType == OutputType.Test) {
            importTestModules(main);
        }
        
        
        CompilationUnit unit = new CompilationUnit(builtin, main);
        
        CompilationUnitNodeVisitor visitor = new CompilationUnitNodeVisitor(options, unit, result);
        visitor.visit(main);
        visitor.visit(builtin);
        return unit;
    }
    
    private static void importTestModules(ModuleStmt main) {
        if(!main.imports.stream().anyMatch(imp -> imp.moduleName.equals("assert"))) {
            main.imports.add(new ImportStmt("assert", null, false));
        }
        if(!main.imports.stream().anyMatch(imp -> imp.moduleName.equals("io"))) {
            main.imports.add(new ImportStmt("io", null, false));
        }
    }
    
    private static ModuleStmt readModule(Preprocessor pp, File moduleFile, PhaseResult result) throws IOException {    
        // TODO: should this be a phaseresult or an exception??
        if(!moduleFile.exists()) {            
            throw new FileNotFoundException(moduleFile.getAbsolutePath());
        }
        
        Source source = new Source(moduleFile.getName(), new FileReader(moduleFile));                                    
        Parser parser = new Parser(pp, result, new Scanner(source));
        ModuleStmt module = parser.parseModule();
        
        return module;                       
    }
            
    private static class CompilationUnitNodeVisitor extends AbstractNodeVisitor {
        
        BackendOptions options;
        CompilationUnit unit;
        PhaseResult result;
        
        CompilationUnitNodeVisitor(BackendOptions options, CompilationUnit unit, PhaseResult result) {
            this.options = options;
            this.unit = unit;
            this.result = result;
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
            
            String fileName = moduleName + ".lita";
            File importFile = options.findModule(fileName);
            
            try {
                ModuleStmt module = readModule(this.options.preprocessor(), importFile, this.result);
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
