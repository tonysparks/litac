/*
 * see license.txt
 */
package litac.lsp;

import org.junit.Test;

import com.google.gson.*;

import litac.compiler.BackendOptions;
import litac.lsp.JsonRpc.*;

/**
 * @author antho
 *
 */
public class RequestHandlerTest {

    @Test
    public void test() {
        BackendOptions options = new BackendOptions();
        
        Gson gson = new GsonBuilder().create();
        LspLogger log = new LspLogger(false);
        MessageSender sender = new MessageSender(gson, log);
        RequestHandler handler = new RequestHandler(options, new Workspace(options, log), sender);
        
        String initJson = "{\"jsonrpc\":\"2.0\",\"id\":0,\"method\":\"initialize\",\"params\":{\"processId\":12372,\"rootPath\":\"c:\\\\Users\\\\antho\\\\git\\\\kraft\",\"rootUri\":\"file:///c%3A/Users/antho/git/kraft\",\"capabilities\":{\"workspace\":{\"applyEdit\":true,\"workspaceEdit\":{\"documentChanges\":true},\"didChangeConfiguration\":{\"dynamicRegistration\":true},\"didChangeWatchedFiles\":{\"dynamicRegistration\":true},\"symbol\":{\"dynamicRegistration\":true,\"symbolKind\":{\"valueSet\":[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26]}},\"executeCommand\":{\"dynamicRegistration\":true},\"configuration\":true,\"workspaceFolders\":true},\"textDocument\":{\"publishDiagnostics\":{\"relatedInformation\":true},\"synchronization\":{\"dynamicRegistration\":true,\"willSave\":true,\"willSaveWaitUntil\":true,\"didSave\":true},\"completion\":{\"dynamicRegistration\":true,\"contextSupport\":true,\"completionItem\":{\"snippetSupport\":true,\"commitCharactersSupport\":true,\"documentationFormat\":[\"markdown\",\"plaintext\"],\"deprecatedSupport\":true,\"preselectSupport\":true},\"completionItemKind\":{\"valueSet\":[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25]}},\"hover\":{\"dynamicRegistration\":true,\"contentFormat\":[\"markdown\",\"plaintext\"]},\"signatureHelp\":{\"dynamicRegistration\":true,\"signatureInformation\":{\"documentationFormat\":[\"markdown\",\"plaintext\"]}},\"definition\":{\"dynamicRegistration\":true},\"references\":{\"dynamicRegistration\":true},\"documentHighlight\":{\"dynamicRegistration\":true},\"documentSymbol\":{\"dynamicRegistration\":true,\"symbolKind\":{\"valueSet\":[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26]},\"hierarchicalDocumentSymbolSupport\":true},\"codeAction\":{\"dynamicRegistration\":true,\"codeActionLiteralSupport\":{\"codeActionKind\":{\"valueSet\":[\"\",\"quickfix\",\"refactor\",\"refactor.extract\",\"refactor.inline\",\"refactor.rewrite\",\"source\",\"source.organizeImports\"]}}},\"codeLens\":{\"dynamicRegistration\":true},\"formatting\":{\"dynamicRegistration\":true},\"rangeFormatting\":{\"dynamicRegistration\":true},\"onTypeFormatting\":{\"dynamicRegistration\":true},\"rename\":{\"dynamicRegistration\":true},\"documentLink\":{\"dynamicRegistration\":true},\"typeDefinition\":{\"dynamicRegistration\":true},\"implementation\":{\"dynamicRegistration\":true},\"colorProvider\":{\"dynamicRegistration\":true},\"foldingRange\":{\"dynamicRegistration\":true,\"rangeLimit\":5000,\"lineFoldingOnly\":true}}},\"trace\":\"off\",\"workspaceFolders\":[{\"uri\":\"file:///c%3A/Users/antho/git/kraft\",\"name\":\"kraft\"}]}}";
        RpcRequest initReq = gson.fromJson(initJson, RpcRequest.class);
        InitializationParams initParams = gson.fromJson(initReq.params, InitializationParams.class);
        handler.handleInitialize(initReq, initParams);
        
        String openJson = "{\"jsonrpc\":\"2.0\",\"method\":\"textDocument/didOpen\",\"params\":{\"textDocument\":{\"uri\":\"file:///c%3A/Users/antho/git/kraft/src/renderer.lita\",\"languageId\":\"litac\",\"version\":1,\"text\":\"import \\\"opengl\\\"\\r\\nimport \\\"cglm/cglm\\\"\\r\\nimport \\\"shader\\\"\\r\\nimport \\\"entity\\\"\\r\\nimport \\\"model\\\"\\r\\n\\r\\npublic func Prepare() {    \\r\\n    glEnable(GL_DEPTH_TEST)    \\r\\n    //glLogicOp(GL_INVERT)\\r\\n//    glEnable(GL_BLEND)\\r\\n//    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)\\r\\n    glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT)\\r\\n    glClearColor(0f, 0.5f, 0.75f, 1f)    \\r\\n}\\r\\n\\r\\npublic func BatchEntityRender(entities: Entity*, len: i32, model: Model*, shader: ShaderProgram, viewMatrix: mat4) {\\r\\n    shader.bind()\\r\\n    shader.loadMat4ByName(\\\"viewMatrix\\\", viewMatrix)\\r\\n    shader.loadFloatByName(\\\"shineDamper\\\", 10f)\\r\\n    shader.loadFloatByName(\\\"reflectivity\\\", 1f)\\r\\n\\r\\n    glEnableVertexAttribArray(AttribPos.POSITION)\\r\\n    glEnableVertexAttribArray(AttribPos.NORMAL)\\r\\n    glEnableVertexAttribArray(AttribPos.TEXCOORD)\\r\\n    \\r\\n    glBindVertexArray(model.vao)\\r\\n    glActiveTexture(GL_TEXTURE0)\\r\\n    glBindTexture(GL_TEXTURE_2D, model.tex.texId)\\r\\n\\r\\n    var transformMatrix: mat4;\\r\\n\\r\\n    for(var i = 0; i < len; i +=1) {\\r\\n        var ent = &entities[i]\\r\\n        ent.transform(transformMatrix)\\r\\n        shader.loadMat4ByName(\\\"transformationMatrix\\\", transformMatrix)\\r\\n\\r\\n        glDrawElements(GL_TRIANGLES, model.indexCount, GL_UNSIGNED_INT, 0)\\r\\n    }\\r\\n    \\r\\n    glDisableVertexAttribArray(AttribPos.POSITION)\\r\\n    glDisableVertexAttribArray(AttribPos.NORMAL)\\r\\n    glDisableVertexAttribArray(AttribPos.TEXCOORD)\\r\\n\\r\\n    shader.unbind()\\r\\n}\"}}}";
        RpcRequest openReq = gson.fromJson(openJson, RpcRequest.class);
        DidOpenParams openParams = gson.fromJson(openReq.params, DidOpenParams.class);
        handler.handleTextDocumentDidOpen(openReq, openParams);
        
        String changeJson = "{\"jsonrpc\":\"2.0\",\"method\":\"textDocument/didChange\",\"params\":{\"textDocument\":{\"uri\":\"file:///c%3A/Users/antho/git/kraft/src/renderer.lita\",\"version\":4},\"contentChanges\":[{\"range\":{\"start\":{\"line\":6,\"character\":1},\"end\":{\"line\":6,\"character\":1}},\"rangeLength\":0,\"text\":\"/\"}]}}";
        RpcRequest changeReq = gson.fromJson(changeJson, RpcRequest.class);
        DidChangeParams changeParams = gson.fromJson(changeReq.params, DidChangeParams.class);
        handler.handleTextDocumentDidChange(changeReq, changeParams);
        
        //String defJson = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"textDocument/definition\",\"params\":{\"textDocument\":{\"uri\":\"file:///c%3A/Users/antho/git/kraft/src/renderer.lita\"},\"position\":{\"line\":15,\"character\":45}}}";
        String defJson = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"textDocument/definition\",\"params\":{\"textDocument\":{\"uri\":\"file:///c%3A/Users/antho/git/kraft/src/renderer.lita\"},\"position\":{\"line\":21,\"character\":14}}}";
        RpcRequest defReq = gson.fromJson(defJson, RpcRequest.class);
        TextDocumentPositionParams defParams = gson.fromJson(defReq.params, TextDocumentPositionParams.class);
        handler.handleTextDocumentDefinition(defReq, defParams);
        
        String symbolJson = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"workspace/symbol\",\"params\":{\"query\":\"\"}}";
        RpcRequest symReq = gson.fromJson(symbolJson, RpcRequest.class);
        WorkspaceSymbolParams symParams = gson.fromJson(symReq.params, WorkspaceSymbolParams.class);
        //handler.handleWorkspaceSymbol(symReq, symParams);
        
        
    }

}
