/*
 * see license.txt
 */
package litac.checker;

import java.util.*;

import litac.ast.Decl;
import litac.checker.TypeInfo.*;

/**
 * @author Tony
 *
 */
public class DependencyGraph {

    static final int PENDING  = 0;
    static final int RESOLVED = 1;
    
    private class DeclState {
        Decl decl;
//        int referenced = 0;
        List<DeclState> dependsOn;
        int state = PENDING;
        
        public DeclState(Decl d) {
            this.decl = d;
        }
                
        boolean hasUnresolved() {
            if(dependsOn == null || dependsOn.isEmpty()) {
                return false;
            }
            
            return dependsOn.stream().anyMatch(d -> d.state != RESOLVED);
        }
    }
    
    private List<Decl> sortedDependencies;
    private List<Decl> sortedAggregates;
    
    private Map<Decl, DeclState> states;
    private PhaseResult result;
    

    public DependencyGraph(PhaseResult result) {
        this.result = result;
        this.sortedDependencies = new ArrayList<>();
        this.sortedAggregates = new ArrayList<>();
        this.states = //new IdentityHashMap<>();
                new LinkedHashMap<>();
    }

    private void buildDependsOn(DeclState state) {
        if(TypeInfo.isAggregate(state.decl.type)) {
            state.dependsOn = new ArrayList<>();
            
            AggregateTypeInfo aggInfo = state.decl.type.as();
            for(FieldInfo field : aggInfo.fieldInfos) {
                if(TypeInfo.isAggregate(field.type)) {
                    Symbol sym = field.type.getResolvedType().sym;
                    if(sym != null) {
                        DeclState other = states.get(sym.decl);
                        if(other != null) {
                            state.dependsOn.add(other);
//                            other.referenced++;
                        }
                    }
                }
            }
        }
        else {
            state.state = RESOLVED;
        }
    }
    
    private void buildStates(List<Decl> declarations) {
        for(Decl d : declarations) {            
            switch(d.kind) {
                case CONST:
                case VAR:
                case FUNC:
                case TYPEDEF:
                    this.sortedDependencies.add(d);
                    break;
                case ENUM:
                    // don't include enums, as they are implemented in the 
                    // forward declarations
                    break;
                case STRUCT:
                case UNION:
                    states.put(d, new DeclState(d));
                    break;
                default:
                    break;
            }
        }
    }
    
    private boolean resolve(DeclState s) {
        if(s.state == RESOLVED) {
            return true;
        }
        
        if(!s.hasUnresolved()) {
            s.state = RESOLVED;
            sortedAggregates.add(s.decl);
            return true;
        }
        
        return false;
    }
    
    private List<DeclState> sort(Collection<DeclState> states) {
        List<DeclState> pending = new ArrayList<>();
        for(DeclState s : states) {
            if(!resolve(s)) {
                pending.addAll(sort(s.dependsOn));
                if(!resolve(s)) {
                    pending.add(s);
                }
            }
        }
        
        return pending;
    }
    
    
    public List<Decl> sort(List<Decl> declarations) {
        buildStates(declarations);
        states.values().forEach(this::buildDependsOn);        
    
        List<DeclState> unresolved = sort(states.values());
        unresolved = sort(unresolved);
        if(!unresolved.isEmpty()) {
            for(DeclState s : unresolved) {
                this.result.addError(s.decl, "could not resolve depenceny alignment for '%s'", s.decl.name);
            }
        }
        
        this.sortedDependencies.addAll(0, this.sortedAggregates);
        
        return this.sortedDependencies;
    }
}
