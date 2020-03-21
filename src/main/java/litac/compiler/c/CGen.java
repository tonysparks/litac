/*
 * see license.txt
 */
package litac.compiler.c;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import litac.*;
import litac.LitaOptions.OutputType;
import litac.ast.*;
import litac.ast.Decl.*;
import litac.ast.Expr.*;
import litac.ast.Stmt.*;
import litac.checker.*;
import litac.checker.TypeInfo.*;
import litac.compiler.*;
import litac.compiler.FieldPath.FieldPathNode;
import litac.compiler.c.CTranspiler.COptions;
import litac.util.*;
import litac.util.Stack;
import litac.compiler.Module;

/**
 * Writes out the AST nodes into a single C file.  It will place all type
 * forward declarations at the top, then traverse each module and print out
 * the implementations, finishing up with the main module.
 * 
 * @author Tony
 *
 */
public class CGen {


    private static final Map<Character, String> escapeChars = new HashMap<>();
    static {
        //escapeChars.put('\a', "\\a");
        escapeChars.put('\b', "\\b");
        //escapeChars.put('\e', "\\e");
        escapeChars.put('\f', "\\f");
        escapeChars.put('\n', "\\n");
        escapeChars.put('\r', "\\r");
        escapeChars.put('\t', "\\t");
        //escapeChars.put('\v', "\\v");
        escapeChars.put('\\', "\\\\");
        escapeChars.put('\'', "\\'");
        escapeChars.put('\"', "\\\"");
        //escapeChars.put('\?', "\\?");
        escapeChars.put('\0', "\\0");
    }
    
    private class Scope {
        Stack<String> constDefs;
        Stack<DeferStmt> defers;
        boolean isLoop = false;
        
        public void addDefer(DeferStmt d) {
            if(this.defers == null) {
                this.defers = new Stack<>();
            }
            this.defers.add(d);
        }
        
        public void addConsts(String name) {
            if(this.constDefs == null) {
                this.constDefs = new Stack<>();
            }
            this.constDefs.add(name);
        }
        
        public boolean hasConsts() {
            return this.constDefs != null && !this.constDefs.isEmpty();
        }
                
        public boolean hasDefers() {
            return defers != null && !defers.isEmpty();
        }
        
        
        public void leave(Buf buf, NodeVisitor visitor) {
            if(hasDefers()) {
                ListIterator<DeferStmt> it = defers.listIterator(defers.size());
                while(it.hasPrevious()) {
                    DeferStmt s = it.previous();
                    s.stmt.visit(visitor);
                    buf.out(";\n");
                }
            }
        }
    }

    private final ModuleId BUILTIN_MODULE_NAME;
    
    private COptions options;
    private Buf buf;
    
    private Set<String> writtenModules;
    private CompilationUnit unit;
    private Module main;
    private Program program;    
    
    private Stack<FuncTypeInfo> currentFuncType;
    private Stack<Boolean> noteStack;
    private Stack<Scope> scope;
    private List<String> localConsts;
    private int aggregateLevel;
    private Pattern testPattern;
    private boolean testMainOnly;
    
    private List<Decl> declarations;
    private int currentLine;
    private String currentFile;
    
    private List<Decl> moduleInitFunc;
    private List<Decl> moduleDestroyFunc;
    private Decl mainFunc;
    
    private Map<TypeSpec, TypeInfo> resolvedTypeMap;
    private CGenNodeVisitor cgen;
    private Preprocessor preprocessor;
    
    public CGen(Preprocessor pp,
                CompilationUnit unit, 
                Program program, 
                COptions options, 
                Buf buf) {
        
        this.preprocessor = pp;
        this.unit = unit;
        this.program = program;
        this.options = options;
        this.buf = buf;
                
        if(this.options.options.testRegex != null) {
            this.testPattern = Pattern.compile(this.options.options.testRegex);
        }
        
        if(options.options.testFile) {
            this.testMainOnly = true;
        }
        
        this.BUILTIN_MODULE_NAME = ModuleId.fromDirectory(options.options.libDir, "builtins");
        
        this.main = program.getMainModule();
        this.resolvedTypeMap = program.getResolvedTypeMap();
        
        this.declarations = new ArrayList<>();
        this.moduleInitFunc = new ArrayList<>();
        this.moduleDestroyFunc = new ArrayList<>();
        this.currentFuncType = new Stack<>();
        this.noteStack = new Stack<>();
        this.scope = new Stack<>();
        this.localConsts = new ArrayList<>();
        
        this.writtenModules = new HashSet<>();
        
        this.cgen = new CGenNodeVisitor(this.buf);
    }
    
    public void write() {
        this.declarations.clear();
        this.moduleInitFunc.clear();
        this.moduleDestroyFunc.clear();
        this.currentFuncType.clear();
        
        this.writtenModules.clear();
        this.scope.clear();
        
        preface();
        
        this.main.getModuleStmt().visit(this.cgen);
        
        PhaseResult result = this.main.getPhaseResult();
        DependencyGraph graph = new DependencyGraph(result);
        
        List<Decl> tests = new ArrayList<>();
        
        this.declarations = graph.sort(this.declarations);
        for(Decl d : this.declarations) {
            
            // ignore test procedures if we are not testing
            if(d.attributes.hasNote("test")) {
                
                // only include tests that match our input regex
                if(isTestIncluded(d.attributes.getNote("test"))) {
                    tests.add(d);
                }
                
                // if we are not testing, don't include test procedures
                // in the final output
                if(!isTesting()) {
                    continue;
                }
            }
            
            // allow for module init functions
            if(d.attributes.hasNote("module_init") && d.kind == DeclKind.FUNC) {
                this.moduleInitFunc.add(d);
            }
            else if(d.attributes.hasNote("module_destroy") && d.kind == DeclKind.FUNC) {
                this.moduleDestroyFunc.add(d);
            }
    
            // do not write out main if we are testing
            if(d.name.equals("main") && 
               d.kind == DeclKind.FUNC) {
                this.mainFunc = d;
                continue;
            }
            
            d.visit(this.cgen);            
        }
        
        if(isTesting()) {
            writeTestMain(buf, tests);
        }
        else {
            writeMain(buf, mainFunc);
        }
    }
    
    /**
     * Writes the preface out to the buffer, this should be called prior to any processing
     * of {@link ModuleStmt}
     */
    private void preface() {
        writeHeader(this.buf);
        writeForwardDeclarations(this.buf);
    }
    
