/*
 * see license.txt
 */
package litac.checker;


import java.util.ArrayList;
import java.util.List;

import litac.ast.NodeVisitor.AbstractNodeVisitor;
import litac.ast.Decl;
import litac.ast.Expr;
import litac.ast.Stmt;
import litac.ast.Decl.*;
import litac.ast.Expr.*;
import litac.ast.Node;
import litac.ast.Stmt.*;
import litac.checker.TypeInfo.*;

/**
 * Responsible to ensuring that the expected defined {@link TypeInfo}s are used, i.e., type checking of the program. 
 * 
 * @author Tony
 *
 */
public class TypeChecker {
    
    private PhaseResult result;
    
    public TypeChecker(PhaseResult result) {
        this.result = result;
    }

    /**
     * Runs a type check on the supplied Module
     * 
     * @param module
     */
    public void typeCheck(Module module) {
        for(Module m : module.getImports()) {            
            typeCheckModule(m);
        }
        
        typeCheckModule(module);                
    }
    
    
    private void typeCheckModule(Module module) {
        TypeCheckerNodeVisitor checker = new TypeCheckerNodeVisitor(this.result);
        module.getModuleStmt().visit(checker);
        
        checker.checkTypes();
    }
    
       
    private class TypeCheckerNodeVisitor extends AbstractNodeVisitor {

        class TypeCheck {
            public Stmt stmt;
            public TypeInfo type;
            public TypeInfo otherType;
            public boolean isCasted;
            
            public TypeCheck(Stmt stmt, 
                             TypeInfo type,
                             TypeInfo otherType,
                             boolean isCasted) {
                this.stmt = stmt;
                this.type = type;            
                this.otherType = otherType;
                this.isCasted = isCasted;
            }
        }
        
        private PhaseResult result;
        private List<TypeCheck> pendingChecks;
        
        public TypeCheckerNodeVisitor(PhaseResult result) {
            this.result = result;
            this.pendingChecks = new ArrayList<>();     
        }
        
        private void addTypeCheck(Expr expr, TypeInfo type) {
            addTypeCheck(expr, type, null);
        }
        
        private void addTypeCheck(Stmt stmt, TypeInfo type, TypeInfo otherType) {
            addTypeCheck(stmt, type, otherType, false);
        }
        
        private void addTypeCheck(Stmt stmt, TypeInfo type, TypeInfo otherType, boolean isCasted) {
            this.pendingChecks.add(new TypeCheck(stmt, type, otherType, isCasted));
//            if(!result.hasErrors()) {
//                checkType(new TypeCheck(stmt, type, otherType, isCasted));
//            }
        }
        
        private void checkType(Stmt stmt, TypeInfo a, TypeInfo b, boolean isCasted) {
            if(isCasted) {
                if(!a.isKind(TypeKind.Ptr) || !b.isKind(TypeKind.Ptr)) {
                    if(!a.canCastTo(b) && !b.canCastTo(a)) {
                        result.addError(stmt,
                                "'%s' can't be casted to '%s'", b, a);    
                    }
                }
            }
            else if(!a.canCastTo(b)) {
                result.addError(stmt,
                        "'%s' is not of type '%s'", b, a);
            }
        }
        
        private void checkType(TypeCheck check) {
            if(check.type == null) {
                result.addError(check.stmt,
                        "unresolved type expression");
                return;
            }
            
            if(check.otherType != null) {
                if(!check.type.isResolved()) {
                    result.addError(check.stmt,
                            "unresolved type expression", check.type);
                    return;
                }
                
                if(!check.otherType.isResolved()) {
                    result.addError(check.stmt,
                            "unresolved type expression", check.otherType);
                    return;
                }
                
                checkType(check.stmt, check.type.getResolvedType(), check.otherType.getResolvedType(), check.isCasted);                    
            }
            else {
                Expr expr = (Expr)check.stmt;
                
                if(!expr.isResolved()) {
                    result.addError(check.stmt,
                            "unresolved type expression", check.stmt);
                    return;
                }
                
                if(!check.type.isResolved()) {
                    result.addError(check.stmt,
                            "unresolved type expression", check.stmt);
                    return;
                }
                
                checkType(check.stmt, check.type.getResolvedType(), expr.getResolvedType().getResolvedType(), check.isCasted);                                        
            }
        }
        
