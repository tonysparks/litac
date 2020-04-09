/*
 * see license.txt
 */
package litac.lsp;

import java.util.*;
import java.util.stream.Collectors;

import litac.ast.*;
import litac.checker.TypeInfo;
import litac.checker.TypeInfo.AggregateTypeInfo;
import litac.checker.TypeResolver.Operand;
import litac.compiler.*;
import litac.compiler.Module;
import litac.compiler.PhaseResult.PhaseError;
import litac.lsp.JsonRpc.*;
import litac.lsp.SourceToAst.SourceLocation;

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
    
    private SourceLocation getSourceLocation(Workspace workspace, Position pos, boolean doFullBuild) {
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
        database.buildDatabase(workspace.getLatestProgram());
        
        List<Location> locations = database.findReferencesFromLocation(location);                
        return locations;
    }
    
    public List<CompletionItem> getAutoCompletionList(Workspace workspace, Position pos) {
        SourceLocation location = getSourceLocation(workspace, pos, false);
        Program program = workspace.getLatestProgram();
        if(program == null) {
            log.log("No program built");
            return Collections.emptyList();
        }
        
        if(location == null) {
            log.log("No source location found");

            // just look for symbols in this module
            final Module module = program.getModule(this.moduleId);                
            return module.getModuleScope().getSymbols().stream()
                    .filter(sym -> sym.declared == module && !sym.isBuiltin() && !sym.isFromGenericTemplate())
                    .map(sym -> LspUtil.fromSymbolCompletionItem(sym))
                    .collect(Collectors.toList());
        }
        
        if(!(location.node instanceof Expr)) {
            log.log("Location is not an Expression: " + location.node.getClass().getSimpleName());
            return Collections.emptyList();
        }
        
        // TODO: Move logic into a Intellisense class..
        
        Expr expr = (Expr)location.node;
        Operand op = expr.getResolvedType();
        TypeInfo type = op.type;
        
        if(TypeInfo.isAggregate(type)) {
            AggregateTypeInfo agg = type.as();
            return agg.fieldInfos.stream()
                    .filter(field -> field.type != null && field.type.sym != null)
                    .map(field -> LspUtil.fromSymbolCompletionItem(field.type.sym))
                    .collect(Collectors.toList());
        }
        
        return Collections.emptyList();
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
