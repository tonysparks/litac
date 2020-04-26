/*
 * see license.txt
 */
package litac.lsp;

import java.util.*;
import java.util.stream.Collectors;

import litac.ast.*;
import litac.ast.Decl.*;
import litac.ast.Expr.*;
import litac.ast.Node.Identifier;
import litac.ast.Stmt.*;
import litac.checker.TypeInfo;
import litac.checker.TypeInfo.*;
import litac.checker.TypeResolver.Operand;
import litac.compiler.*;
import litac.lsp.JsonRpc.*;
import litac.lsp.SourceToAst.SourceLocation;

/**
 * @author Tony
 *
 */
public class ReferenceDatabase {

    private class FieldAccess {
        Map<String, List<Location>> locations;
        
        public FieldAccess(TypeInfo type) {
            this.locations = new HashMap<>();
            
            if(TypeInfo.isAggregate(type)) {
                AggregateTypeInfo agg = type.as();
                for(FieldInfo field : agg.fieldInfos) {
                    //this.locations.put(field.name, new ArrayList<>());
                    
                    Symbol sym = TypeInfo.getBase(field.type).sym;
                    if(sym != null) { 
                        if(!identifiers.containsKey(sym)) {            
                            identifiers.put(sym, new ArrayList<>());
                        }
                        
                        Location loc = LspUtil.locationFromSrcPosLine(field.attributes.srcPos);
                        identifiers.get(sym).add(loc);
                    }
                    
                    // TODO: Array Size Expr's
                }
            }
            /*
            else if(TypeInfo.isEnum(type)) {
                EnumTypeInfo e = type.as();
                for(EnumFieldInfo field : e.fields) {
                    this.locations.put(field.name, new ArrayList<>());
                }    
            }*/                       
        }
        
        public void addLocation(String field, Node node) {
            if(!this.locations.containsKey(field)) {
                this.locations.put(field, new ArrayList<>());
            }
            
            Location loc = LspUtil.locationFromSrcPosLine(node.getSrcPos());
            this.locations.get(field).add(loc);
        }
    }
      
    private Map<Symbol, List<Location>> identifiers;
    private Map<Symbol, FieldAccess> aggregateTypes;
    private Map<ModuleId, List<Location>> moduleReferences;
    
    private IntellisenseDatabase intellisense;
    
    private LspLogger log;
    private Program program;
    
    public ReferenceDatabase(LspLogger log) {
        this.log = log;
        this.identifiers = new IdentityHashMap<>();
        this.aggregateTypes = new IdentityHashMap<>();
        this.moduleReferences = new HashMap<>();
        
        this.intellisense = new IntellisenseDatabase();
    }
    
    public void buildDatabase(Program program) {
        this.identifiers.clear();
        this.aggregateTypes.clear();
        this.intellisense.clear();
        
        this.program = program;
        
        Module main = program.getMainModule();
        ReferenceNodeVisitor visitor = new ReferenceNodeVisitor(program, true);
        main.getModuleStmt().visit(visitor);
        
    }
        
    public List<CompletionItem> findCompletionItems(Module module, Position pos, List<String> fields) {
        List<Symbol> symbols = findSymbols(module.getId(), pos, fields);
        if(symbols == null) {
            return Collections.emptyList();
        }
        
        if(symbols.size() == 1) {
            Symbol sym = symbols.get(0);
            return buildCompletion(module, fields, sym.type);
        }
        
        return symbols.stream()
                .map(sym -> LspUtil.fromSymbolCompletionItem(sym))
                .collect(Collectors.toList());       
    }
    
    private List<Symbol> findSymbols(ModuleId moduleId, Position pos, List<String> fields) {
        IntellisenseScope scope = this.intellisense.getScope(moduleId, pos);
        if(scope == null) {
            log.log("No scope for: " + pos.line);
            return Collections.emptyList();
        }
                       
        if(fields.isEmpty()) {
            log.log("Empty Fields for: " + pos.line);
            return new ArrayList<>(scope.getAllSymbols());
        }
        
        
        String first = fields.get(0);
        Symbol sym = scope.findSymbol(first);
        if(sym != null) {
            log.log("Found symbol for: '" + first + "'");
            return Arrays.asList(sym);
        }
        
        log.log("No symbol found for: '" + first + "'");
        return new ArrayList<>(scope.getAllSymbols());
    }
    