        /**
         * Does type checks for the full module, type checks are delayed to the end of walking the AST tree as certain
         * types may not have been resolved at time of processing the AST node.  
         */
        private void checkTypes() {     
            if(result.hasErrors()) {
                return;
            }
            
            for(TypeCheck check : this.pendingChecks) {
                checkType(check);
            }
            
            this.pendingChecks.clear();
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
            // TODO
        }

        @Override
        public void visit(IfStmt stmt) {
            stmt.condExpr.visit(this);
            stmt.thenStmt.visit(this);
            
            if(stmt.elseStmt != null) {
                stmt.elseStmt.visit(this);
            }
        }

        @Override
        public void visit(WhileStmt stmt) {
            stmt.condExpr.visit(this);
            stmt.bodyStmt.visit(this);
        }


        @Override
        public void visit(DoWhileStmt stmt) {
            stmt.bodyStmt.visit(this);
            stmt.condExpr.visit(this);
        }

 
        @Override
        public void visit(ForStmt stmt) {
            stmt.initStmt.visit(this);
            stmt.condExpr.visit(this);
            stmt.postStmt.visit(this);
            stmt.bodyStmt.visit(this);
        }

        @Override
        public void visit(BreakStmt stmt) {
        }

        @Override
        public void visit(ContinueStmt stmt) {
        }


        @Override
        public void visit(ReturnStmt stmt) {
            FuncDecl funcDecl = null;
            
            Node parent = stmt.getParentNode();
            while(parent != null) {
                if(parent instanceof FuncDecl) {
                    funcDecl = (FuncDecl)parent;
                    break;
                }
                
                parent = parent.getParentNode();
            }
            
            if(stmt.returnExpr != null) {
                stmt.returnExpr.visit(this);                                                
                addTypeCheck(stmt.returnExpr, funcDecl.returnType);
            }
            else {
                addTypeCheck(stmt, TypeInfo.VOID_TYPE, funcDecl.returnType);
            }
        }

        @Override
        public void visit(BlockStmt stmt) {
            for(Stmt s : stmt.stmts) {
                s.visit(this);
            }
        }
        
        @Override
        public void visit(DeferStmt stmt) {
            stmt.stmt.visit(this);            
        }
        
        @Override
        public void visit(EmptyStmt stmt) {
        }
        
        @Override
        public void visit(VarDecl d) {
            if(d.expr != null) {
                d.expr.visit(this);
            }
            
            if(d.expr != null) {
                addTypeCheck(d.expr, d.type);
            }
        }
        
  
        @Override
        public void visit(ConstDecl d) {
            d.expr.visit(this);
            
            addTypeCheck(d.expr, d.type);
        }

        @Override
        public void visit(EnumDecl d) {
            for(EnumFieldInfo f : d.fields) {
                if(f.value != null) {
                    f.value.visit(this);
                    addTypeCheck(f.value, TypeInfo.I32_TYPE);
                }
            }
        }

        @Override
        public void visit(FuncDecl d) {
            FuncTypeInfo funcInfo = d.type.as();
            if(!funcInfo.hasGenerics()) {                
                d.bodyStmt.visit(this);
            }
        }


        @Override
        public void visit(StructDecl d) {
            for(FieldStmt s : d.fields) {
                s.visit(this);
            }
        }
        
        @Override
        public void visit(VarFieldStmt stmt) {
        }

        @Override
        public void visit(UnionDecl d) {
            for(FieldStmt s : d.fields) {
                s.visit(this);
            }
        }
        
        @Override
        public void visit(TypedefDecl d) {
        }

        @Override
        public void visit(CastExpr expr) {
            expr.expr.visit(this);
            
            addTypeCheck(expr, expr.expr.getResolvedType(), expr.castTo, true);
        }
        
        @Override
        public void visit(SizeOfExpr expr) {
            // TODO: Verify it's a type??
            //expr.expr.visit(this);
        }
        
        @Override
        public void visit(InitArgExpr expr) {
            expr.value.visit(this);
        }
                
