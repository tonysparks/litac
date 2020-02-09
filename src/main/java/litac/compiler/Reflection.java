/*
 * see license.txt
 */
package litac.compiler;

import java.util.*;

import litac.ast.*;
import litac.ast.Decl.*;
import litac.ast.Expr.*;
import litac.ast.TypeSpec.*;
import litac.checker.TypeInfo;
import litac.checker.TypeInfo.*;
import litac.checker.TypeResolver.Operand;
import litac.compiler.BackendOptions.TypeInfoOption;
import litac.parser.tokens.TokenType;

/**
 * Builds an array of TypeInfo objects so that types can
 * be reflected
 * 
 * @author Tony
 *
 */
public class Reflection {
    
    private Program program;
    private TypeInfoOption option;
    private NameTypeSpec typeInfoNameSpec;
    private NameTypeSpec typeKindNameSpec;
    
    public Reflection(Program program, TypeInfoOption option) {
        this.program = program;
        this.option = option;
        
    }
    
    public List<Decl> createTypeInfos(List<Decl> declarations) {
        Module typeModule = program.getModule("type");
        if(typeModule == null) {
            return Arrays.asList();
        }
        
        TypeInfo typeKind = typeModule.getType("TypeKind").type;
        TypeInfo typeInfo = typeModule.getType("TypeInfo").type;
        typeInfoNameSpec = program.getResolvedTypeMap().entrySet()
                                    .stream()
                                    .filter((e) -> e.getValue().equals(typeInfo))
                                    .map(e -> e.getKey())
                                    .findFirst().map(s -> (NameTypeSpec)s.as()).orElse(null);
        
        typeKindNameSpec = program.getResolvedTypeMap().entrySet()
                                  .stream()
                                  .filter((e) -> e.getValue().equals(typeKind))
                                  .map(e -> e.getKey())
                                  .findFirst().map(s -> (NameTypeSpec)s.as()).orElse(null);
        
        long numOfTypeInfos = 0;
        
        List<Symbol> symbols = program.getSymbols();
        if(!option.equals(TypeInfoOption.None)) {
            numOfTypeInfos = symbols.stream()
                                    .mapToLong(s -> s.getType().getTypeId())
                                    .max()
                                    .orElse(0);
            
            // typeid of the max, must fit inside the array
            if(numOfTypeInfos > 0) {
                numOfTypeInfos++;
            }
        }
        
        TypeSpec typeSpec = typeInfo.asTypeSpec();
        ArrayTypeSpec arrayInfo = new ArrayTypeSpec(null, typeSpec, new NumberExpr(TypeInfo.I64_TYPE, String.valueOf(numOfTypeInfos)));
        
        List<Expr> infos = addTypeInfos(symbols, typeModule);
        ArrayInitExpr initExpr = new ArrayInitExpr(arrayInfo, infos);

        ArrayTypeSpec typeTableType = new ArrayTypeSpec(null, new PtrTypeSpec(null, typeSpec), new NumberExpr(TypeInfo.I64_TYPE, String.valueOf(numOfTypeInfos)));
        Decl typeTable = new ConstDecl("typeTable", typeTableType, initExpr, 0);
        typeModule.currentScope().addSymbol(typeModule, typeTable, "typeTable", true);
        typeTable.sym.type = new ArrayTypeInfo(new PtrTypeInfo(typeInfo), numOfTypeInfos, null);
        initExpr.resolveTo(Operand.op(typeTable.sym.type));
        
        Symbol sym = typeModule.currentScope().getSymbol("typeInfos");
        if(sym.decl instanceof ConstDecl) {
            ConstDecl typeInfoArray = (ConstDecl)sym.decl;
            typeInfoArray.attributes.notes.removeIf(n -> n.name.equals("foreign"));
            
            NameTypeSpec name = new NameTypeSpec(null, "typeTable");
            IdentifierExpr idExpr = new IdentifierExpr(name);
            idExpr.sym = typeTable.sym;
            
            TypeSpec castTo = new PtrTypeSpec(null, new PtrTypeSpec(null, typeSpec));
            TypeInfo castToInfo = new PtrTypeInfo(new PtrTypeInfo(typeInfo));
            program.getResolvedTypeMap().put(castTo, castToInfo);
            
            CastExpr castExpr = new CastExpr(castTo, idExpr);
            castExpr.resolveTo(Operand.op(castToInfo));            
            typeInfoArray.expr = castExpr;
            
            
            sym.removeForeign();
        }
            
        
        Symbol size = typeModule.currentScope().getSymbol("numOfTypeInfos");
        if(size.decl instanceof ConstDecl) {
            ConstDecl sizeValue = (ConstDecl)size.decl;
            sizeValue.attributes.notes.removeIf(n -> n.name.equals("foreign"));
            sizeValue.expr = new NumberExpr(TypeInfo.I64_TYPE, String.valueOf(numOfTypeInfos));
            size.removeForeign();
        }
                
        return Arrays.asList(typeTable);
    }

