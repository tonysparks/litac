/*
 * see license.txt
 */
package litac.doc;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import litac.ast.Decl.DeclKind;
import litac.ast.Stmt.NoteStmt;
import litac.checker.TypeInfo;
import litac.checker.TypeInfo.*;
import litac.compiler.*;

/**
 * @author Tony
 *
 */
public class MarkdownDocWriter implements DocWriter {

    private Map<String, StringBuilder> docs;
    private StringBuilder buf;
    private BackendOptions options;
    private boolean includePrivate;
    private Module current;
    /**
     * 
     */
    public MarkdownDocWriter(BackendOptions options) {
        this.options = options;
        this.buf = new StringBuilder();
        this.includePrivate = false;
    }

    @Override
    public void start() {
        //System.out.println("Starting doc generation");
        this.buf.append("# LitaC API Documentation - ").append(this.options.outputFileName).append("\n\n");
    }
    
    @Override
    public void end() {
        //System.out.println("Finished doc generation");
        
        System.out.println(this.buf);
    }
    
    @Override
    public void writeModule(Module module) {      
        current = module;        
        buf.append(section(module.name()));
        if(module.getModuleStmt().notes != null) {
            for(NoteStmt note : module.getModuleStmt().notes) {
                if(note.name.equals("doc")) {
                    if(note.attributes != null) {
                        for(String attr : note.attributes) {
                            buf.append(attr).append("\n");
                        }
                    }                    
                }
            }
        }
        
        listImports(module);
        listVariables(module);
        listStructures(module);
        listFunctions(module);
        
        buf.append("\n***\n");
        
        listVariableDetails(module);
        listStructureDetails(module);
        listFunctionDetails(module);
    }
    
    private void listImports(Module module) {
        String m = module.name();
        // List out Imports
        buf.append(subSection(m + " Imports"));
        {        
            List<String> moduleNames = module.getImports().stream()
                    .map(x -> linkTo(x.name()))
                    .sorted()
                    .collect(Collectors.toList());
            
            buf.append(list(moduleNames)).append("\n\n");
        }
    }
    
    private void listVariables(Module module) {
        String m = module.name();
        buf.append(subSection(m + " Variables"));
        {
            List<String> globals = module.getModuleScope().getSymbols().stream()
                    .filter(sym -> !sym.isType() && (includePrivate || sym.isPublic()))
                    .map(sym -> (sym.isConstant() ? "const " : "") + linkTo(sym.name) + ": " + type(sym.type))
                    .sorted()
                    .collect(Collectors.toList());
            
            buf.append(list(globals)).append("\n\n");
        }
    }
    
    private void listStructures(Module module) {
        String m = module.name();
        buf.append(subSection(m + " Types"));
        {
            List<String> types = module.getModuleScope().getSymbols().stream()
                    .filter(sym -> sym.isType() && !sym.decl.kind.equals(DeclKind.FUNC) && (includePrivate || sym.isPublic()))
                    .map(sym -> symbolSummary(sym))
                    .sorted()
                    .collect(Collectors.toList());
            
            buf.append(list(types)).append("\n\n");
        }
    }
    
    private void listFunctions(Module module) {
        String m = module.name();
        buf.append(subSection(m + " Functions"));
        List<String> types = module.getModuleScope().getSymbols().stream()
                .filter(sym -> sym.decl.kind.equals(DeclKind.FUNC) && (includePrivate || sym.isPublic()))
                .map(sym -> symbolSummary(sym))
                .sorted()
                .collect(Collectors.toList());
        
        buf.append(list(types)).append("\n\n");
    }
    
    private void listVariableDetails(Module module) {        
        List<Symbol> globals = module.getModuleScope().getSymbols().stream()
                                        .filter(sym -> !sym.isType() && (includePrivate || sym.isPublic()))                
                                        .sorted((a,b) -> a.name.compareTo(b.name))
                                        .collect(Collectors.toList());
        
        for(Symbol sym : globals) {
            buf.append(sub2Section(sym.name)).append("\n");
            NoteStmt note = sym.decl.attributes.getNote("doc");
            if(note != null && note.attributes != null) {
                for(String attr : note.attributes) {
                    buf.append(blockquote(attr)).append("\n");
                }
            }
        }
    }
    
    private void listStructureDetails(Module module) {
        List<Symbol> globals = module.getModuleScope().getSymbols().stream()
                                        .filter(sym -> sym.isType() && !sym.decl.kind.equals(DeclKind.FUNC) && (includePrivate || sym.isPublic()))                
                                        .sorted((a,b) -> a.name.compareTo(b.name))
                                        .collect(Collectors.toList());
        
        for(Symbol sym : globals) {
            buf.append(sub2Section(sym.name)).append("\n");
            NoteStmt note = sym.decl.attributes.getNote("doc");
            if(note != null && note.attributes != null) {
                for(String attr : note.attributes) {
                    buf.append(blockquote(attr)).append("\n");
                }
            }
        }
            
    }
    