    private List<CompletionItem> buildCompletion(Module module, List<String> fields, TypeInfo type) {
        if(fields.size() > 1 && type != null) {            
            for(int i = 1; i < fields.size(); i++) {
                String fieldName = fields.get(i);
                
                if(TypeInfo.isAggregate(type)) {
                    AggregateTypeInfo aggInfo = type.as();
                    FieldPath path = aggInfo.getFieldPath(fieldName);
                    if(path.hasPath()) {
                        FieldInfo field = path.getTargetField();
                        type = field.type;
                    }
                }
                else if(TypeInfo.isPtrAggregate(type)) {
                    PtrTypeInfo ptrInfo = type.as();
                    AggregateTypeInfo aggInfo = ptrInfo.getBaseType().as();
                    FieldPath path = aggInfo.getFieldPath(fieldName);
                    if(path.hasPath()) {
                        FieldInfo field = path.getTargetField();
                        type = field.type;
                    }                            
                }
                else {
                    break;
                }
            }
        }
        
        if(type != null) {
            if(TypeInfo.isAggregate(type)) {
                AggregateTypeInfo aggInfo = type.as();
                return LspUtil.fromTypeInfoCompletionItems(module, aggInfo);
            }
            else if(TypeInfo.isPtrAggregate(type)) {
                PtrTypeInfo ptrInfo = type.as();
                AggregateTypeInfo aggInfo = ptrInfo.getBaseType().as();
                return LspUtil.fromTypeInfoCompletionItems(module, aggInfo);
            }
            else if(TypeInfo.isEnum(type)) {                
                EnumTypeInfo enumInfo = type.as();
                return LspUtil.fromEnumTypeInfoCompletionItems(enumInfo);
            }
        }
        
        return Collections.emptyList();
    }
    
    public List<Location> findReferencesFromLocation(SourceLocation location) {
        log.log("Finding reference from: " + (location.node != null ? location.node.getClass().getSimpleName() : ""));
        
        List<Location> locations = Collections.emptyList();
        if((location.node instanceof ModuleStmt)) {
            ModuleStmt mStmt = (ModuleStmt)location.node;
            return getModuleReferences(mStmt.id);
        }
        else if(location.node instanceof Decl) {
            Decl decl = (Decl)location.node;
            TypeInfo type = decl.sym.getType();
            
            log.log("Found declaration from: " + type.toString());
            locations = getTypeReferences(type);            
        }
        else if(location.node instanceof IdentifierExpr) {
            IdentifierExpr idExpr = (IdentifierExpr)location.node;
            Node parent = idExpr.getParentNode();
            
            if(parent instanceof GetExpr) {
                GetExpr get = (GetExpr)parent;
                Operand op = get.object.getResolvedType();
                
                log.log("Get Expr for type: " + op.type.name + " from identifier: " + idExpr.sym + " or " + idExpr.type);
                locations = getFieldReferencesReferences(op.type, idExpr.type.name);                
            }
            else if(parent instanceof SetExpr) {
                SetExpr get = (SetExpr)parent;
                Operand op = get.object.getResolvedType();
                
                log.log("Set Expr for type: " + op.type.name + " from identifier: " + idExpr.sym + " or " + idExpr.type);
                locations = getFieldReferencesReferences(op.type, idExpr.type.name);
            }
            else if(idExpr.sym != null) {
                locations = getIdentifierReferences(idExpr.sym);
            }
        }
        else if(location.node instanceof Identifier) {
            Identifier id = (Identifier)location.node;
            
            log.log("Found IdentifierNode from: " + id.identifier);
            Node parent = id.getParentNode();
            if(!(parent instanceof Decl)) {
                log.log("Type of Identifier: " + parent.getClass().getSimpleName());
                
                // an aggregate field or enum field reference
                if((parent instanceof VarFieldStmt) || (parent instanceof EnumFieldEntryStmt)) {                    
                    Node fieldParent = parent.getParentNode();
                    if(fieldParent instanceof Decl) {
                        log.log("Found VarFieldStmt|EnumFieldEntryStmt from: " + fieldParent.getClass().getSimpleName());
                        
                        Decl decl = (Decl)fieldParent;
                        locations = getFieldReferencesReferences(decl.sym.type, id.identifier);
                    }
                }
                
                return locations;
            }
            
            Decl decl = (Decl)parent;
            if((decl instanceof ConstDecl) || (decl instanceof VarDecl) || (decl instanceof ParameterDecl)) {
                log.log("IdentifierNode is: " + decl.kind + " Sym: " + decl.sym);
                locations = getIdentifierReferences(decl.sym);
            }
            else {
                log.log("IdentifierNode is a type: " + decl.kind + " Sym: " + decl.sym.name);
                locations = getIdentifierReferences(decl.sym);
                                
                //if(locations.isEmpty()) 
                {
                    TypeInfo type = decl.sym.getType();
                    log.log("Looking for TypeReferences: " + type.name + " id: " + type.getTypeId());
                    locations = getTypeReferences(type);  
                }
                
                // filter out duplicates
                locations = locations.stream().distinct().collect(Collectors.toList());
            }
        }

        return locations;
    }
    
