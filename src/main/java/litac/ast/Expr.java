/*
 * see license.txt
 */
package litac.ast;

import java.util.List;

import litac.checker.Symbol;
import litac.checker.TypeInfo;
import litac.checker.TypeInfo.StrTypeInfo;
import litac.checker.TypeInfo.TypeKind;
import litac.parser.ErrorCode;
import litac.parser.ParseException;
import litac.parser.tokens.NumberToken;
import litac.parser.tokens.Token;
import litac.parser.tokens.TokenType;

/**
 * @author Tony
 *
 */
public abstract class Expr extends Stmt {
    
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
            case I128: {
                return TypeKind.i128;
            }
            case U128: {
                return TypeKind.u128;
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
    
    private TypeInfo resolvedTo;
    
    public TypeInfo getResolvedType() {
        return resolvedTo;
    }
    
    public void resolveTo(TypeInfo type) {
        if(type != null) {
            this.resolvedTo = type.isResolved() ? type.getResolvedType() : type;
        }
        else {
            this.resolvedTo = null;
        }
    }
    
    public boolean isResolved() {
        return this.resolvedTo != null && this.resolvedTo.isResolved();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T extends Node> T copy() {
        Expr expr = super.copy();
        //expr.resolvedTo = TypeInfo.copy(expr.resolvedTo);
        return (T)expr;
    }
    
    
    /**
     * Used for constant expressions
     */
    public static abstract class ConstExpr extends Expr {
        public TypeInfo type;
        
        protected ConstExpr(TypeInfo type) {
            this.type = type;
        }
    }
    
    public static class CastExpr extends Expr {
        public TypeInfo castTo;
        public Expr expr;
        
        public CastExpr(TypeInfo castTo, Expr expr) {
            this.expr = becomeParentOf(expr);
            this.castTo = castTo;
            
            resolveTo(castTo);
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new CastExpr(this.castTo.copy(), this.expr.copy());
        }
    }
    
    public static class SizeOfExpr extends Expr {
        public Expr expr;
        
        public SizeOfExpr(Expr expr) {
            this.expr = becomeParentOf(expr);
            resolveTo(TypeInfo.U64_TYPE);
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new SizeOfExpr(this.expr.copy());
        }
    }
    
    public static class InitArgExpr extends Expr {
        public String fieldName;
        public int argPosition;
        public Expr value;
        
        public InitArgExpr(String fieldName, int argPosition, Expr value) {
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
    
    public static class InitExpr extends Expr {
        public TypeInfo type;
        public List<InitArgExpr> arguments;
        
        public InitExpr(TypeInfo type, List<InitArgExpr> arguments) {
            this.type = type;
            this.arguments = becomeParentOf(arguments);
            
            resolveTo(type);
        }
        
        @Override
        public void resolveTo(TypeInfo type) {        
            super.resolveTo(type);
            this.type = type;
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new InitExpr(this.type.copy(), copy(this.arguments));
        }
    }
    
    public static class BinaryExpr extends Expr {

        public Expr left;
        public TokenType operator;
        public Expr right;
        
        /**
         * 
         */
        public BinaryExpr(Expr left, TokenType operator, Expr right) {
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
        public List<Expr> values;
        
        public ArrayInitExpr(TypeInfo array, List<Expr> values) {
            this.values = becomeParentOf(values);

            resolveTo(array);
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new ArrayInitExpr(getResolvedType(), copy(this.values));
        }
    }
    
    public static class SubscriptGetExpr extends Expr {
        public Expr object;
        public Expr index;
        
        public SubscriptGetExpr(Expr object, Expr index) {
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
            super(TypeInfo.BOOL_TYPE);
            this.bool = bool;
            
            resolveTo(this.type);
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
    
    public static class FuncCallExpr extends Expr {
        public Expr object;
        public List<Expr> arguments;
        public List<TypeInfo> genericArgs;
        
        public FuncCallExpr(Expr object, List<Expr> arguments, List<TypeInfo> genericArgs) {
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
            return new FuncCallExpr(object.copy(), copy(this.arguments), TypeInfo.copy(genericArgs));
        }
    }
    
    public static class GetExpr extends Expr {
        public Expr object;
        public TypeInfo field;
        
        public GetExpr(Expr object, TypeInfo field) {
            this.object = becomeParentOf(object);
            this.field = field;
            
            resolveTo(this.field);
        }

        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new GetExpr(object.copy(), this.field.copy());
        }
    }
    
    public static class SetExpr extends Expr {
        public Expr object;
        public TypeInfo field;
        
        public TokenType operator;
        public Expr value;
        
        public SetExpr(Expr object, TypeInfo field, TokenType operator, Expr value) {
            this.object = becomeParentOf(object);
            this.field = field;
            
            this.operator = operator;
            this.value = becomeParentOf(value);
            
            resolveTo(this.field);
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
            super(TypeInfo.NULL_TYPE);
            
            resolveTo(this.type);
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
        public NumberToken number;
        
        public NumberExpr(NumberToken number) {
            super(number.getTypeInfo());
            this.number = number;
            
            resolveTo(this.type);
        }
        

        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new NumberExpr(this.number);
        }
    }

    public static class StringExpr extends ConstExpr {
        public String string;
        
        public StringExpr(String string) {
            super(new StrTypeInfo(string));
            this.string = string;
            
            resolveTo(this.type);
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
    
    public static class UnaryExpr extends Expr {
        public TokenType operator;
        public Expr expr;
        
        public UnaryExpr(TokenType operator, Expr expr) {
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

    public static class IdentifierExpr extends Expr {
        public String variable;
        public TypeInfo type;
        public Decl declType;
        public Symbol sym;
        
        public IdentifierExpr(String variable, TypeInfo type) {
            this.variable = variable;
            this.type = type;
            resolveTo(type);
        }

        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }

        @Override
        protected Node doCopy() {            
            IdentifierExpr idExpr = new IdentifierExpr(this.variable, this.type.copy());
            if(this.declType != null) {
                idExpr.declType = this.declType.copy();
            }
            
            return idExpr;
        }
    }

    public static class FuncIdentifierExpr extends IdentifierExpr {

        public FuncIdentifierExpr(String variable, TypeInfo type) {
            super(variable, type);
        }

        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }

        @Override
        protected Node doCopy() {            
            FuncIdentifierExpr idExpr = new FuncIdentifierExpr(this.variable, this.type.copy());
            if(this.declType != null) {
                idExpr.declType = this.declType.copy();
            }
            
            return idExpr;
        }
    }
    

}
