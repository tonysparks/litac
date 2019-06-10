/*
 * see license.txt
 */
package litac.checker;

import java.util.*;

import litac.ast.Decl;
import litac.ast.Decl.ConstDecl;
import litac.ast.Decl.EnumDecl;
import litac.ast.Expr;
import litac.ast.Expr.*;
import litac.checker.TypeInfo.*;
import litac.parser.tokens.TokenType;

/**
 * Builds an array of TypeInfo objects so that types can
 * be reflected
 * 
 * @author Tony
 *
 */
public class Reflection {
    
    public static void createTypeInfos(List<Decl> declarations, Module main) {
        TypeInfo typeInfo = main.getType("TypeInfo");
        ArrayTypeInfo arrayInfo = new ArrayTypeInfo(typeInfo, declarations.size(), null);
        
        List<Expr> infos = addTypeInfos(declarations, main);
        ArrayInitExpr initExpr = new ArrayInitExpr(arrayInfo, infos);
        
        Symbol sym = main.currentScope().getSymbol("typeInfos");
        if(sym.decl instanceof ConstDecl) {
            ConstDecl typeInfoArray = (ConstDecl)sym.decl;
            typeInfoArray.attributes.notes.removeIf(n -> n.note.name.equals("foreign"));
            typeInfoArray.expr = initExpr;
            sym.removeForeign();
        }
    }

    private static List<Expr> addTypeInfos(List<Decl> declarations, Module main) {
        List<Expr> exprs = new ArrayList<>();
        for(Decl d : declarations) {
            exprs.add(toExpr(d, main));
        }
        return exprs;
    }
    
    private static Expr toExpr(Decl d, Module main) {
        StructTypeInfo typeInfo = main.getType("TypeInfo").as();
        EnumTypeInfo typeKind = main.getType("TypeKind").as();
        
        UnionTypeInfo anonInfo = typeInfo.fieldInfos.stream()
                .filter(f -> f.type.isAnonymous())
                .findFirst()
                .get()
                .type.as();
        
        int argPosition = 0;
        List<InitArgExpr> args = new ArrayList<>();
        args.add(new InitArgExpr("name", argPosition++, new StringExpr(d.name)));
        switch(d.kind) {
            case ENUM: {
                TypeInfo kindName = new IdentifierTypeInfo("Enum", Collections.emptyList());
                args.add(new InitArgExpr("kind", argPosition++, new GetExpr(new IdentifierExpr("TypeKind", typeKind), kindName)));
                args.add(new InitArgExpr("enumType", argPosition++, newEnum((EnumDecl)d, main, anonInfo.getField("enumType").type.as())));
                //InitArgExpr arg = new InitArgExpr(fieldName, argPosition, value)
                break;
            }
            case FUNC:
            case STRUCT:
            case UNION:
                break;
            case CONST:
            case PARAM:
            case TYPEDEF:
            case VAR:
            default:
                break;
        
        }
        
        return new InitExpr(typeInfo, args);
    }
    
    private static Expr newEnum(EnumDecl d, Module main, StructTypeInfo enumType) {
        int argPosition = 0;
        List<InitArgExpr> args = new ArrayList<>();
        args.add(new InitArgExpr("name", argPosition++, new StringExpr(d.name)));
        args.add(new InitArgExpr("numOfFields", argPosition++, new NumberExpr(TypeInfo.I32_TYPE, String.valueOf(d.fields.size()))));
        
        StructTypeInfo enumFieldInfo = main.getType("EnumFieldInfo").as();
        List<Expr> arrayValues = new ArrayList<>();

        int i = 0;
        for(EnumFieldInfo field : d.fields) {
            List<InitArgExpr> arrayArg = new ArrayList<>();
            arrayArg.add(new InitArgExpr("name", 0, new StringExpr(field.name)));
            
            Expr value = (field.value != null) 
                    ? field.value : new NumberExpr(TypeInfo.I32_TYPE, String.valueOf(i));
            arrayArg.add(new InitArgExpr("value", 1, value)); // TODO: get REAL value
            arrayValues.add(new UnaryExpr(TokenType.BAND, new InitExpr(enumFieldInfo, arrayArg)));
            
            i++;
        }
        
        args.add(new InitArgExpr("fields", argPosition++, new ArrayInitExpr(enumFieldInfo, arrayValues)));
        
        return new InitExpr(enumType, args);
    }
}