        private void checkAggregateInitFields(InitExpr expr, TypeInfo aggInfo, List<FieldInfo> fieldInfos, List<InitArgExpr> arguments) {
            if(fieldInfos.size() < arguments.size()) {
                // TODO should this be allowed??
                this.result.addError(expr, "incorrect number of arguments");
            }
            
            if(aggInfo.isKind(TypeKind.Union) && arguments.size() > 1) {
                this.result.addError(expr, "incorrect number of arguments for union");
            }
            
            
            // Validate Named fields
            for(int index = 0; index < arguments.size(); index++) {
                InitArgExpr arg = arguments.get(index);
                if(arg.fieldName == null) {                    
                    if(arg.argPosition >= fieldInfos.size()) {
                        this.result.addError(arg, "No field defined at position '%d' for '%s'", arg.argPosition, aggInfo.getName());
                    }
                    else {
                        FieldInfo fieldInfo = fieldInfos.get(arg.argPosition);
                        addTypeCheck(arg, fieldInfo.type);
                    }
                }
                else {
                    boolean matchedField = false;
                    for(int i = 0; i < fieldInfos.size(); i++) {
                        FieldInfo fieldInfo = fieldInfos.get(i);
                        
                        if(fieldInfo.name.equals(arg.fieldName)) {
                            addTypeCheck(arg, fieldInfo.type);
                                       
                            matchedField = true;
                            break;
                        }
                    }
                    
                    if(!matchedField) {
                        this.result.addError(arg, "'%s' is not defined in '%s'", arg.fieldName, aggInfo.getName());
                    }
                }
            }
        }
        
        @Override
        public void visit(InitExpr expr) {
            for(InitArgExpr e : expr.arguments) {
                e.visit(this);
            }
            
            TypeInfo type = expr.getResolvedType();                        
            switch(type.getKind()) {
                case Struct: {
                    StructTypeInfo structInfo = expr.type.as();
                    checkAggregateInitFields(expr, structInfo, structInfo.fieldInfos, expr.arguments);
                    
                    break;
                }
                case Union: {
                    UnionTypeInfo unionInfo = expr.type.as();
                    checkAggregateInitFields(expr, unionInfo, unionInfo.fieldInfos, expr.arguments);
                    
                    break;
                }
                default: {
                    this.result.addError(expr, "'%s' is an invalid type for initialization", type.getName());
                }
            }
            
        }

        @Override
        public void visit(FuncCallExpr expr) {
            expr.object.visit(this);            
            
            
            TypeInfo type = expr.object.getResolvedType();
            if(!type.isKind(TypeKind.Func)) {
                this.result.addError(expr, "'%s' is not a function", type.getName());
                return;
            }
            
            FuncTypeInfo funcInfo = type.as();
            if(funcInfo.parameterDecls.size() != expr.arguments.size()) {
                if(funcInfo.parameterDecls.size() > expr.arguments.size() || !funcInfo.isVararg) {                    
                    this.result.addError(expr, "'%s' called with incorrect number of arguments", type.getName());
                }
            }
            
            int i = 0;
            for(; i < funcInfo.parameterDecls.size(); i++) {
                TypeInfo paramInfo = funcInfo.parameterDecls.get(i).type;
                
                if(i < expr.arguments.size()) {
                    Expr arg = expr.arguments.get(i);
                    arg.visit(this);
                    
                    addTypeCheck(arg, arg.getResolvedType(), paramInfo);
                }
            }
            
            if(funcInfo.isVararg) {
                for(; i < expr.arguments.size(); i++) {
                    Expr arg = expr.arguments.get(i);
                    arg.visit(this);
                }                
            }
        }


        @Override
        public void visit(IdentifierExpr expr) {            
        }
        
        @Override
        public void visit(FuncIdentifierExpr expr) {            
        }
        
