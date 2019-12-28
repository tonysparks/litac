/*
 * see license.txt
 */
package litac.lsp;

import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import litac.ast.NodeVisitor.AbstractNodeVisitor;
import litac.ast.Stmt.*;
import litac.checker.TypeResolver;
import litac.compiler.*;
import litac.lsp.JsonRpc.*;
import litac.parser.*;
import litac.parser.Scanner;

/**
 * Represents a project workspace, manages open documents and the corresponding AST
 */
public class Workspace {

    private String rootModule;
    private BackendOptions options;
    private Map<String, Document> documents;
    private Map<String, String> modules;
    private Program latestProgram;
    private File srcDir;
    
    private LspLogger log;
    
    /**
     * 
     */
    public Workspace(BackendOptions options, LspLogger log) {        
        this.options = options;        
        this.log = log;
                
        this.documents = new HashMap<>();
        this.modules = new HashMap<>();
    }
    
    public void setRoot(File rootModule) {
        this.rootModule = rootModule.getName().replace(".lita", "");
        this.srcDir = rootModule.getParentFile();
    }
    
    private String normalizePath(String path) {
        String fileName = path.replace("\\", "/");
        return fileName;        
    }
    
    private String canonicalPath(String docUri) {
        URI uri = URI.create(docUri);
        return canonicalPath(new File(uri));
    }
    
    private String canonicalPath(File file) {        
        Path path = file.isFile() ? file.getParentFile().toPath() : file.toPath();
        String packages = "";
        if(path.startsWith(options.libDir.toPath())) {
            packages = path.relativize(options.libDir.toPath()).toString();
        }
        if(path.startsWith(this.srcDir.toPath())) {
            packages = path.relativize(this.srcDir.toPath()).toString();
        }
        
        if(packages.length() > 0) {
            packages += "/";
        }
        
        return normalizePath(packages + file.getName()).replace(".lita", "");
        
        /*
        Path path = file.toPath();        
        if(path.startsWith(options.libDir.toPath())) {
            return path.relativize(options.libDir.toPath()).toString();
        }
        if(path.startsWith(this.srcDir.toPath())) {
            return path.relativize(this.srcDir.toPath()).toString();
        }
        
        return path.getFileName().toString();
        */
    }
    
    private String getModuleName(String docUri) {
        String name = canonicalPath(docUri);
        return name.replace(".lita", "");
    }
    
    /**
     * @return the latestProgram
     */
    public Program getLatestProgram() {
        return latestProgram;
    }
            
    public void addDocument(TextDocument document) {
        this.documents.put(canonicalPath(document.uri), new Document(getModuleName(document.uri), document));
    }
    
    public void removeDocument(String documentUri) {
        String moduleName = canonicalPath(documentUri);
        this.documents.remove(moduleName);
        this.modules.remove(moduleName);
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
    }
   
    public void saveDocument(DidSaveTextDocumentParams params) {        
        Document document = this.documents.get(canonicalPath(params.textDocument.uri));
        if(params.text != null) {
            document.setText(params.text);
        }
        
        log.log("Saving: \n" + document.getText());
    }
    
    public Document getDocument(String documentUri) {
        return this.documents.get(canonicalPath(documentUri));
    }
    
    public List<Document> getDocuments() {
        return new ArrayList<>(this.documents.values());
    }
    
    private String getModulePhysicalFileName(String moduleName) {
        if(!this.modules.containsKey(moduleName)) {
            Document document = this.documents.get(moduleName);
            if(document != null) {
                File file = new File(URI.create(document.document.uri));
                this.modules.put(moduleName, normalizePath(file.getAbsolutePath()));
                        //canonicalPath(document.document.uri));
            }
            else {
                File moduleFile = new File(this.srcDir, moduleName + ".lita");
                if(!moduleFile.exists()) {
                    moduleFile = new File(this.options.libDir, moduleName + ".lita");
                }
                
                if(moduleFile.exists()) {                    
                    this.modules.put(moduleName, normalizePath(moduleFile.getAbsolutePath()));
                }
                else {
                    this.modules.put(moduleName, null);
                }
            }
        }
        return this.modules.get(moduleName); 
    }
    
    private String getDocumentText(String moduleName) {
        Document document = this.documents.get(moduleName);
        if(document != null) {
            return document.getText();
        }
        
        String physicalName = getModulePhysicalFileName(moduleName);
        if(physicalName == null) {
            return null;
        }
        
        File moduleFile = new File(physicalName);        
        try {
            return new String(Files.readAllBytes(moduleFile.toPath()));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private ModuleStmt readModule(String moduleName) {                
        String physicalFileName = getModulePhysicalFileName(moduleName);
        String text = getDocumentText(moduleName);
        
        //log.log("Reading module: '" + moduleName +"' mapped to physical name: '" + physicalFileName + "'");
        
        if(physicalFileName == null || text == null) {
            return null;
        }
        
        Source source = new Source(physicalFileName, new StringReader(text));                                    
        Parser parser = new Parser(this.options.preprocessor(), new Scanner(source));
        ModuleStmt module = parser.parseModule();
        
        return module;
    }
    
    private void buildSymbols(Module module, Map<String, Symbol> symbols, Set<String> visitedModules) {
        if(visitedModules.contains(module.name())) {
            return;
        }
        
        visitedModules.add(module.name());        
        module.getModuleScope().getSymbols()
            .forEach(sym -> {
                if(module == sym.declared) {
                    symbols.put(module.name() + "::" + sym.name, sym);
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
    
    public PhaseResult processSource() {
        return processSourceModule(this.rootModule);
        //return new PhaseResult();
    }
    
    public PhaseResult processSource(String documentUri) {
        return processSourceModule(canonicalPath(documentUri));
    }
    
    private PhaseResult processSourceModule(String moduleName) {
        try {
            ModuleStmt rootModule = readModule(moduleName);
            ModuleStmt builtin = readModule("builtins");
            
            CompilationUnit unit = new CompilationUnit(builtin, rootModule);
            CompilationUnitNodeVisitor visitor = new CompilationUnitNodeVisitor(unit.getImports());
            visitor.visit(rootModule);
            visitor.visit(builtin);
                    
            PhaseResult result = new PhaseResult();
            TypeResolver resolver = new TypeResolver(options.preprocessor(), result, unit);        
            this.latestProgram = resolver.resolveTypes();
            
            return result;
        }
        catch(ParseException e) {
            PhaseResult result = new PhaseResult();
            result.addError(e.getToken().getPos(), e.getErrorCode().toString());
            
            return result;
        }
        catch(Exception e) {
            // an unrecoverable error
            return new PhaseResult();
        }
    }
    
    
    private class CompilationUnitNodeVisitor extends AbstractNodeVisitor {
        Map<String, ModuleStmt> imports;
        
        CompilationUnitNodeVisitor(Map<String, ModuleStmt> imports) {
            this.imports = imports;
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
            
            if(this.imports.containsKey(moduleName)) {
                return null;                
            }
            
            ModuleStmt module = readModule(moduleName);
            this.imports.put(moduleName, module);
            
            return module;
        }
    }
}
