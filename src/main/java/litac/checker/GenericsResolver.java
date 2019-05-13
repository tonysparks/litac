/*
 * see license.txt
 */
package litac.checker;

import java.util.*;

import litac.ast.*;
import litac.ast.Decl.*;
import litac.ast.Expr.*;
import litac.ast.NodeVisitor.AbstractNodeVisitor;
import litac.ast.Stmt.*;
import litac.checker.TypeInfo.*;
import litac.compiler.CompilationUnit;
import litac.util.Stack;
import litac.util.Tuple;

/**
 * @author Tony
 *
 */
public class GenericsResolver {

    private CompilationUnit unit;
    
    /**
     * 
     */
    public GenericsResolver(CompilationUnit unit) {
        this.unit = unit;
    }
    
    public void applyGenerics(TypeResolver resolver, Module main) {
        GenericsNodeVisitor generics = new GenericsNodeVisitor(resolver, main, unit);
        this.unit.getMain().visit(generics);
    }

    public static TypeInfo createGenericTypeInfo(TypeInfo type, List<TypeInfo> genericArgs) {
        if(genericArgs.isEmpty()) {
            return type;
        }
        
        switch(type.getKind()) {
            case Struct: {
                StructTypeInfo structInfo = type.as();
                if(!structInfo.hasGenerics()) {
                    return type;
                }
                
                return createStructTypeInfo(type.as(), genericArgs);
            }
            case Func: {
                FuncTypeInfo funcInfo = type.as();
                if(!funcInfo.hasGenerics()) {
                    return type;
                }
                
                throw new RuntimeException("Not implemented");
            }
            case Union: {
                throw new RuntimeException("Not implemented");
            }
            default:
                return type;
        }        
    }
    
    private static TypeInfo createStructTypeInfo(StructTypeInfo structInfo, List<TypeInfo> genericArgs) {
        List<FieldInfo> newFieldInfos = new ArrayList<>(structInfo.fieldInfos.size());
        for(FieldInfo field : structInfo.fieldInfos) {
            for(int i = 0; i < structInfo.genericParams.size(); i++) {
                GenericParam p = structInfo.genericParams.get(i);
                if(p.name.equals(field.type.getName())) {
                    TypeInfo argInfo = createGenericTypeInfo(genericArgs.get(i), genericArgs);
                    field = new FieldInfo(argInfo, field.name, p.name);
                    break;
                }
            }
            
            newFieldInfos.add(field);
        }
        
        
        String newStructName = newDeclGenericName(structInfo, genericArgs);
        StructTypeInfo newStructInfo = new StructTypeInfo(newStructName,
                                                          structInfo.genericParams, // keep the parameters, so we can compare against them
                                                          newFieldInfos, 
                                                          structInfo.isAnonymous);
        
        newStructInfo.sym = structInfo.sym;
        return newStructInfo;
    }
    
    private static TypeInfo createFuncTypeInfo(FuncTypeInfo funcInfo, List<TypeInfo> genericArgs) {
        List<ParameterDecl> newFuncParams = new ArrayList<>();
                   
        
        
        for(ParameterDecl paramDecl : funcInfo.parameterDecls) {
            for(int i = 0; i < funcInfo.genericParams.size(); i++) {
                GenericParam p = funcInfo.genericParams.get(i);
                if(p.name.equals(paramDecl.type.getName())) {
                    // TODO: verify correct number of generic args/params
                    TypeInfo argInfo = genericArgs.get(i);
                    paramDecl = new ParameterDecl(argInfo, paramDecl.name);
                    newFuncParams.add(paramDecl);
                    break;
                }
            }    
            
            newFuncParams.add(paramDecl);
        }
        
        TypeInfo newReturnType = funcInfo.returnType;
        for(int i = 0; i < funcInfo.genericParams.size(); i++) {
            GenericParam p = funcInfo.genericParams.get(i);
            if(p.name.equals(funcInfo.returnType.getName())) {
                // TODO: verify correct number of generic args/params
                TypeInfo argInfo = genericArgs.get(i);
                newReturnType = argInfo;
                break;
            }
        }
         
        String newFuncName = newDeclGenericName(funcInfo, genericArgs);
        FuncTypeInfo newFuncInfo = new FuncTypeInfo(newFuncName, 
                                                    newReturnType, 
                                                    newFuncParams, 
                                                    funcInfo.isVararg, 
                                                    funcInfo.genericParams);
        newFuncInfo.sym = funcInfo.sym;
        return newFuncInfo;
    }
    
