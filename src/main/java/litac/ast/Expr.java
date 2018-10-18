/*
 * see license.txt
 */
package litac.ast;

import java.util.ArrayList;
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
        return this.resolvedTo != null && this.resolvedTo.isResolved();
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
    
    public static class ArrayInitExpr extends Expr {
        public List<Expr> dimensions;
        public List<Expr> values;
        
        public ArrayInitExpr(TypeInfo arrayOf, List<Expr> dimensions, List<Expr> values) {
            this.dimensions = becomeParentOf(dimensions);
            this.values = becomeParentOf(values);
            
            List<Integer> dims = new ArrayList<Integer>(dimensions.size());
            for(Expr d : dimensions) {
                if(d instanceof NumberExpr) {
                    NumberExpr n = (NumberExpr)d;
                    dims.add((Integer)n.number.getValue());
                }
                else {
                    // must infer size from values
                    dims.add(-1);
                }
            }
            
            resolveTo(new ArrayTypeInfo(arrayOf, dims));
        }
        
        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
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
    }
    
    public static class BooleanExpr extends ConstExpr {

        public boolean bool;
        
        /**
         * 
         */
        public BooleanExpr(boolean bool) {
            super(TypeInfo.BOOL_TYPE);
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
            super(TypeInfo.NULL_TYPE);
            
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

    public static class FuncIdentifierExpr extends IdentifierExpr {

        public FuncIdentifierExpr(String variable, TypeInfo type) {
            super(variable, type);
        }

        @Override
        public void visit(NodeVisitor v) {
            v.visit(this);
        }

    }
    

}