    private void writeHeader(Buf buf) {
        buf.out("// Compiled by LitaC on %s with version: %s \n", new Date(), LitaC.VERSION);
        buf.out("#include <stdint.h>   \n");
        buf.out("typedef int8_t    %s;  \n", prefix("i8"));
        buf.out("typedef int16_t   %s;  \n", prefix("i16"));
        buf.out("typedef int32_t   %s;  \n", prefix("i32"));
        buf.out("typedef int64_t   %s;  \n", prefix("i64"));
        //buf.out("typedef int128_t  %s;  \n", prefix("i128"));        
        buf.out("typedef uint8_t   %s;  \n", prefix("u8"));
        buf.out("typedef uint16_t  %s;  \n", prefix("u16"));
        buf.out("typedef uint32_t  %s;  \n", prefix("u32"));
        buf.out("typedef uint64_t  %s;  \n", prefix("u64"));
        //buf.out("typedef uint128_t %s;  \n", prefix("u128"));
        buf.out("typedef float     %s;  \n", prefix("f32"));
        buf.out("typedef double    %s;  \n", prefix("f64"));
        buf.out("typedef int8_t    %s;  \n", prefix("bool"));
        if(!prefix("char").equals("char")) {
            buf.out("typedef char      %s;  \n", prefix("char"));
        }
        buf.out("#define %s 1\n", prefix("true"));
        buf.out("#define %s 0\n", prefix("false"));
        buf.out("#define %s void\n", prefix("void"));
        buf.outln();
        
        this.unit.getBuiltin().visit(this.cgen);
    }
    
    private void writeForwardDecl(Buf buf, String backendName, TypeInfo type) {
        
        if(isForeign(type)) {
            return;
        }
        
        if(isTest(type)) {
            return;
        }
        
        String typeName = backendName;
        switch(type.getKind()) {
            case Func: {
                FuncTypeInfo funcInfo = type.as();     
                if(funcInfo.hasGenerics()) {
                    return;
                }
                
                this.cgen.visitNotes(funcInfo.sym.decl.attributes.notes);
                
                if(funcInfo.returnType.isKind(TypeKind.FuncPtr)) {
                    FuncPtrTypeInfo funcPtr = funcInfo.returnType.as();
                    buf.out("%s (*%s(", getTypeNameForC(funcPtr.returnType), typeName);
                }
                else {
                    buf.out("%s %s(", getTypeNameForC(funcInfo.returnType), typeName);
                }
                boolean isFirst = true;                
                for(ParamInfo p : funcInfo.parameterDecls) {
                    if(!isFirst) buf.out(",");
                                        
                    buf.out("%s", getTypeNameForC(p.type));
                    
                    isFirst = false;
                }

                if(funcInfo.isVararg()) {
                    if(!isFirst) {
                        buf.out(",");
                    }
                    
                    buf.out("...");
                }
                
                buf.out(")");
                
                
                if(funcInfo.returnType.isKind(TypeKind.FuncPtr)) {
                    buf.out(") (");
                    FuncPtrTypeInfo funcPtr = funcInfo.returnType.as();
                    
                    isFirst = true;
                    for(TypeInfo p : funcPtr.params) {
                        if(!isFirst) {
                            buf.out(",");
                        }
                        
                        buf.out("%s", getTypeNameForC(p));
                        
                        isFirst = false;
                    }
                    
                    // TODO: Varargs
                    
                    buf.out(")");
                }
                
                buf.out(";\n");
                
                break;
            }
            case Struct: {
                StructTypeInfo structInfo = type.as();
                if(structInfo.hasGenerics()) {
                    return;
                }
                
                if(!type.isAnonymous() /*&& !structInfo.isEmbedded()*/) {
                    buf.out("typedef struct %s %s;\n", typeName, typeName);
                }
                break;
            }
            case Union: {
                UnionTypeInfo unionInfo = type.as();
                if(unionInfo.hasGenerics()) {
                    return;
                }
                
                if(!type.isAnonymous() /*&& !unionInfo.isEmbedded()*/) {
                    buf.out("typedef union %s %s;\n", typeName, typeName);
                }
                break;
            }
            case Enum: {
                // enums can't be forward declared, an thus the full
                // definition must be defined
                type.sym.decl.visit(this.cgen);
                break;
            }
            case FuncPtr: {
                buf.out("typedef %s %s;\n", getTypeNameForC(type), type.getName());
                break;
            }
            default: {
               // throw new CompileException(String.format("Unsupported forward type declaration '%s'", type.getName()));
            }
        }
    }
    
    private final Comparator<Map.Entry<String, TypeInfo>> comp = new Comparator<Map.Entry<String, TypeInfo>>() {
        @Override
        public int compare(Entry<String, TypeInfo> a, Entry<String, TypeInfo> b) {
            if(a.equals(b)) {
                return 0;
            }
            
            TypeKind aKind = a.getValue().getKind();
            TypeKind bKind = b.getValue().getKind();
            
            if(aKind == TypeKind.Enum){
                if(bKind == TypeKind.Enum) {
                    return 0;
                }
                
                return -1;
            }
            else if(aKind == TypeKind.Func) {
                if(bKind == TypeKind.Func) {
                    return 0;
                }
                if(bKind == TypeKind.Enum) {
                    return 1;
                }
                return 1;
            }
            else if(bKind == TypeKind.Enum) {
                return 1;
            }
            else if(bKind == TypeKind.Func) {
                return -1;
            }
            
            return 0;
        }
    };
    
    private void writeForwardDeclarations(Buf buf) {
        buf.out("// forward declarations\n");
        Map<String, TypeInfo> types = new HashMap<>();
        List<NoteStmt> notes = new ArrayList<>();
        
        writeModuleForwardDecl(buf, this.main, new ArrayList<>(), types, notes);
        
        notes.forEach(note -> note.visit(this.cgen));
        
        types.entrySet()
             .stream()
             .sorted(comp)
             .forEach(type -> writeForwardDecl(buf, type.getKey(), type.getValue()));
        
        buf.out("// end forward declarations\n\n");
    }
    
    private void writeModuleForwardDecl(Buf buf, 
                                        Module module, 
                                        List<Module> writtenModules, 
                                        Map<String, TypeInfo> types,
                                        List<NoteStmt> notes) {
        if(writtenModules.contains(module)) {
            return;
        }
        
        writtenModules.add(module);
        
        notes.addAll(module.getModuleStmt().notes);
        
        module.getImports()
              .stream()
              .forEach(m -> writeModuleForwardDecl(buf, m, writtenModules, types, notes));
        

        module.getDeclaredTypes()
              .forEach(type -> types.put(getTypeNameForC(type.type), type.type));
        
    }
        
    private void writeTestMain(Buf buf, List<Decl> tests) {
        buf.outln();
        buf.out("// Tests").outln();
        buf.out("int main(int argn, char** args) {");
        for(Decl d : tests) {
            if(d.kind == DeclKind.FUNC) {
                String testName = d.attributes.getNote("test").getAttr(0, "");
                buf.out("printf(\"Testing '%%s'\\n\", \"%s\");\n", testName);
                buf.out("%s();\n", cName(d.sym));
            }
        }
        buf.out("}");
    }
    