    private List<Expr> addTypeInfos(List<Symbol> symbols, Module main) {        
        if(option.equals(TypeInfoOption.None)) {
            return Collections.emptyList();
        }
        
        boolean filter = option.equals(TypeInfoOption.Tagged);
        
        List<Expr> exprs = new ArrayList<>();
        for(Symbol s : symbols) {
            switch(s.decl.kind) {            
                case FUNC:
                case STRUCT:
                case UNION:
                //case TYPEDEF:
                case ENUM:
                    // allow for only defining TypeInfo's that are annotated with @typeinfo
                    if(filter && !s.decl.attributes.hasNote("typeinfo")) {
                        continue;                        
                    }
                    
               //     exprs.add(new ArrayDesignationExpr(new NumberExpr(TypeInfo.I64_TYPE, String.valueOf(s.getType().getTypeId())), toExpr(s.decl, main)));
                    break;
                default:
            }            
        }

        exprs.add(new ArrayDesignationExpr(new NumberExpr(TypeInfo.I64_TYPE, String.valueOf(TypeInfo.BOOL_TYPE.getTypeId())), toExpr(TypeInfo.BOOL_TYPE, main)));
        exprs.add(new ArrayDesignationExpr(new NumberExpr(TypeInfo.I64_TYPE, String.valueOf(TypeInfo.CHAR_TYPE.getTypeId())), toExpr(TypeInfo.CHAR_TYPE, main)));
        exprs.add(new ArrayDesignationExpr(new NumberExpr(TypeInfo.I64_TYPE, String.valueOf(TypeInfo.I8_TYPE.getTypeId())), toExpr(TypeInfo.I8_TYPE, main)));
        exprs.add(new ArrayDesignationExpr(new NumberExpr(TypeInfo.I64_TYPE, String.valueOf(TypeInfo.U8_TYPE.getTypeId())), toExpr(TypeInfo.U8_TYPE, main)));
        exprs.add(new ArrayDesignationExpr(new NumberExpr(TypeInfo.I64_TYPE, String.valueOf(TypeInfo.I16_TYPE.getTypeId())), toExpr(TypeInfo.I16_TYPE, main)));
        exprs.add(new ArrayDesignationExpr(new NumberExpr(TypeInfo.I64_TYPE, String.valueOf(TypeInfo.U16_TYPE.getTypeId())), toExpr(TypeInfo.U16_TYPE, main)));
        exprs.add(new ArrayDesignationExpr(new NumberExpr(TypeInfo.I64_TYPE, String.valueOf(TypeInfo.I32_TYPE.getTypeId())), toExpr(TypeInfo.I32_TYPE, main)));
        exprs.add(new ArrayDesignationExpr(new NumberExpr(TypeInfo.I64_TYPE, String.valueOf(TypeInfo.U32_TYPE.getTypeId())), toExpr(TypeInfo.U32_TYPE, main)));
        exprs.add(new ArrayDesignationExpr(new NumberExpr(TypeInfo.I64_TYPE, String.valueOf(TypeInfo.I64_TYPE.getTypeId())), toExpr(TypeInfo.I64_TYPE, main)));
        exprs.add(new ArrayDesignationExpr(new NumberExpr(TypeInfo.I64_TYPE, String.valueOf(TypeInfo.U64_TYPE.getTypeId())), toExpr(TypeInfo.U64_TYPE, main)));
        exprs.add(new ArrayDesignationExpr(new NumberExpr(TypeInfo.I64_TYPE, String.valueOf(TypeInfo.F32_TYPE.getTypeId())), toExpr(TypeInfo.F32_TYPE, main)));
        exprs.add(new ArrayDesignationExpr(new NumberExpr(TypeInfo.I64_TYPE, String.valueOf(TypeInfo.F64_TYPE.getTypeId())), toExpr(TypeInfo.F64_TYPE, main)));
        exprs.add(new ArrayDesignationExpr(new NumberExpr(TypeInfo.I64_TYPE, String.valueOf(TypeInfo.NULL_TYPE.getTypeId())), toExpr(TypeInfo.NULL_TYPE, main)));
        exprs.add(new ArrayDesignationExpr(new NumberExpr(TypeInfo.I64_TYPE, String.valueOf(TypeInfo.VOID_TYPE.getTypeId())), toExpr(TypeInfo.VOID_TYPE, main)));
        
        return exprs;      
    }
    
