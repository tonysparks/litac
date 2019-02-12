/*
 * see license.txt
 */
package litac.compiler.llvm;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import litac.ast.Decl.*;
import litac.ast.Decl;
import litac.ast.Expr;
import litac.ast.Expr.*;
import litac.ast.Stmt.*;
import litac.ast.NodeVisitor;
import litac.ast.Stmt;
import litac.Errors;
import litac.checker.Attributes;
import litac.checker.Module;
import litac.checker.TypeCheckResult;
import litac.checker.TypeCheckResult.TypeCheckError;
import litac.checker.TypeChecker;
import litac.checker.TypeChecker.TypeCheckerOptions;
import litac.checker.TypeInfo;
import litac.checker.TypeInfo.*;
import litac.compiler.Buf;
import litac.compiler.CompileException;
import litac.parser.Parser;
import litac.parser.Scanner;
import litac.parser.Source;
import litac.parser.tokens.TokenType;
import litac.util.OS.OsType;

/**
 * @author Tony
 *
 */
public class LLVMTranspiler {

    private static final DecimalFormat f32Format = new DecimalFormat("##0.0####E0"); // TODO set proper mantissa // "##0.#####E0"
    private static final DecimalFormat f64Format = new DecimalFormat("##0.0####E0"); // TODO set proper mantissa
    
    public static class TranspilerOptions {
        public File outputDir;        
        public String outputFileName;
        public TypeCheckerOptions checkerOptions;
        public OsType targetOS;
        public boolean useTabs;
        public int indentWidth;
        
        public TranspilerOptions() {
            this.checkerOptions = new TypeCheckerOptions();
            this.outputDir = new File(System.getProperty("user.dir"), "output");
            this.outputFileName = "a";
            
            this.targetOS = OsType.WINDOWS;
            
            this.useTabs = false;
            this.indentWidth = 3;
        }
    }

    public static void transpile(ModuleStmt program, TranspilerOptions options) throws Exception {
        TypeCheckResult checkerResult = TypeChecker.typeCheck(options.checkerOptions, program);
        if(checkerResult.hasErrors()) {
            for(TypeCheckError error : checkerResult.getErrors()) {
                Errors.typeCheckError(error.stmt, error.message);
            }
        }
        

        String tabSpaces = "";
        for(int i = 0; i < options.indentWidth; i++) {
            tabSpaces += " ";
        }
        
        Buf buf = new Buf(tabSpaces, options.useTabs);
        
     //   writeForwardDeclarations(checkerResult.getModule(), buf);
        
        LLVMWriterNodeVisitor visitor = new LLVMWriterNodeVisitor(options, program, buf);
        program.visit(visitor);
        
        System.out.println(buf);
        
        Path output = new File("a.ll").toPath();
        if(Files.exists(output)) {
            Files.delete(output);
        }
        Files.write(output, buf.toString().getBytes());
    }


    private static CompileException error(Stmt stmt, String message, Object ...args) {
        return new CompileException(String.format(message, args) + 
                String.format(" at line %d in '%s'", stmt.getLineNumber(), stmt.getSourceFile()));
    }
        
    private static void writeForwardDecl(Module module, Buf buf, TypeInfo type) {
        switch(type.getKind()) {
            case Func: {
                FuncTypeInfo funcInfo = type.as();
                buf.out("%s %s(", funcInfo.returnType.getResolvedType(), funcInfo.name);
                boolean isFirst = true;                
                for(ParameterDecl p : funcInfo.parameterDecls) {
                    if(!isFirst) buf.out(",");
                    
                    buf.out("%s", p.type.getResolvedType());
                    
                    isFirst = false;
                }
                buf.out(");\n");
                break;
            }
            case Struct: {                    
                buf.out("typedef struct %s %s;\n", type.getName(), type.getName());
                break;
            }
            case Union: {
                buf.out("typedef union %s %s;\n", type.getName(), type.getName());
                break;
            }
            case Enum: {
                buf.out("typedef enum %s %s;\n", type.getName(), type.getName());
                break;
            }
            default: {
                throw new CompileException(String.format("Unsupported forward type declaration '%s'", type.getName()));
            }
        }
    }
    
    private static void writeModule(Module module, Buf buf, Set<Module> visited) {
        if(visited.contains(module)) {
            return;
        }
        
        visited.add(module);
        
        for(Module m : module.getImports()) {
            writeModule(m, buf, visited);
        }
        
        buf.out("; Start Module '%s' \n", module.name);
        
        for(TypeInfo type : module.getDeclaredTypes()) {
            writeForwardDecl(module, buf, type);
        }
        
        for(TypeInfo type : module.getDeclaredFuncs()) {
            writeForwardDecl(module, buf, type);
        }
        
        buf.out("; End Module '%s' \n\n", module.name);
    }
    