    private void writeMain(Buf buf, Decl main) {
        FuncDecl funcDecl = null;
        if(main instanceof FuncDecl) {
            funcDecl = (FuncDecl)main;
        }
        
        buf.outln();
        buf.out("// Main").outln();
        
        boolean hasArgs = false;
        if(funcDecl != null) {
            FuncTypeInfo funcInfo = funcDecl.sym.type.as();
            if(funcInfo.parameterDecls.size() > 1) {
                String len = funcInfo.parameterDecls.get(0).name;
                String arg = funcInfo.parameterDecls.get(1).name;
                
                buf.out("int main(int %s, char** %s) {", prefix(len), prefix(arg));
                hasArgs = true;
            }            
        }
        
        if(!hasArgs) {
            buf.out("int main(int argn, char** args) {");
        }
        
        for(Decl d : this.moduleInitFunc) {
            buf.out("%s();\n", cName(d.sym));
        }

        if(funcDecl != null) {
            funcDecl.bodyStmt.visit(this.cgen);
        }
        
        for(Decl d : this.moduleDestroyFunc) {
            buf.out("%s();\n", cName(d.sym));
        }
        
        buf.out("}");
    }
    
    private String typeDeclForC(TypeSpec typeSpec, String declName) {        
        TypeInfo type = this.resolvedTypeMap.get(typeSpec);
        if(type == null) {
            // TODO
        }
        
        return typeDeclForC(type, declName);
    }
    
    private String typeDeclForC(TypeInfo type, String declName) {
        switch (type.getKind()) {
            case Ptr: {
                PtrTypeInfo ptrInfo = type.as();
                TypeInfo baseInfo = ptrInfo.getBaseType();
                if(baseInfo.isKind(TypeKind.Array)) {
                    ArrayTypeInfo arrayInfo = baseInfo.as();
                    if(arrayInfo.length > -1) {
                        String typeDecl = getTypeNameForC(arrayInfo.arrayOf);
                        return String.format("%s (*%s)[%d]", typeDecl, declName, arrayInfo.length);
                    }
                }
                
                String typeDecl = getTypeNameForC(type);    
                return String.format("%s %s", typeDecl, declName); 
            }
            case Const: {
                ConstTypeInfo constInfo = type.as();
                return String.format("const %s", typeDeclForC(constInfo.constOf, declName));
            }
            case Array: {
                ArrayTypeInfo arrayInfo = type.as();      
                TypeInfo baseInfo = arrayInfo.getBaseType();                
                String baseName = getTypeNameForC(baseInfo);
                
                StringBuilder sb = new StringBuilder();
                do {
                    if(arrayInfo.length < 0) {
                        if(arrayInfo.lengthExpr != null) {
                            Buf tmp = new Buf(this.buf.indentWidth(), this.buf.tabs());
                            CGenNodeVisitor visitor = new CGenNodeVisitor(tmp);
                            arrayInfo.lengthExpr.visit(visitor);
                            sb.append("[").append(tmp.toString()).append("]");
                        }
                        else {
                            sb.append("[]");
                        }
                    }
                    else {
                        sb.append(String.format("[%d]", arrayInfo.length));
                    }
                    
                    
                    arrayInfo = arrayInfo.arrayOf.isKind(TypeKind.Array) ?
                                arrayInfo.arrayOf.as() : null;
                    
                } 
                while(arrayInfo != null);
                
                if(baseInfo.isKind(TypeKind.FuncPtr)) {
                    return typeDeclForC(baseInfo, declName + sb.toString());
                }
                else {
                    return String.format("%s %s%s", baseName, declName, sb.toString());
                }
                
            }
            case Str: {
                return String.format("char* %s", declName);
            }
            case Enum: {
                String typeName = getTypeNameForC(type);
                return String.format("%s %s", typeName, declName);
            }
            case FuncPtr: {
                FuncPtrTypeInfo funcInfo = type.as();
                StringBuilder params = new StringBuilder();
                boolean isFirst = true;
                for(TypeInfo p : funcInfo.params) {
                    if(!isFirst) params.append(",");
                    params.append(getTypeNameForC(p));
                    isFirst = false;
                }
                
                return String.format("%s (*%s)(%s)", getTypeNameForC(funcInfo.returnType), declName, params);
            }
            default: {                
                String typeName = getTypeNameForC(type);
                return String.format("%s %s", typeName, declName);
            }
        }
    }
    
    private String getTypeNameForC(TypeSpec typeSpec) {
        return getTypeNameForC(typeSpec, false);
    }
    
    private String getTypeNameForC(TypeInfo type) {
        return getTypeNameForC(type, false);
    }
    
    private String getTypeNameForC(TypeSpec typeSpec, boolean isCast) {
        TypeInfo type = this.resolvedTypeMap.get(typeSpec);        
        return getTypeNameForC(type, isCast);
    }
    
    private String getTypeNameForC(TypeInfo type, boolean isCast) {
        switch (type.getKind()) {
            case Ptr: {
                PtrTypeInfo ptrInfo = type.as();
                TypeInfo baseInfo = ptrInfo.getBaseType();
                if(baseInfo.isKind(TypeKind.Array)) {
                    ArrayTypeInfo arrayInfo = baseInfo.as();
                    if(arrayInfo.length > -1) {
                        String typeDecl = getTypeNameForC(arrayInfo.arrayOf, isCast);
                        return String.format("%s (*)[%d]", typeDecl, arrayInfo.length);
                    }
                }
                return getTypeNameForC(ptrInfo.ptrOf, isCast) + "*";
            }
            case Const: {
                ConstTypeInfo constInfo = type.as();
                return "const " + getTypeNameForC(constInfo.constOf, isCast);
            }
            case Array: {
                ArrayTypeInfo arrayInfo = type.as();
                TypeInfo baseInfo = arrayInfo.getBaseType();
                String baseName = getTypeNameForC(baseInfo);
                
                StringBuilder sb = new StringBuilder();
                do {
                    if(arrayInfo.length < 0) {
                        if(arrayInfo.lengthExpr != null) {
                            Buf tmp = new Buf(this.buf.indentWidth(), this.buf.tabs());
                            CGenNodeVisitor visitor = new CGenNodeVisitor(tmp);
                            arrayInfo.lengthExpr.visit(visitor);
                            sb.append("[").append(tmp.toString()).append("]");
                        }
                        else {
                            sb.append("[]");
                        }
                    }
                    else {
                        sb.append(String.format("[%d]", arrayInfo.length));
                    }
                    
                    
                    arrayInfo = arrayInfo.arrayOf.isKind(TypeKind.Array) ?
                                arrayInfo.arrayOf.as() : null;
                    
                } 
                while(arrayInfo != null);
                
                return String.format("%s%s", baseName, sb.toString());
            }
            case FuncPtr: {
                FuncPtrTypeInfo funcInfo = type.as();
                StringBuilder params = new StringBuilder();
                boolean isFirst = true;
                for(TypeInfo p : funcInfo.params) {
                    if(!isFirst) params.append(",");
                    params.append(getTypeNameForC(p, isCast));
                    isFirst = false;
                }
                
                return String.format("%s (*%s)(%s)", getTypeNameForC(funcInfo.returnType, isCast), 
                        isCast ? "" : cTypeName(funcInfo), params);
            }
            default: {
                return cTypeName(type);
            }
        }
    }
    
    public boolean isTestIncluded(NoteStmt note) { 
        if(this.testMainOnly) {
            String mainFile = program.getMainModule().getModuleStmt().getSourceName();
            return (note.getSourceName().equals(mainFile));
        }
        
        if(this.testPattern == null) {
            return true;
        }
        
        String name = note.getAttr(0, "");
        return this.testPattern.matcher(name).matches();        
    }
    
