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
 * be reflected.
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
        ArrayTypeSpec arrayInfo = new ArrayTypeSpec(null, typeSpec, NumberExpr.expr(TypeInfo.I64_TYPE, numOfTypeInfos));
        
        List<Expr> infos = addTypeInfos(symbols, typeModule);
        ArrayInitExpr initExpr = new ArrayInitExpr(arrayInfo, infos);

        ArrayTypeSpec typeTableType = new ArrayTypeSpec(null, new PtrTypeSpec(null, typeSpec), NumberExpr.expr(TypeInfo.I64_TYPE, numOfTypeInfos));
        Decl typeTable = new ConstDecl("typeTable", typeTableType, initExpr, 0);
        Symbol tableSym = typeModule.currentScope().addSymbol(typeModule, typeTable, "typeTable", true);
        tableSym.type = new ArrayTypeInfo(new PtrTypeInfo(typeInfo), numOfTypeInfos, null);
        
        initExpr.resolveTo(Operand.op(tableSym.type));
        
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
            System.out.println("Type: " + s.decl.name);
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
                    
                    exprs.add(new ArrayDesignationExpr(new NumberExpr(TypeInfo.I64_TYPE, String.valueOf(s.getType().getTypeId())), toExpr(s.decl, main)));
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
        
        GetExpr getExpr = getTypeKindAst(name, typeKind);
        args.add(new InitArgExpr("kind", argPosition++, getExpr));
        
        InitExpr initExpr = new InitExpr(this.typeInfoNameSpec, args);
        initExpr.resolveTo(Operand.op(typeInfo));
        
        UnaryExpr unaryExpr = new UnaryExpr(TokenType.BAND, initExpr);
        unaryExpr.resolveTo(Operand.op(new PtrTypeInfo(typeInfo)));
        
        return unaryExpr;
    }
    
    private GetExpr getTypeKindAst(String name, EnumTypeInfo typeKind) {
        GetExpr getExpr = new GetExpr(new IdentifierExpr(typeKindNameSpec), 
                                      new IdentifierExpr(new NameTypeSpec(null, name)));
        
        getExpr.object.resolveTo(Operand.op(typeKind));
        return getExpr;
    }
    
    private Expr toExpr(Decl d, Module main) {
        StructTypeInfo typeInfo = main.getType("TypeInfo").type.as();
        EnumTypeInfo typeKind = main.getType("TypeKind").type.as();
        
        UnionTypeInfo anonInfo = typeInfo.fieldInfos.stream()
                .filter(f -> f.type.isAnonymous())
                .findFirst()
                .get()
                .type.as();
        
        int argPosition = 0;
        List<InitArgExpr> args = new ArrayList<>();
        args.add(new InitArgExpr("name", argPosition++, new StringExpr(d.name)));
        args.add(new InitArgExpr("id", argPosition++, NumberExpr.expr(TypeInfo.I64_TYPE, d.sym.type.getTypeId())));
        
        switch(d.kind) {
            case ENUM: {
                GetExpr getExpr = getTypeKindAst("Enum", typeKind);
                args.add(new InitArgExpr("kind", argPosition++, getExpr));
                args.add(new InitArgExpr("enumType", argPosition++, newEnum((EnumDecl)d, main, anonInfo.getField("enumType").type.as())));
                break;
            }
            case FUNC: {                
                GetExpr getExpr = getTypeKindAst("Func", typeKind);
                args.add(new InitArgExpr("kind", argPosition++, getExpr));
               // args.add(new InitArgExpr("funcType", argPosition++, newFunc((FuncDecl)d, main, anonInfo.getField("funcType").type.as())));
                break;
            }
            case STRUCT: {                
                GetExpr getExpr = getTypeKindAst("Struct", typeKind);
                args.add(new InitArgExpr("kind", argPosition++, getExpr));
                break;
            }
            case UNION: {
                GetExpr getExpr = getTypeKindAst("Union", typeKind);
                args.add(new InitArgExpr("kind", argPosition++, getExpr));
                break;
            }
            case CONST:
            case PARAM:
            case TYPEDEF:
            case VAR:
            default:
                break;
        
        }
        
        InitExpr initExpr = new InitExpr(this.typeInfoNameSpec, args);
        initExpr.resolveTo(Operand.op(typeInfo));
                
        UnaryExpr unaryExpr = new UnaryExpr(TokenType.BAND, initExpr);
        unaryExpr.resolveTo(Operand.op(new PtrTypeInfo(typeInfo)));
        
        return unaryExpr;
    }
    
    private static Expr newEnum(EnumDecl d, Module main, StructTypeInfo enumType) {
        int argPosition = 0;
        List<InitArgExpr> args = new ArrayList<>();
        args.add(new InitArgExpr("name", argPosition++, new StringExpr(d.name)));
        args.add(new InitArgExpr("numOfFields", argPosition++, NumberExpr.expr(TypeInfo.I32_TYPE, d.fields.size())));
        
        StructTypeInfo enumFieldInfo = main.getType("EnumFieldInfo").type.as();
        NameTypeSpec enumNameSpec = enumFieldInfo.asTypeSpec().as();
        
        List<Expr> arrayValues = new ArrayList<>();

        int i = 0;
        for(EnumFieldInfo field : d.fields) {
            List<InitArgExpr> arrayArg = new ArrayList<>();
            arrayArg.add(new InitArgExpr("name", 0, new StringExpr(field.name)));
            
            Expr value = (field.value != null) 
                    ? field.value : new NumberExpr(TypeInfo.I32_TYPE, String.valueOf(i));
            arrayArg.add(new InitArgExpr("value", 1, value)); // TODO: get REAL value
            //arrayValues.add(new UnaryExpr(TokenType.BAND, new InitExpr(enumFieldInfo, arrayArg)));
            
            InitExpr initExpr = new InitExpr(enumNameSpec, arrayArg);
            initExpr.resolveTo(Operand.op(enumFieldInfo));
            arrayValues.add(initExpr);
            
            i++;
        }
        
        ArrayInitExpr arrayInitExpr = new ArrayInitExpr(enumNameSpec, arrayValues);
        CastExpr castExpr = new CastExpr(new ArrayTypeSpec(null, enumNameSpec, NumberExpr.expr(TypeInfo.I64_TYPE, i)), arrayInitExpr);
        castExpr.resolveTo(Operand.op(new ArrayTypeInfo(enumFieldInfo, i, null)));
        args.add(new InitArgExpr("fields", argPosition++, castExpr));
        
        InitExpr initExpr = new InitExpr(enumType.asTypeSpec().as(), args);
        initExpr.resolveTo(Operand.op(enumFieldInfo));
        
        return initExpr;
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