    private static void writeForwardDeclarations(Module module, Buf buf) {
        // TODO, figure out name scoping for modules
        // options:
        // 1) always scope names as:
        //     moduleName__typeName
        
        writeModule(module, buf, new HashSet<>());            
    }
    
    private static class LLVMWriterNodeVisitor implements NodeVisitor {
        class StructAnonType {
            int anonId;
            StructFieldStmt stmt;
            
            StructAnonType(int anonId, StructFieldStmt stmt) {
                this.anonId = anonId;
                this.stmt = stmt;
            }
        }
        
        class UnionAnonType {
            int anonId;
            UnionFieldStmt stmt;
            
            UnionAnonType(int anonId, UnionFieldStmt stmt) {
                this.anonId = anonId;
                this.stmt = stmt;
            }
        }
        
        class AnonScope {
            Queue<StructAnonType> anonStructs = new LinkedList<>();
            Queue<UnionAnonType> anonUnions = new LinkedList<>();
        }
        
//        class EnumAnonType {
//            int anonId;
//            EnumFieldStmt stmt;
//        }
                
        private TranspilerOptions options;
        
        private List<ModuleStmt> modules;
        private Buf buf;
        private int anonStruct;
        
        private Queue<AnonScope> anonScopes;
        
        private LLVMScope scope;
        
        
        public LLVMWriterNodeVisitor(TranspilerOptions options, ModuleStmt program, Buf buf) {
            this.options = options;
            this.buf = buf;
            
            this.modules = new ArrayList<>();
            this.modules.add(program);
            
            this.anonStruct = 0;
            
            this.anonScopes = new LinkedList<>();
            this.scope = new LLVMScope();
        }
        
        private void printLLVMHeader() {
            buf.out("target datalayout = \"e-m:w-i64:64-f80:128-n8:16:32:64-S128\"\n");
            buf.out("target triple = \"x86_64-pc-windows-msvc19.15.26726\"\n");
            buf.out("attributes #0 = { noinline nounwind optnone uwtable"
                    + " \"correctly-rounded-divide-sqrt-fp-math\"=\"false\""
                    + " \"disable-tail-calls\"=\"false\""
                    + " \"less-precise-fpmad\"=\"false\""
                    + " \"no-frame-pointer-elim\"=\"false\""
                    + " \"no-infs-fp-math\"=\"false\""
                    + " \"no-jump-tables\"=\"false\""
                    + " \"no-nans-fp-math\"=\"false\""
                    + " \"no-signed-zeros-fp-math\"=\"false\""
                    + " \"no-trapping-math\"=\"false\""
                    + " \"stack-protector-buffer-size\"=\"8\""
                    + " \"target-cpu\"=\"x86-64\""
                    + " \"target-features\"=\"+fxsr,+mmx,+sse,+sse2,+x87\""
                    + " \"unsafe-fp-math\"=\"false\""
                    + " \"use-soft-float\"=\"false\" }\n");
        }
        
        @Override
        public void visit(ModuleStmt stmt) {
            printLLVMHeader();
            
            buf.out("source_filename = \"%s\"\n\n", stmt.getSourceFile());
            
            for(ImportStmt i : stmt.imports) {
                i.visit(this);
            }
            
            for(Decl d : stmt.declarations) {
                d.visit(this);
            }
        }

        @Override
        public void visit(ImportStmt stmt) {
            File importFile = new File(this.options.checkerOptions.srcDir.getAbsolutePath(), stmt.moduleName + ".lita");
            if(!importFile.exists()) {
                throw error(stmt, "could not find module '%s' at '%s'", stmt.moduleName, importFile.getAbsolutePath());
            }
            
            Source source = null;
            try {
                source = new Source(importFile.getName(), new FileReader(importFile));
            }
            catch(Exception e) {
                throw new RuntimeException(e);
            }
                        
            Parser parser = new Parser(new Scanner(source));
            ModuleStmt program = parser.parseModule();
            
            this.modules.add(program);
        }

        @Override
        public void visit(VarFieldStmt stmt) {
            buf.out("%s", type(stmt.type));
        }