    public boolean isTesting() {
        return this.options.options.outputType.equals(OutputType.Test);
    }
    
    private boolean isTest(TypeInfo type) {
        if(type.sym == null) {
            return false;
        }
        
        return type.sym.decl.attributes.isTest() && !isTesting();
    }
    
    private boolean isForeign(TypeInfo type) {
        if(type.sym == null) {
            return false;
        }
        
        return type.sym.isForeign();
    }
    
    private boolean isForeign(Decl d) {
        if(d.sym == null) {
            return false;
        }
        
        return d.sym.isForeign();
    }
    
    private String getForeignName(Decl d, String defaultName) {
        if(d == null) {
            return defaultName;
        }
        
        NoteStmt n = d.attributes.getNote("foreign");
        if(n == null) {
            return defaultName;
        }
        
        return n.getAttr(0, defaultName);
    }
    
    
    private String prefix(String name) {
        return String.format("%s%s", this.options.symbolPrefix, name);
    }
    
    private String cTypeName(TypeInfo type) {
        String typeName = Names.escapeName(type);
        
        if(type.sym == null) {
            return prefix(typeName);
        }
        
        Symbol sym = type.sym;
        if(sym.isForeign()) {      
            if(sym.isBuiltin()) {
                return prefix(Names.baseTypeName(type.getName()));
            }
            return getForeignName(sym.decl, Names.baseTypeName(sym.decl.name));
        }
        
        return prefix(Names.backendName(sym.declared.simpleName(), typeName));
    }
    
    private String cName(Symbol sym) {
        String declName = sym.isType() ? Names.escapeName(sym.getType()) : sym.decl.name;
        
        if(sym.isLocal()) {
            return prefix(declName);
        }
        
        if(sym.isForeign()) {
            if(sym.isBuiltin()) {
                return prefix(Names.baseTypeName(sym.decl.name));
            }
            return getForeignName(sym.decl, Names.baseTypeName(sym.decl.name));
        }
        
        if(sym.decl.kind == DeclKind.FUNC) {
            if(sym.decl.name.equals("main")) {
                return declName;
            }
        }
        
        return prefix(Names.backendName(sym.declared.simpleName(), declName));
    }
    
    private void checkLine(Stmt stmt) {
        if(options.options.disableLines) {
            return;
        }
        
        String sourceFile = stmt.getSourceName();
        int line = stmt.getLineNumber();
        
        if(this.currentLine != line ||
           (sourceFile != null && !this.currentFile.equals(sourceFile))) {
            
            this.currentFile = sourceFile;
            this.currentLine = line;
            
            if(this.currentFile != null) {
                buf.out("\n#line %d \"%s\"\n", this.currentLine, this.currentFile);
            }
            else {
                buf.out("\n#line %d\n", this.currentLine);
            }
        }
    }
    
    
    
    /**
     * Writes out parts of the AST to the equivalent C
     * code
     */
    private class CGenNodeVisitor implements NodeVisitor { 
    
        private Buf buf;
                
        public CGenNodeVisitor(Buf buf) {
            this.buf = buf;
        }
        
        private void visitNotes(List<NoteStmt> notes) {
            visitNotes(notes, true);
        }
        
        private void visitNotes(List<NoteStmt> notes, boolean isPrelude) {
            noteStack.push(isPrelude);
            if(notes != null) {
                notes.forEach(n -> n.visit(this));
            }
            noteStack.pop();
        }
            
        @Override
        public void visit(ModuleStmt stmt) {
            if(writtenModules.contains(stmt.id.fullModuleName)) {
                return;
            }
            
            writtenModules.add(stmt.id.fullModuleName);
            
            for(ImportStmt i : stmt.imports) {
                i.visit(this);
            }
            
            declarations.addAll(stmt.declarations);
        }
    
        @Override
        public void visit(ImportStmt stmt) {
            if(stmt.moduleId.equals(BUILTIN_MODULE_NAME)) {
                return;
            }
            
            ModuleStmt module = unit.getModule(stmt.moduleId);
            module.visit(this);
        }
    
        @Override
        public void visit(NoteStmt note) {
            boolean isPrelude = noteStack.isEmpty() || noteStack.peek();
            
            // postlude note
            if(!isPrelude) {
                switch(note.name) {
                    case "packed": {
                        buf.out(" LITAC_PACKED_POP ");
                        break;
                    }
                }
                
                return;
            }
            
            // Prelude Notes
            switch(note.name) {
                case "include": {
                    if(note.attributes != null) {
                        for(String header : note.attributes) {
                            if(header.startsWith("<")) {
                                buf.out("#include %s\n", header);
                            }
                            else {
                                buf.out("#include \"%s\"\n", header);
                            }
                        }
                    }
                    break;
                }
                case "foreign": {
                    break;
                }
                case "cc": { // Calling Conventions
                    if(note.attributes != null) {
                        buf.out("%s ", note.getAttr(0, ""));
                    }
                    break;
                }
                case "raw": {
                    if(note.attributes != null) {
                        for(String line : note.attributes) {
                            buf.appendRaw(line);
                        }
                    }
                    break;
                }
                case "cFile": {
                    if(note.attributes != null) {
                        for(String fileName: note.attributes) {
                            try {
                                File file = new File(fileName);
                                if(!file.exists()) {
                                    file = new File(OS.getWorkingDir(), fileName);
                                    if(!file.exists()) {
                                        file = new File(options.options.libDir.getAbsolutePath(), fileName);
                                    }
                                }
                                
                                String contents = new String(Files.readAllBytes(file.toPath()));
                                buf.appendRaw(contents);
                                buf.outln();
                            }
                            catch(IOException e) {
                                main.getPhaseResult().addError(note, "Unable to load C source file: %s", e);
                            }
                        }
                    }
                    break;
                }
                case "inline": {
                    buf.out("LITAC_INLINE ");
                    break;
                }
                case "noinline": {
                    buf.out("LITAC_NOINLINE ");
                    break;
                }
                case "static": {
                    buf.out("static ");
                    break;
                }
                case "register": {
                    buf.out("register ");
                    break;
                }
                case "threadlocal": {
                    buf.out("LITAC_THREADLOCAL ");
                    break;
                }
                case "packed": {
                    buf.out("LITAC_PACKED ");
                    break;
                }
            }
        }
        
        @Override
        public void visit(VarFieldStmt stmt) {
            checkLine(stmt);
            
            String name = name(stmt.name, stmt.attributes);
            buf.out("%s;\n", typeDeclForC(stmt.type, name));
        }
    
        @Override
        public void visit(ParameterDecl d) {
        }
        
        @Override
        public void visit(ParametersStmt stmt) {
        }
        
        @Override
        public void visit(CompStmt stmt) {
            Stmt s = stmt.evaluateForBody(preprocessor);
            if(s != null) {
                s.visit(this);
            }            
        }
        
        // TODO
        @Override
        public void visit(VarDeclsStmt stmt) {
            for(Decl d : stmt.vars) {
                d.visit(this);
            }
        }
        