    private void listFunctionDetails(Module module) {
        String m = module.name();
    }
    
    private String type(TypeInfo type) {
        if(type == null) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        Symbol sym = type.sym;
        if(sym != null && sym.declared != null && !sym.isBuiltin()) {
            sb.append(linkTo(sym.declared.name())).append("::");
        }
        
        TypeInfo base = TypeInfo.getBase(type);
        if(base.isPrimitive()) {
            sb.append(type.getName());            
        }
        else {              
            sb.append(linkTo(type.getName(), base.name));
        }
        
        return sb.toString();
    }
    
    private String symbolSummary(Symbol sym) {
        StringBuilder sb = new StringBuilder();
        switch(sym.decl.kind) {        
        case ENUM:
            sb.append("enum ").append(linkTo(sym.name));
            break;
        case FUNC:
            sb.append("func ").append(linkTo(sym.name)).append("(");
            FuncTypeInfo funcInfo = sym.type.as();
            
            boolean isFirst = true;
            for(ParamInfo param : funcInfo.parameterDecls) {
                if(!isFirst) sb.append(", ");
                isFirst = false;
                
                sb.append(param.name).append(": ").append(type(param.type));
            }
            sb.append(")");
            if(!funcInfo.returnType.isKind(TypeKind.Void)) {
                sb.append(" : ").append(type(funcInfo.returnType));
            }
            break;        
        case STRUCT:
            sb.append("struct ").append(linkTo(sym.name));
            break;
        case TYPEDEF:            
            sb.append("typedef ").append(type(sym.type)).append(" as ").append(linkTo(sym.name));
            break;
        case UNION:
            sb.append("union ").append(linkTo(sym.name));
            break;        
        default:
            break;
        
        }
        return sb.toString();
    }
    
    private String symbolDetail(Symbol sym) {
        StringBuilder sb = new StringBuilder();
        switch(sym.decl.kind) {        
        case ENUM:
            sb.append("enum ").append(linkTo(sym.name));
            break;
        case FUNC:
            sb.append("func ").append(linkTo(sym.name)).append("(");
            FuncTypeInfo funcInfo = sym.type.as();
            
            boolean isFirst = true;
            for(ParamInfo param : funcInfo.parameterDecls) {
                if(!isFirst) sb.append(", ");
                isFirst = false;
                
                sb.append(param.name).append(": ").append(type(param.type));
            }
            sb.append(")");
            if(!funcInfo.returnType.isKind(TypeKind.Void)) {
                sb.append(" : ").append(type(funcInfo.returnType));
            }
            break;        
        case STRUCT:
            sb.append("struct ").append(linkTo(sym.name));
            break;
        case TYPEDEF:            
            sb.append("typedef ").append(type(sym.type)).append(" as ").append(linkTo(sym.name));
            break;
        case UNION:
            sb.append("union ").append(linkTo(sym.name));
            break;        
        default:
            break;
        
        }
        return sb.toString();
    }
    
    private String list(List<String> items) {
        StringBuilder sb = new StringBuilder();
        for(String item : items) {
            sb.append("* ").append(item).append("\n");
        }
        
        return sb.toString();
    }
    
    private String section(String name) {
        StringBuilder sb = new StringBuilder();
        sb.append("# ").append(escape(name)).append("\n\n");
        return sb.toString();
    }
    
    private String subSection(String name) {
        StringBuilder sb = new StringBuilder();
        sb.append("## ").append(escape(name)).append("\n\n");
        return sb.toString();
    }
    
    private String sub2Section(String name) {
        StringBuilder sb = new StringBuilder();
        sb.append("### ").append(escape(name)).append("\n\n");
        return sb.toString();
    }
    
    private String blockquote(String docs) {
        if(docs == null) return "";
        
        String[] lines = docs.split("\n");
        if(lines == null) return "";
        
        StringBuilder sb = new StringBuilder();
        for(String line : lines) {
            sb.append("> ").append(line).append("\n");
        }
        return sb.toString();
    }
    
    private String linkTo(String section) {
        return linkTo(section, section);
    }
    
    private String linkTo(String text, String section) {
        StringBuilder sb = new StringBuilder();
        String sectionLink = section.replaceAll(" ", "-").replaceAll("_", "\\_");
        sb.append("[").append(escape(text)).append("](#").append(escape(sectionLink)).append(")");
        return sb.toString();
    }
    
    static char[] escapeChars = {
           '\\', '`', '*', '_', '{', '}', '[', ']', '(', ')', '#', '+', '-', '.', '!', '|' 
    };
    
    private boolean isEscapeChar(char c) {
        for(char e : escapeChars) {
            if(e == c) {
                return true;
            }
        }
        return false;
    }
    
    
    private String escape(String text) {
        StringBuilder sb = new StringBuilder(text);
        for(int i = 0; i < sb.length();) {
            char c = sb.charAt(i);
            if(isEscapeChar(c)) {                
                sb.insert(i, "\\");
                i+=1;
            }
            i+=1;
        }
        
        return sb.toString();
    }
}
