/*
 * see license.txt
 */
package litac.ast;

import java.util.*;

import litac.checker.Symbol;
import litac.checker.TypeInfo;
import litac.checker.TypeInfo.*;
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
        expr.resolvedTo = this.resolvedTo; 
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
            if(expr instanceof IdentifierExpr && !(expr instanceof TypeIdentifierExpr)) {
                IdentifierExpr idExpr = (IdentifierExpr)expr;
                expr = new TypeIdentifierExpr(idExpr.variable, idExpr.type);
                expr.setLineNumber(idExpr.getLineNumber());
                expr.setSourceLine(idExpr.getSourceLine());
                expr.setSourceFile(idExpr.getSourceFile());
            }
            
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
    
    public static class TypeOfExpr extends Expr {
        public Expr expr;
        
        public TypeOfExpr(Expr expr) {
            if(expr instanceof IdentifierExpr && !(expr instanceof TypeIdentifierExpr)) {
                IdentifierExpr idExpr = (IdentifierExpr)expr;
                expr = new TypeIdentifierExpr(idExpr.variable, idExpr.type);
                expr.setLineNumber(idExpr.getLineNumber());
                expr.setSourceLine(idExpr.getSourceLine());
                expr.setSourceFile(idExpr.getSourceFile());
            }
            
            this.expr = becomeParentOf(expr);
            resolveTo(TypeInfo.I64_TYPE);
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new TypeOfExpr(this.expr.copy());
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
        public List<TypeInfo> genericArgs;
        
        public InitExpr(TypeInfo type, List<InitArgExpr> arguments) {
            this.type = type;
            this.arguments = becomeParentOf(arguments);
            
            resolveTo(type);
        }
        
        @Override
        public void resolveTo(TypeInfo type) {        
            super.resolveTo(type);
            this.type = type;
            if(type instanceof IdentifierTypeInfo) {
                this.genericArgs = ((IdentifierTypeInfo)type).genericArgs;
            }
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }
        
        @Override
        protected Node doCopy() {            
            return new InitExpr(TypeInfo.copy(this.type), copy(this.arguments));
        }
    }
    
    public static class TernaryExpr extends Expr {
        public Expr cond;
        public Expr then;
        public Expr other;

        public TernaryExpr(Expr cond, Expr then, Expr other) {
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
    
    public static class ArrayDesignationExpr extends Expr {
        public Expr index;
        public Expr value;
        
        public ArrayDesignationExpr(Expr index, Expr value) {
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
        public IdentifierExpr field;
        public boolean isMethodCall;
        
        public GetExpr(Expr object, IdentifierExpr field) {
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
        public String number;
        
        public NumberExpr(NumberToken token) {
            this(token.getTypeInfo(), token.getText());
        }
        
        public NumberExpr(TypeInfo type, int number) {
            this(type, String.valueOf(number));
        }
        
        public NumberExpr(TypeInfo type, String number) {
            super(type);
            this.number = number;
            
            resolveTo(this.type);
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
            return new NumberExpr(type.copy(), this.number);
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
    
    public static class CharExpr extends ConstExpr {
        public String character;
        
        public CharExpr(String character) {
            super(TypeInfo.CHAR_TYPE);
            this.character = character;
            
            resolveTo(this.type);
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
            idExpr.sym = sym;
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
            idExpr.sym = this.sym;
            return idExpr;
        }
    }
    
    public static class TypeIdentifierExpr extends IdentifierExpr {
        
        public TypeIdentifierExpr(String variable, TypeInfo type) {
            super(variable, type);
        }

        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }

        @Override
        protected Node doCopy() {            
            TypeIdentifierExpr idExpr = new TypeIdentifierExpr(this.variable, this.type.copy());
            idExpr.sym = this.sym;
            return idExpr;
        }
    }

}