    public List<Location> getTypeReferences(TypeInfo type) {
        List<Location> locations = new ArrayList<>();                
        
        TypeInfo baseType = TypeInfo.getBase(type);
        
        if(baseType.isKind(TypeKind.Func)) {
            FuncTypeInfo funcInfo = baseType.as();
            TypeInfo recv = funcInfo.getReceiverType();
            if(recv != null) {
                return getFieldReferencesReferences(recv, baseType.name);
            }
        }
        
        program.getResolvedTypeMap().entrySet().forEach(e -> {
            //log.log("Looking at type: " + TypeInfo.getBase(e.getValue()).getTypeId() + " vs " + baseType.getTypeId());
            if(TypeInfo.getBase(e.getValue()).getTypeId() == baseType.getTypeId()) {
                Location loc = LspUtil.locationFromSrcPosLine(e.getKey().pos);
                if(loc != null) {
                    locations.add(loc);
                }
            }
        });
        
        return locations;
    }
    
    public List<Location> getFieldReferencesReferences(TypeInfo owner, String fieldName) {
        TypeInfo type = TypeInfo.getBase(owner);
        FieldAccess access = aggregateTypes.get(type.sym);
        if(access == null) {
            return Collections.emptyList();
        }
        
        List<Location> locations = access.locations.get(fieldName);
        if(locations == null) {
            return Collections.emptyList();
        }
        
        return locations;
    }
    
    
    public List<Location> getIdentifierReferences(Symbol symbol) {
        List<Location> locations = this.identifiers.get(symbol);
        if(locations == null) {
            return Collections.emptyList();
        }                
        
        return locations;
    }
    
    public List<Location> getModuleReferences(ModuleId moduleId) {
        return this.moduleReferences.get(moduleId);
    }
    
    private class ReferenceNodeVisitor implements NodeVisitor {

        private Set<ModuleId> visitedModules;
        private Program program;
        private boolean fullRebuild;
        
        public ReferenceNodeVisitor(Program program, boolean fullRebuild) {
            this.program = program;
            this.fullRebuild = fullRebuild;
            this.visitedModules = new HashSet<>();
        }
        
        @Override
        public void visit(ModuleStmt stmt) {
            if(fullRebuild) {
                for(ImportStmt imp : stmt.imports) {
                    imp.visit(this);
                    
                    if(!moduleReferences.containsKey(imp.moduleId)) {
                        moduleReferences.put(imp.moduleId, new ArrayList<>());
                    }
                    
                    Location loc = LspUtil.locationFromSrcPosLine(imp.getSrcPos());
                    moduleReferences.get(imp.moduleId).add(loc);
                }
            }
            
            intellisense.beginModule(stmt);
            for(Decl d : stmt.declarations) {
                d.visit(this);                
            }
            
            for(Stmt s : stmt.notes) {
                s.visit(this);
            }
            intellisense.endModule();
        }

        @Override
        public void visit(ImportStmt stmt) {
            if(visitedModules.contains(stmt.moduleId)) {
                return;
            }
            
            visitedModules.add(stmt.moduleId);
            
            Module module = program.getModule(stmt.moduleId);
            if(module == null) {
                return;
            }
            
            module.getModuleStmt().visit(this);
            
        }

        @Override
        public void visit(NoteStmt stmt) {
        }

        @Override
        public void visit(VarFieldStmt stmt) {
            if(stmt.defaultExpr != null) {
                stmt.defaultExpr.visit(this);
            }
        }

        @Override
        public void visit(StructFieldStmt stmt) {
            stmt.decl.visit(this);            
        }

        @Override
        public void visit(UnionFieldStmt stmt) {
            stmt.decl.visit(this);
        }

        @Override
        public void visit(EnumFieldStmt stmt) {
            stmt.decl.visit(this);
        }

        @Override
        public void visit(EnumFieldEntryStmt stmt) {
            if(stmt.value != null) {
                stmt.value.visit(this);
            }
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
            if(stmt.stmt != null) {
                stmt.stmt.visit(this);
            }
        }