        private boolean typeCheckAggregate(TypeInfo type, TypeInfo field, Expr expr, Expr value) {            
            switch(type.getKind()) {
                case Ptr: {
                    PtrTypeInfo ptrInfo = type.as();
                    return typeCheckAggregate(ptrInfo.ptrOf, field, expr, value);                    
                }
                case Struct: {
                    StructTypeInfo structInfo = type.as();
                    for(FieldInfo fieldInfo : structInfo.fieldInfos) {  
                        if(fieldInfo.type.isAnonymous()) {
                            if(typeCheckAggregate(fieldInfo.type, field, expr, value)) {
                                return true;
                            }
                        }
                        else if(fieldInfo.name.equals(field.getName())) {                            
                            if(value != null) {
                                addTypeCheck(expr, value.getResolvedType(), fieldInfo.type);
                            }
                            
                            return true;
                        }
                    }
                    this.result.addError(expr, "'%s' does not have field '%s'", structInfo.name, field.name);
                    break;
                }                
                case Union: {
                    UnionTypeInfo unionInfo = type.as();
                    for(FieldInfo fieldInfo : unionInfo.fieldInfos) {
                        if(fieldInfo.type.isAnonymous()) {
                            if(typeCheckAggregate(fieldInfo.type, field, expr, value)) {
                                return true;
                            }
                        }
                        else if(fieldInfo.name.equals(field.getName())) {                            
                            if(value != null) {
                                addTypeCheck(expr, value.getResolvedType(), fieldInfo.type);
                            }
                            
                            return true;
                        }
                    }
                    this.result.addError(expr, "'%s' does not have field '%s'", unionInfo.name, field.name);
                    break;
                }
                case Enum: {
                    if(value != null) {
                        this.result.addError(expr, "'%s.%s' can not be reassigned", type.name, field.name);
                    }
                    else {
                        EnumTypeInfo enumInfo = type.as();
                        for(EnumFieldInfo fieldInfo : enumInfo.fields) {
                            if(fieldInfo.name.equals(field.getName())) {                                
                                return true;
                            }
                        }
                        this.result.addError(expr, "'%s' does not have field '%s'", enumInfo.name, field.name);
                    }
                    break;
                }
                default: {
                    this.result.addError(expr, "'%s' is an invalid type for aggregate access", type.getName());
                }
            }
            
            return false;
        }
        
        @Override
        public void visit(GetExpr expr) {
            expr.object.visit(this);            
            
            TypeInfo type = expr.object.getResolvedType();
            typeCheckAggregate(type, expr.field, expr, null);
        }
        
        @Override
        public void visit(SetExpr expr) {
            if(expr.object.getResolvedType().isKind(TypeKind.Enum)) {
                this.result.addError(expr, "can't reassign enum '%s'", expr.object.getResolvedType());
                return;
            }
            
            expr.object.visit(this);            
            expr.value.visit(this);
            
            TypeInfo type = expr.object.getResolvedType();
            typeCheckAggregate(type, expr.field, expr, expr.value);
        }

        @Override
        public void visit(UnaryExpr expr) {
            expr.expr.visit(this);
            
            switch(expr.operator) {
                case STAR: {
                    TypeInfo type = expr.expr.getResolvedType().getResolvedType();
                    if(!type.isKind(TypeKind.Ptr) && !type.isKind(TypeKind.Str)) {
                        this.result.addError(expr, "'%s' is not a pointer type", type);
                        return;
                    }
                    
                    break;
                }
                default:
            }
        }

        @Override
        public void visit(GroupExpr expr) {
            expr.expr.visit(this);
        }

        private void checkConstant(Expr expr) {
            if(expr instanceof IdentifierExpr) {
                IdentifierExpr idExpr = (IdentifierExpr)expr;
                if(idExpr.sym != null) {
                    if(idExpr.sym.isConstant()) {
                        this.result.addError(expr, "can't reassign constant variable '%s'", idExpr.variable);
                    }
                }
            }
        }
        
