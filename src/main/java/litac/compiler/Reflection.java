/*
 * see license.txt
 */
package litac.compiler;

import java.util.*;

import litac.ast.*;
import litac.ast.Decl.*;
import litac.ast.Expr.*;
import litac.ast.Stmt.*;
import litac.ast.TypeSpec.*;
import litac.checker.*;
import litac.checker.TypeInfo.*;
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
    
    public Reflection(Program program,
                      TypeInfoOption option) {
        this.program = program;
        this.option = option;
    }
    
    public List<Decl> createTypeInfos(Module typeModule, TypeResolver resolver, List<Decl> declarations) {
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
        
        // Get a list of all declaration types
        List<Expr> infos = addTypeInfos(symbols, typeModule);
        
        Expr numOfTypeInfosExpr = NumberExpr.expr(TypeInfo.I64_TYPE, numOfTypeInfos);
        TypeSpec typeInfoTypeSpec = new ArrayTypeSpec(null, new PtrTypeSpec(null, new NameTypeSpec(null, "TypeInfo")), numOfTypeInfosExpr);
        
        Expr typeInfosExpr = new ArrayInitExpr(typeInfoTypeSpec, infos);
        Decl typeInfosDecl = new ConstDecl("typeInfos", typeInfoTypeSpec.copy(), typeInfosExpr, 0);
        typeInfosDecl.attributes.isGlobal = true;
        typeInfosDecl.attributes.isPublic = true; 
        typeInfosDecl.attributes.addNote(new NoteStmt("generated"));
                
        Decl numOfTypeInfosDecl = new ConstDecl("numOfTypeInfos", new NameTypeSpec(null, "i64"), numOfTypeInfosExpr.copy(), 0);
        numOfTypeInfosDecl.attributes.isGlobal = true;
        numOfTypeInfosDecl.attributes.isPublic = true;
        numOfTypeInfosDecl.attributes.addNote(new NoteStmt("generated"));
        
        List<Decl> generatedTypes = Arrays.asList(typeInfosDecl, numOfTypeInfosDecl);
        resolveTypes(typeModule, resolver, generatedTypes);

        return generatedTypes;
    }
    
    private void resolveTypes(Module typeModule, TypeResolver resolver, List<Decl> generatedTypes) {
        ModuleStmt moduleStmt = typeModule.getModuleStmt();
        List<Decl> previousDecls = moduleStmt.declarations;
        
        moduleStmt.declarations = generatedTypes;        
        resolver.resolveModuleTypes(typeModule);
        
        for(Decl decl : generatedTypes) {
            for(int i = 0; i < previousDecls.size(); i++) {
                Decl prevDecl = previousDecls.get(i);
                if(prevDecl.name.equals(decl.name)) {
                    previousDecls.set(i, decl);
                    break;
                }
            }                        
        }
                
        moduleStmt.declarations = previousDecls;
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
                    
                    exprs.add(new ArrayDesignationExpr(NumberExpr.expr(TypeInfo.I64_TYPE, s.getType().getTypeId()), toExpr(s.decl, main)));
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
        //StructTypeInfo typeInfo = main.getType("TypeInfo").type.as();
        EnumTypeInfo typeKind = main.getType("TypeKind").type.as();
        
        int argPosition = 0;
        List<InitArgExpr> args = new ArrayList<>();
        args.add(new InitArgExpr("name", argPosition++, new StringExpr(prim.getName())));
        args.add(new InitArgExpr("id", argPosition++, NumberExpr.expr(TypeInfo.I64_TYPE, prim.getTypeId())));
        
        String name = Character.isLowerCase(prim.getKind().name().charAt(0)) ? prim.getKind().name().toUpperCase() : prim.getKind().name();
        
        GetExpr getExpr = getTypeKindAst(name, typeKind);
        args.add(new InitArgExpr("kind", argPosition++, getExpr));
        
        InitExpr initExpr = new InitExpr(this.typeInfoNameSpec, args);
        UnaryExpr unaryExpr = new UnaryExpr(TokenType.BAND, initExpr);
        return unaryExpr;
    }
    
    private GetExpr getTypeKindAst(String name, EnumTypeInfo typeKind) {
        GetExpr getExpr = new GetExpr(new IdentifierExpr(typeKindNameSpec), 
                                      new IdentifierExpr(new NameTypeSpec(null, name)));
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
                args.add(new InitArgExpr("funcType", argPosition++, newFunc((FuncDecl)d, main, anonInfo.getField("funcType").type.as())));
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
        UnaryExpr unaryExpr = new UnaryExpr(TokenType.BAND, initExpr);
        return unaryExpr;
    }
    
    private Expr newEnum(EnumDecl d, Module main, StructTypeInfo enumType) {
        
        NameTypeSpec enumTypeSpec = enumType.asTypeSpec().as();
        program.getResolvedTypeMap().put(enumTypeSpec, enumType);
        
        StructTypeInfo enumFieldInfo = main.getType("EnumFieldInfo").type.as();
        NameTypeSpec enumNameSpec = enumFieldInfo.asTypeSpec().as();
        program.getResolvedTypeMap().put(enumNameSpec, enumFieldInfo);
        
        
        int argPosition = 0;
        List<InitArgExpr> args = new ArrayList<>();
        args.add(new InitArgExpr("numOfFields", argPosition++, NumberExpr.expr(TypeInfo.I32_TYPE, d.fields.size())));
        
        List<Expr> arrayValues = new ArrayList<>();

        if(!d.fields.isEmpty()) {
            int i = 0;
            for(EnumFieldInfo field : d.fields) {
                List<InitArgExpr> arrayArg = new ArrayList<>();
                arrayArg.add(new InitArgExpr("name", 0, new StringExpr(field.name)));
                
                Expr value = (field.value != null) 
                        ? field.value : new NumberExpr(TypeInfo.I32_TYPE, String.valueOf(i));
                arrayArg.add(new InitArgExpr("value", 1, value));
                
                InitExpr initExpr = new InitExpr(enumNameSpec, arrayArg);
                arrayValues.add(initExpr);
                
                i++;
            }
            
            ArrayTypeSpec arrayTypeSpec = new ArrayTypeSpec(null, enumNameSpec, NumberExpr.expr(TypeInfo.I64_TYPE, i));
            ArrayInitExpr arrayInitExpr = new ArrayInitExpr(arrayTypeSpec, arrayValues);
            
            InitArgExpr initArgExpr = new InitArgExpr("fields", argPosition++, arrayInitExpr);
            args.add(initArgExpr);
        }
        
        InitExpr initExpr = new InitExpr(enumTypeSpec, args);
        return initExpr;
    }
    
    private Expr newFunc(FuncDecl d, Module main, StructTypeInfo funcType) {

        FuncTypeInfo funcInfo = d.sym.type.as();
        
        
        NameTypeSpec funcTypeSpec = funcType.asTypeSpec().as();
        program.getResolvedTypeMap().put(funcTypeSpec, funcType);
        
        StructTypeInfo paramInfo = main.getType("ParamInfo").type.as();
        NameTypeSpec paramNameSpec = paramInfo.asTypeSpec().as();
        program.getResolvedTypeMap().put(paramNameSpec, paramInfo);
        
        int argPosition = 0;
        List<InitArgExpr> args = new ArrayList<>();
        args.add(new InitArgExpr("numOfParams", argPosition++, NumberExpr.expr(TypeInfo.I32_TYPE, d.params.params.size())));
        args.add(new InitArgExpr("isVararg", argPosition++, new BooleanExpr(d.params.isVararg)));
        args.add(new InitArgExpr("returnType", argPosition++, NumberExpr.expr(TypeInfo.I64_TYPE, funcInfo.returnType.getTypeId())));
        
        // args, numOrArgs
        //StructTypeInfo genericInfo = main.getType("GenericInfo").type.as();
        List<Expr> arrayValues = new ArrayList<>();

        if(!d.params.params.isEmpty()) {
            int i = 0;
            for(ParameterDecl param: d.params.params) {
                List<InitArgExpr> paramInfoArgs = new ArrayList<>();
                // TODO: paramInfoArg.add(new InitArgExpr("genInfo", 0, new StringExpr(param.name)));
                paramInfoArgs.add(new InitArgExpr("name", 0, new StringExpr(param.name)));
                if(param.sym != null) {
                    paramInfoArgs.add(new InitArgExpr("type", 1, NumberExpr.expr(TypeInfo.I64_TYPE, param.sym.type.getTypeId())));
                    paramInfoArgs.add(new InitArgExpr("modifiers", 2, NumberExpr.expr(TypeInfo.I64_TYPE, param.sym.decl.attributes.modifiers)));
                }
                            
                arrayValues.add(new InitExpr(paramNameSpec, paramInfoArgs));            
                i++;
            }
            
            ArrayTypeSpec arrayTypeSpec = new ArrayTypeSpec(null, paramNameSpec, NumberExpr.expr(TypeInfo.I64_TYPE, i));
            ArrayInitExpr arrayInitExpr = new ArrayInitExpr(arrayTypeSpec, arrayValues);
            
            InitArgExpr initArgExpr = new InitArgExpr("params", argPosition++, arrayInitExpr);
            args.add(initArgExpr);
        }
        
        
        InitExpr initExpr = new InitExpr(funcTypeSpec, args);
        return initExpr;        
    }
}
