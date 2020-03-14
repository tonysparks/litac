/*
 * see license.txt
 */
package litac.lsp;

import java.util.List;

import com.google.gson.JsonElement;

import litac.checker.TypeInfo;
import litac.compiler.Symbol;

/**
 * Json RPC - Language Server Protocol types
 */
public class JsonRpc {
    public static enum MessageType {
        Error(1),
        Warning(2),
        Info(3),
        Log(4)
        ;
        
        private int value;
        MessageType(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }
    
    public static enum ErrorCodes {
        ParseError(-32700),
        InvalidRequest(-32600),
        MethodNotFound(-32601),
        InvalidParams(-32602),
        InternalError(-32603),
        ServerErrorStart(-32099),
        ServerErrorEnd(-32000),
        ServerNotInitialized(-32002),
        UnknownErrorCode(-32001),
    
        // Defined by the protocol.
        RequestCancelled(-32800),
        ContentModified(-32801),
    
        ;
        
        private int value;
        ErrorCodes(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }
    
    public static enum SymbolKind {
        File(1),
        Module(2),
        Namespace(3),
        Package(4),
        Class(5),
        Method(6),
        Property(7),
        Field(8),
        Constructor(9),
        Enum(10),
        Interface(11),
        Function(12),
        Variable(13),
        Constant(14),
        String(15),
        Number(16),
        Boolean(17),
        Array(18),
        Object(19),
        Key(20),
        Null(21),
        EnumMember(22),
        Struct(23),
        Event(24),
        Operator(25),
        TypeParameter(26),
        
        ;
        
        private int value;
        SymbolKind(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
        
        public static SymbolKind fromSymbol(Symbol sym) {            
            switch(sym.kind) {
            case CONST:
                return Constant;
            case FUNC:
                return Function;
            case VAR:
                return Variable;
            case TYPE:
                TypeInfo type = sym.getType();
                if(type == null) {
                    return Null;
                }
                switch(type.getKind()) {
                    case Array:
                        return Array;
                    case Bool:
                        return Boolean;
                    case Char:
                        return Number;
                    case Const:
                        return Constant;
                    case Enum:
                        return Enum;
                    case Func:
                        return Function;
                    case FuncPtr:
                        return Object;
                    case GenericParam:
                        return TypeParameter;
                    case Null:
                        return Null;
                    case Ptr:
                        return Object;
                    case Str:
                        return String;
                    case Struct:
                        return Struct;
                    case Union:
                        return Struct;
                    case Void:
                        return Null;
                    case f32:
                    case f64:
                    case i16:
                    case i32:
                    case i64:
                    case i8:
                    case u16:
                    case u32:
                    case u64:
                    case u8:
                        return Number;
                }
            }
            return Variable;            
        }
    }
    
//    public static class RpcMessage {
//        public String jsonrpc = "2.0";       
//    }
    
    public static class RpcRequest {
        public String jsonrpc = "2.0"; 
        public Integer id;
        public String method;
        public JsonElement params;
    }
    
    public static class RpcResponse {
        public String jsonrpc = "2.0"; 
        public Integer id;
        public Object result;
        public ResponseError error;
    }
    
    public static class RpcNotificationMessage {
        public String jsonrpc = "2.0"; 
        public String method;
        public Object params;
    }
    
    public static class ResponseError {
        public Integer code;
        public String message;
        public Object data;
    }
    
    public static class WorkspaceFolder {
        public String uri;
        public String name;
    }
    
    public static class InitializationParams {
        public Integer processId;
        public String rootPath;
        public String rootUri;
        public WorkspaceFolder[] workspaceFolders;
    }
    
    public static class DidOpenParams {
        public TextDocument textDocument;
    }
    
    public static class DidCloseParams {
        public TextDocumentIdentifier textDocument;
    }
    
    public static class DidChangeParams {
        public VersionedTextDocumentIdentifier textDocument;
        public TextDocumentContentChangeEvent[] contentChanges;
    }
    
    public static class VersionedTextDocumentIdentifier extends TextDocumentIdentifier {
        public Integer version;
    }
    
    public static class TextDocumentContentChangeEvent {
        public Range range;
        public Integer rangeLength;
        public String text;
    }
    
    public static class Range {
        public Position start;
        public Position end;
    }
    
    public static class Position {
        public Integer line;
        public Integer character;
    }
    
    public static class Location {
        public String uri;
        public Range range;
    }
    
    public static class TextDocumentIdentifier {
        public String uri;
    }
        
    public static class TextDocument {
        public String uri;
        public String languageId;
        public Integer version;
        public String text;
    }
    
    public static class ShowMessageParams {        
        public int type;
        public String message;
    }
    
    public static class Capabilities {
        public ServerCapabilities capabilities;
    }
    
    public static class ServerCapabilities {
        public int textDocumentSync;
        public boolean definitionProvider;
        public boolean documentSymbolProvider;
        public boolean workspaceSymbolProvider;
        public CompletionOptions completionProvider;
    }
    
    public static class CompletionRegistrationOptions {
        public String[] triggerCharacters;
        public String[] allCommitCharacters;
        public Boolean resolveProvider;
    }
    
    public static class CompletionOptions {
        public Boolean resolveProvider;
        public String[] triggerCharacters;        
    }
    
    public static class Diagnostic {
        public Range range;
        public int severity;
        public String code;
        public String source;
        public String message;
        // public DiagnosticRelatedInformation[] relatedInformation;
        
    }
    
    public static class PublishDiagnosticsParams {
        public String uri;
        public List<Diagnostic> diagnostics;
    }
    
    public static class TextDocumentPositionParams {
        public TextDocumentIdentifier textDocument;
        public Position position;
    }
    
    public static class CompletionContext {
        public int triggerKind; // 1 Invoked, 2 TriggerCharacter, 3 TriggerForIncompleteCompletions
        public String triggerCharacter;
    }
    
    public static class CompletionParams extends TextDocumentPositionParams {
        public CompletionContext context;
    }
    
    public static class DidSaveTextDocumentParams {
        public TextDocumentIdentifier textDocument;
        public String text;
    }
    
    public static class DocumentSymbolParams {
        public TextDocumentIdentifier textDocument;
    }
    
    public static class WorkspaceSymbolParams {
        public String query;
    }
    
    public static class SymbolInformation {
        public String name;
        public int kind;
        public boolean deprecated;
        public Location location;
        public String containerName;
    }
    
    public static class CompletionItem {
        public String label;
        public Integer kind;
        public String detail;
        public String documentation;
        public Boolean deprecated;
        public Boolean preselect;
        public String sortText;
        public String filterText;
        public String insertText;
        //public InsertTextFormat insertTextFormat;
        //public TextEdit textEdit;
        //public TextEdit[] additionalTextEdits;
        public String[] commitCharacters;
        //public Command command;
        public Object data;
    }
    
    public static class CompletionList {
        
    }
}
