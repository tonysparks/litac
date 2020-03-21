/*
 * see license.txt
 */
package litac.lsp;

import java.util.*;
import java.util.stream.Collectors;

import litac.ast.ModuleId;
import litac.compiler.*;
import litac.compiler.Module;
import litac.compiler.PhaseResult.PhaseError;
import litac.lsp.JsonRpc.*;

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
    
    public String getText() {
        return this.document.text;
    }
    
    public Location getDefinitionLocation(Workspace workspace, Position pos) {
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
               // program.getModules().stream().map(m -> m.name()).reduce((a,b) -> a + ", " + b).ifPresent(log::log);
                
                return null;
            }
        }
        
        SourceToAst sta = new SourceToAst(program, module, pos);
        return sta.findSourceLocation(module.getModuleStmt());        
    }
    
    public List<CompletionItem> getAutoCompletionList(Workspace workspace, Position pos) {
        Program program = workspace.getLatestProgram();
        if(program == null) {
            workspace.processSource(this.document.uri);
            program = workspace.getLatestProgram();
            if(program == null) {    
                return null;
            }
        }
                
        Module module = program.getModule(this.moduleId);
        if(module == null) {            
            workspace.processSource(this.document.uri);
            program = workspace.getLatestProgram();
            if(program == null) {
                return null;
            }
            
            module = program.getModule(this.moduleId);            
            if(module == null) {
                return null;
            }
        }
        
        final Module docModule = module;
        
        // TODO
        // Ideas:
        // index into source and iterate backwards until a . is found, then
        // look for the object -- this solves object. and object.startOf use cases
        // what about ctrl+space from empty context or from middle of name (i.e., Load(ctrl+space) for LoadFile, LoadModel, etc.)
//        int toIndex = getLineStart(pos.line) + pos.character;    
//        int startingIndex = readIdentifier(toIndex);
//        if(startingIndex <= toIndex && startingIndex > -1 && toIndex > -1) {
//            String text = getText();
//            String identifier = text.substring(startingIndex, toIndex + 1);
//            
//            return module.getModuleScope().getSymbols().stream()
//                    .filter(sym -> sym.declared == docModule && !sym.isBuiltin() && !sym.isFromGenericTemplate() && sym.name.startsWith(identifier))
//                    .map(sym -> LspUtil.fromSymbolCompletionItem(sym))
//                    .collect(Collectors.toList()); 
//        }
        
        //SourceToAst sta = new SourceToAst(program, module, pos);
        //sta.findSourceLocation(stmt)
        //return sta.findSourceLocation(module.getModuleStmt());
        
        return module.getModuleScope().getSymbols().stream()
                .filter(sym -> sym.declared == docModule && !sym.isBuiltin() && !sym.isFromGenericTemplate())
                .map(sym -> LspUtil.fromSymbolCompletionItem(sym))
                .collect(Collectors.toList()); 
    }
    
//    private int readIdentifier(int index) {
//        while(index > -1) {
//            char c = this.buffer.charAt(index);            
//            if(!WordToken.isValidIdentifierCharacter(c)) {
//                break;
//            }
//            
//            index--;
//        }
//        
//        while(index < this.buffer.length()) {
//            char prevC = this.buffer.charAt(index);
//            if(WordToken.isValidStartIdentifierCharacter(prevC)) {
//                break;
//            }
//            
//            index++;
//        }
//        
//        return index;
//    }
    
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
