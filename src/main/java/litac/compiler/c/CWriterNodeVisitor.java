/*
 * see license.txt
 */
package litac.compiler.c;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;

import litac.LitaC;
import litac.ast.Decl;
import litac.ast.Expr;
import litac.ast.NodeVisitor;
import litac.ast.Stmt;
import litac.ast.Decl.*;
import litac.ast.Expr.*;
import litac.ast.Stmt.*;
import litac.checker.Module;
import litac.checker.Note;
import litac.checker.Symbol;
import litac.checker.TypeInfo;
import litac.checker.TypeInfo.*;
import litac.compiler.Buf;
import litac.compiler.CompilationUnit;
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
public class CWriterNodeVisitor implements NodeVisitor {

    private COptions options;
    private Buf buf;
    
    private Set<String> writtenModules;
    private CompilationUnit unit;
    private Module main;
        
    private Stack<Queue<DeferStmt>> defers;
    private int aggregateLevel;
    
    public CWriterNodeVisitor(CompilationUnit unit, Module main, COptions options, Buf buf) {
        this.unit = unit;
        this.main = main;
        this.options = options;
        this.buf = buf;
        
        this.writtenModules = new HashSet<>();
        this.defers = new Stack<>();
        preface();
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
        buf.out("typedef int8_t   i8;  \n");
        buf.out("typedef int16_t  i16; \n");
        buf.out("typedef int32_t  i32; \n");
        buf.out("typedef int64_t  i64; \n");
        buf.out("typedef uint8_t  u8;  \n");
        buf.out("typedef uint16_t u16; \n");
        buf.out("typedef uint32_t u32; \n");
        buf.out("typedef uint64_t u64; \n");
        buf.out("typedef float    f32; \n");
        buf.out("typedef double   f64; \n");
        buf.out("typedef int8_t   bool; \n");
        buf.out("#define true 1\n");
        buf.out("#define false 0\n");
        buf.outln();
    }
    