        @Override
        public void visit(StructFieldStmt stmt) {
            int anonId = this.anonStruct++;
            buf.out("%%.struct.anon.%d", anonId);
            this.anonScopes.peek().anonStructs.add(new StructAnonType(anonId, stmt));
        }

        @Override
        public void visit(UnionFieldStmt stmt) {
            buf.outln();
            buf.out("union %s {", stmt.decl.name);            
            for(FieldStmt f : stmt.decl.fields) {
                f.visit(this);
            }            
            buf.out("} %s;\n", stmt.decl.name);
            buf.outln();

        }
        
        private String type(TypeInfo type) {
            String typeName = "";
            switch(type.getKind()) {
                case f32: 
                    typeName = "float";
                    break;
                case f64:
                    typeName = "double";
                    break;
                case Ptr: { 
                    PtrTypeInfo ptrInfo = type.as();
                    typeName = type(ptrInfo.ptrOf.getResolvedType()) + "*";
                    break;
                }
                case bool: {
                    typeName = "i1";
                    break;
                }
                case Struct: {
                    StructTypeInfo structInfo = type.as();
                    typeName = "%struct." + structInfo.getResolvedType().getName();
                    break;
                }      
                case Identifier: {
                    IdentifierTypeInfo idInfo = type.as();
                    typeName = type(idInfo.getResolvedType());
                    break;
                }
                default:
                    typeName = type.getResolvedType().getName();
                    break;
            }
            
            return typeName;
        }
        
        private void decl(Decl d) {
            declAttributes(d);            
            buf.out("%s ", type(d.type));
        }

        private void declAttributes(Decl d) {
            Attributes attrs = d.attributes;
            if(attrs.isGlobal) {
                if(!attrs.isPublic) {
                    buf.out(" internal");
                }
                
                buf.out(" global ");
            }
            
        }
        
        @Override
        public void visit(ConstDecl d) {
            buf.out("@%s =", d.name);
            decl(d);
            d.expr.visit(this);
            buf.out("\n");
        }

        @Override
        public void visit(EnumDecl d) {
            buf.outln();
            buf.out("enum %s {", d.name);
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
            buf.outln();
            buf.out("define %s @%s(", type(d.returnType), d.name);
            boolean isFirst = true;
            for(ParameterDecl p : d.parameterDecls) {
                if(!isFirst) {
                    buf.out(",");
                }
                
                buf.out("%s %%%s", type(p.type), p.name);
                
                isFirst = false;
            }
            buf.out(") #0 "); // TODO determine which function attribute to use
            
            
            
            buf.out("{");
            
            this.scope.enter();
            Identifiers ids = this.scope.peekIdentifiers();
            ids.alloc(); // always start at %1 (%0 register is reserved for???)
//            
//            int paramIndex = d.parameterInfos.size();            
//            for(int i = 0; i < d.parameterInfos.size(); i++) {
//                ParameterInfo p = d.parameterInfos.get(i);
//                buf.out("%%%s = alloca %s \n", p.name, type(p.type));
//            }
//            
//            for(int i = d.parameterInfos.size() - 1; i >=0; i--) {
//                ParameterInfo p = d.parameterInfos.get(i);                
//                buf.out("store %s %s, %s* %s \n", type(p.type), ids.get(i), type(p.type), ids.get(paramIndex + (paramIndex - i)));
//            }
            
            d.bodyStmt.visit(this);
            
            this.scope.leave();
            buf.out("}");
            buf.outln();
        }

        private void beginAnon() {
            this.anonScopes.add(new AnonScope());
        }
        
        private void endAnon() {
            AnonScope scope = this.anonScopes.poll();
            while(!scope.anonStructs.isEmpty()) {
                StructAnonType struct = scope.anonStructs.poll();
                buf.outln();
                
                buf.out("%%.struct.anon.%d = type {", struct.anonId);
                
                beginAnon();
                
                boolean isFirst = true;
                for(FieldStmt f : struct.stmt.decl.fields) {
                    if(!isFirst) buf.out(", ");
                    f.visit(this);
                    
                    isFirst = false;
                }
                buf.out("}\n");
                buf.outln();
                
                endAnon();
            }
            
            
            while(!scope.anonUnions.isEmpty()) {
                
            }
        }

        @Override
        public void visit(StructDecl d) {
            buf.outln();
            
            buf.out("%%.struct.%s = type {", d.name);
            
            beginAnon();
            
            boolean isFirst = true;
            for(FieldStmt f : d.fields) {
                if(!isFirst) buf.out(", ");
                f.visit(this);
                
                isFirst = false;
            }
            buf.out("}\n", d.name);
            buf.outln();
            
            endAnon();
        }

