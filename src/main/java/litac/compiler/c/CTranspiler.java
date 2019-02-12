/*
 * see license.txt
 */
package litac.compiler.c;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import litac.ast.Decl.*;
import litac.ast.Decl;
import litac.ast.Expr;
import litac.ast.Expr.*;
import litac.ast.Stmt.*;
import litac.ast.NodeVisitor;
import litac.ast.Stmt;
import litac.Errors;
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
import litac.util.OS.OsType;

/**
 * @author Tony
 *
 */
public class CTranspiler {

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

    public static void transpile(ModuleStmt program, TranspilerOptions options) {
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
        
        writeForwardDeclarations(checkerResult.getModule(), buf);
        
        CWriterNodeVisitor visitor = new CWriterNodeVisitor(options, program, buf);
        program.visit(visitor);
        
        System.out.println(buf);
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
        
        buf.out("// Start Module '%s' \n", module.name);
        
        for(TypeInfo type : module.getDeclaredTypes()) {
            writeForwardDecl(module, buf, type);
        }
        
        for(TypeInfo type : module.getDeclaredFuncs()) {
            writeForwardDecl(module, buf, type);
        }
        
        buf.out("// End Module '%s' \n\n", module.name);
    }
    
    private static void writeForwardDeclarations(Module module, Buf buf) {
        // TODO, figure out name scoping for modules
        // options:
        // 1) always scope names as:
        //     moduleName__typeName
        
        writeModule(module, buf, new HashSet<>());            
    }
    
    private static class CWriterNodeVisitor implements NodeVisitor {

        private TranspilerOptions options;
        
        private List<ModuleStmt> modules;
        private Buf buf;
        
        public CWriterNodeVisitor(TranspilerOptions options, ModuleStmt program, Buf buf) {
            this.options = options;
            this.buf = buf;
            
            this.modules = new ArrayList<>();
            this.modules.add(program);            
        }
        
        @Override
        public void visit(ModuleStmt stmt) {
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
            buf.out("%s %s;\n", stmt.type, stmt.name);
        }

        @Override
        public void visit(ParameterDecl d) {
        }
        
        @Override
        public void visit(StructFieldStmt stmt) {
            buf.outln();
            buf.out("struct %s {", stmt.decl.name);
            for(FieldStmt f : stmt.decl.fields) {
                f.visit(this);
            }
            buf.out("} %s;\n", stmt.decl.name);
            buf.outln();
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

        @Override
        public void visit(ConstDecl d) {
            buf.out("#define %s ", d.name);
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
            buf.out("%s %s(", d.returnType, d.name);
            boolean isFirst = true;
            for(ParameterDecl p : d.parameterDecls) {
                if(!isFirst) {
                    buf.out(",");
                }
                
                buf.out("%s %s", p.type, p.name);
                
                isFirst = false;
            }
            buf.out(") ");
            boolean isBlock = (d.bodyStmt instanceof BlockStmt);
            if(!isBlock) buf.out("{");
            d.bodyStmt.visit(this);
            if(!isBlock) buf.out("}");
            buf.outln();
        }


        @Override
        public void visit(StructDecl d) {
            buf.outln();
            buf.out("struct %s {", d.name);
            for(FieldStmt f : d.fields) {
                f.visit(this);
            }
            buf.out("} %s;\n", d.name);
            buf.outln();
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
            buf.out("%s %s", d.type, d.name);
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
            for(Stmt s : stmt.stmts) {
                s.visit(this);
                if(s instanceof Expr) {
                    buf.out(";\n");
                }
            }
            buf.out("}");
        }

        @Override
        public void visit(DeferStmt stmt) {
            // TODO
        }
  
        
        @Override
        public void visit(InitExpr expr) {
            buf.out("%s {", expr.type.name);
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
            buf.out("%s", expr.variable.replace("::", "__"));
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
}
