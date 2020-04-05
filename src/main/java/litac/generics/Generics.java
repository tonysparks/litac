/*
 * see license.txt
 */
package litac.generics;

import java.util.List;

import litac.ast.*;
import litac.ast.Decl.*;
import litac.checker.TypeInfo;

/**
 * Handles creating new {@link TypeInfo}s based on generic types with generic arguments.
 * As an example:
 * 
 *   Array<T> is represented by IdentityTypeInfo with genericParam 'T', when used
 *   in an expression, such as Array<i32>, the IdentityTypeInfo gets converted to a 
 *   StructTypeInfo named Arrayi32 with i32 type replaced where T was.
 * 
 * @author Tony
 *
 */
public class Generics {
    
    public static StructDecl createStructDecl(StructDecl decl, String newName, List<GenericArg> genArgs) {
        return (StructDecl)createDecl(decl, newName, genArgs);
    }
    
    public static UnionDecl createUnionDecl(UnionDecl decl, String newName, List<GenericArg> genArgs) {
        return (UnionDecl)createDecl(decl, newName, genArgs);
    }
    
    public static FuncDecl createFuncDecl(FuncDecl decl, String newName, List<GenericArg> genArgs) {
        return (FuncDecl)createDecl(decl, newName, genArgs);
    }

    public static TypedefDecl createTypedefDecl(TypedefDecl decl, String newName, List<GenericArg> genArgs) {
        return (TypedefDecl)createDecl(decl, newName, genArgs);
    }
    
    private static Decl createDecl(GenericDecl decl, String newName, List<GenericArg> genArgs) {
        GenericDecl newDecl = decl.copy();
        GenericsNodeVisitor replacer = new GenericsNodeVisitor(decl.genericParams, genArgs);
        newDecl.visit(replacer);

        newDecl.name = newName;
        newDecl.attributes = decl.attributes;

        int removed = 0;
        for (int i = 0; i < genArgs.size(); i++) {
            int index = i - removed;
            if (index < newDecl.genericParams.size()) {
                newDecl.genericParams.remove(index);
                removed++;
            }
        }
        return newDecl;
    }
}
