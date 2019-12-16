/*
 * see license.txt
 */
package litac.lsp;

import java.util.*;
import java.util.stream.Collectors;

import litac.compiler.*;
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
    public final String moduleName;
    
    public Document(String moduleName, TextDocument document) {
        this.moduleName = moduleName;
        this.document = document;
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
    
    public Location getDefinitionLocation(Program program, Position pos) {
        if(program == null) {
            return null;
        }
                
        Module module = program.getModule(this.moduleName);
        if(module == null) {
            return null;
        }
        
        SourceToAst sta = new SourceToAst(program, pos);
        sta.visit(module.getModuleStmt());
        
        return sta.getLocation();
    }
    
    public List<SymbolInformation> getSymbols(Program program) {
        if(program == null) {
            return null;
        }
        
        
        Module module = program.getModule(this.moduleName);
        if(module == null) {
            return null;
        }
                
        return module.getModuleScope().getSymbols().stream()
                .filter(sym -> sym.declared == module && !sym.isBuiltin())
                .map(sym -> LspUtil.fromSymbol(sym))
                .collect(Collectors.toList());        
    }
}
