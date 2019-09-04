/*
 * see license.txt
 */
package litac.ast;

import java.math.BigDecimal;
import java.util.*;

import litac.ast.TypeSpec.NameTypeSpec;
import litac.checker.TypeResolver.Operand;
import litac.checker.TypeInfo;
import litac.checker.TypeInfo.*;
import litac.compiler.Symbol;
import litac.parser.*;
import litac.parser.tokens.*;

/**
 * @author Tony
 *
 */
public abstract class Expr extends Stmt {
    
    public static enum ExprKind {        
        CAST,
        SIZE_OF,
        TYPE_OF,
        INIT_ARG,
        INIT,
        TERNARY,
        BINARY,
        ARRAY_INIT,
        ARRAY_DESIGNATION,
        SUBSCRIPT_GET,
        SUBSCRIPT_SET,
        BOOLEAN,
        FUNC_CALL,
        GET,
        SET,
        GROUP,
        NULL,
        NUMBER,
        STRING,
        CHAR,
        UNARY,
        IDENTIFER,
        FUNC_IDENTIFIER,
        TYPE_IDENTIFIER,
    }
    
    public static TypeKind fromTokenType(Token token) {
        switch(token.getType()) {
            case I8: {
                return TypeKind.i8;                
            }
            case U8: {
                return TypeKind.u8;
            }
            case I16: {
                return TypeKind.i16;
            }
            case U16: {
                return TypeKind.u16;
            }
            case I32: {
                return TypeKind.i32;
            }
            case U32: {
                return TypeKind.u32;
            }
            case I64: {
                return TypeKind.i64;
            }
            case U64: {
                return TypeKind.u64;
            }
            case F32: {
                return TypeKind.f32;
            }
            case F64: {
                return TypeKind.f64;
            }
            default: {
                throw new ParseException(ErrorCode.INVALID_NUMBER, token);
            }
        }
    }
    
    private Operand resolvedTo;
    protected ExprKind kind;
    
    Expr(ExprKind kind) {
        this.kind = kind;
    }
    
    
    public ExprKind getKind() {
        return kind;
    }
    
    public Operand getResolvedType() {
        return resolvedTo;
    }
    
    public void resolveTo(Operand type) {
        this.resolvedTo = type;
    }
    
    public boolean isResolved() {
        return this.resolvedTo != null;
    }
    
    public void unresolve() {
        this.resolvedTo = null;
    }
    