        @Override
        public void visit(UnionDecl d) {
            buf.outln();
            buf.out("union %s {", d.name);
            for(FieldStmt f : d.fields) {
                f.visit(this);
            }
            buf.out("} %s;\n", d.name);
            buf.outln();
            
        }

        @Override
        public void visit(TypedefDecl d) {
            // TODO Auto-generated method stub
        }



        @Override
        public void visit(VarDecl d) {
            buf.out("%s %s", type(d.type), d.name);
            if(d.expr != null) {
                buf.out(" = ");
                d.expr.visit(this);
            }
            buf.out(";\n");
        }
        
        @Override
        public void visit(ParameterDecl d) {
        }

        
        @Override
        public void visit(IfStmt stmt) {
            stmt.condExpr.visit(this);
            
            Identifiers ids = this.scope.peekIdentifiers();
            Labels labels = this.scope.peekLabels();
            
            String thenLbl = labels.label("then");                        
            String elseLbl = labels.label("else");
            String endLbl  = labels.label("end");
                        
            if(stmt.elseStmt == null) {
                elseLbl = endLbl;
            }
            
            buf.out("br i1 %s, label %%%s, label %%%s \n", ids.peek(), thenLbl, elseLbl);
            
            buf.out("%s: \n", thenLbl);
            stmt.thenStmt.visit(this);            
            buf.out("br label %%%s \n", endLbl);
            
            if(stmt.elseStmt != null) {
                buf.out("%s: \n", elseLbl);                
                stmt.elseStmt.visit(this);                
                buf.out("br label %%%s \n", endLbl);
            }
            
            buf.out("%s: \n", endLbl);         
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

        @Override
        public void visit(BreakStmt stmt) {
            buf.out("break;\n");
        }

        @Override
        public void visit(ContinueStmt stmt) {
            buf.out("continue;\n");
        }


        @Override
        public void visit(ReturnStmt stmt) {
            if(stmt.returnExpr != null) {
                stmt.returnExpr.visit(this);
                String retId = this.scope.peekIdentifiers().dealloc(); 
                buf.out("ret %s %s", type(stmt.returnExpr.getResolvedType()), retId);                
            }
            else {
                buf.out("ret void");
            }
            buf.out("\n");
        }

        @Override
        public void visit(BlockStmt stmt) {
            for(Stmt s : stmt.stmts) {
                s.visit(this);
                if(s instanceof Expr) {
                    buf.out("\n");
                }
            }
        }

        @Override
        public void visit(DeferStmt stmt) {
            // TODO
        }
  
        
        @Override
        public void visit(InitExpr expr) {
            buf.out("%s {", expr.type.getName());
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
            switch(expr.type.getKind()) {
                case f32:
                    buf.out(f32Format.format((Double)expr.number.getValue()));
                    break;
                case f64:
                    buf.out(f64Format.format((Double)expr.number.getValue()));
                    break;
                default:
                    buf.out(expr.number.getText());
            }
        }


        @Override
        public void visit(StringExpr expr) {
            buf.appendRaw("\"").appendRaw(expr.string).appendRaw("\"");
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
            String idName = expr.variable.replace("::", "__");
            if(expr.declType.attributes.isGlobal) {
                buf.out("@%s", idName);    
            }
            else {
                buf.out("%%%s", idName);
            }
        }

        @Override
        public void visit(FuncIdentifierExpr expr) {            
            buf.out("%s", expr.variable.replace("::", "__"));
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
            if(expr.field.isKind(TypeKind.Ptr)) {
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

        private void visitComparison(BinaryExpr expr, TypeInfo resultInfo) {
            
            String cond = null;
            switch(expr.operator) {
                case EQUALS_EQUALS:
                    cond = "eq";
                    break;
                case GREATER_EQUALS:
                    cond = "ge";
                    break;
                case GREATER_THAN: {
                    cond = "gt";
                    break;
                }
                case LESS_EQUALS: {
                    cond = "le";
                    break;
                }
                case LESS_THAN: {
                    cond = "lt";
                    break;
                }
                case NOT_EQUALS: {
                    cond = "ne";
                    break;
                }
                default: 
                    cond = null;
            }
            
            String op = "icmp";
            String sign = "s";
            switch(resultInfo.getKind()) {
                case f32:
                case f64:
                    op = "fcmp";
                    sign = "o";
                    break;
                case i128:
                case i64:
                case i32:
                case i16:
                case i8:
                    if(cond != "eq" || cond != "ne") {
                        sign = "s";
                    }
                    break;
                case u128:
                case u64:
                case u32:
                case u16:
                case u8:
                    if(cond != "eq" || cond != "ne") {
                        sign = "u";
                    }
                    break; 
                default:
            }
            
            if(cond != null) {
                buf.out("%s %s %s ", op, sign + cond, type(resultInfo));
            }
            
        }
        

        @Override
        public void visit(BinaryExpr expr) {                        
            TypeInfo resultInfo = expr.getResolvedType();
                        
            if(expr.operator == TokenType.EQUALS) {
//                String id = this.scope.peekIdentifiers().alloc();
//                String type = type(expr.left.getResolvedType());
//                buf.out("%s = alloca %s \n", id, type);
//                
//                buf.out("store %s ", type(expr.right.getResolvedType()));
//                expr.right.visit(this);
//                buf.out(", %s* %s", type, id);
//                buf.out("\n");
//                expr.left.visit(this);
//                buf.out(" = load %s, %s* %s\n", type, type, id);
                
                //String id = this.scope.peekIdentifiers().alloc();
                String type = type(expr.left.getResolvedType());
                //buf.out("%s = alloca %s \n", id, type);
                
                buf.out("store %s ", type(expr.right.getResolvedType()));
                expr.right.visit(this);
                buf.out(", %s* ", type);
                expr.left.visit(this);
                buf.out("\n");
                //buf.out(" = load %s, %s* %s\n", type, type, id);
                
                return; /// RETURN
            }
            
            String id = this.scope.peekIdentifiers().alloc();
            buf.out("%s = ", id);
            
            visitComparison(expr, resultInfo);
            
            switch(expr.operator) {    
                case EQUALS: {
                    
                }
                case LSHIFT: {
                    buf.out("shl %s ", type(resultInfo));
                    break;
                }
                case RSHIFT: {
                    buf.out("lshr %s ", type(resultInfo));
                    break;
                }
                case BAND: {
                    buf.out("and %s ", type(resultInfo));
                    break;
                }
                case BOR: {
                    buf.out("or %s ", type(resultInfo));
                    break;
                }
                case XOR: {
                    buf.out("xor %s ", type(resultInfo));
                    break;
                }
                case PLUS: {
                    switch(resultInfo.getKind()) {
                        case f32:
                        case f64: 
                            buf.out("fadd %s ", type(resultInfo));
                            break;
                        default: {
                            buf.out("add %s ", type(resultInfo));
                            break;
                        }
                    }
                    break;
                }
                case MINUS: {
                    switch(resultInfo.getKind()) {
                        case f32:
                        case f64: 
                            buf.out("fsub %s ", type(resultInfo));
                            break;
                        default: {
                            buf.out("sub %s ", type(resultInfo));
                            break;
                        }
                    }
                    break;
                }
                case STAR: {
                    switch(resultInfo.getKind()) {
                        case f32:
                        case f64: 
                            buf.out("fmul %s ", type(resultInfo));
                            break;
                        default: {
                            buf.out("mul %s ", type(resultInfo));
                            break;
                        }
                    }
                    break;
                }
                case SLASH: {
                    switch(resultInfo.getKind()) {
                        case f32:
                        case f64: 
                            buf.out("fdiv %s ", type(resultInfo));
                            break;
                        case i128:
                        case i64:
                        case i32:
                        case i16:
                        case i8:
                            buf.out("sdiv %s ", type(resultInfo));
                            break;
                        case u128:
                        case u64:
                        case u32:
                        case u16:
                        case u8:
                            buf.out("udiv %s ", type(resultInfo));
                            break;
                        default: {
                            error(expr, "invalid divide binary expression");
                            break;
                        }
                    }
                    break;
                }
                case MOD: {
                    switch(resultInfo.getKind()) {
                        case f32:
                        case f64: 
                            buf.out("frem %s ", type(resultInfo));
                            break;
                        case i128:
                        case i64:
                        case i32:
                        case i16:
                        case i8:
                            buf.out("srem %s ", type(resultInfo));
                            break;
                        case u128:
                        case u64:
                        case u32:
                        case u16:
                        case u8:
                            buf.out("urem %s ", type(resultInfo));
                            break;
                        default: {
                            error(expr, "invalid modulas binary expression");
                            break;
                        }
                    }
                    break;
                }
            }
            
            expr.left.visit(this);
            buf.out(", ");
            expr.right.visit(this);
            buf.out("\n");
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
}
