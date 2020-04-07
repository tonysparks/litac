/*
 * see license.txt
 */
package litac.ast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import litac.parser.tokens.Token;
import litac.util.Tuple;


/**
 * Abstract Syntax Tree Node
 * 
 * @author Tony
 *
 */
public abstract class Node {

    public static class SrcPos {
        public File sourceFile;
        public String sourceName;
        public String sourceLine;
        public int lineNumber;
        public int position;
        public Token token;
        
        public SrcPos() {            
        }
        
        public SrcPos(File sourceFile,
                      String sourceName, 
                      String sourceLine, 
                      int lineNumber,
                      int position,
                      Token token) {
            this.sourceFile = sourceFile;
            this.sourceName = sourceName;
            this.sourceLine = sourceLine;
            this.lineNumber = lineNumber;
            this.position = position;
            this.token = token;
        }
        
        @Override
        public String toString() {        
            return String.format("'%s' at line %d:\n%s", this.sourceName, this.lineNumber, this.sourceLine);
        }
    }
    
    public static class Identifier extends Node {
        public String identifier;
        
        public Identifier(String identifier) {
            super();
            this.identifier = identifier;
        }
        
        @Override
        public void visit(NodeVisitor v) {
        }
        
        @Override
        protected Node doCopy() {
            return new Identifier(this.identifier);
        }
    }
    
    private Node parentNode;
    private SrcPos pos;
    
    public Node() {
        this.pos = new SrcPos();
    }
    
    public abstract void visit(NodeVisitor v);
    
    protected abstract Node doCopy();
    
    @SuppressWarnings("unchecked")
    public <T extends Node> T copy() {
        Node node = doCopy();
        node.pos = this.pos;
        return (T)node;
    }

    @SuppressWarnings("unchecked")
    public <T extends Node> T setSrcPos(SrcPos pos) {
        this.pos = pos;
        return (T) this;
    }
    
    /**
     * @return the pos
     */
    public SrcPos getSrcPos() {
        return pos;
    }
    
    public File getSourceFile() {
        return this.pos.sourceFile;
    }
    
    public void setSourceFile(File file) {
        this.pos.sourceFile = file;
    }
    
    /**
     * @return the sourceName
     */
    public String getSourceName() {
        return this.pos.sourceName;
    }
    
    /**
     * @param sourceName the sourceName to set
     */
    public void setSourceName(String sourceName) {
        this.pos.sourceName = sourceName;
    }
    
    /**
     * @return the sourceLine
     */
    public String getSourceLine() {
        return this.pos.sourceLine;
    }
    
    /**
     * @param sourceLine the sourceLine to set
     */
    public void setSourceLine(String sourceLine) {
        this.pos.sourceLine = sourceLine;
    }
    
    /**
     * @return the lineNumber
     */
    public int getLineNumber() {
        return this.pos.lineNumber;
    }
    
    /**
     * @param lineNumber the lineNumber to set
     */
    public void setLineNumber(int lineNumber) {
        this.pos.lineNumber = lineNumber;
    }
    
    public void setPosition(int position) {
        this.pos.position = position;
    }
    
    public void setToken(Token token) {
        this.pos.token = token;
    }
    
    public void updateSrcPos(SrcPos srcPos) {
        this.pos.lineNumber = srcPos.lineNumber;
        this.pos.position = srcPos.position;
        this.pos.sourceFile = srcPos.sourceFile;
        this.pos.sourceName = srcPos.sourceName;
        this.pos.sourceLine = srcPos.sourceLine;
        this.pos.token = srcPos.token;
    }
    
    /**
     * @param parentNode the parentNode to set
     */
    public void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }
    
    /**
     * @return the parentNode
     */
    public Node getParentNode() {
        return parentNode;
    }

    protected <T extends Node> T becomeParentOf(T node) {
        if(node != null) {
            node.setParentNode(this);
        }
        return node;
    }
    
    protected <T extends Node> List<T> becomeParentOf(List<T> nodes) {
        if(nodes != null) {
            for(int i = 0; i < nodes.size(); i++) {
                becomeParentOf(nodes.get(i));
            }
        }
        return nodes;
    }
    
    protected <T extends Node, Y extends Node> List<Tuple<T, Y>> becomeParentOfByTuples(List<Tuple<T, Y>> nodes) {
        if(nodes != null) {
            for(int i = 0; i < nodes.size(); i++) {
                Tuple<T, Y> tuple = nodes.get(i);
                becomeParentOf(tuple.getFirst());
                becomeParentOf(tuple.getSecond());
            }
        }
        return nodes;
    }
    
    protected <T extends Node> List<T> copy(List<T> values) {
        if(values == null) {
            return null;
        }
        
        List<T> clonedValues = new ArrayList<>(values.size());
    
        for(T v : values) {
            clonedValues.add(v.copy());
        }
        
        return clonedValues;
    }
    
    protected <T extends Node> T copy(T node) {
        if(node == null) {
            return null;
        }
        
        return node.copy();
    }

}
