/*
 * see license.txt
 */
package litac.lsp;

import java.io.File;

import litac.ast.*;
import litac.ast.Decl.*;
import litac.ast.Expr.*;
import litac.ast.Node.SrcPos;
import litac.ast.Stmt.*;
import litac.ast.TypeSpec.*;
import litac.checker.TypeInfo;
import litac.checker.TypeInfo.*;
import litac.checker.TypeResolver.Operand;
import litac.compiler.*;
import litac.lsp.JsonRpc.*;

/**
 * Finds the Ast node definition (represented by a {@link Location}) from a {@link Position}
 */
public class SourceToAst implements NodeVisitor {

    private class AstNodeFound extends RuntimeException {        
        private static final long serialVersionUID = 7748524454625404184L;
        
        Location location;        
        public AstNodeFound(Location location) {
            this.location = location;
        }
        
    }
    
    private Position pos;
    private Program program;
    private Module module;
    
    public SourceToAst(Program program, Module module, Position pos) {
        this.program = program;
        this.module = module;
        this.pos = pos;
    }
    
    public Location findSourceLocation(ModuleStmt stmt) {
        // horrible hack, but makes the visiting code
        // cleaner...
        try {
            visit(stmt);
        }
        catch(AstNodeFound found) {
            return found.location;
        }
        
        return null;
    }
    
    private boolean isNodeAtPos(Node node) {
        if(node == null) {
            return false;
        }
        
        SrcPos srcPos = node.getSrcPos();
        return isNodeAtPos(srcPos);
    }
    
    private boolean isNodeAtPos(TypeSpec typeSpec) {
        if(typeSpec == null) {
            return false;
        }
        
        switch(typeSpec.kind) {
            case ARRAY:
                ArrayTypeSpec arraySpec = typeSpec.as();
                if(arraySpec.numElements != null) {
                    arraySpec.numElements.visit(this);
                }
                
                if(isNodeAtPos(arraySpec.base)) {
                    findFromTypeSpec(arraySpec.base);
                }
                
                break;
            case CONST:
                ConstTypeSpec constSpec = typeSpec.as();
                if(isNodeAtPos(constSpec.base)) {
                    findFromTypeSpec(constSpec.base);
                }
                break;
            case PTR:
                PtrTypeSpec ptrSpec = typeSpec.as();
                if(isNodeAtPos(ptrSpec.base) ) {
                    findFromTypeSpec(ptrSpec.base);
                }
                break;
            case FUNC_PTR:
                FuncPtrTypeSpec funcSpec = typeSpec.as();
                for(TypeSpec a : funcSpec.args) {
                    if(isNodeAtPos(a)) {
                        findFromTypeSpec(a);
                    }
                }
                
                if(isNodeAtPos(funcSpec.base)) findFromTypeSpec(funcSpec.base);
                if(isNodeAtPos(funcSpec.ret))  findFromTypeSpec(funcSpec.ret);                
                break;
            case NAME:
                break;
            
            default:
                break;
        
        }
        
        SrcPos srcPos = typeSpec.pos;
        return isNodeAtPos(srcPos);
    }
    