    private Expr toExpr(TypeInfo prim, Module main) {
        StructTypeInfo typeInfo = main.getType("TypeInfo").type.as();
        EnumTypeInfo typeKind = main.getType("TypeKind").type.as();
        
        int argPosition = 0;
        List<InitArgExpr> args = new ArrayList<>();
        args.add(new InitArgExpr("name", argPosition++, new StringExpr(prim.getName())));
        args.add(new InitArgExpr("id", argPosition++, new NumberExpr(TypeInfo.I64_TYPE, String.valueOf(prim.getTypeId()))));
        
        String name = Character.isLowerCase(prim.getKind().name().charAt(0)) ? prim.getKind().name().toUpperCase() : prim.getKind().name();
        
        GetExpr getExpr = new GetExpr(new IdentifierExpr(typeKindNameSpec), 
                                      new IdentifierExpr(new NameTypeSpec(null, name)));
        getExpr.object.resolveTo(Operand.op(typeKind));
        
        args.add(new InitArgExpr("kind", argPosition++, getExpr));
        
        
        InitExpr initExpr = new InitExpr(this.typeInfoNameSpec, args);
        initExpr.resolveTo(Operand.op(typeInfo));
        initExpr.type = this.typeInfoNameSpec;
        
        UnaryExpr unaryExpr = new UnaryExpr(TokenType.BAND, initExpr);
        unaryExpr.resolveTo(Operand.op(new PtrTypeInfo(typeInfo)));
        
        return unaryExpr;
    }
    
    private Expr toExpr(Decl d, Module main) {
        /*StructTypeInfo typeInfo = main.getType("TypeInfo").as();
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
                args.add(new InitArgExpr("kind", argPosition++, new GetExpr(new IdentifierExpr("TypeKind", typeKind), new IdentifierExpr(kindName.name, kindName))));
                args.add(new InitArgExpr("enumType", argPosition++, newEnum((EnumDecl)d, main, anonInfo.getField("enumType").type.as())));
                break;
            }
            case FUNC: {
                TypeInfo kindName = new IdentifierTypeInfo("Func", Collections.emptyList());
                args.add(new InitArgExpr("kind", argPosition++, new GetExpr(new IdentifierExpr("TypeKind", typeKind), new IdentifierExpr(kindName.name, kindName))));
                args.add(new InitArgExpr("funcType", argPosition++, newFunc((FuncDecl)d, main, anonInfo.getField("funcType").type.as())));
                break;
            }
            case STRUCT: {
                TypeInfo kindName = new IdentifierTypeInfo("Struct", Collections.emptyList());
                args.add(new InitArgExpr("kind", argPosition++, new GetExpr(new IdentifierExpr("TypeKind", typeKind), new IdentifierExpr(kindName.name, kindName))));
                break;
            }
            case UNION: {
                TypeInfo kindName = new IdentifierTypeInfo("Union", Collections.emptyList());
                args.add(new InitArgExpr("kind", argPosition++, new GetExpr(new IdentifierExpr("TypeKind", typeKind), new IdentifierExpr(kindName.name, kindName))));
                break;
            }
            case CONST:
            case PARAM:
            case TYPEDEF:
            case VAR:
            default:
                break;
        
        }
        
        return new UnaryExpr(TokenType.BAND, new InitExpr(typeInfo, args));*/
        return null;
    }
    
    private static Expr newEnum(EnumDecl d, Module main, StructTypeInfo enumType) {
        /*int argPosition = 0;
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
        
        return new InitExpr(enumType, args);*/
        return null;
    }
    
    private static Expr newFunc(FuncDecl d, Module main, StructTypeInfo funcType) {
        /*int argPosition = 0;
        List<InitArgExpr> args = new ArrayList<>();
        args.add(new InitArgExpr("name", argPosition++, new StringExpr(d.name)));
        args.add(new InitArgExpr("numOfParams", argPosition++, new NumberExpr(TypeInfo.I32_TYPE, String.valueOf(d.params.params.size()))));
        args.add(new InitArgExpr("isVararg", argPosition++, new BooleanExpr(d.params.isVararg)));
        
//        StructTypeInfo enumFieldInfo = main.getType("EnumFieldInfo").as();
//        List<Expr> arrayValues = new ArrayList<>();
//
//        int i = 0;
//        for(EnumFieldInfo field : d.fields) {
//            List<InitArgExpr> arrayArg = new ArrayList<>();
//            arrayArg.add(new InitArgExpr("name", 0, new StringExpr(field.name)));
//            
//            Expr value = (field.value != null) 
//                    ? field.value : new NumberExpr(TypeInfo.I32_TYPE, String.valueOf(i));
//            arrayArg.add(new InitArgExpr("value", 1, value)); // TODO: get REAL value
//            //arrayValues.add(new UnaryExpr(TokenType.BAND, new InitExpr(enumFieldInfo, arrayArg)));
//            arrayValues.add(new InitExpr(enumFieldInfo, arrayArg));
//            
//            i++;
//        }
//        
//        args.add(new InitArgExpr("fields", argPosition++, new CastExpr(new ArrayTypeInfo(enumFieldInfo, i, null), new ArrayInitExpr(enumFieldInfo, arrayValues))));
        
        return new InitExpr(funcType, args);*/
        return null;
    }
}
