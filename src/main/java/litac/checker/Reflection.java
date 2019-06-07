/*
 * see license.txt
 */
package litac.checker;

import java.util.*;

import litac.ast.Decl;
import litac.ast.Decl.ConstDecl;
import litac.ast.Expr;
import litac.ast.Expr.*;
import litac.checker.TypeInfo.*;

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
        EnumTypeInfo typeKind = main.getType("TypeKind").as();
        ArrayTypeInfo arrayInfo = new ArrayTypeInfo(typeInfo, declarations.size(), null);
        
        List<Expr> infos = addTypeInfos(declarations, typeInfo, typeKind);
        ArrayInitExpr initExpr = new ArrayInitExpr(arrayInfo, infos);
//        ConstDecl typeInfoArray = new ConstDecl("typeInfos", arrayInfo, initExpr, 0);
//        typeInfoArray.attributes.isGlobal = true;
//        typeInfoArray.attributes.isPublic = true;
        
        Symbol sym = main.currentScope().getSymbol("typeInfos");
        if(sym.decl instanceof ConstDecl) {
            ConstDecl typeInfoArray = (ConstDecl)sym.decl;
            typeInfoArray.attributes.notes.removeIf(n -> n.note.name.equals("foreign"));
            typeInfoArray.expr = initExpr;
            sym.removeForeign();
        }
//        main.currentScope().addSymbol(main, typeInfoArray, "typeInfos", arrayInfo, true);
//        
//        return typeInfoArray;
    }

    private static List<Expr> addTypeInfos(List<Decl> declarations, TypeInfo typeInfo, EnumTypeInfo typeKind) {
        List<Expr> exprs = new ArrayList<>();
        for(Decl d : declarations) {
            exprs.add(toExpr(d, typeInfo, typeKind));
        }
        return exprs;
    }
    
    private static Expr toExpr(Decl d, TypeInfo typeInfo, EnumTypeInfo typeKind) {
        int argPosition = 0;
        List<InitArgExpr> args = new ArrayList<>();
        args.add(new InitArgExpr("name", argPosition++, new StringExpr(d.name)));
        switch(d.kind) {
            case ENUM: {
                TypeInfo kindName = new IdentifierTypeInfo(typeKind.fields.get(0).name, Collections.emptyList());
                args.add(new InitArgExpr("kind", argPosition++, new GetExpr(new IdentifierExpr("TypeKind", typeKind), kindName)));
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
}
