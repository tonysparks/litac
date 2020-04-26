/*
 * see license.txt
 */
package litac.lsp;

import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import litac.LitaOptions;
import litac.ast.ModuleId;
import litac.ast.Node.SrcPos;
import litac.ast.NodeVisitor.AbstractNodeVisitor;
import litac.ast.Stmt.*;
import litac.checker.TypeResolver;
import litac.compiler.*;
import litac.lsp.JsonRpc.*;
import litac.parser.*;
import litac.parser.Scanner;
import litac.compiler.Module;

/**
 * Represents a project workspace, manages open documents and the corresponding AST
 */
public class Workspace {

    private ModuleId rootModule;
    private LitaOptions options;
    private Map<String, Document> documents;
    private Program latestProgram;
    private File srcDir;
    private String latestDocumentUri;
    private boolean isFullyBuilt;
    private ReferenceDatabase references;
    
    private LspLogger log;
    
    /**
     * 
     */
    public Workspace(LitaOptions options, LspLogger log) {        
        this.options = options;        
        this.log = log;
                
        this.documents = new HashMap<>();
        
        this.rootModule = null;
        this.srcDir = options.getSrcDir();
        this.isFullyBuilt = false;
        
        this.references = new ReferenceDatabase(log);
    }
    
    public void setRoot(File rootModule, File sourceDir) {
        if(rootModule != null) {
            this.rootModule = ModuleId.from(options.libDir, sourceDir, rootModule);
        }
        if(sourceDir != null) {
            this.srcDir = sourceDir;
            this.options.setSrcDir(this.srcDir);
        }
        
        log.log("Source Directory: '" + this.srcDir + "' and RootModule: " + rootModule);
    }
    
    
    private String canonicalPath(String docUri) {
        return new File(URI.create(docUri)).toString();
    }
    
    private ModuleId getModuleId(String docUri) {
        ModuleId module = ModuleId.from(options.libDir, this.srcDir, new File(URI.create(docUri)));
        return module;
    }
    
    
    /**
     * @return the latestProgram
     */
    public Program getLatestProgram() {
        return latestProgram;
    }
    
    /**
     * @return the references
     */
    public ReferenceDatabase getReferences() {
        return references;
    }
            
    public void addDocument(TextDocument document) {
        this.latestDocumentUri = canonicalPath(document.uri);
        this.documents.put(this.latestDocumentUri, new Document(getModuleId(document.uri), document, this.log));
        this.isFullyBuilt = false;
    }
    
    public void removeDocument(String documentUri) {
        String moduleName = canonicalPath(documentUri);
        this.documents.remove(moduleName);
        this.isFullyBuilt = false;
    }

    public void changedDocument(String documentUri, DidChangeParams change) {
        Document document = this.documents.get(canonicalPath(documentUri));
        
        for(TextDocumentContentChangeEvent event : change.contentChanges) {
            if(event.range != null) {
                document.insert(event.range, event.text);
            }
            else {
                document.setText(event.text);
            }
        }
        
        this.isFullyBuilt = false;
    }
   
    public void saveDocument(DidSaveTextDocumentParams params) {        
        Document document = this.documents.get(canonicalPath(params.textDocument.uri));
        if(params.text != null) {
            document.setText(params.text);
        }
        
        log.log("Saving: " + params.textDocument.uri);
    }
    
    public Document getDocument(String documentUri) {
        return this.documents.get(canonicalPath(documentUri));
    }
    
    public List<Document> getDocuments() {
        return new ArrayList<>(this.documents.values());
    }
    
