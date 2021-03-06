/*
 * see license.txt
 */
package litac.compiler;

import java.util.*;

import litac.checker.TypeInfo;
import litac.checker.TypeInfo.*;

/**
 * Finds the appropriate Aggregate -> FieldInfo to using for "using" fields.  LitaC allows for
 * multiple levels of embedded using fields, thus the need for finding the "path" of a field from
 * a aggregate graph.
 * 
 * @author Tony
 *
 */
public class FieldPath {

    public static class FieldPathNode {
        public final AggregateTypeInfo ownerInfo;
        public final FieldInfo field;
        private boolean isRoot;
        
        public FieldPathNode(AggregateTypeInfo ownerInfo, 
                             FieldInfo field,
                             boolean isRoot) {
            this.ownerInfo = ownerInfo;
            this.field = field;
            this.isRoot = isRoot;
        }
        
        @Override
        public String toString() {
            return String.format("%s.%s", ownerInfo.getName(), field.name);
        }
        
        private boolean isAgg(TypeInfo type) {
            if (TypeInfo.isAggregate(type)) {
                return true;
            }
            
            // only only one level of pointers for using expression
            if(TypeInfo.isPtrAggregate(type)) {
                return true;
            }
            
            return false;
        }
        
        private AggregateTypeInfo getAgg(TypeInfo type) {
            AggregateTypeInfo aggInfo = type.isKind(TypeKind.Ptr) ? 
                    ((PtrTypeInfo)type).getBaseType().as()
                    : type.as();
            return aggInfo;
        }
        
        private List<FieldPathNode> edges() {
            List<FieldPathNode> edges = new ArrayList<>();
            if(isAgg(field.type)) {
                AggregateTypeInfo aggInfo = getAgg(field.type);
                                                
                List<FieldInfo> fieldInfos = isRoot ? aggInfo.fieldInfos : aggInfo.usingInfos;
                if(fieldInfos == null) {
                    return edges;
                }
                
                for(FieldInfo f : fieldInfos) {
                    if(f.type.strictEquals(this.ownerInfo)) {
                        continue;
                    }
                    if(TypeInfo.isPtrAggregate(f.type)) {
                        PtrTypeInfo ptrInfo = f.type.as();
                        if(ptrInfo.getBaseType().strictEquals(this.ownerInfo)) {
                            continue;
                        }
                    }
                    boolean isRoot = isAgg(f.type) && getAgg(f.type).hasUsingFields() || aggInfo.isUsingField(f);
                    edges.add(new FieldPathNode(aggInfo, f, isRoot));
                    
                }
            }
            
            return edges;
        }
    }
    
    private List<FieldPathNode> path;
    private FieldPathNode targetField;
    
    public FieldPath(AggregateTypeInfo ownerInfo, String field) {
        this.path = findPath(ownerInfo, field);
        if(hasPath()) {
            this.targetField = this.path.get(path.size() - 1);
        }
    }
    
    public FieldPath(AggregateTypeInfo ownerInfo, TypeInfo fieldWithType) {
        this.path = findPath(ownerInfo, fieldWithType);
        if(hasPath()) {
            this.targetField = this.path.get(path.size() - 1); 
        }
    }
    
    public boolean hasPath() {
        return this.path != null && !this.path.isEmpty();
    }
    
    public List<FieldPathNode> getPath() {
        return this.path;
    }
    
    public FieldInfo getTargetField() {
        return this.targetField != null ? this.targetField.field : null;
    }
    
    private List<FieldPathNode> findPath(AggregateTypeInfo ownerInfo, TypeInfo fieldWithType) {
        FieldInfo info = ownerInfo.getFieldUsingType(fieldWithType);
        if(info != null) {
            return Arrays.asList(new FieldPathNode(ownerInfo, info, true));
        }               
        
        if(!ownerInfo.hasUsingFields()) {
            return Collections.emptyList();
        }
        
        List<FieldPathNode> result = new ArrayList<>();
        for(FieldInfo usingInfo : ownerInfo.usingInfos) {
            FieldPathNode node = findNode(TypeInfo.getBase(usingInfo.type).as(), fieldWithType, result);
            if(node != null) {
                result.add(node);    
                break;
            }
        }
        
        return result;
    }
    
