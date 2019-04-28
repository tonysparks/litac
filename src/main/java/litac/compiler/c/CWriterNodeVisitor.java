/*
 * see license.txt
 */
package litac.compiler.c;

import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

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
import litac.checker.TypeInfo;
import litac.checker.TypeInfo.EnumFieldInfo;
import litac.checker.TypeInfo.FuncTypeInfo;
import litac.checker.TypeInfo.TypeKind;
import litac.compiler.Buf;
import litac.compiler.CompilationUnit;
import litac.compiler.CompileException;
import litac.compiler.c.CTranspiler.COptions;
import litac.util.Names;
import litac.util.Stack;

/**
 * @author Tony
 *
 */
public class CWriterNodeVisitor implements NodeVisitor {

    private COptions options;
    private Buf buf;
    
    private NameCache names;
    private Set<String> writtenModules;
    private CompilationUnit unit;
    
    private Stack<String> currentModule;
    private Stack<Queue<DeferStmt>> defers;
    
    public CWriterNodeVisitor(CompilationUnit unit, NameCache names, COptions options, Buf buf) {
        this.unit = unit;
        this.options = options;
        this.buf = buf;
        
        this.names = names;
        this.writtenModules = new HashSet<>();
        this.defers = new Stack<>();
        this.currentModule = new Stack<>();
        this.currentModule.add("");
        preface();
        this.currentModule.pop();
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
        buf.outln();
    }
    
    private void writeForwardDecl(Buf buf, String backendName, TypeInfo type) {
        
        if(isForeign(backendName)) {
            return;
        }
        
        String typeName = getTypeNameForC(type);
        
        switch(type.getKind()) {
            case Func: {
                FuncTypeInfo funcInfo = type.as();                                
                buf.out("%s %s(", getTypeNameForC(funcInfo.returnType), funcInfo.name);
                boolean isFirst = true;                
                for(ParameterDecl p : funcInfo.parameterDecls) {
                    if(!isFirst) buf.out(",");
                    
                    buf.out("%s", getTypeNameForC(p.type.getResolvedType()));
                    
                    isFirst = false;
                }
                buf.out(");\n");
                break;
            }
            case Struct: {                
                if(!type.isAnonymous()) {
                    buf.out("typedef struct %s %s;\n", typeName, typeName);
                }
                break;
            }
            case Union: {
                if(!type.isAnonymous()) {
                    buf.out("typedef union %s %s;\n", typeName, typeName);
                }
                break;
            }
            case Enum: {
                buf.out("typedef enum %s %s;\n", typeName, typeName);
                break;
            }
            default: {
               // throw new CompileException(String.format("Unsupported forward type declaration '%s'", type.getName()));
            }
        }
    }
    
    private void writeForwardDeclarations(Buf buf) {        
        Map<String, TypeInfo> types = this.names.getTypes();
        for(Map.Entry<String, TypeInfo> type : types.entrySet()) {
            writeForwardDecl(buf, type.getKey(), type.getValue());
        }
    }
    
    private String getTypeNameForC(TypeInfo type) {
        String typeName = type.getResolvedType().getName();
        return getTypeNameForC(typeName);
    }
    
    private String getTypeNameForC(String typeName) {
        String rawType = typeName.replace("*", "");
        if(isForeign(rawType)) {
            return typeName;
        }
        // TODO: Account for current processing of current module
        String backendRawType = this.names.getBackendName(this.currentModule.peek(), rawType);
        if(backendRawType == null) {
            return typeName;
        }
        
        return typeName.replace(rawType, backendRawType);
    }
    
    private String getBackendName(String litaName) {
        return this.names.getBackendName(this.currentModule.peek(), litaName);
    }
    
    private boolean isForeign(String typeName) {
        return this.names.isForeign(this.currentModule.peek(), typeName);
    }
        