    private String getDocumentText(ModuleId moduleId) {
        Document document = this.documents.get(moduleId.id);
        if(document != null) {
            return document.getText();
        }
        
        if(!moduleId.moduleFile.exists()) {
            return null;
        }
                       
        try {
            return new String(Files.readAllBytes(moduleId.moduleFile.toPath()));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private ModuleStmt readModule(ModuleId importedModule, PhaseResult result ) {                
        String physicalFileName = importedModule.moduleFile.getAbsolutePath(); 
        String text = getDocumentText(importedModule);
        
        //log.log("Reading module: '" + importedModule.fullModuleName +"' mapped to physical name: '" + physicalFileName + "'");
        
        if(physicalFileName == null || text == null) {
            log.log("ReadModule null: " + physicalFileName + " Text: \n" + text);
            return null;
        }
        
        Source source = new Source(new File(physicalFileName != null ? physicalFileName : "<unknown>"), new StringReader(text));                                    
        Parser parser = new Parser(this.options, result, new Scanner(source));
        ModuleStmt module = parser.parseModule();
        importAssertModule(module);
        
        return module;
    }
    
    private void buildSymbols(Module module, Map<String, Symbol> symbols, Set<String> visitedModules) {
        if(visitedModules.contains(module.getId().id)) {
            return;
        }
        
        visitedModules.add(module.getId().id);
        module.getModuleScope().getSymbols()
            .forEach(sym -> {
                if(module == sym.declared) {
                    symbols.put(module.simpleName() + "::" + sym.name, sym);
                }
            });
        module.getImports().forEach(mod -> buildSymbols(mod, symbols, visitedModules));
    }
    
    public List<SymbolInformation> findSymbols(String query) {
        if(this.latestProgram == null) {
            return Collections.emptyList();
        }
        
        boolean endsWith = query.startsWith("*");
        boolean contains = query.startsWith("*") && query.endsWith("*") && query.length() > 2;
        String normalizedQuery = query.replace("*", "");
        
        Map<String, Symbol> symbols = new HashMap<>();
        buildSymbols(this.latestProgram.getMainModule(), symbols, new HashSet<>());
        
        return symbols.values().stream()
            .filter(sym -> {
                if(contains) {
                    return sym.name.contains(normalizedQuery);
                }
                
                if(endsWith) {
                    return sym.name.endsWith(normalizedQuery);
                }
                
                return sym.name.startsWith(normalizedQuery) && !sym.isBuiltin();
            })                
            .map(sym -> LspUtil.fromSymbol(sym))
            .sorted((a,b) -> a.name.compareTo(b.name))
            .collect(Collectors.toList());
    }
    
    public boolean isFullyBuilt() {
        return isFullyBuilt;
    }
    
    public PhaseResult processSource() {
        this.isFullyBuilt = true;
        
        log.log("Doing full rebuild...");
        
        ModuleId module = this.rootModule;        
        if(module == null && this.latestDocumentUri != null) {
            module = ModuleId.from(options.libDir, this.srcDir, new File(this.latestDocumentUri));
        }
        
        // we can't compile any source
        if(module == null) {
            log.log("Rebuild failed to find root module");
            return new PhaseResult();
        }
        
        log.log("Rebuild with root module: '" + module.id + "'");        
        PhaseResult result = processSourceModule(module, true);
        
        if(result.hasErrors()) {
            log.log("Rebuild failed with errors: " + LspUtil.phaseErrorToString(result.getErrors()));
        }
        else {
            log.log("Rebuild successfully");
        }
        
        return result;
    }
    
    public PhaseResult processSource(String documentUri) {
        ModuleId module = getModuleId(documentUri);        
        return processSourceModule(module, false);
    }
    
    private PhaseResult processSourceModule(ModuleId module, boolean fullRebuild) {
        PhaseResult result = new PhaseResult();
        try {
            
            ModuleStmt rootModule = readModule(module, result);
                        
            ModuleId builtinsId = ModuleId.fromDirectory(this.options.libDir, "builtins");
            ModuleStmt builtin = readModule(builtinsId, result);
            
            CompilationUnit unit = new CompilationUnit(builtin, rootModule);            
            importAssertModule(rootModule);                
                        
            CompilationUnitNodeVisitor visitor = new CompilationUnitNodeVisitor(unit.getImports(), result);
            visitor.visit(rootModule);
            visitor.visit(builtin);
            
            TypeResolver resolver = new TypeResolver(options, result, unit);        
            this.latestProgram = resolver.resolveTypes();
        }
        catch(ParseException e) {
            log.log("ParseException: " + e.getMessage());
            result.addError(e.getToken().getPos(), e.getErrorCode().toString());            
        }
        catch(Exception e) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            log.log("Exception: " + writer.toString());
            result.addError((SrcPos)null, "internal compiler error: %s", e.getMessage());
        }
        
        return result;
    }
    
    private void importAssertModule(ModuleStmt main) {        
        ModuleId assertId = ModuleId.fromDirectory(options.libDir, "assert");
        if(!main.imports.stream().anyMatch(imp -> imp.moduleId.equals(assertId)) && !main.id.equals(assertId)) {
            main.imports.add(new ImportStmt("assert", null, assertId, false));
        }
    }
    
    
    private class CompilationUnitNodeVisitor extends AbstractNodeVisitor {
        Map<ModuleId, ModuleStmt> imports;
        PhaseResult result;
        
        CompilationUnitNodeVisitor(Map<ModuleId, ModuleStmt> imports, PhaseResult result) {
            this.imports = imports;
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
            // check and see if we've already imported this module..
            if(this.imports.containsKey(stmt.moduleId)) {
                return null;                
            }
            
            ModuleStmt module = readModule(stmt.moduleId, this.result);
            this.imports.put(stmt.moduleId, module);
            
            return module;
        }
    }
}