    private FieldPathNode findNode(AggregateTypeInfo ownerInfo, TypeInfo fieldWithType, List<FieldPathNode> result) {
        FieldInfo info = ownerInfo.getFieldUsingType(fieldWithType);
        if(info != null) {
            FieldPathNode node = new FieldPathNode(ownerInfo, info, false);            
            return node;            
        }               
        
        if(!ownerInfo.hasUsingFields()) {
            return null;
        }
        
        for(FieldInfo usingInfo : ownerInfo.usingInfos) {
            FieldPathNode node = findNode(TypeInfo.getBase(usingInfo.type).as(), fieldWithType, result);
            if(node != null) {
                result.add(node);    
                return node;
            }
        }
        
        return null;
    }
    
    private List<FieldPathNode> findPath(AggregateTypeInfo ownerInfo, String field) {
        FieldInfo f = ownerInfo.getField(field);
        if(f != null) {
            return Arrays.asList(new FieldPathNode(ownerInfo, f, true));
        }
        
        if(!ownerInfo.hasUsingFields()) {
            return Collections.emptyList();
        }
        
        for(FieldInfo usingInfo : ownerInfo.usingInfos) {
            List<FieldPathNode> result = aStar(new FieldPathNode(ownerInfo, usingInfo, true), field);
            if(result != null) {
                return result;
            }
        }
        
        return null;
    }
    
    private List<FieldPathNode> reconstructPath(Map<FieldPathNode, FieldPathNode> cameFrom, FieldPathNode currentNode, List<FieldPathNode> result) {
        if(cameFrom.containsKey(currentNode)) {
            /* Find the rest of the path */
            reconstructPath(cameFrom, cameFrom.get(currentNode), result);
            result.add(currentNode);    /* Notice this is after the recursive call - we want the results to be in descending order */
            
            return result;
        }
        
        return result;
    }
    
    
    private List<FieldPathNode> aStar(FieldPathNode start, String goal) {
        Map<FieldPathNode, FieldPathNode> cameFrom = new HashMap<>();
        
        Set<FieldPathNode> closedSet = new HashSet<>();
        List<FieldPathNode> openSet = new ArrayList<>();
        
        /* Push the start node so we have a starting point */
        openSet.add(start);
        
        /*
         * Until we run out of nodes of interest, lets compile our path.  If there
         * are no more nodes of interest, and we have not found our goal node, this means
         * there is no path.
         */
        while(!openSet.isEmpty()) {
            
            /* Get the most optimal node to work from */
            FieldPathNode x = openSet.get(0); 
            
            /* If this node is the goal, we are done */
            if(x.field.name.equals(goal)) {
                
                /* optimal path from start to finish */               
                List<FieldPathNode> path = reconstructPath(cameFrom, x, new ArrayList<>());
                path.add(0, start);
                return path;
            }
            
            /* Remove this node so we don't visit it again */
            openSet.remove(x);
            closedSet.add(x);
            
            List<FieldPathNode> edges = x.edges();            
            for(int i = 0; i < edges.size(); i++) {
                FieldPathNode edge = edges.get(i);     
                if(edge == null) {
                    continue;
                }
                
                FieldPathNode y = edge;
                
                /* If this node has been visited before, ignore it and move on */
                if(y == null || closedSet.contains(y)) {
                    continue;
                }
                    
                /* If this neighbor has not been tested, lets go ahead and add it */
                if(!openSet.contains(y)) {
                    openSet.add(y);
                    cameFrom.put(y, x);
                }                                   
            }
        }
        
        return null;    /* No path found */
    }
}