    private static String newDeclGenericName(GenericTypeInfo type, List<TypeInfo> genericArgs) {
        StringBuilder newName = new StringBuilder(type.getName());
        
        for(int i = 0; i < genericArgs.size(); i++) {
            TypeInfo argInfo = genericArgs.get(i);
            newName.append("").append(argInfo.getName().replace("::", ""));
        }
        
        return newName.toString();
    }
    
    public class GenericsNodeVisitor extends AbstractNodeVisitor {

        class GenericDecl {
            ModuleStmt module;
            Decl originalDecl;
            
            public GenericDecl(ModuleStmt module,
                               Decl originalDecl) {
                this.module = module;
                this.originalDecl = originalDecl;
            }
        }
        
       // private NameCache names;
        private CompilationUnit unit;
        private Set<String> visitedModules;
        
        private Map<String, Map<String,GenericDecl>> genericDecls;
        private Stack<ModuleStmt> modules;
        
        private List<Decl> newDeclarations;
        private TypeResolver resolver;
        private Module module;
        
        public GenericsNodeVisitor(TypeResolver resolver, Module module, CompilationUnit unit/*, NameCache names*/) {
            this.resolver = resolver;
            this.unit = unit;
            this.module = module;
            
            //this.names = names;
            this.visitedModules = new HashSet<>();
            this.genericDecls = new HashMap<>();
            this.genericDecls.put("", new HashMap<>());
            
            this.modules = new Stack<>();
            this.newDeclarations = new ArrayList<>();
        }
            
        @Override
        public void visit(ModuleStmt stmt) {
            if(this.visitedModules.contains(stmt.name)) {
                return;
            }
            
            this.visitedModules.add(stmt.name);
            this.modules.add(stmt);
            
            for(ImportStmt i : stmt.imports) {
                i.visit(this);
            }
                    
            for(Decl d : stmt.declarations) {
                d.visit(this);
            }        
            
            stmt.declarations.addAll(0, this.newDeclarations);
            this.newDeclarations.clear();
            
            this.modules.pop();
        }
        
        @Override
        public void visit(ImportStmt stmt) {
            ModuleStmt module = this.unit.getModule(stmt.moduleName);
            module.visit(this);
        }

        @Override
        public void visit(VarDecl d) {
            if(d.expr != null) {
                d.expr.visit(this);
            }
        }
        
        @Override
        public void visit(FuncDecl d) {        
            FuncTypeInfo funcInfo = d.type.as();
            
            if(!funcInfo.genericParams.isEmpty()) {
                this.genericDecls.get("").put(d.name, new GenericDecl(this.modules.peek(), d));
            }
            
            if(d.bodyStmt != null) {
                d.bodyStmt.visit(this);
            }
        }
        
        @Override
        public void visit(BlockStmt stmt) {
            for(Stmt s : stmt.stmts) {
                s.visit(this);
            }
        }
        

        
        private void resolveParent(Expr expr, TypeInfo type) {
            Node parent = expr.getParentNode();
            if(parent instanceof Decl) {
                Decl decl = (Decl)parent;
                decl.type = type;
            }
            else if(parent instanceof Expr) {
                Expr e = (Expr)parent;
                e.resolveTo(type);
            }
        }
        
        @Override
        public void visit(InitExpr expr) {        
            StructTypeInfo structInfo = expr.type.getResolvedType().as();
            if(/*!structInfo.hasGenerics()*/expr.genericArgs.isEmpty()) {
                return;
            }
            
            
            List<Tuple<String, TypeInfo>> replacements = new ArrayList<>();
            
            List<FieldInfo> newFieldInfos = new ArrayList<>(structInfo.fieldInfos.size());
            for(FieldInfo field : structInfo.fieldInfos) {
                if(field.genericArg != null) {
                    for(int i = 0; i < structInfo.genericParams.size(); i++) {
                        GenericParam p = structInfo.genericParams.get(i);
    //                    if(p.typeName.equals(field.type.getName())) {
    //                        TypeInfo argInfo = expr.genericArgs.get(i);
    //                        replacements.add(new Tuple<>(p.typeName, argInfo));
    //                        field = new FieldInfo(argInfo, field.name, p.typeName);
    //                        break;
    //                    }
                        if(p.name.equals(field.genericArg)) {
                            TypeInfo argInfo = expr.genericArgs.get(i);
                            replacements.add(new Tuple<>(p.name, argInfo));
                            field = new FieldInfo(argInfo, field.name, p.name);
                            break;
                        }
                    }
                }
                
                newFieldInfos.add(field);
            }
//            
//            String newStructName = newDeclGenericName(structInfo, expr.genericArgs);
            String newStructName = structInfo.name;
            StructTypeInfo newStructInfo = new StructTypeInfo(structInfo.name, 
                                                              Collections.emptyList(), 
                                                              newFieldInfos, 
                                                              structInfo.isAnonymous);
            
//            StructTypeInfo newStructInfo = structInfo;
//            String newStructName = structInfo.name;
            
            expr.resolveTo(newStructInfo);
            resolveParent(expr, newStructInfo);
            
            // TODO : copy over structure
            List<FieldStmt> newFieldStmts = new ArrayList<>();
            StructDecl decl = structInfo.sym.decl.copy();
            //StructDecl decl = new StructDecl(newStructName, newStructInfo, newFieldStmts);
            decl.name = newStructName;
            decl.type = newStructInfo;
            
            TypeReplacerNodeVisitor replacer = new TypeReplacerNodeVisitor(replacements);
            decl.visit(replacer);
            
            this.resolver.resolveStmt(this.module, decl);
            
            boolean isAlreadyDefined = (this.module.getType(newStructName) != null);
            Symbol sym = null;
            if(!isAlreadyDefined) {
                sym = this.module.declareStruct(decl, newStructName, newStructInfo);
                this.newDeclarations.add(decl);
            }
            else {
                sym = this.module.getType(newStructName).sym;                
            }
            
            decl.sym = sym;
        }
        
        
        