        @Override
        public void visit(ConstDeclsStmt stmt) {
            for(Decl d : stmt.consts) {
                d.visit(this);
            }
        }
        
        @Override
        public void visit(StructFieldStmt stmt) {
            checkLine(stmt);
            
            buf.outln();
            if(stmt.decl.sym.type.isAnonymous()) {
                buf.out("struct {");
                for(FieldStmt f : stmt.decl.fields) {
                    f.visit(this);
                }
                buf.out("};\n");
            }
            else {
                buf.out("struct %s {", cTypeName(stmt.decl.sym.type));
                for(FieldStmt f : stmt.decl.fields) {
                    f.visit(this);
                }
                buf.out("} %s;\n", stmt.decl.name);
            }
            buf.outln();
        }
    
        @Override
        public void visit(UnionFieldStmt stmt) {
            checkLine(stmt);
            
            buf.outln();
            if(stmt.decl.sym.type.isAnonymous()) {
                buf.out("union {");            
                for(FieldStmt f : stmt.decl.fields) {
                    f.visit(this);
                }            
                buf.out("};\n");
            }
            else {
                buf.out("union %s {", cTypeName(stmt.decl.sym.type));            
                for(FieldStmt f : stmt.decl.fields) {
                    f.visit(this);
                }            
                buf.out("} %s;\n", stmt.decl.name);
            }
            buf.outln();
        }
        
        @Override
        public void visit(EnumFieldStmt stmt) {
            buf.outln();
            buf.out("enum %s %s;\n", cTypeName(stmt.decl.sym.type), stmt.decl.name);
            buf.outln();
        }
        
        @Override
        public void visit(ConstDecl d) {       
            String name = cName(d.sym);
            
            if(isForeign(d)) {
                return;
            }
            visitNotes(d.attributes.notes);
            
            checkLine(d);
            
            if(Expr.isConstExpr(d.expr)) {
                buf.out("#define ");                   
                buf.out("%s (", name);  
                d.expr.visit(this); 
                buf.out(")\n");
                
                // globals are not undef (are namespaced by the compiler)   
                if(!d.attributes.isGlobal) {
                    scope.peek().addConsts(name);
                }
            }
            else {
                if(d.sym.type.isPrimitive()) {
                    buf.out("const ");
                }
                
                buf.out("%s = ", typeDeclForC(d.sym.type, name));
                d.expr.visit(this);
                buf.out(";\n");
            }

        }
    
        private String name(String name, Attributes attributes) {
            NoteStmt note = attributes.getNote("alias");
            if(note == null) {
                return name;
            }
            
            String alias = note.getAttr(0, null);
            if(alias == null) {
                main.getPhaseResult().addError(note, "'alias' note must define an alias name");
                return "";
            }
            
            return alias;
            
        }
    
        private void writeEnumStrFunc(String name, EnumDecl d) {
            NoteStmt asStr = d.attributes.getNote("asStr");
            if(asStr != null) {            
                buf.outln();
                buf.out("const char* __%s_%s_AsStr(%s __e) {", d.sym.declared.simpleName(), d.name, name);            
                buf.out("switch(__e) {");            
                for(EnumFieldInfo f : d.fields) {      
                    String strValue = f.attributes.hasNote("asStr") ? f.attributes.getNote("asStr").getAttr(0, f.name) : f.name;
                    buf.out("case %s_%s: return \"%s\";\n", name, f.name, strValue);
                }
                buf.out("default: return \"\";");
                buf.out("}\n");            
                buf.out("}");
                
                buf.outln();
            }
        }
    
        @Override
        public void visit(EnumDecl d) {
            String name = cName(d.sym);
            
            if(isForeign(d.sym.type)) {
                writeEnumStrFunc(name, d);
                return;
            }
            
            visitNotes(d.attributes.notes);
    
            buf.outln();
            checkLine(d);
            
            buf.out("typedef enum %s {", name);
            boolean isFirst = true;
            for(EnumFieldInfo f : d.fields) {
                if(!isFirst) buf.out(",\n");
                
                buf.out("%s_%s", name, f.name);
                if(f.value != null) {
                    buf.out(" = ");
                    f.value.visit(this);
                }                        
                isFirst = false;
            }            
            buf.out("} %s;", name);
            buf.outln();
            
            writeEnumStrFunc(name, d);
        }
    
    
        @Override
        public void visit(FuncDecl d) {
            String name = cName(d.sym);
            
            if(isForeign(d.sym.type)) {
                return;
            }
            
            FuncTypeInfo funcInfo = d.sym.type.as();
            if(funcInfo.hasGenerics()) {
                return;
            }
    
            visitNotes(d.attributes.notes);
            
            currentFuncType.add(funcInfo);
            
            buf.outln();
            checkLine(d);
            
            if(funcInfo.returnType.isKind(TypeKind.FuncPtr)) {
                FuncPtrTypeInfo funcPtr = funcInfo.returnType.as();
                buf.out("%s (*%s(", getTypeNameForC(funcPtr.returnType), name);
            }
            else {
                buf.out("%s %s(", getTypeNameForC(funcInfo.returnType), name);
            }
            
            boolean isFirst = true;
            for(ParameterDecl p : d.params.params) {
                if(!isFirst) {
                    buf.out(",");
                }
                            
                buf.out("%s", typeDeclForC(p.sym.type, prefix(p.name))); 
                
                isFirst = false;
            }
            
            if(funcInfo.isVararg()) {
                if(!isFirst) {
                    buf.out(",");
                }
                
                buf.out("...");
            }
            
            
            buf.out(")");
            
            if(funcInfo.returnType.isKind(TypeKind.FuncPtr)) {
                buf.out(") (");
                FuncPtrTypeInfo funcPtr = funcInfo.returnType.as();
                
                isFirst = true;
                for(TypeInfo p : funcPtr.params) {
                    if(!isFirst) {
                        buf.out(",");
                    }
                    
                    buf.out("%s", getTypeNameForC(p));
                    
                    isFirst = false;
                }
                
                // TODO: Varargs
                
                buf.out(")");
            }
            
            buf.out(" ");
            
            boolean isBlock = (d.bodyStmt instanceof BlockStmt);
            if(!isBlock) buf.out("{");
            d.bodyStmt.visit(this);
            
            
            for(String c: localConsts) {
                buf.out("#undef %s\n", c);
            }
            localConsts.clear();
            
            if(!isBlock) buf.out("}");
            buf.outln();
            
            currentFuncType.pop();
        }
    
    
        @Override
        public void visit(StructDecl d) {
            String name = cName(d.sym);
            
            if(isForeign(d.sym.type)) {
                return;
            }
            
            StructTypeInfo structInfo = d.sym.type.as();
            if(structInfo.hasGenerics()) {
                return;
            }
            
            if(structInfo.isEmbedded() && aggregateLevel < 1) {
                return;
            }
            visitNotes(d.attributes.notes);
            
            aggregateLevel++;
            buf.outln();
            checkLine(d);
            
            buf.out("struct %s {", name);
            for(FieldStmt f : d.fields) {
                f.visit(this);
            }
            buf.out("};\n");
            visitNotes(d.attributes.notes, false);
            buf.outln();
            aggregateLevel--;
        }
    
