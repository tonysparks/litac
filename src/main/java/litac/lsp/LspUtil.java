/*
 * see license.txt
 */
package litac.lsp;

import java.io.File;

import litac.ast.Decl;
import litac.ast.Node.SrcPos;
import litac.compiler.Symbol;
import litac.lsp.JsonRpc.*;

/**
 */
public class LspUtil {

    public static Range fromSrcPosLine(SrcPos srcPos) {
        int lineNumber = Math.max(0, srcPos.lineNumber - 1);
                
        Range range = new Range();
        range.start = new Position();
        range.start.line = lineNumber;
        range.start.character = 0;
        
        range.end = new Position();
        range.end.line = lineNumber;        
        range.end.character = srcPos.sourceLine.length();
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
            String uri = new File(decl.getSrcPos().sourceFile).toURI().toString();
            
            info.location = new Location();
            info.location.uri = uri;
            info.location.range = LspUtil.fromSrcPosLine(decl.getSrcPos());
        }
        
        return info;
    }
}