    @SuppressWarnings("unchecked")
    public <T extends Expr> T as() {
        return (T) this;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T extends Node> T copy() {
        Expr expr = super.copy();
        expr.resolvedTo = this.resolvedTo; 
        return (T)expr;
    }
    
    
    /**
     * Used for constant expressions
     */
    public static abstract class ConstExpr extends Expr {                
        protected ConstExpr(ExprKind kind, Operand op) {
            super(kind);
            resolveTo(op);
        }
    }
    
    public static class CastExpr extends Expr {
        public TypeSpec castTo;
        public Expr expr;
        
        public CastExpr(TypeSpec castTo, Expr expr) {
            super(ExprKind.CAST);
            this.expr = becomeParentOf(expr);
            this.castTo = castTo;            
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new CastExpr(TypeSpec.copy(this.castTo), this.expr.copy());
        }
    }
    
    public static class SizeOfExpr extends Expr {
        public TypeSpec type;
        public Expr expr;
        
        public SizeOfExpr(Expr expr) {
            super(ExprKind.SIZE_OF);
            this.expr = becomeParentOf(expr);
            resolveTo(Operand.op(TypeInfo.U64_TYPE));
        }
        
        public SizeOfExpr(TypeSpec type) {
            super(ExprKind.SIZE_OF);
            this.type = type;
            resolveTo(Operand.op(TypeInfo.U64_TYPE));
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {
            if(this.expr != null) {
                return new SizeOfExpr(this.expr.copy());
            }
            return new SizeOfExpr(TypeSpec.copy(this.type));
        }
    }
    
    public static class TypeOfExpr extends Expr {
        public Expr expr;
        public TypeSpec type;
        
        public TypeOfExpr(Expr expr) {
            super(ExprKind.TYPE_OF);
            this.expr = becomeParentOf(expr);
            resolveTo(Operand.op(TypeInfo.I64_TYPE));
        }
        
        public TypeOfExpr(TypeSpec type) {
            super(ExprKind.TYPE_OF);
            this.type = type;
            resolveTo(Operand.op(TypeInfo.I64_TYPE));
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() { 
            if(this.expr != null) {
                return new TypeOfExpr(this.expr.copy());
            }
            return new TypeOfExpr(TypeSpec.copy(this.type));
        }
    }
    
    public static class InitArgExpr extends Expr {
        public String fieldName;
        public int argPosition;
        public Expr value;
        
        public InitArgExpr(String fieldName, int argPosition, Expr value) {
            super(ExprKind.INIT_ARG);
            this.fieldName = fieldName;
            this.argPosition = argPosition;
            this.value = becomeParentOf(value);
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new InitArgExpr(this.fieldName, this.argPosition, this.value.copy());
        }
    }
    
    public static abstract class GenericExpr extends Expr {
        public List<TypeSpec> genericArgs;
        
        public GenericExpr(ExprKind kind) {
            super(kind);
            this.genericArgs = Collections.emptyList();
        }
        
        protected void setGenericArgs(List<TypeSpec> genArgs) {
            this.genericArgs = genArgs;
        }
        
        public void addGenericArg(TypeSpec arg) {
            this.genericArgs.add(arg);
        }
        
        protected GenericExpr copyArgs(GenericExpr copy) {
            copy.genericArgs = TypeSpec.copy(this.genericArgs);
            return copy;
        }
    }
    
    public static class InitExpr extends GenericExpr {
        public NameTypeSpec type;
        public List<InitArgExpr> arguments;
        
        public InitExpr(NameTypeSpec type, List<InitArgExpr> arguments) {
            super(ExprKind.INIT);
            this.type = type;
            this.arguments = becomeParentOf(arguments);            
        }
                
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            NameTypeSpec nameSpec = (this.type != null) ? TypeSpec.copy(this.type).as() : null;
            return copyArgs(new InitExpr(nameSpec, copy(this.arguments)));
        }
    }
    
    public static class TernaryExpr extends Expr {
        public Expr cond;
        public Expr then;
        public Expr other;

        public TernaryExpr(Expr cond, Expr then, Expr other) {
            super(ExprKind.TERNARY);
            this.cond = becomeParentOf(cond);
            this.then = becomeParentOf(then);
            this.other = becomeParentOf(other);
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {
            return new TernaryExpr(copy(this.cond), copy(this.then), copy(this.other));
        }
    }
    
    public static class BinaryExpr extends Expr {
        public Expr left;
        public TokenType operator;
        public Expr right;

        public BinaryExpr(Expr left, TokenType operator, Expr right) {
            super(ExprKind.BINARY);
            this.left = becomeParentOf(left);
            this.operator = operator;
            this.right = becomeParentOf(right);
        }

        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }

        @Override
        protected Node doCopy() {            
            return new BinaryExpr(this.left.copy(), this.operator, this.right.copy());
        }
    }
    
    public static class ArrayInitExpr extends Expr {
        public TypeSpec type;
        public List<Expr> values;
        
        public ArrayInitExpr(TypeSpec type, List<Expr> values) {
            super(ExprKind.ARRAY_INIT);
            this.type = type;
            this.values = becomeParentOf(values);
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new ArrayInitExpr(TypeSpec.copy(this.type), copy(this.values));
        }
    }
    
    public static class ArrayDesignationExpr extends Expr {
        public Expr index;
        public Expr value;
        
        public ArrayDesignationExpr(Expr index, Expr value) {
            super(ExprKind.ARRAY_DESIGNATION);
            this.index = becomeParentOf(index);
            this.value = becomeParentOf(value);
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new ArrayDesignationExpr(this.index.copy(), this.value.copy());
        }
    }
    
    public static class SubscriptGetExpr extends Expr {
        public Expr object;
        public Expr index;
        
        public SubscriptGetExpr(Expr object, Expr index) {
            super(ExprKind.SUBSCRIPT_GET);
            this.object = becomeParentOf(object);
            this.index = becomeParentOf(index);
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new SubscriptGetExpr(object.copy(), index.copy());
        }
    }
    
    public static class SubscriptSetExpr extends Expr {
        public Expr object;
        public Expr index;
        public TokenType operator;
        public Expr value;
        
        public SubscriptSetExpr(Expr object, Expr index, TokenType operator, Expr value) {
            super(ExprKind.SUBSCRIPT_SET);
            this.object = becomeParentOf(object);
            this.index = becomeParentOf(index);
            this.operator = operator;
            this.value = becomeParentOf(value);
            
            resolveTo(value.getResolvedType());
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new SubscriptSetExpr(object.copy(), index.copy(), this.operator, this.value.copy());
        }
    }
    
    public static class BooleanExpr extends ConstExpr {
        public boolean bool;
        
        public BooleanExpr(boolean bool) {
            super(ExprKind.BOOLEAN, Operand.opConst(TypeInfo.BOOL_TYPE, bool));
            this.bool = bool;
        }
        
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new BooleanExpr(this.bool);
        }

    }
    
    public static class FuncCallExpr extends GenericExpr {
        public Expr object;
        public List<Expr> arguments;
        
        public FuncCallExpr(Expr object, List<Expr> arguments, List<TypeSpec> genericArgs) {
            super(ExprKind.FUNC_CALL);
            this.object = becomeParentOf(object);
            this.arguments = becomeParentOf(arguments);
            this.genericArgs = genericArgs;
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }

        @Override
        protected Node doCopy() {            
            return new FuncCallExpr(object.copy(), copy(this.arguments), TypeSpec.copy(genericArgs));
        }
    }
    
    public static class GetExpr extends Expr {
        public Expr object;
        public IdentifierExpr field;
        public boolean isMethodCall;
        
        public GetExpr(Expr object, IdentifierExpr field) {
            super(ExprKind.GET);
            this.object = becomeParentOf(object);
            this.field = becomeParentOf(field);            
        }
        
        public void setField(IdentifierExpr newField) {
            this.field = becomeParentOf(newField);
        }

        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new GetExpr(object.copy(), field.copy());
        }
    }
    
