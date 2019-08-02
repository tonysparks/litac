/*
 * see license.txt
 */
package litac.compiler.c;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import litac.LitaC;
import litac.ast.*;
import litac.ast.Decl.*;
import litac.ast.Expr.*;
import litac.ast.Stmt.*;
import litac.checker.*;
import litac.checker.TypeInfo.*;
import litac.compiler.*;
import litac.compiler.BackendOptions.OutputType;
import litac.compiler.FieldPath.FieldPathNode;
import litac.compiler.c.CTranspiler.COptions;
import litac.util.Names;
import litac.util.Stack;

/**
 * Writes out the AST nodes into a single C file.  It will place all type
 * forward declarations at the top, then traverse each module and print out
 * the implementations, finishing up with the main module.
 * 
 * @author Tony
 *
 */
public class CGenNodeVisitor implements NodeVisitor {

    private COptions options;
    private Buf buf;
    
    private Set<String> writtenModules;
    private CompilationUnit unit;
    private Module main;
    private Program program;    
    
    private Stack<FuncTypeInfo> currentFuncType;
    private Stack<Stack<DeferStmt>> defers;
    private int aggregateLevel;
    private Pattern testPattern;
    
    private List<Decl> declarations;
    private int currentLine;
    private String currentFile;
    
    private List<Decl> moduleInitFunc;
    private List<Decl> moduleDestroyFunc;
    private Decl mainFunc;
    
    public CGenNodeVisitor(CompilationUnit unit, Program program, COptions options, Buf buf) {
        this.unit = unit;
        this.program = program;
        this.options = options;
        this.buf = buf;
                
        if(this.options.options.testRegex != null) {
            this.testPattern = Pattern.compile(this.options.options.testRegex);
        }
        
        this.main = program.getMainModule();
        this.declarations = new ArrayList<>();
        this.moduleInitFunc = new ArrayList<>();
        this.moduleDestroyFunc = new ArrayList<>();
        this.currentFuncType = new Stack<>();
        
        this.writtenModules = new HashSet<>();
        this.defers = new Stack<>();
        preface();
    }
    