        @Override
        public void visit(BinaryExpr expr) {
            expr.left.visit(this);
            expr.right.visit(this);
            
            TypeInfo leftType = expr.left.getResolvedType().getResolvedType();
            TypeInfo rightType = expr.right.getResolvedType().getResolvedType();
            
            addTypeCheck(expr.left, rightType);
            addTypeCheck(expr.right, leftType);
            
            
            switch(expr.operator) {
                case EQUALS: {                    
                    checkConstant(expr.left);
                    break;
                }
            
                case BAND_EQ:
                case BNOT_EQ:
                case BOR_EQ:
                case XOR_EQ:
                case LSHIFT_EQ:
                case RSHIFT_EQ: 
                    checkConstant(expr.left);
                    // fallthru
                case BAND:
                case BNOT:
                case BOR:
                case XOR:
                case LSHIFT:
                case RSHIFT: {
                    if(!TypeInfo.isInteger(leftType)) {
                        this.result.addError(expr.left, "illegal, left operand has type '%s'", leftType.getResolvedType().getName());
                    }
                    
                    if(!TypeInfo.isInteger(rightType)) {
                        this.result.addError(expr.right, "illegal, right operand has type '%s'", rightType.getResolvedType().getName());
                    }
                    break;
                }
                    
                case AND:
                case OR:
                    break;
                
                case EQUALS_EQUALS:
                case NOT_EQUALS:
                    break;
                    
                case GREATER_EQUALS:
                case GREATER_THAN:
                case LESS_EQUALS:
                case LESS_THAN:
                    if(!TypeInfo.isNumber(leftType)) {
                        this.result.addError(expr.left, "illegal, left operand has type '%s'", leftType.getResolvedType().getName());
                    }
                    
                    if(!TypeInfo.isNumber(rightType)) {
                        this.result.addError(expr.right, "illegal, right operand has type '%s'", rightType.getResolvedType().getName());
                    }
                    
                    break;
                
                case MINUS_EQ:
                case PLUS_EQ:
                case MOD_EQ:
                case MUL_EQ:
                case DIV_EQ:
                    checkConstant(expr.left);
                    // fallthru
                case MINUS:
                case PLUS:
                case MOD:
                case STAR:
                case SLASH:
                    
                    if(!TypeInfo.isNumber(leftType) && !leftType.isKind(TypeKind.Ptr) && !leftType.isKind(TypeKind.Str)) {
                        this.result.addError(expr.left, "illegal, left operand has type '%s'", leftType.getResolvedType().getName());
                    }
                    
                    if(!TypeInfo.isNumber(rightType) && !rightType.isKind(TypeKind.Ptr) && !rightType.isKind(TypeKind.Str)) {
                        this.result.addError(expr.right, "illegal, right operand has type '%s'", rightType.getResolvedType().getName());
                    }
                    
                    break;
            default:
                break;
            
            }
        }


        @Override
        public void visit(ArrayInitExpr expr) {
            ArrayTypeInfo arrayInfo = expr.getResolvedType().as();
            
            for(Expr v : expr.values) {
                v.visit(this);
                
                addTypeCheck(v, v.getResolvedType(), arrayInfo.arrayOf.getResolvedType());
            }
            
        }
        
        @Override
        public void visit(SubscriptGetExpr expr) {
            expr.object.visit(this);
            expr.index.visit(this);
            
            TypeKind objectKind = expr.object.getResolvedType().getKind();
            switch(objectKind) {
                case Str:
                case Array:
                case Ptr:
                    break;
                default: {
                    this.result.addError(expr, "invalid index into '%s'", objectKind.name());
                    return;
                }
            }
            
            TypeKind indexKind = expr.index.getResolvedType().getKind();
            switch(indexKind) {
                case Char:
                case i8:
                case u8:
                case i16:
                case u16:
                case i32:
                case u32:
                case i64:
                case u64:
                case i128:
                case u128:
                    break;
                default: {
                    this.result.addError(expr, "'%s' invalid index value", indexKind.name());
                    return;
                }
            }
        }

        @Override
        public void visit(SubscriptSetExpr expr) {
            expr.object.visit(this);
            expr.index.visit(this);
            expr.value.visit(this);
            
            TypeInfo objectInfo = expr.object.getResolvedType();
            TypeKind objectKind = objectInfo.getKind();
            switch(objectKind) {
                case Str:
                case Array:
                case Ptr:
                    break;
                default: {
                    this.result.addError(expr, "invalid index into '%s'", objectKind.name());
                    return;
                }
            }
            
            TypeKind indexKind = expr.index.getResolvedType().getKind();
            switch(indexKind) {
                case i8:
                case u8:
                case i16:
                case u16:
                case i32:
                case u32:
                case i64:
                case u64:
                case i128:
                case u128:
                    break;
                default: {
                    this.result.addError(expr, "'%s' invalid index value", indexKind.name());
                    return;
                }
            }
            
            addTypeCheck(expr.object, expr.value.getResolvedType(), objectInfo);
        }
    }
    
}
