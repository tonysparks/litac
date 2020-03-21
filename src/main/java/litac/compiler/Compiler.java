/*
 * see license.txt
 */
package litac.compiler;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

import litac.LitaOptions;
import litac.ast.*;
import litac.ast.Node.SrcPos;
import litac.checker.TypeResolver;
import litac.compiler.c.CTranspiler;
import litac.doc.DocGen;
import litac.util.Profiler;
import litac.util.Profiler.Segment;
import litac.compiler.Module;

/**
 * @author Tony
 *
 */
public class Compiler {

    private LitaOptions options;
    
    public Compiler(LitaOptions options) {
        this.options = options;
    }
    
    public static CompileException error(Stmt stmt, String message, Object ...args) {
        return new CompileException(String.format(message, args) + 
                String.format(" at line %d in '%s'", stmt.getLineNumber(), stmt.getSourceName()));
    }
    
    public PhaseResult compile(File rootModule) throws Exception {
        PhaseResult result = new PhaseResult();
        
        try {
            CompilationUnit unit = parse(this.options, rootModule, result);
            Program program = typeCheck(options, result, unit);
            
            if(this.options.reflectionEnabled()) {
                reflection(program, unit);
            }
            
            if(this.options.generateDocs) {
                generateDocs(options, program);
            }
            
            if(!result.hasErrors() && !options.checkerOnly) {
                compile(options, result, unit, program);
            }
        }
        catch(Exception e) {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            result.addError((SrcPos)null, "internal compiler error: %s", writer.toString());
        }
                
        return result;
    }
    
    private CompilationUnit parse(LitaOptions options, File rootModule, PhaseResult result) throws IOException {
        try(Segment s = Profiler.startSegment("Lexing/Parsing")) {
            CompilationUnit unit = CompilationUnit.modules(this.options, rootModule, result);
            return unit;
        }
    }
    
    private Program typeCheck(LitaOptions options, PhaseResult result, CompilationUnit unit) {
        try(Segment s = Profiler.startSegment("Type Checker")) {
            TypeResolver resolver = new TypeResolver(options, result, unit);
                    
            Program program = resolver.resolveTypes();
            return program;
        }
    }
    
    
    /**
     * Will scan all global declarations and depending on the configuration create
     * the appropriate TypeInfo's representing the declarations.
     * 
     * @param program
     * @param unit
     */
    private void reflection(Program program, CompilationUnit unit) {
        try(Segment s = Profiler.startSegment("Reflection Generation")) {
            List<Decl> declarations = program.getSymbols().stream()
                    .filter(sym -> !sym.isLocal() && !sym.isBuiltin())
                    .map(sym -> sym.decl)
                    .collect(Collectors.toList());
            
            PhaseResult result = new PhaseResult();
            TypeResolver resolver = new TypeResolver(this.options, result, unit);
            Reflection reflection = new Reflection(program, 
                                                   this.options.typeInfo);
            
            ModuleId type = ModuleId.fromDirectory(options.libDir, "type");
            Module typeModule = program.getModule(type);
            reflection.createTypeInfos(typeModule, resolver, declarations);
        }
    }
    
    private void generateDocs(LitaOptions options, Program program) {
        DocGen docGen = new DocGen(options);
        docGen.generate(program);
    }
    
    private void compile(LitaOptions options, 
                                PhaseResult checkerResult, 
                                CompilationUnit unit,
                                Program program) throws Exception {
        
        switch(options.backendType) {            
            case C: {
                CTranspiler.transpile(checkerResult, unit, program, options);
                break;
            }
            default: {
                throw error(null, "unsupported backend type '%s'", options.backendType.toString());
            }
        }      
        
    }

}
