/*
 * see license.txt
 */
package litac.ast;

import java.util.ArrayList;
import java.util.List;

import litac.util.Tuple;


/**
 * Abstract Syntax Tree Node
 * 
 * @author Tony
 *
 */
public abstract class Node {

    private Node parentNode;
    private int lineNumber;
    private String sourceLine;
    private String sourceFile;
        
    public abstract void visit(NodeVisitor v);
    
    protected abstract Node doCopy();
    
    @SuppressWarnings("unchecked")
    public <T extends Node> T copy() {
        Node node = doCopy();
        node.setLineNumber(this.lineNumber);
        node.setSourceLine(this.sourceLine);
        node.setSourceFile(this.sourceFile);
        return (T)node;
    }
    
    
    
    /**
     * @return the sourceFile
     */
    public String getSourceFile() {
        return sourceFile;
    }
    
    /**
     * @param sourceFile the sourceFile to set
     */
    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }
    
    /**
     * @return the sourceLine
     */
    public String getSourceLine() {
        return sourceLine;
    }
    
    /**
     * @param sourceLine the sourceLine to set
     */
    public void setSourceLine(String sourceLine) {
        this.sourceLine = sourceLine;
    }
    
    /**
     * @return the lineNumber
     */
    public int getLineNumber() {
        return lineNumber;
    }
    
    /**
     * @param lineNumber the lineNumber to set
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
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