        @Override
        public void visit(UnionDecl d) {
            String name = cName(d.sym);
            
            if(isForeign(d.sym.type)) {
                return;
            }
            
            UnionTypeInfo unionInfo = d.sym.type.as();
            if(unionInfo.hasGenerics()) {
                return;
            }
            
            if(unionInfo.isEmbedded() && aggregateLevel < 1) {
                return;
            }
            visitNotes(d.attributes.notes);
            
            aggregateLevel++;
            buf.outln();
            checkLine(d);
            
            buf.out("union %s {", name);
            for(FieldStmt f : d.fields) {
                f.visit(this);
            }
            buf.out("};\n");
            buf.outln();
            aggregateLevel--;
        }
    
        @Override
        public void visit(TypedefDecl d) {
            // No need to typedef in C, as the Type alias is resolved to the real
            // type in the Lita compiler
            //buf.out("typedef %s %s;\n", cTypeName(d.type), prefix(d.alias));
            
            if(isForeign(d)) {
                return;
            }
            
            if(d.sym.type.isKind(TypeKind.FuncPtr)) {
                FuncPtrTypeInfo funcInfo = d.sym.type.as();
                if(funcInfo.hasGenerics()) {
                    return;
                }
                visitNotes(d.attributes.notes);
                checkLine(d);
                
                buf.out("typedef %s;\n", typeDeclForC(d.type, cName(d.sym)));
            }
        }
    
    
    
        @Override
        public void visit(VarDecl d) {
            String name = cName(d.sym);
            
            if(isForeign(d)) {
                return;
            }
            visitNotes(d.attributes.notes);
                    
            checkLine(d);
            buf.out("%s", typeDeclForC(d.sym.type, name));
            
            if(d.expr != null) {
                buf.out(" = ");
                d.expr.visit(this);
            }
            buf.out(";\n");
        }
    
        
        @Override
        public void visit(IfStmt stmt) {
            checkLine(stmt);
            
            buf.out("if (");
            stmt.condExpr.visit(this);
            buf.out(") {\n");
            pushScope();
            stmt.thenStmt.visit(this);            
            if(stmt.thenStmt instanceof Expr) buf.out(";");
            buf.out("\n");
            popScope();
            buf.out("}\n");
            
            if(stmt.elseStmt != null) {
                buf.out("else {\n");   
                pushScope();
                stmt.elseStmt.visit(this);
                if(stmt.elseStmt instanceof Expr) buf.out(";");
                buf.out("\n");
                popScope();
                buf.out("}\n");
            }
        }
    
        @Override
        public void visit(WhileStmt stmt) {
            checkLine(stmt);
            
            buf.out("while (");
            stmt.condExpr.visit(this);
            buf.out(") {\n");
            pushLoopScope();
            stmt.bodyStmt.visit(this);
            if(stmt.bodyStmt instanceof Expr) buf.out(";");
            buf.out("\n");
            popScope();
            buf.out("}\n");
        }
    
    
        @Override
        public void visit(DoWhileStmt stmt) {
            checkLine(stmt);
            
            buf.out("do {");
            pushLoopScope();
            stmt.bodyStmt.visit(this);
            if(stmt.bodyStmt instanceof Expr) buf.out(";");
            buf.out("\n");
            popScope();            
            buf.out("\n}\n while (");
            stmt.condExpr.visit(this);
            buf.out(");");
        }
    
    
        @Override
        public void visit(ForStmt stmt) {
            checkLine(stmt);
            
            buf.out("for(");
            if(stmt.initStmt != null) {
                stmt.initStmt.visit(this);
                if(stmt.initStmt instanceof Expr) {
                    buf.out(";");    
                }
            }
            else {
                buf.out(";");    
            }
            if(stmt.condExpr != null) stmt.condExpr.visit(this);
            buf.out(";");
            if(stmt.postStmt != null) stmt.postStmt.visit(this);
            buf.out(") {");
            pushLoopScope();
            stmt.bodyStmt.visit(this);
            if(stmt.bodyStmt instanceof Expr) buf.out(";");
            buf.out("\n");
            popScope();            
            buf.out("\n}\n");
        }
        
        @Override
        public void visit(SwitchCaseStmt stmt) {
            checkLine(stmt);
            
            buf.out("case ");
            stmt.cond.visit(this);
            buf.out(": ");
            stmt.stmt.visit(this);
            buf.out(";\n");
        }
        
        @Override
        public void visit(SwitchStmt stmt) {
            checkLine(stmt);
            
            buf.out("switch(");
            stmt.cond.visit(this);
            buf.out(") {");
            for(SwitchCaseStmt s : stmt.stmts) {
                s.visit(this);
            }
            
            if(stmt.defaultStmt != null) {
                buf.out("default: ");
                stmt.defaultStmt.visit(this);
                buf.out(";\n");
            }
            
            buf.out("}\n");
        }
         
        private void pushLoopScope() {
            Scope s = new Scope();
            s.isLoop = true;
            scope.add(s);
        }
        private void pushScope() {
            scope.add(new Scope());
        }        
        
        private void popScope() {
            Scope s = scope.pop();
            s.leave(buf, this);
            
            if(s.hasConsts()) {
                localConsts.addAll(s.constDefs);
            }
        }
        
        private void leaveLoopScope() {
            for(int i = scope.size() - 1; i >= 0; i--) {
                Scope s = scope.get(i);
                s.leave(buf, this);
                
                if(s.isLoop) {
                    break;
                }
            }
        }
    
        @Override
        public void visit(BreakStmt stmt) {
            leaveLoopScope();
            checkLine(stmt);
            buf.out("break;\n");
        }
    
        @Override
        public void visit(ContinueStmt stmt) {
            leaveLoopScope();
            checkLine(stmt);
            buf.out("continue;\n");
        }
    
    
        @Override
        public void visit(ReturnStmt stmt) {
            checkLine(stmt);
            
            if(scope.stream().anyMatch(s -> s.hasDefers()) && stmt.returnExpr != null) {                
                TypeInfo type = currentFuncType.peek().returnType;
                buf.out("{\n%s = ", typeDeclForC(type, "___result"));
                stmt.returnExpr.visit(this);
                buf.out(";\n");
                
                List<Scope> currentScope = new ArrayList<>(scope);
                for(Scope s : currentScope) {
                    s.leave(buf, this);
                }
                
                buf.out("return ___result;\n}\n");                        
            }
            else {
                List<Scope> currentScope = new ArrayList<>(scope);
                for(Scope s : currentScope) {
                    s.leave(buf, this);
                }
            
                buf.out("return");
                if(stmt.returnExpr != null) {
                    buf.out(" ");
                    stmt.returnExpr.visit(this);
                }
                buf.out(";\n");
            }
        }
    
        @Override
        public void visit(BlockStmt stmt) {
            checkLine(stmt);
            buf.out("{");
            
            pushScope();
            for(Stmt s : stmt.stmts) {
                boolean isExpr = s instanceof Expr;
                if(isExpr) {
                    checkLine(s);
                }
                
                s.visit(this);
                
                if(isExpr) {
                    buf.out(";\n");
                }
            }
            popScope();
            
            buf.out("}\n");
        }
    