    public void write() {
        this.main.getModuleStmt().visit(this);
        
        DependencyGraph graph = new DependencyGraph(this.main.getPhaseResult());
        List<Decl> typeInfos = Reflection.createTypeInfos(this.declarations, this.program, this.options.options.typeInfo);
        this.declarations.addAll(0, typeInfos); 
        
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
            
            d.visit(this);            
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
        
        this.unit.getBuiltin().visit(this);
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
                
                visitNotes(funcInfo.sym.decl.attributes.notes);
                
                if(funcInfo.returnType.isKind(TypeKind.FuncPtr)) {
                    FuncPtrTypeInfo funcPtr = funcInfo.returnType.as();
                    buf.out("%s (*%s(", getTypeNameForC(funcPtr.returnType), typeName);
                }
                else {
                    buf.out("%s %s(", getTypeNameForC(funcInfo.returnType), typeName);
                }
                boolean isFirst = true;                
                for(ParameterDecl p : funcInfo.parameterDecls) {
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
                type.sym.decl.visit(this);
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
        
        notes.forEach(note -> note.visit(this));
        
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
              .forEach(type -> types.put(getTypeNameForC(type), type));
        
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
        buf.outln();
        buf.out("// Main").outln();
        buf.out("int main(int argn, char** args) {");
        
        for(Decl d : this.moduleInitFunc) {
            buf.out("%s();\n", cName(d.sym));
        }
        
        if(main instanceof FuncDecl) {
            FuncDecl funcDecl = (FuncDecl)main;
            funcDecl.bodyStmt.visit(this);
        }
        
        for(Decl d : this.moduleDestroyFunc) {
            buf.out("%s();\n", cName(d.sym));
        }
        
        buf.out("}");
    }
    
    private String typeDeclForC(TypeInfo type, String declName) {
        switch (type.getKind()) {
            case Ptr: {
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
                        sb.append("[]");
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
    
    //private String funcPtr(FuncPtrTypeInfo funcInfo,)
    
    private String getTypeNameForC(TypeInfo type) {
        switch (type.getKind()) {
            case Ptr: {
                PtrTypeInfo ptrInfo = type.as();
                return getTypeNameForC(ptrInfo.ptrOf) + "*";
            }
            case Const: {
                ConstTypeInfo constInfo = type.as();
                return "const " + getTypeNameForC(constInfo.constOf);
            }
            case Array: {
                ArrayTypeInfo arrayInfo = type.as();                
                String name = getTypeNameForC(arrayInfo.arrayOf);
                if(arrayInfo.length < 0) {
                    return String.format("%s[]", name);
                }
                
                return String.format("%s[%d]", name, arrayInfo.length);
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
                
                return String.format("%s (*%s)(%s)", getTypeNameForC(funcInfo.returnType), /*prefix(funcInfo.getName())*/ cTypeName(funcInfo), params);
            }
            default: {
                return cTypeName(type);
            }
        }
    }
    
    public boolean isTestIncluded(NoteStmt note) {        
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
        type = type.getResolvedType();
        String typeName = Names.escapeName(type);
        
        if(type.sym == null) {
            return prefix(typeName);
        }
        
        Symbol sym = type.sym;
        if(sym.isForeign()) {            
            return getForeignName(sym.decl, Names.baseTypeName(type.getName()));
        }
        
        return prefix(Names.backendName(sym.declared.name(), typeName));
    }
    
    private String cName(Symbol sym) {
        String declName = sym.isType() ? Names.escapeName(sym.getType()) : sym.decl.name;
        
        if(sym.isLocal()) {
            return prefix(declName);
        }
        
        if(sym.isForeign()) {
            return getForeignName(sym.decl, Names.baseTypeName(sym.decl.name));
        }
        
        if(sym.decl.kind == DeclKind.FUNC) {
            if(sym.decl.name.equals("main")) {
                return declName;
            }
        }
        
        return prefix(Names.backendName(sym.declared.name(), declName));
    }
    
    private void checkLine(Stmt stmt) {
        if(options.options.disableLines) {
            return;
        }
        
        String sourceFile = stmt.getSourceFile();
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
    
    private void visitNotes(List<NoteStmt> notes) {
        if(notes != null) {
            notes.forEach(n -> n.visit(this));
        }
    }
        
    @Override
    public void visit(ModuleStmt stmt) {
        if(this.writtenModules.contains(stmt.name)) {
            return;
        }
        
        this.writtenModules.add(stmt.name);
        
        for(ImportStmt i : stmt.imports) {
            i.visit(this);
        }
                
        this.declarations.addAll(stmt.declarations);
    }

    @Override
    public void visit(ImportStmt stmt) {
        if(stmt.moduleName.equals("builtin")) {
            return;
        }
        
        ModuleStmt module = this.unit.getModule(stmt.moduleName);
        module.visit(this);
    }

    @Override
    public void visit(NoteStmt note) {
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
                    for(String file: note.attributes) {
                        try {
                            String contents = new String(Files.readAllBytes(new File(file).toPath()));
                            buf.appendRaw(contents);
                            buf.outln();
                        }
                        catch(IOException e) {
                            this.main.getPhaseResult().addError(note, "Unable to load C source file: %s", e);
                        }
                    }
                }
                break;
            }
            case "inline": {
                buf.out("INLINE ");
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
                buf.out("THREADLOCAL ");
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
        if(stmt.decl.type.isAnonymous()) {
            buf.out("struct {");
            for(FieldStmt f : stmt.decl.fields) {
                f.visit(this);
            }
            buf.out("};\n");
        }
        else {
            buf.out("struct %s {", cTypeName(stmt.decl.type));
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
        if(stmt.decl.type.isAnonymous()) {
            buf.out("union {");            
            for(FieldStmt f : stmt.decl.fields) {
                f.visit(this);
            }            
            buf.out("};\n");
        }
        else {
            buf.out("union %s {", cTypeName(stmt.decl.type));            
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
        buf.out("enum %s %s;\n", cTypeName(stmt.decl.type), stmt.decl.name);
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
        if(d.type.isPrimitive()) {
            buf.out("const ");
        }
        buf.out("%s = ", typeDeclForC(d.type, name));
        d.expr.visit(this);
        buf.out(";\n");
    }

    private String name(String name, Attributes attributes) {
        NoteStmt note = attributes.getNote("alias");
        if(note == null) {
            return name;
        }
        
        String alias = note.getAttr(0, null);
        if(alias == null) {
            this.main.getPhaseResult().addError(note, "'alias' note must define an alias name");
            return "";
        }
        
        return alias;
        
    }

    private void writeEnumStrFunc(String name, EnumDecl d) {
        NoteStmt asStr = d.attributes.getNote("asStr");
        if(asStr != null) {            
            buf.outln();
            buf.out("const char* __%s_%s_AsStr(%s __e) {", d.sym.declared.name(), d.name, name);            
            buf.out("switch(__e) {");            
            for(EnumFieldInfo f : d.fields) {                                
                buf.out("case %s_%s: return \"%s\";\n", name, f.name, f.name);                
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
        
        if(isForeign(d.type)) {
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
        
        if(isForeign(d.type)) {
            return;
        }
        
        FuncTypeInfo funcInfo = d.type.as();
        if(funcInfo.hasGenerics()) {
            return;
        }

        visitNotes(d.attributes.notes);
        
        currentFuncType.add(funcInfo);
        
        buf.outln();
        checkLine(d);
        
        if(d.returnType.isKind(TypeKind.FuncPtr)) {
            FuncPtrTypeInfo funcPtr = d.returnType.as();
            buf.out("%s (*%s(", getTypeNameForC(funcPtr.returnType), name);
        }
        else {
            buf.out("%s %s(", getTypeNameForC(d.returnType), name);
        }
        
        boolean isFirst = true;
        for(ParameterDecl p : d.params.params) {
            if(!isFirst) {
                buf.out(",");
            }
                        
            buf.out("%s", typeDeclForC(p.type, prefix(p.name))); 
            
            isFirst = false;
        }
        
        if(funcInfo.isVararg()) {
            if(!isFirst) {
                buf.out(",");
            }
            
            buf.out("...");
        }
        
        
        buf.out(")");
        
        if(d.returnType.isKind(TypeKind.FuncPtr)) {
            buf.out(") (");
            FuncPtrTypeInfo funcPtr = d.returnType.as();
            
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
        if(!isBlock) buf.out("}");
        buf.outln();
        
        currentFuncType.pop();
    }


    @Override
    public void visit(StructDecl d) {
        String name = cName(d.sym);
        
        if(isForeign(d.type)) {
            return;
        }
        
        StructTypeInfo structInfo = d.type.as();
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
        buf.outln();
        aggregateLevel--;
    }

    @Override
    public void visit(UnionDecl d) {
        String name = cName(d.sym);
        
        if(isForeign(d.type)) {
            return;
        }
        
        UnionTypeInfo unionInfo = d.type.as();
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
        
        if(d.type.isKind(TypeKind.FuncPtr)) {
            FuncPtrTypeInfo funcInfo = d.type.as();
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
        buf.out("%s", typeDeclForC(d.type, name));
        
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
        buf.out(") \n");            
        stmt.thenStmt.visit(this);
        if(stmt.thenStmt instanceof Expr) buf.out(";");
        buf.out("\n");
        
        if(stmt.elseStmt != null) {
            buf.out("else \n");                
            stmt.elseStmt.visit(this);
            if(stmt.elseStmt instanceof Expr) buf.out(";");
            buf.out("\n");
        }
    }

    @Override
    public void visit(WhileStmt stmt) {
        checkLine(stmt);
        
        buf.out("while (");
        stmt.condExpr.visit(this);
        buf.out(")\n");
        stmt.bodyStmt.visit(this);
        if(stmt.bodyStmt instanceof Expr) buf.out(";");
        buf.out("\n");
    }


    @Override
    public void visit(DoWhileStmt stmt) {
        checkLine(stmt);
        
        buf.out("do {");
        stmt.bodyStmt.visit(this);
        buf.out("}\n while (");
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
        stmt.bodyStmt.visit(this);
        buf.out("}\n");
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
    
    
    private void outputDefer() {
        if(!this.defers.isEmpty()) {
            outputDefer(this.defers.peek());
        }
    }
    
    private void outputDefer(Stack<DeferStmt> q) {
        ListIterator<DeferStmt> it = q.listIterator(q.size());
        while(it.hasPrevious()) {
            DeferStmt s = it.previous();
            s.stmt.visit(this);
            buf.out(";\n");
        }
    }

    @Override
    public void visit(BreakStmt stmt) {
        outputDefer();
        checkLine(stmt);
        buf.out("break;\n");
    }

    @Override
    public void visit(ContinueStmt stmt) {
        outputDefer();
        checkLine(stmt);
        buf.out("continue;\n");
    }


    @Override
    public void visit(ReturnStmt stmt) {
        checkLine(stmt);
        
        if(!this.defers.isEmpty() && 
            this.defers.stream().anyMatch(s->!s.isEmpty()) && 
            stmt.returnExpr != null) {
            
            //TypeInfo type = stmt.returnExpr.getResolvedType().getResolvedType();
            TypeInfo type = this.currentFuncType.peek().returnType.getResolvedType();
            buf.out("{\n%s = ", typeDeclForC(type, "___result"));
            stmt.returnExpr.visit(this);
            buf.out(";\n");
            
            for(Stack<DeferStmt> d : this.defers) {
                outputDefer(d);
            }
            
            buf.out("return ___result;\n}\n");                        
        }
        else {
            for(Stack<DeferStmt> d : this.defers) {
                outputDefer(d);
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
        
        int deferCount = this.defers.size();
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
        
        if(this.defers.size() > deferCount) {
            outputDefer(this.defers.pop());
        }
        
        buf.out("}\n");
    }

    @Override
    public void visit(DeferStmt stmt) {
        checkLine(stmt);
        
        if(this.defers.isEmpty()) {
            this.defers.add(new Stack<>());
        }
        
        this.defers.peek().add(stmt);
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
        buf.out("(%s)", getTypeNameForC(expr.castTo));
        expr.expr.visit(this);
    }
    
    @Override
    public void visit(SizeOfExpr expr) {
        buf.out("sizeof(");
        expr.expr.visit(this);
        buf.out(")");
    }
    
    @Override
    public void visit(TypeOfExpr expr) {
        buf.out("(%s)", expr.expr.getResolvedType().getResolvedType().getTypeId());
    }
    
    @Override
    public void visit(InitArgExpr expr) {
        String fieldName = expr.fieldName;
        
        Node parent = expr.getParentNode();
        if(fieldName != null && parent instanceof InitExpr) {
            InitExpr initExpr = (InitExpr)parent;
            AggregateTypeInfo type = initExpr.getResolvedType().getResolvedType().as();
            FieldInfo field = type.getFieldWithAnonymous(expr.fieldName);
            fieldName = fieldName(field);           
        }
        
        if(fieldName != null) {
            buf.out(".%s = ", fieldName);
        }                
        expr.value.visit(this);
    }
    
    @Override
    public void visit(InitExpr expr) {            
        /*if(expr.type.isAnonymous()) {
            buf.out(" {");
        }
        else {
            buf.out("(%s) {", expr.type.name);
        }*/
        
        if(!(expr.getParentNode() instanceof Decl)) {
            buf.out("(%s)", getTypeNameForC(expr.type));
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
        
        switch(expr.type.getKind()) {
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
        
        List<ParameterDecl> params = null;
        if(expr.object.getResolvedType().isKind(TypeKind.Func)) {
            FuncTypeInfo funcInfo = expr.object.getResolvedType().as();
            params = funcInfo.parameterDecls;
        }
        
        List<Expr> suppliedArguments = new ArrayList<>(expr.arguments);
        
        GetExpr getExpr = getMethodCall(expr.object);
        if(getExpr != null) {
            suppliedArguments.add(0, getExpr.object);
            buf.out("%s", getTypeNameForC(getExpr.field.type));
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
                    ParameterDecl p = params.get(i);
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

    @Override
    public void visit(IdentifierExpr expr) {
        Symbol sym = expr.sym;
        if(sym != null) {
            if(sym.isUsing()) {
                TypeInfo paramInfo = sym.decl.type;
                AggregateTypeInfo aggInfo = null;
                if(paramInfo.isKind(TypeKind.Ptr)) {
                    PtrTypeInfo ptrInfo = paramInfo.as();
                    aggInfo = ptrInfo.getBaseType().as();
                }
                else {
                    aggInfo = sym.decl.type.as();
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
            else {
                buf.out("%s", cName(sym));
            }
        }
        else {
            buf.out("%s", prefix(expr.type.getName()));
        }
    }

    @Override
    public void visit(FuncIdentifierExpr expr) {
        visit((IdentifierExpr)expr);
    }

    @Override
    public void visit(TypeIdentifierExpr expr) {
        if(expr.sym == null) {
            buf.out("%s", getTypeNameForC(expr.type));
        }
        else {
            visit((IdentifierExpr)expr);
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
        
        TypeInfo objectInfo = expr.object.getResolvedType();
        if(objectInfo.isKind(TypeKind.Enum)) {
            if(isForeign(objectInfo.getResolvedType())) {
                buf.out("%s", expr.field.type.name);
            }
            else {
                buf.out("%s_%s", cName(objectInfo.getResolvedType().sym), expr.field.type.name);
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
                
                aggInfo = ((PtrTypeInfo)objectInfo).getBaseType().as();
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
        TypeInfo objectInfo = expr.object.getResolvedType();
            
        AggregateTypeInfo aggInfo = null;
        if(objectInfo.isKind(TypeKind.Ptr)) {
            if(!TypeInfo.isPtrAggregate(objectInfo)) {
                buf.out("->%s %s ", expr.field.type.name, expr.operator.getText());
                return;
            }
            
            aggInfo = ((PtrTypeInfo)objectInfo).getBaseType().as();
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
        buf.out("%s", expr.operator.getText());
        expr.expr.visit(this);
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
