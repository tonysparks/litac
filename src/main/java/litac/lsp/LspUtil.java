/*
 * see license.txt
 */
package litac.lsp;

import java.util.*;

import litac.ast.Decl;
import litac.ast.Node.SrcPos;
import litac.ast.Stmt.NoteStmt;
import litac.checker.TypeInfo;
import litac.checker.TypeInfo.*;
import litac.compiler.*;
import litac.compiler.Module;
import litac.compiler.PhaseResult.PhaseError;
import litac.lsp.JsonRpc.*;

/**
 */
public class LspUtil {

    public static Location locationFromSrcPosLine(SrcPos srcPos) {
        if(srcPos == null || srcPos.sourceFile == null) {
            return null;
        }
        
        String uri = srcPos.sourceFile.toURI().toString();
        
        Location location = new Location();
        location.uri = uri;
        location.range = LspUtil.fromSrcPosLine(srcPos);
        return location;
    }
    
    public static Range fromSrcPosLine(SrcPos srcPos) {
        int lineNumber = Math.max(0, srcPos.lineNumber - 1);
                
        Range range = new Range();
        range.start = new Position();
        range.start.line = lineNumber;
        range.start.character = 0;
        
        range.end = new Position();
        range.end.line = lineNumber;    
        if(srcPos.sourceLine != null) {
            range.end.character = srcPos.sourceLine.length();
        }
        else {
            range.end.character = 1;
        }
        return range;
    }
    
    public static Range fromSrcPosToken(SrcPos srcPos) {
        int lineNumber = Math.max(0, srcPos.lineNumber - 1);
                
        Range range = new Range();
        range.start = new Position();
        range.start.line = lineNumber;
        range.start.character = srcPos.position;
        
        range.end = new Position();
        range.end.line = lineNumber;        
        range.end.character = srcPos.position + srcPos.token.getText().length();
        return range;
    }
    
    public static SymbolInformation fromSymbol(Symbol sym) {
        SymbolInformation info = new SymbolInformation();
        info.deprecated = sym.isType() && sym.decl.attributes.hasNote("deprecated");
        info.name = sym.name;
        info.kind = SymbolKind.fromSymbol(sym).getValue();
        
        Decl decl = sym.decl;
        if(decl != null && decl.getSrcPos().sourceFile != null) {
            info.location = LspUtil.locationFromSrcPosLine(decl.getSrcPos());
        }
        
        return info;
    }
    
    public static CompletionItem fromSymbolCompletionItem(Symbol sym) {
        CompletionItem item = new CompletionItem();
        if(sym == null) {
            return item;
        }
        
        item.deprecated = sym.decl != null && sym.decl.attributes.hasNote("deprecated");
        if(sym.decl != null && sym.decl.attributes.hasNote("doc")) {
            NoteStmt note = sym.decl.attributes.getNote("doc");
            item.documentation = note.getAttr(0, "");
        }
        
        if(sym.type == null) {
            return item;
        }
        
        String name = sym.name;
        if(sym.type.isKind(TypeKind.Func)) {
            FuncTypeInfo funcInfo = sym.type.as();
            if(funcInfo.isMethod()) {
                name = funcInfo.name;
            }
        }
        
        item.kind = CompletionItemKind.fromSymbol(sym).getValue();
        item.detail = sym.name;
        item.label = name;
        return item;
    }
    
    public static CompletionItem fromFieldInfoCompletionItem(FieldInfo field) {
        CompletionItem item = new CompletionItem();
        String name = field.name;
        item.detail = name;
        item.label = name;
        item.deprecated = field.attributes.hasNote("deprecated");
        if(field.attributes.hasNote("doc")) {
            NoteStmt note = field.attributes.getNote("doc");
            item.documentation = note.getAttr(0, "");
        }
        
        if(field.type.sym != null) {            
            item.kind = CompletionItemKind.fromSymbol(field.type.sym).getValue();
        }
        else {
            item.kind = CompletionItemKind.Field.getValue();
        }
        
        return item;
    }
    
    public static CompletionItem fromEnumFieldInfoCompletionItem(EnumFieldInfo field) {
        CompletionItem item = new CompletionItem();
        String name = field.name;
        item.detail = name;
        item.label = name;
        item.deprecated = field.attributes.hasNote("deprecated");
        if(field.attributes.hasNote("doc")) {
            NoteStmt note = field.attributes.getNote("doc");
            item.documentation = note.getAttr(0, "");
        }
                
        item.kind = CompletionItemKind.EnumMember.getValue();
        return item;
    }
    
    
    public static List<CompletionItem> fromTypeInfoCompletionItems(Module module, AggregateTypeInfo aggInfo) {
        List<CompletionItem> results = new ArrayList<>(aggInfo.fieldInfos.size());
        for(FieldInfo field : aggInfo.fieldInfos) {
            results.add(fromFieldInfoCompletionItem(field));
        }
                
        if(aggInfo.usingInfos != null) {
            for(FieldInfo field : aggInfo.usingInfos) {
                AggregateTypeInfo aggFieldInfo = TypeInfo.getBase(field.type).as();
                results.addAll(fromTypeInfoCompletionItems(module, aggFieldInfo));
            }
        }
        
        List<Symbol> methods = module.getMethodsFor(aggInfo);
        for(Symbol sym : methods) {
            results.add(fromSymbolCompletionItem(sym));
        }
        
        return results;
    }
    
    public static List<CompletionItem> fromEnumTypeInfoCompletionItems(EnumTypeInfo enumInfo) {
        List<CompletionItem> results = new ArrayList<>(enumInfo.fields.size());
        for(EnumFieldInfo field : enumInfo.fields) {
            results.add(fromEnumFieldInfoCompletionItem(field));
        }
                
        return results;
    }
    
    public static String phaseErrorToString(List<PhaseError> errors) {
        if(errors == null || errors.isEmpty()) {
            return "";
        }
        
        StringBuffer sb = new StringBuffer();
        for(PhaseError error : errors) {
            sb.append(error.message).append(" in ").append(error.pos).append(",");
        }
        
        return sb.toString();
    }
    
    public static boolean isAtLocation(SrcPos srcPos, Position pos) {
        if(srcPos.token == null) {
            return false;
        }
        
        int fromIndex = srcPos.position;
        int toIndex = fromIndex + srcPos.token.getText().length();
        if((pos.line+1) == srcPos.lineNumber) {
            if(pos.character >= fromIndex && pos.character <= toIndex) {
                return true;
            }
        }
        
        return false;
    }    
}
