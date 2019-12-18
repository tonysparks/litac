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
        
//        String openJson = "{\"jsonrpc\":\"2.0\",\"method\":\"textDocument/didOpen\",\"params\":{\"textDocument\":{\"uri\":\"file:///c%3A/Users/antho/git/kraft/src/renderer.lita\",\"languageId\":\"litac\",\"version\":1,\"text\":\"import \\\"opengl\\\"\\r\\nimport \\\"cglm/cglm\\\"\\r\\nimport \\\"shader\\\"\\r\\nimport \\\"entity\\\"\\r\\nimport \\\"model\\\"\\r\\n\\r\\npublic func Prepare() {    \\r\\n    glEnable(GL_DEPTH_TEST)    \\r\\n    //glLogicOp(GL_INVERT)\\r\\n//    glEnable(GL_BLEND)\\r\\n//    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)\\r\\n    glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT)\\r\\n    glClearColor(0f, 0.5f, 0.75f, 1f)    \\r\\n}\\r\\n\\r\\npublic func BatchEntityRender(entities: Entity*, len: i32, model: Model*, shader: ShaderProgram, viewMatrix: mat4) {\\r\\n    shader.bind()\\r\\n    shader.loadMat4ByName(\\\"viewMatrix\\\", viewMatrix)\\r\\n    shader.loadFloatByName(\\\"shineDamper\\\", 10f)\\r\\n    shader.loadFloatByName(\\\"reflectivity\\\", 1f)\\r\\n\\r\\n    glEnableVertexAttribArray(AttribPos.POSITION)\\r\\n    glEnableVertexAttribArray(AttribPos.NORMAL)\\r\\n    glEnableVertexAttribArray(AttribPos.TEXCOORD)\\r\\n    \\r\\n    glBindVertexArray(model.vao)\\r\\n    glActiveTexture(GL_TEXTURE0)\\r\\n    glBindTexture(GL_TEXTURE_2D, model.tex.texId)\\r\\n\\r\\n    var transformMatrix: mat4;\\r\\n\\r\\n    for(var i = 0; i < len; i +=1) {\\r\\n        var ent = &entities[i]\\r\\n        ent.transform(transformMatrix)\\r\\n        shader.loadMat4ByName(\\\"transformationMatrix\\\", transformMatrix)\\r\\n\\r\\n        glDrawElements(GL_TRIANGLES, model.indexCount, GL_UNSIGNED_INT, 0)\\r\\n    }\\r\\n    \\r\\n    glDisableVertexAttribArray(AttribPos.POSITION)\\r\\n    glDisableVertexAttribArray(AttribPos.NORMAL)\\r\\n    glDisableVertexAttribArray(AttribPos.TEXCOORD)\\r\\n\\r\\n    shader.unbind()\\r\\n}\"}}}";
        //String openJson = "{\"jsonrpc\":\"2.0\",\"method\":\"textDocument/didOpen\",\"params\":{\"textDocument\":{\"uri\":\"file:///c%3A/Users/antho/git/kraft/src/main.lita\",\"languageId\":\"litac\",\"version\":1,\"text\":\"import \\\"io\\\"\\r\\nimport \\\"common\\\"\\r\\nimport \\\"input_system\\\"\\r\\nimport \\\"game\\\"\\r\\n\\r\\n\\r\\nimport \\\"opengl\\\"\\r\\nimport \\\"glfw\\\"\\r\\nimport \\\"model\\\"\\r\\nimport \\\"renderer\\\"\\r\\nimport \\\"texture\\\"\\r\\nimport \\\"shader\\\"\\r\\nimport \\\"camera\\\"\\r\\nimport \\\"light\\\"\\r\\nimport \\\"entity\\\"\\r\\nimport \\\"math\\\"\\r\\nimport \\\"obj_loader\\\"\\r\\nimport \\\"block\\\"\\r\\nimport \\\"chunk\\\"\\r\\n\\r\\nimport \\\"libc\\\" as c\\r\\n\\r\\nimport \\\"cglm/cglm\\\"\\r\\n\\r\\n\\r\\n@cFile(\\\"glad.h\\\");\\r\\n@raw(\\\"\\\"\\\"\\r\\n#include <GLFW/glfw3.h>\\r\\n#include <cglm/cglm.h>\\r\\n\\\"\\\"\\\");\\r\\n\\r\\nimport \\\"stb_image\\\" as stb;\\r\\n\\r\\nconst WIDTH = 1280\\r\\nconst HEIGHT = 720\\r\\n\\r\\nconst FOV = 90f\\r\\nconst NEAR_PLANE = 0.01f\\r\\nconst FAR_PLANE  = 10_000f\\r\\n\\r\\nvar isRunning = false;\\r\\n//var REMOVE_ME : Test;\\r\\n/*\\r\\nvar model = Model{};\\r\\nvar model2 = Model{};\\r\\n*/\\r\\n\\r\\nfunc array_length<T>(array: T*) : i32 {\\r\\n    return (sizeof(array) / sizeof(array[0])) as (i32)\\r\\n}\\r\\n\\r\\nvar texture: Texture;\\r\\nvar shader: ShaderProgram;\\r\\n\\r\\nvar camera = Camera{\\r\\n    .position = vec3{0,0,20},\\r\\n    .target = vec3{0,0,0},\\r\\n    .up = vec3{0,1f,0},\\r\\n    .fov = FOV,\\r\\n    .pitch = 0,\\r\\n    .yaw = 0,\\r\\n    .roll = 0\\r\\n}\\r\\nvar projectionMatrix: mat4;\\r\\nvar transformMatrix: mat4;\\r\\nvar viewMatrix: mat4;\\r\\nvar window: GLFWwindow*;\\r\\n\\r\\nvar box = Entity {\\r\\n    .pos = vec3{0, 0, 0},\\r\\n    .rot = vec3{0, 0, 0},\\r\\n    .scale = 1f,\\r\\n    .xform = mat4{0}\\r\\n}\\r\\n\\r\\nconst MAX_CHUNKS = 10\\r\\nvar chunks: [MAX_CHUNKS]Chunk;\\r\\n\\r\\nconst GX = 40\\r\\nconst GY = 40\\r\\nconst SIZE = GY*GX\\r\\nvar ground: [SIZE]Entity;\\r\\n\\r\\nvar light = Light {\\r\\n    .pos = vec3{0, 100, 0},\\r\\n    .color = vec3{1, 1, 1},\\r\\n}\\r\\n\\r\\nfunc CmdQuit(args: char const*) {\\r\\n    isRunning = false;\\r\\n}\\r\\n\\r\\nfunc Init() {\\r\\n    camera.init()\\r\\n\\r\\n    var fragSource: char*;\\r\\n    var status = ReadFile(\\\"../assets/shaders/basicFragment.glsl\\\", &fragSource)\\r\\n\\r\\n    var vertSource: char*;\\r\\n    ReadFile(\\\"../assets/shaders/basicVertex.glsl\\\", &vertSource)\\r\\n\\r\\n    shader = LoadProgram(vertSource, fragSource)        \\r\\n    glCheckErrorPrint()\\r\\n    shader.bindAttr(AttribPos.POSITION, \\\"position\\\")\\r\\n    shader.bindAttr(AttribPos.NORMAL, \\\"normal\\\")\\r\\n    shader.bindAttr(AttribPos.TEXCOORD, \\\"texCoord\\\")\\r\\n    shader.link()\\r\\n    shader.validate()\\r\\n\\r\\n    glCheckErrorPrint()\\r\\n\\r\\n    texture = LoadTextureFromFile(\\\"../assets/texture.png\\\")\\r\\n/*    \\r\\n    printf(\\\"Width: %f Height: %f\\\\n\\\", texture.width, texture.height)\\r\\n    var indexLength = 36; // array_length<u32>(indices)\\r\\n    var dataLength =  8 * 4 * 6;// array_length<f32>(data)\\r\\n    printf(\\\"I: %d, D: %d\\\\n\\\", indexLength, dataLength)\\r\\n    LoadModel(indices, indexLength, data, dataLength, texture, &model)\\r\\n*/\\r\\n\\r\\n    LoadBlocks(texture)\\r\\n\\r\\n    var z = 0\\r\\n    var x = 0\\r\\n    for(var i = 0; i < MAX_CHUNKS; i += 1) {\\r\\n        if(i % 5 == 0) {\\r\\n            z += 1\\r\\n            x = 0\\r\\n        }\\r\\n        chunks[i].init(vec3{CHUNK_WIDTH*(x*2),0,CHUNK_DEPTH*(z*2)})\\r\\n        x += 1\\r\\n    }\\r\\n\\r\\n    //LoadOBJFromFile(\\\"../assets/skull.obj\\\", &model2)\\r\\n\\r\\n/*\\r\\n    var i = 0\\r\\n    for(var z = 0; z < GY; z+=1) {\\r\\n        for(var x = 0; x < GX; x+=1) {\\r\\n            var t = &ground[i]\\r\\n            t.pos[0] = x * 2;\\r\\n            t.pos[1] = 0;\\r\\n            t.pos[2] = z * -2;\\r\\n            t.scale = 1f\\r\\n            i+=1\\r\\n        }\\r\\n    }\\r\\n*/    \\r\\n\\r\\n    var aspectRatio = (GetWindowWidth() as (f32) / GetWindowHeight() as (f32)) as (f32);\\r\\n\\tvar yScale = ((1f / c::tan(toRadians(FOV /2f))) * aspectRatio) as (f32);\\r\\n\\tvar xScale = yScale / aspectRatio;\\r\\n    var frustumLength = FAR_PLANE - NEAR_PLANE;\\r\\n\\r\\n    glm_mat4_identity(projectionMatrix)\\r\\n    projectionMatrix[0][0] = xScale;\\r\\n    projectionMatrix[1][1] = yScale;\\r\\n    projectionMatrix[2][2] = -((FAR_PLANE + NEAR_PLANE) / frustumLength);\\r\\n    projectionMatrix[2][3] = -1;\\r\\n    projectionMatrix[3][2] = -((2 * NEAR_PLANE * FAR_PLANE) / frustumLength);\\r\\n    projectionMatrix[3][3] = 0;\\r\\n\\r\\n    printf(\\\"Aspect: %f\\\\n\\\", aspectRatio)    \\r\\n    PrintMatrix(projectionMatrix)\\r\\n\\r\\n    glm_mat4_identity(transformMatrix)\\r\\n    glm_mat4_identity(viewMatrix)  \\r\\n\\r\\n    shader.bind()\\r\\n    shader.loadMat4ByName(\\\"transformationMatrix\\\", transformMatrix)\\r\\n    shader.loadMat4ByName(\\\"projectionMatrix\\\", projectionMatrix)\\r\\n    shader.loadMat4ByName(\\\"viewMatrix\\\", viewMatrix)\\r\\n    shader.loadVec3ByName(\\\"lightPos\\\", light.pos)\\r\\n    shader.loadVec3ByName(\\\"lightColor\\\", light.color)\\r\\n    shader.unbind()\\r\\n}\\r\\n\\r\\nfunc Update(timeStep: TimeStep*) {    \\r\\n    InputSysUpdate(timeStep);\\r\\n//    ConsoleUpdate(timeStep);\\r\\n\\r\\n   // box.update(timeStep)\\r\\n    GameUpdate(timeStep)\\r\\n\\r\\n    var controls = 0\\r\\n    if(InputSysIsKeyPressed(GLFW_KEY_W)) {\\r\\n        controls |= MovementControls.MOVE_FORWARD\\r\\n    }\\r\\n    else if(InputSysIsKeyPressed(GLFW_KEY_S)) {\\r\\n        controls |= MovementControls.MOVE_BACKWARD\\r\\n    }\\r\\n\\r\\n    if(InputSysIsKeyPressed(GLFW_KEY_A)) {\\r\\n        controls |= MovementControls.MOVE_LEFT\\r\\n    }\\r\\n    else if(InputSysIsKeyPressed(GLFW_KEY_D)) {\\r\\n        controls |= MovementControls.MOVE_RIGHT\\r\\n    }\\r\\n\\r\\n    if(InputSysIsKeyPressed(GLFW_KEY_Q)) {\\r\\n        controls |= MovementControls.MOVE_UP\\r\\n    }\\r\\n    else if(InputSysIsKeyPressed(GLFW_KEY_E)) {\\r\\n        controls |= MovementControls.MOVE_DOWN\\r\\n    }\\r\\n\\r\\n    if(InputSysIsKeyPressed(GLFW_KEY_LEFT_SHIFT)) {\\r\\n        controls |= MovementControls.LEAN_LEFT\\r\\n    }\\r\\n    else if(InputSysIsKeyPressed(GLFW_KEY_SPACE)) {\\r\\n        controls |= MovementControls.LEAN_RIGHT\\r\\n    }\\r\\n\\r\\n    var m = MousePos{0,0}\\r\\n    InputSysGetMouseDeltaPos(m)\\r\\n    camera.move(controls, m[0], m[1])\\r\\n\\r\\n    glm_mat4_identity(viewMatrix)    \\r\\n    camera.viewMatrix(viewMatrix)\\r\\n}\\r\\n\\r\\nfunc Draw(alpha: f32) {\\r\\n    glViewport(0, 0, WIDTH, HEIGHT)\\r\\n    Prepare()\\r\\n\\r\\n    for(var i = 0; i < MAX_CHUNKS; i+=1) {        \\r\\n        chunks[i].draw(shader, texture, viewMatrix)\\r\\n    }\\r\\n//    BatchEntityRender(ground, SIZE, &model, shader, viewMatrix)\\r\\n//    GameDraw();\\r\\n//   ConsoleDraw();\\r\\n}\\r\\n\\r\\nfunc GetWindowWidth() : i32 {\\r\\n    if(!window) {\\r\\n        return 0;\\r\\n    }\\r\\n\\r\\n    var width: i32;\\r\\n    var height: i32;\\r\\n    glfwGetWindowSize(window, &width, &height);\\r\\n    return width;\\r\\n}\\r\\n\\r\\nfunc GetWindowHeight() : i32 {\\r\\n    if(!window) {\\r\\n        return 0;\\r\\n    }\\r\\n    var width: i32;\\r\\n    var height: i32;\\r\\n    glfwGetWindowSize(window, &width, &height);\\r\\n    return height;\\r\\n}\\r\\n\\r\\nfunc main(len: i32, args: char**) : i32 {\\r\\n    glfwSetErrorCallback(errorCallback);\\r\\n    \\r\\n    if (!glfwInit()) {\\r\\n        log(LogLevel.FATAL, \\\"Failed to initialize glfw!\\\\n\\\")\\r\\n    }    \\r\\n    defer glfwTerminate();\\r\\n\\r\\n    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);\\r\\n    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);\\r\\n    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);\\r\\n    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);\\r\\n\\r\\n    window = glfwCreateWindow(WIDTH, HEIGHT, \\\"Kraft!\\\", null, null);\\r\\n    if (!window) {\\r\\n        log(LogLevel.FATAL, \\\"Failed to initialize window context!\\\\n\\\")\\r\\n    }\\r\\n    defer glfwDestroyWindow(window);\\r\\n\\r\\n    glfwSetKeyCallback(window, keyCallback);\\r\\n    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);\\r\\n    \\r\\n    glfwMakeContextCurrent(window);\\r\\n    gladLoadGLLoader(glfwGetProcAddress as (GLADloadproc));\\r\\n    glfwSwapInterval(1);\\r\\n\\r\\n    Init()\\r\\n\\r\\n    glEnable(GL_CULL_FACE)\\r\\n    glCullFace(GL_BACK)\\r\\n    //glFrontFace(GL_CW)\\r\\n\\r\\n    var currentTime = GetTicks()\\r\\n    var accumulator = 0_i64\\r\\n    var previousTime : Time = 0_i64\\r\\n\\r\\n    const maxIterations = 10\\r\\n    const maxDelta = 250_i64\\r\\n    const frameRate = 30_i64\\r\\n    const fixedTimeStep = 1000_i64 / frameRate\\r\\n\\r\\n    const timeStep = TimeStep {\\r\\n        .frameTime: fixedTimeStep,\\r\\n        .gameClock: 0_i64,\\r\\n        .frameTimeFraction: fixedTimeStep as (f64) / 1000_f64\\r\\n    }\\r\\n\\r\\n    isRunning = true;\\r\\n\\r\\n    while (!glfwWindowShouldClose(window) && isRunning) {\\r\\n        /*\\r\\n        while(deltaTime >= fixedTimeStep) {\\r\\n            timeStep.gameClock = gameClock\\r\\n            timeStep.frameTime = deltaTime\\r\\n\\r\\n            glfwPollEvents()\\r\\n            Update(&timeStep)\\r\\n\\r\\n            deltaTime   -= fixedTimeStep\\r\\n            gameClock   += fixedTimeStep\\r\\n            currentTime += fixedTimeStep\\r\\n            \\r\\n            if(deltaTime >= 4 * fixedTimeStep) {\\r\\n                currentTime = GetTicks()\\r\\n                break;\\r\\n            }\\r\\n        }*/\\r\\n        var newTime = GetTicks()\\r\\n        var deltaTime = newTime - currentTime;\\r\\n\\r\\n        // don't let the game get too far behind\\r\\n        if(deltaTime > maxDelta) {\\r\\n            deltaTime = maxDelta;\\r\\n        }\\r\\n        \\r\\n        glfwPollEvents()\\r\\n\\r\\n        currentTime = newTime\\r\\n        accumulator += deltaTime\\r\\n        var iteration = 0\\r\\n\\r\\n        while(accumulator >= fixedTimeStep && iteration < maxIterations) {\\r\\n            Update(&timeStep)\\r\\n\\r\\n            timeStep.gameClock += fixedTimeStep\\r\\n            accumulator        -= fixedTimeStep\\r\\n            iteration          += 1                \\r\\n        }\\r\\n\\r\\n        var alpha : f32 = (accumulator as (f64) / fixedTimeStep as (f64)) as (f32)        \\r\\n        Draw(alpha)\\r\\n        glfwSwapBuffers(window);\\r\\n    }\\r\\n\\r\\n\\r\\n}\\r\\n\\r\\npublic func GetKeyState(key: i32) : i32 {\\r\\n    return glfwGetKey(window, key)\\r\\n}\\r\\n\\r\\npublic func GetMousePos(mousePos: MousePos) {\\r\\n    var x:f64;\\r\\n    var y:f64;\\r\\n\\r\\n    glfwGetCursorPos(window, &x, &y)\\r\\n\\r\\n    mousePos[0] = x as (f32)\\r\\n    mousePos[1] = y as (f32)\\r\\n\\r\\n    //printf(\\\"Mouse: %f,%f\\\\n\\\", x, y)\\r\\n}\\r\\n\\r\\npublic func GetMouseButtonState(btn: i32) : i32 {\\r\\n    return glfwGetMouseButton(window, btn)\\r\\n}\\r\\n\\r\\nfunc keyCallback(window: GLFWwindow*, key: i32, scancode: i32, action: i32, mods: i32) {\\r\\n    if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS)\\r\\n        glfwSetWindowShouldClose(window, GLFW_TRUE);\\r\\n}\\r\\n\\r\\nfunc errorCallback(error: i32, description: char const*) {\\r\\n    log(LogLevel.ERROR, \\\"%s\\\", description)\\r\\n}\\r\\n\\r\\n\\r\\n\"}}}";
        String openJson = "{\"jsonrpc\":\"2.0\",\"method\":\"textDocument/didOpen\",\"params\":{\"textDocument\":{\"uri\":\"file:///c%3A/Users/antho/git/kraft/src/test.lita\",\"languageId\":\"litac\",\"version\":1,\"text\":\"struct X {\\r\\n    test: i32\\r\\n    blah: i32\\r\\n}\\r\\n\\r\\nfunc (x: X) fn() : i32{\\r\\n    return 0;\\r\\n}\\r\\n\\r\\nfunc test() {\\r\\n    var x = X{}\\r\\n    var t = x.fn()\\r\\n}\"}}}";
        RpcRequest openReq = gson.fromJson(openJson, RpcRequest.class);
        DidOpenParams openParams = gson.fromJson(openReq.params, DidOpenParams.class);
        handler.handleTextDocumentDidOpen(openReq, openParams);
        
        String changeJson = "{\"jsonrpc\":\"2.0\",\"method\":\"textDocument/didChange\",\"params\":{\"textDocument\":{\"uri\":\"file:///c%3A/Users/antho/git/kraft/src/renderer.lita\",\"version\":4},\"contentChanges\":[{\"range\":{\"start\":{\"line\":6,\"character\":1},\"end\":{\"line\":6,\"character\":1}},\"rangeLength\":0,\"text\":\"/\"}]}}";
        RpcRequest changeReq = gson.fromJson(changeJson, RpcRequest.class);
        DidChangeParams changeParams = gson.fromJson(changeReq.params, DidChangeParams.class);
        //handler.handleTextDocumentDidChange(changeReq, changeParams);
        
        //String defJson = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"textDocument/definition\",\"params\":{\"textDocument\":{\"uri\":\"file:///c%3A/Users/antho/git/kraft/src/renderer.lita\"},\"position\":{\"line\":15,\"character\":45}}}";
        //String defJson = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"textDocument/definition\",\"params\":{\"textDocument\":{\"uri\":\"file:///c%3A/Users/antho/git/kraft/src/renderer.lita\"},\"position\":{\"line\":21,\"character\":14}}}";
        //String defJson = "{\"jsonrpc\":\"2.0\",\"id\":59,\"method\":\"textDocument/definition\",\"params\":{\"textDocument\":{\"uri\":\"file:///c%3A/Users/antho/git/kraft/src/main.lita\"},\"position\":{\"line\":54,\"character\":15}}}";
        //String defJson = "{\"jsonrpc\":\"2.0\",\"id\":3,\"method\":\"textDocument/definition\",\"params\":{\"textDocument\":{\"uri\":\"file:///c%3A/Users/antho/git/kraft/src/main.lita\"},\"position\":{\"line\":55,\"character\":18}}}";
        //String defJson = "{\"jsonrpc\":\"2.0\",\"id\":3,\"method\":\"textDocument/definition\",\"params\":{\"textDocument\":{\"uri\":\"file:///c%3A/Users/antho/git/kraft/src/main.lita\"},\"position\":{\"line\":58,\"character\":12}}}";
        //String defJson = "{\"jsonrpc\":\"2.0\",\"id\":3,\"method\":\"textDocument/definition\",\"params\":{\"textDocument\":{\"uri\":\"file:///c%3A/Users/antho/git/kraft/src/main.lita\"},\"position\":{\"line\":76,\"character\":20}}}";
        //String defJson = "{\"jsonrpc\":\"2.0\",\"id\":3,\"method\":\"textDocument/definition\",\"params\":{\"textDocument\":{\"uri\":\"file:///c%3A/Users/antho/git/kraft/src/main.lita\"},\"position\":{\"line\":103,\"character\":34}}}";
        String defJson = "{\"jsonrpc\":\"2.0\",\"id\":2,\"method\":\"textDocument/definition\",\"params\":{\"textDocument\":{\"uri\":\"file:///c%3A/Users/antho/git/kraft/src/test.lita\"},\"position\":{\"line\":11,\"character\":16}}}";
        RpcRequest defReq = gson.fromJson(defJson, RpcRequest.class);
        TextDocumentPositionParams defParams = gson.fromJson(defReq.params, TextDocumentPositionParams.class);
        handler.handleTextDocumentDefinition(defReq, defParams);
        
        String symbolJson = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"workspace/symbol\",\"params\":{\"query\":\"\"}}";
        RpcRequest symReq = gson.fromJson(symbolJson, RpcRequest.class);
        WorkspaceSymbolParams symParams = gson.fromJson(symReq.params, WorkspaceSymbolParams.class);
        //handler.handleWorkspaceSymbol(symReq, symParams);
        
        
    }

}
