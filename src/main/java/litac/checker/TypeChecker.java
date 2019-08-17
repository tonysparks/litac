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
import litac.compiler.*;
import litac.util.Tuple;

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
        TypeCheckerNodeVisitor checker = new TypeCheckerNodeVisitor(module, this.result);
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
        
        @SuppressWarnings("unused")
        private Module module;
        private PhaseResult result;
        private List<TypeCheck> pendingChecks;
        private Map<String, Boolean> labels;
        
        public TypeCheckerNodeVisitor(Module module, PhaseResult result) {
            this.module = module;
            this.result = result;
            this.pendingChecks = new ArrayList<>();
            this.labels = new HashMap<>();
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
                if(!TypeInfo.isPtrLike(a) || !TypeInfo.isPtrLike(b)) {
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
                
                checkType(check.stmt, expr.getResolvedType().getResolvedType(), check.type.getResolvedType(), check.isCasted);                                        
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
            if(stmt.initStmt != null) {
                stmt.initStmt.visit(this);
            }
            
            if(stmt.condExpr != null) {
                stmt.condExpr.visit(this);
            }
            
            if(stmt.postStmt != null) {
                stmt.postStmt.visit(this);
            }
            
            stmt.bodyStmt.visit(this);
        }
        
        @Override
        public void visit(SwitchCaseStmt stmt) {
            stmt.cond.visit(this);
            stmt.stmt.visit(this);
        }
        
        @Override
        public void visit(SwitchStmt stmt) {
            stmt.cond.visit(this);
            
            for(SwitchCaseStmt s : stmt.stmts) {
                s.visit(this);
            }
            
            if(stmt.defaultStmt != null) {
                stmt.defaultStmt.visit(this);
            }
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
        public void visit(GotoStmt stmt) {
            this.labels.putIfAbsent(stmt.label, false);
        }
        
        @Override
        public void visit(LabelStmt stmt) {
            this.labels.put(stmt.label, true);
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
            if(!d.attributes.isForeign() && d.expr == null) {
                this.result.addError(d, "const declaration must have an assignment (unless it is '@foreign')");
                return;
            }
            
            if(d.expr != null) {
                d.expr.visit(this);
                
                addTypeCheck(d.expr, d.type);
            }
        }

        @Override
        public void visit(EnumDecl d) {
            Map<String, EnumFieldInfo> definedFields = new HashMap<>();
            for(EnumFieldInfo f : d.fields) {
                if(f.value != null) {
                    f.value.visit(this);
                    addTypeCheck(f.value, TypeInfo.I32_TYPE);
                }
                
                if(definedFields.containsKey(f.name)) {
                    this.result.addError(d, "duplicate member '%s'", f.name);
                }
                
                definedFields.put(f.name, f);
            }
        }

        @Override
        public void visit(FuncDecl d) {
            FuncTypeInfo funcInfo = d.type.as();
            if(!funcInfo.hasGenerics()) {
                this.labels.clear();
                d.bodyStmt.visit(this);
                
                this.labels.forEach((label, isDefined) -> {
                    if(!isDefined) {
                        result.addError(d.bodyStmt, "'%s' label not found", label);
                    }
                });
            }
            
            boolean hasDefault = false;
            for(ParameterDecl p : funcInfo.parameterDecls) {
                if(p.defaultValue != null) {
                    p.defaultValue.visit(this);
                    addTypeCheck(p.defaultValue, p.type);
                    hasDefault = true;
                }
                else if(hasDefault) {
                    this.result.addError(d, "'%s' must have default arguments defined last", d.name);
                }
            }
            
            if(funcInfo.isMethod()) {
                TypeInfo recv = funcInfo.getReceiverType().getResolvedType();
                if(TypeInfo.isFunc(recv) || recv.isKind(TypeKind.Void)) {
                    this.result.addError(d, "'%s' is an invalid receiver for method '%s'", recv.getName(), funcInfo.getName());
                }
            }
        }

        private void checkDuplicateFields(Stmt stmt, AggregateTypeInfo aggInfo, Map<String, Tuple<AggregateTypeInfo, FieldInfo>> definedFields) {
            for(FieldInfo field : aggInfo.fieldInfos) {
                if(definedFields.containsKey(field.name)) {
                    Tuple<AggregateTypeInfo, FieldInfo> tuple = definedFields.get(field.name);
                    this.result.addError(stmt, "duplicate member '%s' from '%s' and '%s'", 
                            field.name, aggInfo.name, tuple.getFirst().name);
                }
                definedFields.put(field.name, new Tuple<>(aggInfo, field));
                
                if(field.attributes.isUsing()) {
                    if(field.type.isKind(TypeKind.Struct) || field.type.isKind(TypeKind.Union)) {
                        checkDuplicateFields(stmt, field.type.as(), definedFields);
                    }                
                }
            }
        }
        
        @Override
        public void visit(StructDecl d) {
            for(FieldStmt s : d.fields) {
                s.visit(this);
            }
            
            Map<String, Tuple<AggregateTypeInfo, FieldInfo>> definedFields = new HashMap<>();
            AggregateTypeInfo aggInfo = d.type.as();
            
            // TODO, check cyclic cycle:
            //
            /*
                struct Entity {
                    pos: using Entity
                    type: i32
                }
             */
            checkDuplicateFields(d, aggInfo, definedFields);
        }
        
        @Override
        public void visit(VarFieldStmt stmt) {
        }

        @Override
        public void visit(UnionDecl d) {
            for(FieldStmt s : d.fields) {
                s.visit(this);
            }
            
            Map<String, Tuple<AggregateTypeInfo, FieldInfo>> definedFields = new HashMap<>();
            AggregateTypeInfo aggInfo = d.type.as();
            
            checkDuplicateFields(d, aggInfo, definedFields);
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
            expr.expr.visit(this);
        }
        
        @Override
        public void visit(TypeOfExpr expr) {
            expr.expr.visit(this);
        }
        
        @Override
        public void visit(InitArgExpr expr) {
            expr.value.visit(this);
        }
        
        private boolean checkInitField(Expr expr, AggregateTypeInfo aggInfo, InitArgExpr arg) {
            List<FieldInfo> fieldInfos = aggInfo.fieldInfos;
            
            for(int i = 0; i < fieldInfos.size(); i++) {
                FieldInfo fieldInfo = fieldInfos.get(i);
                
                if(fieldInfo.name.equals(arg.fieldName)) {
                    addTypeCheck(arg, fieldInfo.type);
                    return true;
                }
                else if(fieldInfo.type.isAnonymous()) {
                    if(checkInitField(expr, fieldInfo.type.as(), arg)) {
                        return true;
                    }
                }
            }
            
            return false;
        }
                
        private void checkAggregateInitFields(InitExpr expr, AggregateTypeInfo aggInfo, List<InitArgExpr> arguments) {
            List<FieldInfo> fieldInfos = aggInfo.fieldInfos;
            
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
                    boolean matchedField = checkInitField(expr, aggInfo, arg);                    
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
            checkGenericArgs(expr, expr.genericArgs, type);
            
            switch(type.getKind()) {
                case Union:
                case Struct: {
                    AggregateTypeInfo structInfo = expr.type.as();
                    checkAggregateInitFields(expr, structInfo, expr.arguments);
                    break;
                }
                default: {
                    this.result.addError(expr, "'%s' is an invalid type for initialization", type.getName());
                }
            }
            
        }
        
        private void checkGenericArgs(Stmt stmt, List<TypeInfo> genericArgs, TypeInfo type) {
            if(type.getResolvedType() instanceof GenericTypeInfo) {
                GenericTypeInfo genInfo = type.as();
                if(genInfo.hasGenerics()) {
                    if(genInfo.genericParams.size() != genericArgs.size()) {
                        this.result.addError(stmt, 
                                "incorrect number of generic argument types, requires %d and has %d", genInfo.genericParams.size(), genericArgs.size());;        
                    }
                }
            }
        }
        
        private void checkNumberOfArgs(FuncCallExpr expr, String funcName, FuncPtrTypeInfo funcInfo, int numberOfSuppliedArgs, int numberOfDefaultArgs) {
            int maxNumOfArgs = funcInfo.params.size();
            if(maxNumOfArgs == numberOfSuppliedArgs) {
                return;
            }
            
            int minNumOfArgs = maxNumOfArgs - numberOfDefaultArgs;
            
            if(numberOfSuppliedArgs == minNumOfArgs) {
                return;
            }
            
            if(minNumOfArgs > numberOfSuppliedArgs) {
                this.result.addError(expr, "'%s' called with too few arguments", funcName);
                return;
            }
            
            if(funcInfo.isVararg) {
                return;
            }
            
            if(maxNumOfArgs < numberOfSuppliedArgs) {            
                this.result.addError(expr, "'%s' called with too many arguments", funcName);
            }
        }

        @Override
        public void visit(FuncCallExpr expr) {
            expr.object.visit(this);            
            
            TypeInfo type = expr.object.getResolvedType();
            if(!type.isKind(TypeKind.Func) && !type.isKind(TypeKind.FuncPtr)) {
                this.result.addError(expr, "'%s' is not a function", type.getName());
                return;
            }
            
            List<Expr> suppliedArguments = new ArrayList<>(expr.arguments);
            
            // see if this is method call syntax
            if(expr.object instanceof GetExpr) {
                GetExpr getExpr = (GetExpr) expr.object;
                if(getExpr.isMethodCall) {                    
                    suppliedArguments.add(0, getExpr.object);
                }
            }
            
            checkGenericArgs(expr, expr.genericArgs, type);
            
            FuncPtrTypeInfo funcInfo = null;
            if(type.isKind(TypeKind.Func)) {
                FuncTypeInfo func = type.as();
                funcInfo = func.asPtr();
                
                int numberOfDefaultArgs = 0;
                for(ParameterDecl p : func.parameterDecls) {
                    if(p.defaultValue != null) {
                        numberOfDefaultArgs++;
                    }
                }
                
                checkNumberOfArgs(expr, type.getName(), funcInfo, suppliedArguments.size(), numberOfDefaultArgs);
            }
            else {
                funcInfo = type.as();
                
                checkNumberOfArgs(expr, type.getName(), funcInfo, suppliedArguments.size(), 0);
            }
            
            int i = 0;
            for(; i < funcInfo.params.size(); i++) {
                TypeInfo paramInfo = funcInfo.params.get(i);
                
                if(i < suppliedArguments.size()) {
                    Expr arg = suppliedArguments.get(i);
                    arg.visit(this);

                    addTypeCheck(arg, arg.getResolvedType(), paramInfo);
                }
            }
            
            if(funcInfo.isVararg) {
                for(; i < suppliedArguments.size(); i++) {
                    Expr arg = suppliedArguments.get(i);
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
        
        @Override
        public void visit(TypeIdentifierExpr expr) {
        }
        
        private boolean typeCheckAggregate(TypeInfo type, TypeInfo field, Expr expr, Expr value) {
            return typeCheckAggregate(type, field, expr, value, true);
        }
        
        private boolean typeCheckAggregate(TypeInfo type, TypeInfo field, Expr expr, Expr value, boolean error) {            
            switch(type.getKind()) {
                case Ptr: {
                    PtrTypeInfo ptrInfo = type.as();
                    return typeCheckAggregate(ptrInfo.ptrOf, field, expr, value, error);                    
                }
                case Const: {
                    ConstTypeInfo constInfo = type.as();
                    return typeCheckAggregate(constInfo.constOf, field, expr, value, error);
                }
                case Union:
                case Struct: {
                    AggregateTypeInfo structInfo = type.as();
                    for(FieldInfo fieldInfo : structInfo.fieldInfos) {  
                        if(fieldInfo.type.isAnonymous()) {
                            if(typeCheckAggregate(fieldInfo.type, field, expr, value, false)) {
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
                    
                    FieldPath path = structInfo.getFieldPath(field.getName());
                    if(path.hasPath()) {
                        TypeInfo usingField = path.getTargetField().type;    
                        if(value != null) {
                            addTypeCheck(expr, value.getResolvedType(), usingField);
                        }
                        return true;
                    }

                    // do not allow method call syntax for Set operations
                    if(value == null) {
                        TypeInfo resolvedType = field.getResolvedType();
                        FuncTypeInfo funcInfo = null;
                        if(resolvedType.isKind(TypeKind.Func)) {
                            funcInfo = resolvedType.as();
                        }
                        
                        if(funcInfo != null) {
                            if(funcInfo.parameterDecls.isEmpty()) {
                                if(error) {
                                    this.result.addError(expr, "'%s' does not have a parameter of '%s'", funcInfo.getName(), structInfo.name);
                                }
                                break;
                            }
                            
                            ParameterDecl objectParam = funcInfo.parameterDecls.get(0);
                            TypeInfo baseType = objectParam.type.getResolvedType();
                            if(TypeInfo.isPtrAggregate(baseType)) {
                                baseType = ((PtrTypeInfo) baseType.as()).getBaseType().getResolvedType();
                            }
                            
                            if(!baseType.strictEquals(structInfo)) {
                                if(error) {
                                    this.result.addError(expr, "'%s' does not have a parameter of '%s'", funcInfo.getName(), structInfo.name);
                                }
                                break;
                            }
                            
                            return true;
                        }
                    }
                    
                    if(error) {
                        this.result.addError(expr, "'%s' does not have field '%s'", structInfo.name, field.name);
                    }
                    break;
                }
                case Enum: {
                    if(value != null) {
                        if(error) {
                            this.result.addError(expr, "'%s.%s' can not be reassigned", type.name, field.name);
                        }
                    }
                    else {
                        EnumTypeInfo enumInfo = type.as();
                        for(EnumFieldInfo fieldInfo : enumInfo.fields) {
                            if(fieldInfo.name.equals(field.getName())) {                                
                                return true;
                            }
                        }
                        
                        if(error) {
                            this.result.addError(expr, "'%s' does not have field '%s'", enumInfo.name, field.name);
                        }
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
            expr.field.visit(this);
            
            TypeInfo type = expr.object.getResolvedType();
            typeCheckAggregate(type, expr.field.type, expr, null);
        }
        
        @Override
        public void visit(SetExpr expr) {
            if(expr.object.getResolvedType().isKind(TypeKind.Enum)) {
                this.result.addError(expr, "can't reassign enum '%s'", expr.object.getResolvedType());
                return;
            }
            
            expr.object.visit(this);            
            expr.field.visit(this);
            expr.value.visit(this);
            
            TypeInfo type = expr.object.getResolvedType();            
            typeCheckAggregate(type, expr.field.type, expr, expr.value);
        }

        @Override
        public void visit(UnaryExpr expr) {
            expr.expr.visit(this);
            
            switch(expr.operator) {
                case STAR: {
                    TypeInfo type = expr.expr.getResolvedType().getResolvedType();
                    if(!TypeInfo.isPtrLike(type)) {                            
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
            
            addTypeCheck(expr.left, expr.getResolvedType().getResolvedType());
            addTypeCheck(expr.right, expr.getResolvedType().getResolvedType());
            
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
        public void visit(TernaryExpr expr) {
            expr.cond.visit(this);
            expr.then.visit(this);
            expr.other.visit(this);
            
            addTypeCheck(expr, expr.then.getResolvedType(), expr.other.getResolvedType());
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
        public void visit(ArrayDesignationExpr expr) {
            expr.index.visit(this);
            if(expr.index instanceof IdentifierExpr) {
                IdentifierExpr idExpr = (IdentifierExpr)expr.index;
                Symbol sym = idExpr.sym;
                if(!sym.isConstant()) {
                    this.result.addError(expr.index, "'%s' must be a constant", idExpr.variable);
                }
            }
            addTypeCheck(expr.index, TypeInfo.I32_TYPE);
            
            expr.value.visit(this);
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
                case Enum:
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
                case Array: {
                    ArrayTypeInfo arrayInfo = objectInfo.as();
                    objectInfo = arrayInfo.arrayOf.getResolvedType();
                    break;
                }
                case Str: {
                    objectInfo = TypeInfo.CHAR_TYPE;
                    break;
                }
                case Ptr: {
                    PtrTypeInfo ptrInfo = objectInfo.as();
                    objectInfo = ptrInfo.ptrOf.getResolvedType();
                    break;
                }
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
                case Enum:
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