        @Override
        public void visit(DeferStmt stmt) {
            checkLine(stmt);
            
            scope.peek().addDefer(stmt);
        }
        
        @Override
        public void visit(GotoStmt stmt) {
            checkLine(stmt);
            
            buf.out("goto %s;\n", stmt.label);
        }
        
        @Override
        public void visit(LabelStmt stmt) {
            checkLine(stmt);
            
            buf.out("%s:\n", stmt.label);
        }
    
        @Override
        public void visit(EmptyStmt stmt) {
            checkLine(stmt);
        }
        
        @Override
        public void visit(CastExpr expr) {                
            buf.out("(%s)", getTypeNameForC(expr.castTo, true));
            expr.expr.visit(this);
        }
        
        @Override
        public void visit(SizeOfExpr expr) {
            buf.out("sizeof(");
            if(expr.expr != null) {
                expr.expr.visit(this);
            }
            else {
                buf.out("%s", cTypeName(expr.getResolvedType().type));
            }
            buf.out(")");
        }
        
        @Override
        public void visit(TypeOfExpr expr) {
            if(expr.expr != null) {
                buf.out("(%s)", expr.expr.getResolvedType().type.getTypeId());
            }
            else {
                buf.out("(%s)", expr.getResolvedType().val);
            }
        }
        
        @Override
        public void visit(OffsetOfExpr expr) {
            buf.out("offsetof(%s, %s)", getTypeNameForC(expr.type), expr.field);
        }
        
        @Override
        public void visit(InitArgExpr expr) {
            String fieldName = expr.fieldName;
            
            Node parent = expr.getParentNode();
            if(fieldName != null && parent instanceof InitExpr) {
                InitExpr initExpr = (InitExpr)parent;
                AggregateTypeInfo type = initExpr.getResolvedType().type.as();
                FieldInfo field = type.getFieldWithAnonymous(expr.fieldName);
                if(field != null) {
                    fieldName = fieldName(field);
                }
            }
            
            if(fieldName != null) {
                buf.out(".%s = ", fieldName);
            }                
            expr.value.visit(this);
        }
        
        @Override
        public void visit(InitExpr expr) {                        
            Node parent = expr.getParentNode();                        
            boolean requiresCast = parent instanceof ReturnStmt ||
                                   parent instanceof FuncCallExpr;
            
            TypeInfo expectedType = expr.expectedType;
            if(expectedType == null || TypeInfo.isPtr(expectedType) || requiresCast) {
                String typeName = getTypeNameForC(expr.type);
                buf.out("(%s)", typeName);
            }
            
            buf.out(" {");
            boolean isFirst = true;
            for(Expr e : expr.arguments) {
                if(!isFirst) buf.out(",\n");
                e.visit(this);            
                isFirst = false;
            }
            buf.out("}");
        }
    
    
        @Override
        public void visit(NullExpr expr) {
            buf.out("NULL");
        }
    
    
        @Override
        public void visit(BooleanExpr expr) {
            if(expr.bool) {
                buf.out(prefix("true"));
            }
            else {
                buf.out(prefix("false"));
            }
        }
    
        @Override
        public void visit(NumberExpr expr) {
            String n = expr.number; 
            buf.out(n);
            
            TypeInfo type = expr.getResolvedType().type;
            switch(type.getKind()) {
                case f32:
                    if(!n.contains(".")) {
                        buf.out(".");
                    }
                    buf.out("f");
                    break;
                case u8:
                case u16:
                case u32:
                    buf.out("U");
                    break;
                case u64:
                    buf.out("UL");
                    break;            
                case i64:
                    buf.out("L");
                    break;            
                default:
            }
            
        }
    
        @Override
        public void visit(StringExpr expr) {
            buf.appendRaw("\"");
            for(int i = 0; i < expr.string.length(); i++) {
                char c = expr.string.charAt(i);
                if(escapeChars.containsKey(c)) {
                    buf.appendRaw(escapeChars.get(c));
                }
                else {
                    buf.appendRaw(c);
                }
            }
            buf.appendRaw("\"");
        }
        
        @Override
        public void visit(CharExpr expr) {
            buf.appendRaw("'");
            for(int i = 0; i < expr.character.length(); i++) {
                char c = expr.character.charAt(i);
                if(escapeChars.containsKey(c)) {
                    buf.appendRaw(escapeChars.get(c));
                }
                else {
                    // Character.isISOControl(ch)
                    buf.appendRaw(c);
                }
            }
            buf.appendRaw("'");
        }
    
    
        @Override
        public void visit(GroupExpr expr) {
            buf.out("(");
            expr.expr.visit(this);
            buf.out(")");
        }
    
        @Override
        public void visit(FuncCallExpr expr) {
            expr.object.visit(this);
            
            List<ParamInfo> params = null;
            if(expr.object.getResolvedType().type.isKind(TypeKind.Func)) {
                FuncTypeInfo funcInfo = expr.object.getResolvedType().type.as();
                params = funcInfo.parameterDecls;
            }
            
            List<Expr> suppliedArguments = new ArrayList<>(expr.arguments);
            
            GetExpr getExpr = getMethodCall(expr.object);
            if(getExpr != null) {
                suppliedArguments.add(0, getExpr.object);
                buf.out("%s", getTypeNameForC(getExpr.field.getResolvedType().type));
            }
            
            buf.out("(");
            boolean isFirst = true;
            
            int i = 0;
            for(; i < suppliedArguments.size(); i++) {
                Expr e = suppliedArguments.get(i);
                if(!isFirst) buf.out(",");
                e.visit(this);                
                isFirst = false;
            }
            
            // check and see if we should apply default
            // parameters
            if(params != null) {
                if(i < params.size()) {
                    for(; i < params.size(); i++) {
                        ParamInfo p = params.get(i);
                        if(p.defaultValue != null) {
                            if(!isFirst) buf.out(",");
                            p.defaultValue.visit(this);
                            isFirst = false;
                        }
                    }
                }
            }
            
            buf.out(")");
        }
        
        private void visitSymbol(Symbol sym) {
            if(sym == null) {
                return;
            }
            
            if(!sym.isUsing()) {
                buf.out("%s", cName(sym));
                return;
            }
                
            TypeInfo paramInfo = sym.usingParent;
            AggregateTypeInfo aggInfo = null;
            if(paramInfo.isKind(TypeKind.Ptr)) {
                PtrTypeInfo ptrInfo = paramInfo.as();
                aggInfo = ptrInfo.getBaseType().as();
            }
            else {
                aggInfo = sym.usingParent.as();
            }
            
            FieldPath path = aggInfo.getFieldPath(sym.name);
            buf.out("%s", cName(sym.decl.sym));
            
            if(!path.hasPath()) {
                if(paramInfo.isKind(TypeKind.Ptr)) {
                    buf.out("->");
                }
                else {
                    buf.out(".");
                }
                buf.out("%s", cName(sym));
            }
            else {
                TypeInfo objectInfo = paramInfo;
                for(FieldPathNode n : path.getPath()) {                
                    if(objectInfo.isKind(TypeKind.Ptr)) {
                        buf.out("->");
                    }
                    else {
                        buf.out(".");
                    }
                    
                    buf.out("%s", n.field.name);
                    objectInfo = n.field.type;
                }
            }
    
        
        }
    