    public static class SetExpr extends Expr {
        public Expr object;
        public IdentifierExpr field;
        
        public TokenType operator;
        public Expr value;
        
        public SetExpr(Expr object, IdentifierExpr field, TokenType operator, Expr value) {
            super(ExprKind.SET);
            this.object = becomeParentOf(object);
            this.field = becomeParentOf(field);
            
            this.operator = operator;
            this.value = becomeParentOf(value);
        }

        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new SetExpr(object.copy(), this.field.copy(), this.operator, this.value.copy());
        }
    }
    
    public static class GroupExpr extends Expr {
        public Expr expr;
        
        public GroupExpr(Expr expr) {
            super(ExprKind.GROUP);
            this.expr = becomeParentOf(expr);
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new GroupExpr(this.expr.copy());
        }
    }
        
    public static class NullExpr extends ConstExpr {
        public NullExpr() {
            super(ExprKind.NULL, Operand.opConst(TypeInfo.NULL_TYPE, null));
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new NullExpr();
        }
    }
    
    public static class NumberExpr extends ConstExpr {
        public String number;
                
        public NumberExpr(NumberToken token) {
            this(token.getTypeInfo(), token.getText());
        }
        
        public NumberExpr(TypeInfo type, int number) {
            this(type, String.valueOf(number));
        }
        
        public NumberExpr(TypeInfo type, String number) {
            super(ExprKind.NUMBER, Operand.opConst(type, new BigDecimal(number)));        
            this.number = number;            
        }
        
        public int asInt() {
            return Integer.parseInt(this.number);
        }
        

        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new NumberExpr(TypeInfo.copy(getResolvedType().type), this.number);
        }
    }

    public static class StringExpr extends ConstExpr {
        public String string;
        
        public StringExpr(String string) {
            super(ExprKind.STRING, Operand.opConst(new StrTypeInfo(string), string));
            this.string = string;            
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        public String toString() {
            return this.string;
        }
        
        @Override
        protected Node doCopy() {            
            return new StringExpr(this.string);
        }
    }
    
    public static class CharExpr extends ConstExpr {
        public String character;
        
        public CharExpr(String character) {
            super(ExprKind.CHAR, Operand.opConst(TypeInfo.CHAR_TYPE, character));
            this.character = character;            
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        public String toString() {
            return this.character;
        }
        
        @Override
        protected Node doCopy() {            
            return new CharExpr(this.character);
        }
    }
    
    public static class UnaryExpr extends Expr {
        public TokenType operator;
        public Expr expr;
        
        public UnaryExpr(TokenType operator, Expr expr) {
            super(ExprKind.UNARY);
            this.operator = operator;
            this.expr = becomeParentOf(expr);
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }

        @Override
        protected Node doCopy() {            
            return new UnaryExpr(this.operator, this.expr.copy());
        }
    }

    public static class IdentifierExpr extends GenericExpr {
        public NameTypeSpec type;
        public Symbol sym;
        
        public IdentifierExpr(NameTypeSpec typeSpec) {
            super(ExprKind.IDENTIFER);
            this.type = typeSpec;
            this.genericArgs = typeSpec.genericArgs;
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }

        @Override
        protected Node doCopy() {            
            IdentifierExpr idExpr = new IdentifierExpr(TypeSpec.copy(this.type).as());    
            idExpr.sym = sym;
            return copyArgs(idExpr);
        }
    }

    public static class FuncIdentifierExpr extends IdentifierExpr {

        public FuncIdentifierExpr(NameTypeSpec typeSpec) {
            super(typeSpec);
            this.kind = ExprKind.FUNC_IDENTIFIER;
        }

        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }

        @Override
        protected Node doCopy() {            
            FuncIdentifierExpr idExpr = new FuncIdentifierExpr(TypeSpec.copy(this.type).as());
            idExpr.sym = sym;
            return idExpr;
        }
    }
    
    public static class TypeIdentifierExpr extends IdentifierExpr {

        public TypeIdentifierExpr(NameTypeSpec typeSpec) {
            super(typeSpec);
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }

        @Override
        protected Node doCopy() {            
            TypeIdentifierExpr idExpr = new TypeIdentifierExpr(TypeSpec.copy(this.type).as());
            idExpr.sym = sym;
            return idExpr;
        }
    }

}