    @Override
    public void visit(ModuleStmt stmt) {
        if(this.writtenModules.contains(stmt.name)) {
            return;
        }
        
        this.writtenModules.add(stmt.name);
        this.currentModule.add(stmt.name);
        
        for(ImportStmt i : stmt.imports) {
            i.visit(this);
        }
        
        for(NoteStmt n : stmt.notes) {
            n.visit(this);
        }
        
//        for(NoteStmt n : this.module.getNotes()) {
//            n.visit(this);
//        }
        
        for(Decl d : stmt.declarations) {
            d.visit(this);
        }
        
        this.currentModule.pop();
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
            buf.out("struct %s {", stmt.decl.name);
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
            buf.out("union %s {", stmt.decl.name);            
            for(FieldStmt f : stmt.decl.fields) {
                f.visit(this);
            }            
            buf.out("} %s;\n", stmt.decl.name);
        }
        buf.outln();

    }

    @Override
    public void visit(ConstDecl d) {
        String name = Names.backendName(this.currentModule.peek(), d.name);
        
        buf.out("#define %s ", name);
        d.expr.visit(this);
        buf.out("\n");
    }

    @Override
    public void visit(EnumDecl d) {
        String name = Names.backendName(this.currentModule.peek(), d.name);
        
        buf.outln();
        buf.out("enum %s {", name);
        boolean isFirst = true;
        for(EnumFieldInfo f : d.fields) {
            if(!isFirst) buf.out(",");
            
            buf.out("%s", f.name);
            if(f.value != null) {
                buf.out(" = ");
                f.value.visit(this);
            }
            buf.out("\n");
            
            isFirst = false;
        }            
        buf.out("};");
        buf.outln();
    }


    @Override
    public void visit(FuncDecl d) {
        String name = Names.backendName(this.currentModule.peek(), d.name);
        
        if(isForeign(name)) {
            return;
        }
        
        buf.outln();
        buf.out("%s %s(", getTypeNameForC(d.returnType.getResolvedType()), name);
        boolean isFirst = true;
        for(ParameterDecl p : d.params.params) {
            if(!isFirst) {
                buf.out(",");
            }
            
            buf.out("%s %s", getTypeNameForC(p.type.getResolvedType()), p.name);
            
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
        String name = Names.backendName(this.currentModule.peek(), d.name);
        
        buf.outln();
        buf.out("struct %s {", name);
        for(FieldStmt f : d.fields) {
            f.visit(this);
        }
        buf.out("};\n");
        buf.outln();
    }

    @Override
    public void visit(UnionDecl d) {
        String name = Names.backendName(this.currentModule.peek(), d.name);
        
        buf.outln();
        buf.out("union %s {", name);
        for(FieldStmt f : d.fields) {
            f.visit(this);
        }
        buf.out("};\n");
        buf.outln();
        
    }

    @Override
    public void visit(TypedefDecl d) {
        // TODO Auto-generated method stub
    }



    @Override
    public void visit(VarDecl d) {
        String name = Names.backendName(this.currentModule.peek(), d.name);
        
        buf.out("%s %s", getTypeNameForC(d.type), name);
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
        buf.out(";");
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
            outputDefer();
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
    public void visit(InitExpr expr) {            
        /*if(expr.type.isAnonymous()) {
            buf.out(" {");
        }
        else {
            buf.out("(%s) {", expr.type.name);
        }*/
        
        buf.out(" {");
        boolean isFirst = true;
        for(Expr e : expr.arguments) {
            if(!isFirst) buf.out(",");
            e.visit(this);
            buf.out("\n");
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
        buf.out(expr.number.getText());
    }


    @Override
    public void visit(StringExpr expr) {
        // TODO: escape all special chars
        String str = expr.string.replaceAll("(\\r|\\n|\\r\\n)+", "\\\\n");
        buf.appendRaw("\"").appendRaw(str).appendRaw("\"");
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
        String identifier = expr.variable;
        if(isForeign(identifier)) {
            buf.out("%s", Names.identifierFrom(identifier));    
        }
        else {            
            buf.out("%s", (expr.variable));
        }
    }

    @Override
    public void visit(FuncIdentifierExpr expr) {               
        String identifier = expr.variable;
        if(isForeign(identifier)) {
            buf.out("%s", Names.identifierFrom(identifier));    
        }
        else {            
            buf.out("%s", (expr.variable));
        }
    }

    @Override
    public void visit(GetExpr expr) {
        expr.object.visit(this);
        if(expr.object.getResolvedType().isKind(TypeKind.Ptr)) {
            buf.out("->");
        }
        else {
            buf.out(".");
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
        for(Expr d : expr.dimensions) {
            d.visit(this);
        }
        
        // TODO
        
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
