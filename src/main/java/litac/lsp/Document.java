/*
 * see license.txt
 */
package litac.lsp;

import java.util.*;
import java.util.stream.Collectors;

import litac.ast.*;
import litac.compiler.*;
import litac.compiler.Module;
import litac.compiler.PhaseResult.PhaseError;
import litac.lsp.JsonRpc.*;
import litac.lsp.SourceToAst.SourceLocation;
import litac.parser.tokens.WordToken;

/**
 * Represents a litac module document
 */
public class Document {

    private List<Integer> lineMap;
    private StringBuilder buffer;
    
    public TextDocument document;
    public List<PhaseError> errors;
    public final ModuleId moduleId;
    
    private LspLogger log;
    
    public Document(ModuleId moduleId, TextDocument document, LspLogger log) {
        this.moduleId = moduleId;
        this.document = document;
        this.log = log;
        this.lineMap = new ArrayList<>();
        this.errors = new ArrayList<>();
        
        setText(document.text);
    }

    public int getLineStart(int lineNumber) {
        return this.lineMap.get(lineNumber);
    }
        
    public void insert(Range range, String text) {
        int fromIndex = getLineStart(range.start.line) + range.start.character;
        int toIndex = getLineStart(range.end.line) + range.end.character;
        
        this.buffer.replace(fromIndex, toIndex, text);   
        refreshLineMap();
    }
    
    public void setText(String text) {
        this.buffer = new StringBuilder(text);
        refreshLineMap();
    }
    
    private void refreshLineMap() {
        this.lineMap.clear();
        this.lineMap.add(0); // first line starts with the first character
         
        for(int i = 0; i < this.buffer.length(); i++) {
            char c = this.buffer.charAt(i);
            if(c == '\n') {
                this.lineMap.add(i + 1);
            }
        }       
        
        this.document.text = this.buffer.toString();
    }
    
    private SourceLocation getSourceLocation(Workspace workspace, Position pos) {
        return getSourceLocation(workspace, pos, false);
    }
    
    private Module getModule(Workspace workspace, boolean doFullBuild) {
        if(doFullBuild && !workspace.isFullyBuilt()) {
            workspace.processSource();
        }
        
        Program program = workspace.getLatestProgram();
        if(program == null) {
            PhaseResult result = workspace.processSource(this.document.uri);
            program = workspace.getLatestProgram();
            if(program == null) {
                this.log.log("Unable to compile program: '" + this.document.uri +"'");
                if(result.hasErrors()) {
                    this.log.log("Errors: " + result.getErrors().stream().map(e -> e.message).reduce((a,b) -> a + ", " + b).orElse(""));
                }
                return null;
            }
        }
        
        Module module = program.getModule(this.moduleId);
        if(module == null) {                   
            PhaseResult result = workspace.processSource(this.document.uri);
            program = workspace.getLatestProgram();
            if(program == null) {
                this.log.log("Unable to compile program: '" + this.document.uri +"' for module: '" + this.moduleId + "'");
                if(result.hasErrors()) {
                    this.log.log("Errors: " + result.getErrors().stream().map(e -> e.message).reduce((a,b) -> a + ", " + b).orElse(""));
                }
                return null;
            }
            
            module = program.getModule(this.moduleId);
            if(module == null) {
                this.log.log("Unable to find module: '" + this.moduleId + "'");
                return null;
            }
        }
        
        return module;
    }
    
    private SourceLocation getSourceLocation(Workspace workspace, Position pos, boolean doFullBuild) {
        Module module = getModule(workspace, doFullBuild);
        Program program = workspace.getLatestProgram();
        SourceToAst sta = new SourceToAst(log, program, module, pos);
        return sta.findSourceLocation(module.getModuleStmt());
    }
    
    public String getText() {
        return this.document.text;
    }
    
    public Location getDefinitionLocation(Workspace workspace, Position pos) {
        SourceLocation location = getSourceLocation(workspace, pos);
        if(location == null) {
            return null;
        }
        
        return location.location;
    }
    
    public List<Location> getReferences(Workspace workspace, Position pos) {
        
        SourceLocation location = getSourceLocation(workspace, pos, true);
        if(location == null) {
            log.log("No source location found");
            return Collections.emptyList();
        }
        
        Program program = workspace.getLatestProgram();
        if(program == null) {
            log.log("No program built");
            return Collections.emptyList();
        }
        
        ReferenceDatabase database = workspace.getReferences();
        database.buildDatabase(program);
        
        List<Location> locations = database.findReferencesFromLocation(location);                
        return locations;
    }
    
    public List<CompletionItem> getAutoCompletionList(Workspace workspace, Position pos) {
        Module module = getModule(workspace, true);
        if(module == null) {
            log.log("No program built");
            return Collections.emptyList();
        }
        
        Program program = workspace.getLatestProgram();
        if(program == null) {
            log.log("No program built");
            return Collections.emptyList();
        }
        
        ReferenceDatabase database = workspace.getReferences();
        database.buildDatabase(program);
        
        List<String> fields = findIdentifier(pos);
        return database.findCompletionItems(module, pos, fields);              
    }
    
    private List<String> findIdentifier(Position pos) {
        List<String> fields = new ArrayList<>();
        
        int index = getLineStart(pos.line) + pos.character - 1;
        StringBuffer sb = new StringBuffer();
        while(index > -1) {
            char c = this.buffer.charAt(index);
            
            if(Character.isWhitespace(c)) {                
                index = skipWhitespace(index);
                if(index < 0) {
                    break;
                }
                
                continue;
            }
            
            if(!WordToken.isValidIdentifierCharacter(c) && c != '.') {        
                break;
            }
            
            index--;
            sb.append(c);
        }
                
        String split[] = sb.reverse().toString().split("\\.");
        log.log("Splits: " + Arrays.toString(split) + " vs " + sb.reverse().toString());
        fields.addAll(Arrays.asList(split));
                
        return fields;
    }
    
    private int skipWhitespace(int index) {
        while(index > -1) {
            char c = this.buffer.charAt(index);
            if(Character.isWhitespace(c)) {
                index--;
                continue;
            }
            if(c == '.') {
                return index;
            }
            return -1;
        }
        
        return index;
    }
    
    public List<SymbolInformation> getSymbols(Program program) {
        if(program == null) {
            return null;
        }
        
        
        Module module = program.getModule(this.moduleId);
        if(module == null) {
            return null;
        }
                
        return module.getModuleScope().getSymbols().stream()
                .filter(sym -> sym.declared == module && !sym.isBuiltin() && !sym.isFromGenericTemplate())
                .map(sym -> LspUtil.fromSymbol(sym))
                .collect(Collectors.toList());        
    }
}
