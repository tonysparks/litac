/*
 * see license.txt
 */
package litac.compiler;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import litac.LitaOptions;
import litac.LitaOptions.OutputType;
import litac.ast.ModuleId;
import litac.ast.NodeVisitor.AbstractNodeVisitor;
import litac.ast.Stmt.ImportStmt;
import litac.ast.Stmt.ModuleStmt;
import litac.parser.*;

/**
 * @author Tony
 *
 */
public class CompilationUnit {

    private ModuleStmt main;
    private ModuleStmt builtin;
    private Map<ModuleId, ModuleStmt> imports;
    
    public CompilationUnit(ModuleStmt builtin, ModuleStmt main) {
        this.builtin = builtin;
        this.main = main;
        this.imports = new HashMap<>();
        this.imports.put(builtin.id, builtin);
    }
    
    /**
     * @return the imports
     */
    public Map<ModuleId, ModuleStmt> getImports() {
        return imports;
    }
    
    public ModuleStmt getModule(ModuleId module) {
        return this.imports.get(module);
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
    public static CompilationUnit modules(LitaOptions options, File moduleFile, PhaseResult result) throws IOException {   
        
        ModuleId builtinId = ModuleId.fromDirectory(options.libDir, "builtins");
        ModuleStmt builtin = readModule(options, builtinId.moduleFile, result);
        ModuleStmt main = readModule(options, moduleFile, result);
        
        main.imports.add(new ImportStmt("builtins", null, builtinId, false));
     
        if(options.outputType == OutputType.Test) {
            importTestModules(options, main);
        }
        
        
        CompilationUnit unit = new CompilationUnit(builtin, main);
        
        CompilationUnitNodeVisitor visitor = new CompilationUnitNodeVisitor(options, unit, result);
        visitor.visit(main);
        visitor.visit(builtin);
        return unit;
    }
    
    private static void importTestModules(LitaOptions options, ModuleStmt module) {
        ModuleId assertId = ModuleId.fromDirectory(options.libDir, "assert");
        ModuleId ioId = ModuleId.fromDirectory(options.libDir, "io");
        
        if(!module.imports.stream().anyMatch(imp -> imp.moduleId.equals(assertId))) {
            module.imports.add(new ImportStmt("assert", null, assertId, false));
        }
        if(!module.imports.stream().anyMatch(imp -> imp.moduleId.equals(ioId))) {
            module.imports.add(new ImportStmt("io", null, ioId, false));
        }
    }
    
    private static ModuleStmt readModule(LitaOptions options, File moduleFile, PhaseResult result) throws IOException {    
        // TODO: should this be a phaseresult or an exception??
        if(!moduleFile.exists()) {            
            throw new FileNotFoundException(moduleFile.getAbsolutePath());
        }
        
        Source source = new Source(moduleFile, new FileReader(moduleFile));                                    
        Parser parser = new Parser(options, result, new Scanner(source));
        ModuleStmt module = parser.parseModule();
        
        return module;                       
    }
            
    private static class CompilationUnitNodeVisitor extends AbstractNodeVisitor {
        
        LitaOptions options;
        CompilationUnit unit;
        PhaseResult result;
        
        CompilationUnitNodeVisitor(LitaOptions options, CompilationUnit unit, PhaseResult result) {
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
            ModuleId moduleId = stmt.moduleId;
            
            if(this.unit.imports.containsKey(moduleId)) {
                return null;                
            }
            
            try {
                ModuleStmt module = readModule(this.options, moduleId.moduleFile, this.result);
                this.unit.imports.put(moduleId, module);
                return module;
            }
            catch (FileNotFoundException e) {
                throw Compiler.error(stmt.getSrcPos(), "could not find module '%s' at '%s'", stmt.moduleName, moduleId.moduleFile.getAbsolutePath());
            }
            catch (IOException e) {
                throw Compiler.error(stmt.getSrcPos(), "I/O error '%s'", e.getMessage());
            }
        }
        

    }
}