    private boolean isNodeAtPos(SrcPos srcPos) {  
        if(srcPos.token == null) {
            return false;
        }
        
        int fromIndex = srcPos.position;
        int toIndex = fromIndex + srcPos.token.getText().length();
        if((pos.line+1) == srcPos.lineNumber) {
            if(pos.character >= fromIndex && pos.character <= toIndex) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean findFromTypeSpec(TypeSpec typeSpec) {
        if(typeSpec == null) {
            return false;
        }
        
        TypeInfo type = program.getResolvedTypeMap().get(typeSpec);
        if(!findFromTypeInfo(type)) {
            // Account for typedefs
            if(typeSpec.kind == TypeSpecKind.NAME) {
                Symbol sym = module.getAliasedType(typeSpec.toString());
                if(sym != null) {
                    return findFromDecl(sym.decl);
                }
            }
        }
        
        return false;
    }
    
    private boolean findFromExpr(Expr expr) {
        Operand op = expr.getResolvedType();
        if(op == null) {
            return false;
        }
        
        TypeInfo type = op.type;
        return findFromTypeInfo(type);
    }
    
    private boolean findFromTypeInfo(TypeInfo type) {        
        if(type == null) {
            return false;
        }
        TypeInfo baseType = TypeInfo.getBase(type);
        if(baseType == null || baseType.sym == null) {
            return false;
        }
        
        return findFromDecl(baseType.sym.decl);
    }
    
    private boolean findFromDecl(Decl decl) {        
        if(decl == null) {
            return false;
        }        
        SrcPos srcPos = decl.getSrcPos();
        return findFromSrcPos(srcPos);
    }
    
    private boolean findFromSrcPos(SrcPos srcPos) {                
        if(srcPos.sourceFile == null) {
            return false;
        }
        
        Location location = new Location();
        location.uri = new File(srcPos.sourceFile).toURI().toString();
        location.range = LspUtil.fromSrcPosToken(srcPos);        
        throw new AstNodeFound(location);
    }
    
    @Override
    public void visit(ModuleStmt stmt) {
        for(Stmt s:stmt.imports) {
            s.visit(this);
        }
        
        for(Decl d: stmt.declarations) {
            if(d.sym != null && d.sym.isFromGenericTemplate()) {
                continue;
            }
            
            d.visit(this);            
        }
        
        for(Stmt s : stmt.notes) {
            s.visit(this);
        }
    }

    @Override
    public void visit(ImportStmt stmt) {
        if(isNodeAtPos(stmt)) {
            String moduleName = stmt.alias != null ? stmt.alias : stmt.moduleName;
            Module m = this.module.getModule(moduleName);
            if(m != null) {
                findFromSrcPos(m.getModuleStmt().getSrcPos());
            }
        }
    }

    @Override
    public void visit(NoteStmt stmt) {
        if(isNodeAtPos(stmt)) {
            findFromSrcPos(stmt.getSrcPos());
        }
    }

    @Override
    public void visit(VarFieldStmt stmt) {
        if(isNodeAtPos(stmt) || isNodeAtPos(stmt.type)) {
            findFromTypeSpec(stmt.type);
        }        
    }

    @Override
    public void visit(StructFieldStmt stmt) {
        if(isNodeAtPos(stmt)) {
            findFromDecl(stmt.decl);
        }
    }

    @Override
    public void visit(UnionFieldStmt stmt) {
        if(isNodeAtPos(stmt)) {
            findFromDecl(stmt.decl);
        }
    }

    @Override
    public void visit(EnumFieldStmt stmt) {
        if(isNodeAtPos(stmt)) {
            findFromDecl(stmt.decl);
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
        stmt.condExpr.visit(this);
        stmt.bodyStmt.visit(this);
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
        for(Stmt s: stmt.stmts) {
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
        for(Stmt s: stmt.params) {
            s.visit(this);
        }
    }
    
    @Override
    public void visit(CompStmt stmt) {
        if(stmt.body != null) {
            for(Stmt s : stmt.body) {
                s.visit(this);
            }
        }
        
        if(stmt.end != null) {
            stmt.end.visit(this);
        }
    }

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
    public void visit(GotoStmt stmt) {
    }

    @Override
    public void visit(LabelStmt stmt) {
    }

    @Override
    public void visit(ConstDecl d) {
        if(d.expr != null) {
            d.expr.visit(this);
        }
        
        if(isNodeAtPos(d) || isNodeAtPos(d.type)) {
            findFromTypeSpec(d.type);
        }
    }

    @Override
    public void visit(EnumDecl d) {
    }

    @Override
    public void visit(FuncDecl d) {
        if(d.bodyStmt != null) {
            d.bodyStmt.visit(this);
        }
        d.params.visit(this);
        
        if(isNodeAtPos(d.returnType)) {
            findFromTypeSpec(d.returnType);
        }
    }

    @Override
    public void visit(StructDecl d) {
        for(Stmt s: d.fields) {
            s.visit(this);            
        }
    }

    @Override
    public void visit(TypedefDecl d) {
        if(isNodeAtPos(d) || isNodeAtPos(d.type)) {
            findFromTypeSpec(d.type);
        }
    }

    @Override
    public void visit(UnionDecl d) {
        for(Stmt s: d.fields) {
            s.visit(this);            
        }
    }

    @Override
    public void visit(VarDecl d) {
        if(d.expr != null) {
            d.expr.visit(this);
        }
        
        if(isNodeAtPos(d) || isNodeAtPos(d.type)) {
            findFromTypeSpec(d.type);
        }        
    }

    @Override
    public void visit(ParameterDecl d) {
        if(d.defaultValue != null) {
            d.defaultValue.visit(this);
        }
        
        if(isNodeAtPos(d) || isNodeAtPos(d.type)) {
            findFromTypeSpec(d.type);
        }
    }

    @Override
    public void visit(CastExpr expr) {
        expr.expr.visit(this);
        
        if(isNodeAtPos(expr.castTo)) {
            findFromTypeSpec(expr.castTo);
        }
    }

    @Override
    public void visit(SizeOfExpr expr) {
        expr.expr.visit(this);
    }

    @Override
    public void visit(TypeOfExpr expr) {
        if(expr.expr != null) {
            expr.expr.visit(this);
        }
        
        if(isNodeAtPos(expr.type)) {
            findFromTypeSpec(expr.type);
        }
    }

    @Override
    public void visit(InitArgExpr expr) {
        if(expr.value != null) {
            expr.value.visit(this);
        }
        
        if(isNodeAtPos(expr) || isNodeAtPos(expr.value)) {
            // TODO: Navigate to parent type?
            //findFromTypeSpec(expr.getParentNode().)
            findFromExpr(expr);
        }
    }

    @Override
    public void visit(InitExpr expr) {
        if(isNodeAtPos(expr)) {
            if(findFromTypeSpec(expr.type)) {
               return; 
            }            
        }
        
        if(expr.genericArgs != null) {
            for(TypeSpec t : expr.genericArgs) {
                if(isNodeAtPos(t)) {
                    if(findFromTypeSpec(t)) {
                        return;
                    }
                }
            }
        }
        
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
        if(isNodeAtPos(expr)) {
            findFromExpr(expr);
        }
        
        expr.object.visit(this);
        for(Expr e : expr.arguments) {
            e.visit(this);
        }
    }

    @Override
    public void visit(IdentifierExpr expr) {
        if(isNodeAtPos(expr)) {
            if(expr.sym != null && findFromDecl(expr.sym.decl)) {
                return;
            }
            if(findFromExpr(expr) || 
               findFromTypeSpec(expr.type)) {
                return;
            }            
        }
        
        if(expr.genericArgs != null) {
            for(TypeSpec t : expr.genericArgs) {
                if(isNodeAtPos(t)) {
                    if(findFromTypeSpec(t)) {
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void visit(FuncIdentifierExpr expr) {
        visit((IdentifierExpr)expr);       
    }

    @Override
    public void visit(TypeIdentifierExpr expr) {
        if(isNodeAtPos(expr)) {
            if(expr.sym != null && findFromDecl(expr.sym.decl)) {
                return;
            }
            if(findFromExpr(expr) || 
               findFromTypeSpec(expr.type)) {
                return;
            }            
        }
        
        if(expr.genericArgs != null) {
            for(TypeSpec t : expr.genericArgs) {
                if(isNodeAtPos(t)) {
                    if(findFromTypeSpec(t)) {
                        return;
                    }
                }
            }
        }        
    }
    
    private void visitField(Expr object, IdentifierExpr field) {
        if(!isNodeAtPos(field)) {
            return;
        }
        
        if(field instanceof IdentifierExpr) {
            Operand op = object.getResolvedType();
            if(op == null) {
                return;
            }
            
            if(TypeInfo.isEnum(op.type)) {
                EnumTypeInfo enumInfo = op.type.as();
                EnumFieldInfo enumField = enumInfo.getField(field.type.name);
                if(enumField == null) {
                    return;
                }
                
                findFromSrcPos(enumField.attributes.srcPos);
            }
            else if(TypeInfo.isFieldAccessible(op.type)) {
                
                AggregateTypeInfo aggInfo = TypeInfo.getBase(op.type).as();
                
                // first check and see if this is a field member of the 
                // aggregate
                FieldPath path = aggInfo.getFieldPath(field.type.name);
                if(path != null && path.hasPath()) {
                    FieldInfo aggField = path.getTargetField();
                    if(aggField != null) {
                        findFromSrcPos(aggField.attributes.srcPos);                           
                    }
                    
                }
                
                // Now check if this is a member function
                Operand fieldOp = field.getResolvedType();
                if(fieldOp == null) {
                    return;
                }
                
                if(TypeInfo.isFunc(fieldOp.type)) {
                    findFromTypeInfo(fieldOp.type);
                }                    
            }
        }
        
        field.visit(this);
        
    }

    @Override
    public void visit(GetExpr expr) {
        if(isNodeAtPos(expr)) {
            findFromExpr(expr);
        }
        
        expr.object.visit(this);
        visitField(expr.object, expr.field);
    }

    @Override
    public void visit(SetExpr expr) {
        if(isNodeAtPos(expr)) {
            findFromExpr(expr);
        }
        
        expr.object.visit(this);
        visitField(expr.object, expr.field);        
        expr.value.visit(this);
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
        if(isNodeAtPos(expr)) {
            findFromExpr(expr);
        }
        
        if(isNodeAtPos(expr.type)) {
            findFromTypeSpec(expr.type);
        }
        
        for(Expr e : expr.values) {
            e.visit(this);
        }
    }

    @Override
    public void visit(ArrayDesignationExpr expr) {
        if(isNodeAtPos(expr)) {
            findFromExpr(expr);
        }
        
        expr.value.visit(this);
        expr.index.visit(this);        
    }

    @Override
    public void visit(SubscriptGetExpr expr) {
        if(isNodeAtPos(expr)) {
            findFromExpr(expr);
        }
        expr.object.visit(this);
        expr.index.visit(this);
    }

    @Override
    public void visit(SubscriptSetExpr expr) {
        if(isNodeAtPos(expr)) {
            findFromExpr(expr);
        }
        expr.object.visit(this);
        expr.index.visit(this);
        expr.value.visit(this);
    }

}
