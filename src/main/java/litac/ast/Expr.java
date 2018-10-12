/*
 * see license.txt
 */
package litac.ast;

import java.util.List;

import litac.ast.TypeInfo.*;
import litac.ast.TypeInfo.TypeKind;
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
        this.resolvedTo = type;
    }
    
    public boolean isResolved() {
        return this.resolvedTo != null;
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
    
    public static class InitExpr extends Expr {
        public TypeInfo type;
        public List<Expr> arguments;
        
        public InitExpr(TypeInfo type, List<Expr> arguments) {
            this.type = type;
            this.arguments = arguments;
            
            resolveTo(type);
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
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

    }
    
    public static class BooleanExpr extends ConstExpr {

        public boolean bool;
        
        /**
         * 
         */
        public BooleanExpr(boolean bool) {
            super(new PrimitiveTypeInfo("bool", TypeKind.bool));
            this.bool = bool;
            
            resolveTo(this.type);
        }
        
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }

    }
    
    public static class DotExpr extends Expr {

        public Expr field;
        
        public DotExpr(Expr field) {
            this.field = becomeParentOf(field);
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }

    }
    
    public static class FuncCallExpr extends Expr {

        public Expr object;
        public List<Expr> arguments;
        
        public FuncCallExpr(Expr object, List<Expr> arguments) {
            this.object = becomeParentOf(object);
            this.arguments = becomeParentOf(arguments);
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
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
    }
        
    public static class NullExpr extends ConstExpr {
        public NullExpr() {
            super(new NullTypeInfo());
            
            resolveTo(this.type);
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
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

    }

    public static class IdentifierExpr extends Expr {

        public String variable;
        public TypeInfo type;
        
        public IdentifierExpr(String variable, TypeInfo type) {
            this.variable = variable;
            this.type = type;
            resolveTo(type);
        }

        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }

    }


}