        @Override
        public void visit(IdentifierExpr expr) {
            Symbol sym = null;
            
            TypeInfo type = resolvedTypeMap.get(expr.type);
            if(type != null) {
                sym = type.sym;
            }
            else {
                sym = expr.sym;
            }
            
            if(sym == null) {
                buf.out("%s", prefix(expr.type.toString()));
            }
            else {
                visitSymbol(sym);
            }
        }
    
        @Override
        public void visit(FuncIdentifierExpr expr) {
            visit((IdentifierExpr)expr);
        }
    
        @Override
        public void visit(TypeIdentifierExpr expr) {
            TypeInfo type = resolvedTypeMap.get(expr.type);
            if(type != null) {
                buf.out("%s", getTypeNameForC(expr.type));
            }
            else {
                Symbol sym = expr.sym;
                if(sym == null) {
                    buf.out("%s", prefix(expr.type.toString()));
                }
                else {
                    visitSymbol(sym);
                }    
            }
        }
        
        private boolean hasMethodCall(Expr expr) {
            return getMethodCall(expr) != null;
        }
        
        private GetExpr getMethodCall(Expr expr) {
            if(expr instanceof GetExpr) {
                GetExpr e = (GetExpr)expr;
                if(e.isMethodCall) {
                    return e;
                }
                
                return getMethodCall(e.object);
            }
            
            return null;
        }
        
        
        private String fieldName(FieldInfo field) {        
            return name(field.name, field.attributes);
        }
        
        @Override
        public void visit(GetExpr expr) {
            // if this is a method call, the expression will be
            // handled by the functionCall expression
            if(hasMethodCall(expr)) {            
                return;
            }
            
            TypeInfo objectInfo = expr.object.getResolvedType().type;
            if(objectInfo.isKind(TypeKind.Enum)) {
                if(isForeign(objectInfo)) {
                    buf.out("%s", expr.field.type.name);
                }
                else {
                    buf.out("%s_%s", cName(objectInfo.sym), expr.field.type.name);
                }
            }
            else {
                expr.object.visit(this);
                
                AggregateTypeInfo aggInfo = null;
                if(objectInfo.isKind(TypeKind.Ptr)) {
                    if(!TypeInfo.isPtrAggregate(objectInfo)) {
                        buf.out("->%s", expr.field.type.name);
                        return;
                    }
                    
                    PtrTypeInfo ptrInfo = objectInfo.as();
                    aggInfo = ptrInfo.getBaseType().as();
                }
                else {
                    aggInfo = objectInfo.as();
                }
                
                FieldPath path = aggInfo.getFieldPath(expr.field.type.name);
                if(!path.hasPath()) {
                    buf.out((objectInfo.isKind(TypeKind.Ptr)) ? "->" : ".");
                    
                    FieldInfo field = aggInfo.getFieldWithAnonymous(expr.field.type.name);
                    buf.out("%s", fieldName(field));
                }
                else {
                    for(FieldPathNode n : path.getPath()) {                
                        if(objectInfo.isKind(TypeKind.Ptr)) {
                            buf.out("->");
                        }
                        else {
                            buf.out(".");
                        }
                        
                        buf.out("%s", fieldName(n.field));
                        objectInfo = n.field.type;
                    }
                }
            }
        }
        
        @Override
        public void visit(SetExpr expr) {
            expr.object.visit(this);
            TypeInfo objectInfo = expr.object.getResolvedType().type;
                
            AggregateTypeInfo aggInfo = null;
            if(objectInfo.isKind(TypeKind.Ptr)) {
                if(!TypeInfo.isPtrAggregate(objectInfo)) {
                    buf.out("->%s %s ", expr.field.type.name, expr.operator.getText());
                    return;
                }
                
                PtrTypeInfo ptrInfo = objectInfo.as();
                aggInfo = ptrInfo.getBaseType().as();
            }
            else {
                aggInfo = objectInfo.as();
            }
            
            FieldPath path = aggInfo.getFieldPath(expr.field.type.name);
            if(!path.hasPath()) {
                buf.out((objectInfo.isKind(TypeKind.Ptr)) ? "->" : ".");
                
                FieldInfo field = aggInfo.getFieldWithAnonymous(expr.field.type.name);
                buf.out("%s %s", fieldName(field), expr.operator.getText());            
            }
            else {
                for(FieldPathNode n : path.getPath()) {
                    if(objectInfo.isKind(TypeKind.Ptr)) {
                        buf.out("->");
                    }
                    else {
                        buf.out(".");
                    }
                    
                    buf.out("%s", fieldName(n.field));
                    objectInfo = n.field.type;
                }
                
                buf.out("%s ", expr.operator.getText());
            }
            
            expr.value.visit(this);            
        }
    
    
        @Override
        public void visit(UnaryExpr expr) {
            buf.out("%s(", expr.operator.getText());
            expr.expr.visit(this);
            buf.out(")");
        }
    
    
        @Override
        public void visit(BinaryExpr expr) {
            expr.left.visit(this);
            buf.out(" %s ", expr.operator.getText());
            expr.right.visit(this);
        }
        
        @Override
        public void visit(TernaryExpr expr) {
            buf.out("(");
            expr.cond.visit(this);
            buf.out(") ? ");
            expr.then.visit(this);
            buf.out(" : ");
            expr.other.visit(this);        
        }
                
        @Override
        public void visit(ArrayInitExpr expr) {
            if(!expr.values.isEmpty()) {
                Node parent = expr.getParentNode();                
                boolean requiresCast = parent instanceof ReturnStmt ||
                                       parent instanceof FuncCallExpr;
                
                TypeInfo expectedType = expr.expectedType;
                if(expectedType == null || TypeInfo.isPtr(expectedType) || requiresCast) {
                    String typeName = getTypeNameForC(expr.getResolvedType().type);
                    buf.out("(%s)", typeName);
                }
                
                buf.out("{");
                boolean isFirst = true;
                for(Expr v : expr.values) {
                    if(!isFirst) buf.out(",\n");
                    isFirst = false;
                    
                    v.visit(this);
                }
                buf.out("}");
            }
            else {
                buf.appendRaw("{}");
            }
        }
        
        @Override
        public void visit(ArrayDesignationExpr expr) {
            buf.out("[");
            expr.index.visit(this);
            buf.out("] = ");
            expr.value.visit(this);
        }
        
        @Override
        public void visit(SubscriptGetExpr expr) {
            expr.object.visit(this);
            buf.out("[");
            expr.index.visit(this);
            buf.out("]");
        }
        
    
        
        @Override
        public void visit(SubscriptSetExpr expr) {
            expr.object.visit(this);
            buf.out("[");
            expr.index.visit(this);
            buf.out("] %s ", expr.operator.getText());
            
            expr.value.visit(this);
        }
        
    }
}