        @Override
        public void visit(FuncCallExpr expr) {    
            FuncTypeInfo funcInfo = expr.object.getResolvedType().as();
//            if(/*!funcInfo.hasGenerics()*/expr.genericArgs.isEmpty()) {
//                return;
//            }
            if(!funcInfo.hasGenerics()) {
                return;
            }
            
            List<ParameterDecl> newFuncParams = new ArrayList<>();
            List<Tuple<String, TypeInfo>> replacements = new ArrayList<>();
                       
            String newFuncName = newDeclGenericName(funcInfo, expr.genericArgs);
            boolean isAlreadyDefined = (this.module.getFuncType(newFuncName) != null);
            
            for(ParameterDecl paramDecl : funcInfo.parameterDecls) {
                boolean isGenericParam = false;
                for(int i = 0; i < funcInfo.genericParams.size(); i++) {
                    GenericParam p = funcInfo.genericParams.get(i);
                    if(p.name.equals(paramDecl.type.getName())) {
                        // TODO: verify correct number of generic args/params
                        TypeInfo argInfo = expr.genericArgs.get(i);
                        replacements.add(new Tuple<>(p.name, argInfo));
                        newFuncParams.add(new ParameterDecl(argInfo, paramDecl.name));
                        isGenericParam = true;
                        break;
                    }
                }    
                
                if(!isGenericParam) {
                    newFuncParams.add(paramDecl);
                }
            }
            
            TypeInfo newReturnType = funcInfo.returnType;
            for(int i = 0; i < funcInfo.genericParams.size(); i++) {
                GenericParam p = funcInfo.genericParams.get(i);
                if(p.name.equals(funcInfo.returnType.getName())) {
                    // TODO: verify correct number of generic args/params
                    TypeInfo argInfo = expr.genericArgs.get(i);
                    replacements.add(new Tuple<>(p.name, argInfo));
                    newReturnType = argInfo;
                    break;
                }
            }
                        
            FuncTypeInfo newFuncInfo = new FuncTypeInfo(newFuncName, 
                                                        newReturnType, 
                                                        newFuncParams, 
                                                        funcInfo.isVararg, 
                                                        Collections.emptyList());
            
            expr.object.resolveTo(newFuncInfo);
            resolveParent(expr, newReturnType);
            
            // TODO: Get correct module
            GenericDecl genDecl = this.genericDecls.get("").get(funcInfo.name);
            TypeReplacerNodeVisitor replacer = new TypeReplacerNodeVisitor(replacements);
            FuncDecl decl = genDecl.originalDecl.copy();
            decl.name = newFuncName;
            decl.type = newFuncInfo;
            decl.returnType = newFuncInfo.returnType;
            decl.params.params = newFuncParams;
            
            decl.visit(replacer);
            
            this.resolver.resolveStmt(this.module, decl);
            
            Symbol sym = null;
            if(!isAlreadyDefined) {
                // funcInfo.sym.declared
                sym = this.module.declareFunc(decl, newFuncName, newFuncInfo);
                this.newDeclarations.add(decl);
            }
            else {
                sym = this.module.getFuncType(newFuncName).sym;
            }
            
            if(expr.object instanceof FuncIdentifierExpr) {
                FuncIdentifierExpr idExpr = (FuncIdentifierExpr)expr.object;
                idExpr.variable = newFuncName;
                idExpr.sym = sym;
            }
        }                
        
    }
}
