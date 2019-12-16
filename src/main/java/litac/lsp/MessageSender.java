/*
 * see license.txt
 */
package litac.lsp;

import java.io.File;
import java.util.*;

import com.google.gson.*;

import litac.compiler.PhaseResult;
import litac.compiler.PhaseResult.PhaseError;
import litac.lsp.JsonRpc.*;

/**
 * Sends messages back to the client
 * 
 * @author Tony
 *
 */
public class MessageSender {

    private Gson gson;
    private LspLogger log;
    
    public MessageSender(Gson gson, LspLogger log) {
        this.gson = gson;
        this.log = log;
    }

    public void sendMessage(Object msg) {
        String message = gson.toJson(msg);
        String output = String.format("Content-Length: %d\r\n\r\n%s", message.length(), message);        
        System.out.print(output);
        System.out.flush();        
        log.log("Sent: " + output);
    }
    
    public void sendDiagnostics(Workspace workspace, String documentUri) {
        PhaseResult result = workspace.processSource();
        List<PhaseError> errors = result.getErrors();
        
        PublishDiagnosticsParams params = new PublishDiagnosticsParams();
        params.uri = documentUri;
        params.diagnostics = Collections.emptyList();
        if(!errors.isEmpty()) {
            for(PhaseError error : errors) {
                String uri = new File(error.pos.sourceFile).toURI().toString();
                Document doc = workspace.getDocument(uri);
                if(doc == null) {
                    continue;
                }
                
                doc.errors.add(error);
            }
            
            Document doc = workspace.getDocument(documentUri);
            if(doc != null) {
                params.diagnostics = new ArrayList<>();
                for(PhaseError error : doc.errors) {
                    Diagnostic d = new Diagnostic();                
                    d.message = error.message;
                    d.severity = 1;
                    d.source = error.pos.sourceLine;
                    d.range = LspUtil.fromSrcPosLine(error.pos);
                    
                    params.diagnostics.add(d);
                }
            }
            
            doc.errors.clear();
        }
        
        RpcNotificationMessage notification = new RpcNotificationMessage();
        notification.method = "textDocument/publishDiagnostics";
        notification.params = params;
        
        sendMessage(notification);
    }
    
    public void sendDiagnostics(Workspace workspace) {
        PhaseResult result = workspace.processSource();
        List<PhaseError> errors = result.getErrors();
        if(errors.isEmpty()) {
            return;
        }
        
        for(PhaseError error : errors) {
            String uri = new File(error.pos.sourceFile).toURI().toString();
            Document doc = workspace.getDocument(uri);
            if(doc == null) {
                continue;
            }
            
            doc.errors.add(error);
        }
        
        for(Document doc : workspace.getDocuments()) {
            if(doc.errors.isEmpty()) {
                continue;
            }
            
            PublishDiagnosticsParams params = new PublishDiagnosticsParams();
            params.uri = doc.document.uri;
            params.diagnostics = new ArrayList<>();
            for(PhaseError error : doc.errors) {
                Diagnostic d = new Diagnostic();                
                d.message = error.message;
                d.severity = 1;
                d.source = error.pos.sourceLine;
                d.range = LspUtil.fromSrcPosLine(error.pos);
                
                params.diagnostics.add(d);
            }
            
            doc.errors.clear();
            
            RpcNotificationMessage notification = new RpcNotificationMessage();
            notification.method = "textDocument/publishDiagnostics";
            notification.params = params;
            
            sendMessage(notification);
        }
    }
    
    public void showMessage(MessageType type, String message) {
        ShowMessageParams params = new ShowMessageParams();
        params.type = type.getValue();
        params.message = message;
        
        RpcNotificationMessage notification = new RpcNotificationMessage();
        notification.method = "window/showMessage";
        notification.params = params;
        
        sendMessage(notification);
    }
    
    public void logMessage(MessageType type, String message) {
        ShowMessageParams params = new ShowMessageParams();
        params.type = type.getValue();
        params.message = message;
        
        RpcNotificationMessage notification = new RpcNotificationMessage();
        notification.method = "window/logMessage";
        notification.params = params;
        
        sendMessage(notification);
    }
}
