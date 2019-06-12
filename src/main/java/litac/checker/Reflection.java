/*
 * see license.txt
 */
package litac.checker;

import java.util.*;

import litac.ast.Decl;
import litac.ast.Decl.*;
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
    
    public static List<Decl> createTypeInfos(List<Decl> declarations, Program program) {
        Module typeModule = program.getModule("type");
        TypeInfo typeInfo = typeModule.getType("TypeInfo");
        
        List<Symbol> symbols = program.getSymbols();
        long numOfTypeInfos = symbols.stream()
                                     .mapToLong(s -> s.type.getTypeId())
                                     .max()
                                     .orElse(0);
        
        // typeid of the max, must fit inside the array
        if(numOfTypeInfos > 0) {
            numOfTypeInfos++;
        }
        
        
        ArrayTypeInfo arrayInfo = new ArrayTypeInfo(typeInfo, numOfTypeInfos, null);
        
        List<Expr> infos = addTypeInfos(symbols, typeModule);
        ArrayInitExpr initExpr = new ArrayInitExpr(arrayInfo, infos);

        Decl typeTable = new ConstDecl("typeTable", new ArrayTypeInfo(new PtrTypeInfo(typeInfo), numOfTypeInfos, null), initExpr, 0);
        typeModule.currentScope().addSymbol(typeModule, typeTable, "typeTable", typeTable.type, true);
        
        Symbol sym = typeModule.currentScope().getSymbol("typeInfos");
        if(sym.decl instanceof ConstDecl) {
            ConstDecl typeInfoArray = (ConstDecl)sym.decl;
            typeInfoArray.attributes.notes.removeIf(n -> n.note.name.equals("foreign"));
            
            IdentifierExpr idExpr = new IdentifierExpr("typeTable", typeTable.sym.type);
            idExpr.sym = typeTable.sym;
            typeInfoArray.expr = new CastExpr(new PtrTypeInfo(new PtrTypeInfo(typeInfo)), idExpr);
            
            sym.removeForeign();
        }
        
        Symbol size = typeModule.currentScope().getSymbol("numOfTypeInfos");
        if(size.decl instanceof ConstDecl) {
            ConstDecl sizeValue = (ConstDecl)size.decl;
            sizeValue.attributes.notes.removeIf(n -> n.note.name.equals("foreign"));
            sizeValue.expr = new NumberExpr(TypeInfo.I64_TYPE, String.valueOf(numOfTypeInfos));
            size.removeForeign();
        }
                
        return Arrays.asList(typeTable);
    }

    private static List<Expr> addTypeInfos(List<Symbol> symbols, Module main) {
        List<Expr> exprs = new ArrayList<>();
        for(Symbol s : symbols) {
            switch(s.decl.kind) {
                case FUNC:
                case STRUCT:
                case UNION:
                //case TYPEDEF:
                case ENUM:
                    exprs.add(new ArrayDesignationExpr(new NumberExpr(TypeInfo.I64_TYPE, String.valueOf(s.type.getTypeId())), toExpr(s.decl, main)));
                    break;
                default:
            }
            
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
        args.add(new InitArgExpr("id", argPosition++, new NumberExpr(TypeInfo.I64_TYPE, String.valueOf(d.type.getTypeId()))));
        
        switch(d.kind) {
            case ENUM: {
                TypeInfo kindName = new IdentifierTypeInfo("Enum", Collections.emptyList());
                args.add(new InitArgExpr("kind", argPosition++, new GetExpr(new IdentifierExpr("TypeKind", typeKind), kindName)));
                args.add(new InitArgExpr("enumType", argPosition++, newEnum((EnumDecl)d, main, anonInfo.getField("enumType").type.as())));
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
        
        return new UnaryExpr(TokenType.BAND, new InitExpr(typeInfo, args));
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
            //arrayValues.add(new UnaryExpr(TokenType.BAND, new InitExpr(enumFieldInfo, arrayArg)));
            arrayValues.add(new InitExpr(enumFieldInfo, arrayArg));
            
            i++;
        }
        
        args.add(new InitArgExpr("fields", argPosition++, new CastExpr(new ArrayTypeInfo(enumFieldInfo, i, null), new ArrayInitExpr(enumFieldInfo, arrayValues))));
        
        return new InitExpr(enumType, args);
    }
}