        @Override
        public void visit(SwitchStmt stmt) {
            stmt.cond.visit(this);
            if(stmt.defaultStmt != null) {
                stmt.defaultStmt.visit(this);
            }
            
            for(Stmt s : stmt.stmts) {
                s.visit(this);
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
            if(stmt.returnExpr != null) {
                stmt.returnExpr.visit(this);
            }
        }

        @Override
        public void visit(BlockStmt stmt) {
            for(Stmt s : stmt.stmts) {
                s.visit(this);
            }
        }

        @Override
        public void visit(FuncBodyStmt stmt) {
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
        public void visit(ParametersStmt stmt) {
            for(ParameterDecl d : stmt.params) {
                d.visit(this);
            }            
        }

        @Override
        public void visit(VarDeclsStmt stmt) {
            for(Decl d: stmt.vars) {
                d.visit(this);
            }
        }

        @Override
        public void visit(ConstDeclsStmt stmt) {
            for(Decl d: stmt.consts) {
                d.visit(this);
            }
        }

        @Override
        public void visit(GotoStmt stmt) {
        }

        @Override
        public void visit(LabelStmt stmt) {
        }

        @Override
        public void visit(CompStmt stmt) {            
        }

        @Override
        public void visit(ConstDecl d) {
            if(!identifiers.containsKey(d.sym)) {
                identifiers.put(d.sym, new ArrayList<>());
            }
            
            if(d.expr != null) {
                d.expr.visit(this);
            }
            
            // TODO: Array Size Expr's
            intellisense.addSymbol(d.sym);
        }

        @Override
        public void visit(EnumDecl d) {
            if(!aggregateTypes.containsKey(d.sym)) {
                aggregateTypes.put(d.sym, new FieldAccess(d.sym.type));
            }
            
            for(EnumFieldEntryStmt field : d.fields) {
                field.visit(this);                
            }
            
            intellisense.addSymbol(d.sym);
        }

        @Override
        public void visit(FuncDecl d) {
            if(!d.isMethod()) {
                intellisense.addSymbol(d.sym);    
            }
            
            intellisense.beginScope(d);
            
            if(!identifiers.containsKey(d.sym)) {
                identifiers.put(d.sym, new ArrayList<>());
            }
            d.params.visit(this);   
            
            if(d.bodyStmt != null) {
                d.bodyStmt.visit(this);
            }
            
            intellisense.popScope();
        }

        @Override
        public void visit(StructDecl d) {
            if(!identifiers.containsKey(d.sym)) {
                identifiers.put(d.sym, new ArrayList<>());
            }
            
            if(!aggregateTypes.containsKey(d.sym)) {
                aggregateTypes.put(d.sym, new FieldAccess(d.sym.type));
            }
        }

        @Override
        public void visit(TypedefDecl d) {
            if(!identifiers.containsKey(d.sym)) {
                identifiers.put(d.sym, new ArrayList<>());
            }
        }

        @Override
        public void visit(UnionDecl d) {
            if(!identifiers.containsKey(d.sym)) {
                identifiers.put(d.sym, new ArrayList<>());
            }
            
            if(!aggregateTypes.containsKey(d.sym)) {
                aggregateTypes.put(d.sym, new FieldAccess(d.sym.type));
            }
        }

        @Override
        public void visit(VarDecl d) {       
            if(!identifiers.containsKey(d.sym)) {
                identifiers.put(d.sym, new ArrayList<>());
            }
            
            if(d.expr != null) {
                d.expr.visit(this);
            }
            
            // TODO: Array Size Expr's
            intellisense.addSymbol(d.sym);
        }

        @Override
        public void visit(ParameterDecl d) {
            if(d.defaultValue != null) {
                d.defaultValue.visit(this);
            }
            
            if(d.sym == null) {
                return;
            }
         
            Symbol sym = d.sym;
            if(!identifiers.containsKey(sym)) {
                identifiers.put(sym, new ArrayList<>());
            }
            
            Location loc = LspUtil.locationFromSrcPosLine(d.getSrcPos());
            identifiers.get(sym).add(loc);   
            
            intellisense.addSymbol(sym);
        }

        @Override
        public void visit(CastExpr expr) {
            expr.expr.visit(this);
        }

        @Override
        public void visit(SizeOfExpr expr) {
            if(expr.expr != null) {
                expr.expr.visit(this);
            }
        }

        @Override
        public void visit(TypeOfExpr expr) {
            if(expr.expr != null) {
                expr.expr.visit(this);
            }
        }

        @Override
        public void visit(OffsetOfExpr expr) {
            // TODO
        }

        @Override
        public void visit(InitArgExpr expr) {
            if(expr.value != null) {
                expr.value.visit(this);
            }
        }

        @Override
        public void visit(InitExpr expr) {
            for(Expr e : expr.arguments) {
                e.visit(this);
            }
        }

        @Override
        public void visit(NullExpr expr) {
        }

        @Override
        public void visit(BooleanExpr expr) {
        }

        @Override
        public void visit(NumberExpr expr) {
        }

        @Override
        public void visit(StringExpr expr) {
        }

        @Override
        public void visit(CharExpr expr) {            
        }

        @Override
        public void visit(GroupExpr expr) {
            expr.expr.visit(this);
        }

        @Override
        public void visit(FuncCallExpr expr) {
            expr.object.visit(this);
            for(Expr e : expr.arguments) {
                e.visit(this);
            }
            
            Operand op = expr.object.getResolvedType();
            if(op == null) {
                return;
            }
            
            TypeInfo type = TypeInfo.getBase(op.type);
            if(type == null) {
                return;
            }
                        
            if(!type.isKind(TypeKind.Func)) {
                return;
            }
            
            FuncTypeInfo funcInfo = type.as();
            boolean isMethod = funcInfo.isMethod();
            if(!isMethod) {
                return;
            }
            
            TypeInfo object = funcInfo.parameterDecls.get(0).type;
            
            if(!aggregateTypes.containsKey(object.sym)) {
                aggregateTypes.put(object.sym, new FieldAccess(object));
            }
            
            aggregateTypes.get(object.sym).addLocation(funcInfo.name, expr);            
        }

        @Override
        public void visit(IdentifierExpr expr) {
            Symbol sym = expr.sym;
            if(sym == null) {
                return;
            }
                        
            if(!identifiers.containsKey(sym)) {
                identifiers.put(sym, new ArrayList<>());
            }
                        
            Location loc = LspUtil.locationFromSrcPosLine(expr.getSrcPos());
            identifiers.get(sym).add(loc);                        
        }

        @Override
        public void visit(FuncIdentifierExpr expr) {
            Symbol sym = expr.sym;
            if(sym == null) {
                return;
            }
                        
            if(!identifiers.containsKey(sym)) {
                identifiers.put(sym, new ArrayList<>());
            }
                        
            Location loc = LspUtil.locationFromSrcPosLine(expr.getSrcPos());
            identifiers.get(sym).add(loc);
        }

        @Override
        public void visit(TypeIdentifierExpr expr) {
            Symbol sym = expr.sym;
            if(sym == null) {
                return;
            }
                        
            if(!identifiers.containsKey(sym)) {
                identifiers.put(sym, new ArrayList<>());
            }
                        
            Location loc = LspUtil.locationFromSrcPosLine(expr.getSrcPos());
            identifiers.get(sym).add(loc);
        }

        @Override
        public void visit(GetExpr expr) {
            expr.object.visit(this);
            expr.field.visit(this);
            
            Operand op = expr.object.getResolvedType();  
            if(op == null) {
                return;
            }
            
            TypeInfo type = TypeInfo.getBase(op.type);
            if(type == null) {
                return;
            }
            
            if(!aggregateTypes.containsKey(type.sym)) {
                aggregateTypes.put(type.sym, new FieldAccess(type));
            }
            
            aggregateTypes.get(type.sym).addLocation(expr.field.type.name, expr.field);
        }

        @Override
        public void visit(SetExpr expr) {
            expr.object.visit(this);
            expr.field.visit(this);
            expr.value.visit(this);
            
            Operand op = expr.object.getResolvedType();   
            if(op == null) {
                return;
            }
            
            TypeInfo type = TypeInfo.getBase(op.type);
            if(type == null) {
                return;
            }
            
            if(!aggregateTypes.containsKey(type.sym)) {
                aggregateTypes.put(type.sym, new FieldAccess(type));
            }
            
            aggregateTypes.get(type.sym).addLocation(expr.field.type.name, expr.field);
        }

        @Override
        public void visit(UnaryExpr expr) {
            expr.expr.visit(this);
        }

        @Override
        public void visit(BinaryExpr expr) {
            expr.left.visit(this);
            expr.right.visit(this);
        }

        @Override
        public void visit(TernaryExpr expr) {
            expr.cond.visit(this);
            expr.then.visit(this);
            expr.other.visit(this);
        }

        @Override
        public void visit(ArrayInitExpr expr) {
            for(Expr e : expr.values) {
                e.visit(this);
            }
        }

        @Override
        public void visit(ArrayDesignationExpr expr) {
            expr.index.visit(this);
            expr.value.visit(this);
        }

        @Override
        public void visit(SubscriptGetExpr expr) {
            expr.object.visit(this);            
            expr.index.visit(this);
        }

        @Override
        public void visit(SubscriptSetExpr expr) {            
            expr.object.visit(this);
            expr.index.visit(this);
            expr.value.visit(this);            
        }
        
    }
}