    private void writeForwardDecl(Buf buf, String backendName, TypeInfo type) {
        
        if(isForeign(type)) {
            return;
        }
        
        String typeName = backendName;
        switch(type.getKind()) {
            case Func: {
                FuncTypeInfo funcInfo = type.as();     
                if(funcInfo.hasGenerics()) {
                    return;
                }
                
                buf.out("%s %s(", getTypeNameForC(funcInfo.returnType), typeName);
                boolean isFirst = true;                
                for(ParameterDecl p : funcInfo.parameterDecls) {
                    if(!isFirst) buf.out(",");
                    
                    buf.out("%s", getTypeNameForC(p.type));
                    
                    isFirst = false;
                }
                buf.out(");\n");
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
            
            if(aKind == TypeKind.Func) {
                if(bKind == TypeKind.Func) {
                    return 0;
                }
                return 1;
            }
            else {
                if(bKind == TypeKind.Func) {
                    return -1;
                }
            }
            return 0;
        }
    };
    
    private void writeForwardDeclarations(Buf buf) {    
        Map<String, TypeInfo> types = new HashMap<>();
        writeModuleForwardDecl(buf, this.main, new ArrayList<>(), types);
        
        types.entrySet()
             .stream()
             .sorted(comp)
             .forEach(type -> writeForwardDecl(buf, type.getKey(), type.getValue()));        
    }
    
    private void writeModuleForwardDecl(Buf buf, Module module, List<Module> writtenModules, Map<String, TypeInfo> types) {
        if(writtenModules.contains(module)) {
            return;
        }
        
        writtenModules.add(module);
                
        module.getImports()
              .stream()
              .forEach(m -> writeModuleForwardDecl(buf, m, writtenModules, types));
        

        module.getDeclaredTypes()
              .forEach(type -> types.put(getTypeNameForC(type), type));
        
    }
    
    private String typeDeclForC(TypeInfo type, String declName) {
        switch (type.getKind()) {
            case Ptr: {
                PtrTypeInfo ptrInfo = type.as();
                TypeInfo baseInfo = ptrInfo.getBaseType();
                String baseName = getTypeNameForC(baseInfo);
                
                StringBuilder sb = new StringBuilder(baseName);
                do {
                    sb.append("*");
                    
                    ptrInfo = ptrInfo.ptrOf.isKind(TypeKind.Ptr) ?
                              ptrInfo.ptrOf.as() : null;
                }
                while(ptrInfo != null);
                
                
                return String.format("%s %s", sb.toString(), declName); 
            }
            case Array: {
                ArrayTypeInfo arrayInfo = type.as();      
                TypeInfo baseInfo = arrayInfo.getBaseType();
                String baseName = getTypeNameForC(baseInfo);
                
                StringBuilder sb = new StringBuilder();
                do {
                    if(arrayInfo.length < 0) {
                        sb.insert(0, "[]");
                    }
                    else {
                        sb.insert(0, String.format("[%d]", arrayInfo.length));
                    }
                    
                    
                    arrayInfo = arrayInfo.arrayOf.isKind(TypeKind.Array) ?
                                arrayInfo.arrayOf.as() : null;
                    
                } 
                while(arrayInfo != null);
                
                return String.format("%s %s%s", baseName, declName, sb.toString()); 
            }
            case Str: {
                return String.format("char* %s", declName);
            }
            case Enum: {
                String typeName = getTypeNameForC(type);
                return String.format("enum %s %s", typeName, declName);
            }
            default: {                
                String typeName = getTypeNameForC(type);
                return String.format("%s %s", typeName, declName);
            }
        }
    }
    
    private String getTypeNameForC(TypeInfo type) {
        switch (type.getKind()) {
            case Ptr: {
                PtrTypeInfo ptrInfo = type.as();
                return getTypeNameForC(ptrInfo.ptrOf) + "*";
            }
            case Array: {
                ArrayTypeInfo arrayInfo = type.as();                
                String name = getTypeNameForC(arrayInfo.arrayOf);
                if(arrayInfo.length < 0) {
                    return String.format("%s[]", name);
                }
                
                return String.format("%s[%d]", name, arrayInfo.length);
            }
            default: {
                return cTypeName(type);
            }
        }
    }
    
    private boolean isForeign(TypeInfo type) {
        if(type.sym == null) {
            return false;
        }
        
        return type.sym.isForeign();
    }
       
    private String cTypeName(TypeInfo type) {
        type = type.getResolvedType();
        if(type.sym == null) {
            return type.getName();
        }
        
        Symbol sym = type.sym;
        if(sym.isForeign()) {
            return type.getName();
        }
        
        return Names.backendName(sym.declared.name(), type.getName());
    }
    
    private String cName(Symbol sym) {
        if(sym.isLocal() || sym.isForeign()) {
            return sym.decl.name;
        }
        
        return Names.backendName(sym.declared.name(), sym.decl.name);
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
        
        for(NoteStmt n : stmt.notes) {
            n.visit(this);
        }
                
        
        //
        // TODO: Do proper dependency ordering from all modules
        //
        stmt.declarations
            .stream()            
            .sorted((a,b) -> {
                if(a.equals(b)) {
                    return 0;
                }
                
                TypeKind aKind = a.type.getKind();
                TypeKind bKind = b.type.getKind();
                
                if(aKind == TypeKind.Func) {
                    if(bKind == TypeKind.Func) {
                        return 0;
                    }
                    return 1;
                }
                else {
                    if(bKind == TypeKind.Func) {
                        return -1;
                    }
                }
                return 0;
            })
            .forEach(d -> d.visit(this));        
    }

    @Override
    public void visit(ImportStmt stmt) {
        ModuleStmt module = this.unit.getModule(stmt.moduleName);
        module.visit(this);
    }

    @Override
    public void visit(NoteStmt stmt) {
        Note note = stmt.note;
        switch(note.name) {
            case "include": {
                if(note.attributes != null) {
                    for(String header : note.attributes) {
                        buf.out("#include \"%s\"\n", header);
                    }
                }
                break;
            }
            case "foreign": {
                break;
            }
        }
    }
    
    @Override
    public void visit(VarFieldStmt stmt) {
        buf.out("%s %s;\n", getTypeNameForC(stmt.type), stmt.name);
    }

    @Override
    public void visit(ParameterDecl d) {
    }
    
    @Override
    public void visit(ParametersStmt stmt) {
    }
    
    @Override
    public void visit(StructFieldStmt stmt) {
        buf.outln();
        if(stmt.decl.type.isAnonymous()) {
            buf.out("struct {");
            for(FieldStmt f : stmt.decl.fields) {
                f.visit(this);
            }
            buf.out("};\n");
        }
        else {
            buf.out("struct %s {", stmt.decl.type.getName());
            for(FieldStmt f : stmt.decl.fields) {
                f.visit(this);
            }
            buf.out("} %s;\n", stmt.decl.name);
        }
        buf.outln();
    }

    @Override
    public void visit(UnionFieldStmt stmt) {
        buf.outln();
        if(stmt.decl.type.isAnonymous()) {
            buf.out("union {");            
            for(FieldStmt f : stmt.decl.fields) {
                f.visit(this);
            }            
            buf.out("};\n");
        }
        else {
            buf.out("union %s {", stmt.decl.type.getName());            
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
        
        buf.out("enum %s {", stmt.decl.type.getName());  
        boolean isFirst = true;
        for(EnumFieldInfo f : stmt.decl.fields) {
            if(!isFirst) buf.out(",\n");
            isFirst = false;
            buf.out("%s", f.name);
            if(f.value != null) {
                buf.out(" = ");
                f.value.visit(this);
            }
        }                    
        buf.out("} %s;\n", stmt.decl.name);
        
        buf.outln();
    }
    
    @Override
    public void visit(ConstDecl d) {
        String name = cName(d.sym);
        
        if(isForeign(d.type)) {
            return;
        }
        
        buf.out("const %s = ", this.typeDeclForC(d.type, name));
        d.expr.visit(this);
        buf.out(";\n");
    }

    @Override
    public void visit(EnumDecl d) {
        String name = cName(d.sym);
        
        if(isForeign(d.type)) {
            return;
        }
        
        buf.outln();
        buf.out("enum %s {", name);
        boolean isFirst = true;
        for(EnumFieldInfo f : d.fields) {
            if(!isFirst) buf.out(",\n");
            
            buf.out("%s", f.name);
            if(f.value != null) {
                buf.out(" = ");
                f.value.visit(this);
            }                        
            isFirst = false;
        }            
        buf.out("};");
        buf.outln();
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
        
        buf.outln();
        buf.out("%s %s(", getTypeNameForC(d.returnType), name);
        boolean isFirst = true;
        for(ParameterDecl p : d.params.params) {
            if(!isFirst) {
                buf.out(",");
            }
            
            buf.out("%s %s", getTypeNameForC(p.type), p.name);
            
            isFirst = false;
        }
        
        // TODO: Varargs
        
        buf.out(") ");
        boolean isBlock = (d.bodyStmt instanceof BlockStmt);
        if(!isBlock) buf.out("{");
        d.bodyStmt.visit(this);
        if(!isBlock) buf.out("}");
        buf.outln();
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
        
        aggregateLevel++;
        buf.outln();
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
        
        aggregateLevel++;
        buf.outln();
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
        // TODO Auto-generated method stub
    }



    @Override
    public void visit(VarDecl d) {
        String name = cName(d.sym);
        
        if(isForeign(d.type)) {
            return;
        }
        
        buf.out("%s", typeDeclForC(d.type, name));
        
        if(d.expr != null) {
            buf.out(" = ");
            d.expr.visit(this);
        }
        buf.out(";\n");
    }

    
    @Override
    public void visit(IfStmt stmt) {
        buf.out("if (");
        stmt.condExpr.visit(this);
        buf.out(") \n");            
        stmt.thenStmt.visit(this);            
        buf.out("\n");
        
        if(stmt.elseStmt != null) {
            buf.out("else \n");                
            stmt.elseStmt.visit(this);                
            buf.out("\n");
        }
    }

    @Override
    public void visit(WhileStmt stmt) {
        buf.out("while (");
        stmt.condExpr.visit(this);
        buf.out(")\n");
        stmt.bodyStmt.visit(this);
        buf.out("\n");
    }


    @Override
    public void visit(DoWhileStmt stmt) {
        buf.out("do ");
        stmt.bodyStmt.visit(this);
        buf.out("\n while (");
        stmt.condExpr.visit(this);
        buf.out(");");
    }


    @Override
    public void visit(ForStmt stmt) {
        buf.out("for(");
        if(stmt.initStmt != null) stmt.initStmt.visit(this);
        //buf.out(";");
        if(stmt.condExpr != null) stmt.condExpr.visit(this);
        buf.out(";");
        if(stmt.postStmt != null) stmt.postStmt.visit(this);
        buf.out(") {");
        stmt.bodyStmt.visit(this);
        buf.out("}\n");
    }
    
    
    private void outputDefer() {
        outputDefer(this.defers.peek());
    }
    
    private void outputDefer(Queue<DeferStmt> q) {
        for(DeferStmt s : q){
            s.stmt.visit(this);
            buf.out(";\n");
        }
    }

    @Override
    public void visit(BreakStmt stmt) {
        outputDefer();
        buf.out("break;\n");
    }

    @Override
    public void visit(ContinueStmt stmt) {
        outputDefer();
        buf.out("continue;\n");
    }


    @Override
    public void visit(ReturnStmt stmt) {
        for(Queue<DeferStmt> d : this.defers) {
            outputDefer(d);
        }
        
        buf.out("return");
        if(stmt.returnExpr != null) {
            buf.out(" ");
            stmt.returnExpr.visit(this);
        }
        buf.out(";\n");
    }

    @Override
    public void visit(BlockStmt stmt) {
        buf.out("{");
        
        this.defers.add(new LinkedList<>());
        for(Stmt s : stmt.stmts) {
            s.visit(this);
            if(s instanceof Expr) {
                buf.out(";\n");
            }
        }
        
        if(!stmt.stmts.isEmpty() && !(stmt.stmts.get(stmt.stmts.size() - 1) instanceof ReturnStmt)) {
            outputDefer(this.defers.pop());
        }
        
        buf.out("}\n");
    }

    @Override
    public void visit(DeferStmt stmt) {
        this.defers.peek().add(stmt);
    }

    @Override
    public void visit(EmptyStmt stmt) {
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
    public void visit(InitArgExpr expr) {
        if(expr.fieldName != null) {
            buf.out(".%s = ", expr.fieldName);
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
            buf.out("true");
        }
        else {
            buf.out("false");
        }
    }

    @Override
    public void visit(NumberExpr expr) {
        String n = expr.number.getText(); 
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
            case u128:
                buf.out("ULL");
                break;
            case i64:
                buf.out("L");
                break;
            case i128:
                buf.out("LL");
                break;
            default:
        }
        
    }


    @Override
    public void visit(StringExpr expr) {
        // TODO: escape all special chars
        String str = expr.string.replaceAll("(\\r|\\n|\\r\\n)+", "\\\\n");
        buf.appendRaw("\"").appendRaw(str).appendRaw("\"");
    }
    
    @Override
    public void visit(CharExpr expr) {
        // TODO: escape all special chars
        String str = expr.character.replaceAll("(\\r|\\n|\\r\\n)+", "\\\\n");
        buf.appendRaw("\'").appendRaw(str).appendRaw("\'");
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
        buf.out("(");
        boolean isFirst = true;
        for(Expr e : expr.arguments) {
            if(!isFirst) buf.out(",");
            e.visit(this);                
            isFirst = false;
        }
        buf.out(")");
    }

    @Override
    public void visit(IdentifierExpr expr) {
        Symbol sym = expr.sym;
        if(sym != null) {
            buf.out("%s", cName(sym));
        }
        else {
            buf.out("%s", expr.type.getName());
        }
    }

    @Override
    public void visit(FuncIdentifierExpr expr) {
        Symbol sym = expr.sym;
        if(sym != null) {
            buf.out("%s", cName(sym));
        }
        else {
            buf.out("%s", expr.type.getName());
        }
    }

    @Override
    public void visit(GetExpr expr) {
        if(!expr.object.getResolvedType().isKind(TypeKind.Enum)) {
            expr.object.visit(this);
            if(expr.object.getResolvedType().isKind(TypeKind.Ptr)) {
                buf.out("->");
            }
            else {
                buf.out(".");
            }
        }
        
        buf.out("%s", expr.field.name);        
    }
    
    @Override
    public void visit(SetExpr expr) {
        expr.object.visit(this);
        if(expr.object.getResolvedType().isKind(TypeKind.Ptr)) {
            buf.out("->");
        }
        else {
            buf.out(".");
        }
        buf.out("%s %s ", expr.field.name, expr.operator.getText());
        
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
    public void visit(ArrayInitExpr expr) {
        if(!expr.values.isEmpty()) {
            buf.out(" {");
            boolean isFirst = true;
            for(Expr v : expr.values) {
                if(!isFirst) buf.out(",");
                isFirst = false;
                
                v.visit(this);
            }
            buf.out(" }");
        }
        else {
            buf.appendRaw("{0}");
        }
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
