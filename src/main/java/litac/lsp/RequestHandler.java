/*
 * see license.txt
 */
package litac.lsp;

import java.io.File;
import java.util.*;

import litac.compiler.BackendOptions;
import litac.lsp.JsonRpc.*;

/**
 * Handles requests from the client
 * 
 * @author Tony
 *
 */
public class RequestHandler {

    private Workspace workspace;
    private MessageSender sender;
    
    public RequestHandler(BackendOptions options,
                          Workspace workspace, 
                          MessageSender sender) {
        this.workspace = workspace;
        this.sender = sender;
    }
    
    private File scanRootModule(File sourcePath) {
        // TODO: Scan the folder structures and look for the 'main' class?
        // or should this be an arg???
        return new File(sourcePath, "/src/main.lita");
    }

    public void handleInitialize(RpcRequest rpc, InitializationParams msg) {
        File sourcePath = new File(msg.rootPath);
        File rootModule;
        
        if(sourcePath.isFile()) {
            rootModule = sourcePath;
        }
        else {
            rootModule = scanRootModule(sourcePath);
        }
                
        this.workspace.setRoot(rootModule);
        
        Capabilities capabilities = new Capabilities();
        capabilities.capabilities = new ServerCapabilities();
        capabilities.capabilities.textDocumentSync = 2;
        capabilities.capabilities.definitionProvider = true;
        capabilities.capabilities.documentSymbolProvider = true;
        capabilities.capabilities.workspaceSymbolProvider = true;
        capabilities.capabilities.completionProvider = new CompletionOptions();
        capabilities.capabilities.completionProvider.resolveProvider = true;
        capabilities.capabilities.completionProvider.triggerCharacters = new String[]{"."};
        
        RpcResponse response = new RpcResponse();
        response.id = rpc.id;
        response.result = capabilities;
        this.sender.sendMessage(response);
    }
    
    public void handleTextDocumentDidOpen(RpcRequest rpc, DidOpenParams params) {        
        this.workspace.addDocument(params.textDocument);        
        this.sender.sendDiagnostics(this.workspace, params.textDocument.uri);
    }
    
    public void handleTextDocumentDidClose(RpcRequest rpc, DidCloseParams params) {        
        this.workspace.removeDocument(params.textDocument.uri);        
    }
    
    public void handleTextDocumentDidChange(RpcRequest rpc, DidChangeParams params) {        
        this.workspace.changedDocument(params.textDocument.uri, params);
        this.sender.sendDiagnostics(this.workspace, params.textDocument.uri);
    }
    
    public void handleTextDocumentDidSave(RpcRequest rpc, DidSaveTextDocumentParams params) {        
        this.workspace.saveDocument(params);
        this.sender.sendDiagnostics(this.workspace, params.textDocument.uri);
    }
    
    public void handleTextDocumentCompletion(RpcRequest rpc, CompletionParams params) {
        Document doc = this.workspace.getDocument(params.textDocument.uri);
        List<CompletionItem> items = Collections.emptyList();
        if(doc != null) {
            items = doc.getAutoCompletionList(workspace, params.position);
        }
        
        RpcResponse resp = new RpcResponse();
        resp.id = rpc.id;
        resp.result = items;
        this.sender.sendMessage(resp);
    }
    
    public void handleTextDocumentDefinition(RpcRequest rpc, TextDocumentPositionParams params) {
        Document doc = this.workspace.getDocument(params.textDocument.uri);
        Location location = null;
        if(doc != null) {
            location = doc.getDefinitionLocation(this.workspace, params.position);
        }
        
        RpcResponse resp = new RpcResponse();
        resp.id = rpc.id;
        resp.result = location;
        this.sender.sendMessage(resp);
    }
    
    public void handleTextDocumentDocumentSymbol(RpcRequest rpc, DocumentSymbolParams params) {
        Document doc = this.workspace.getDocument(params.textDocument.uri);
        List<SymbolInformation> symbols = Collections.emptyList();
        if(doc != null) {
            this.workspace.processSource(params.textDocument.uri);
            symbols = doc.getSymbols(this.workspace.getLatestProgram());
        }
        
        RpcResponse resp = new RpcResponse();
        resp.id = rpc.id;
        resp.result = symbols;
        this.sender.sendMessage(resp);
    }
    
    public void handleWorkspaceSymbol(RpcRequest rpc, WorkspaceSymbolParams params) {
        List<SymbolInformation> symbols = this.workspace.findSymbols(params.query);
        
        RpcResponse resp = new RpcResponse();
        resp.id = rpc.id;
        resp.result = symbols;
        this.sender.sendMessage(resp);
    }
}
