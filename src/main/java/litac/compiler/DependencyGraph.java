/*
 * see license.txt
 */
package litac.compiler;

import java.util.*;

import litac.ast.Decl;
import litac.checker.*;
import litac.checker.TypeInfo.*;

/**
 * Sorts the programs declarations so that the CGen compiler can output them
 * in an order that allows the C program to compile.
 * 
 * The sort order:
 * 1) sorted aggregates (attempts to order based on dependency order)
 * 2) Global declarations
 * 3) Functions/Typedefs
 * 
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
    
    private List<Decl> primitiveGlobals;
    private List<Decl> sortedGlobals;
    private List<Decl> sortedDependencies;
    private List<Decl> sortedAggregates;
    
    private Map<Decl, DeclState> states;
    private PhaseResult result;
    

    public DependencyGraph(PhaseResult result) {
        this.result = result;
        this.sortedGlobals = new ArrayList<>();
        this.sortedDependencies = new ArrayList<>();
        this.sortedAggregates = new ArrayList<>();
        this.primitiveGlobals = new ArrayList<>();
        this.states = new LinkedHashMap<>();
    }

    private void buildDependsOn(DeclState state) {
        if(TypeInfo.isAggregate(state.decl.sym.type)) {
            state.dependsOn = new ArrayList<>();
            
            AggregateTypeInfo aggInfo = state.decl.sym.type.as();
            for(FieldInfo field : aggInfo.fieldInfos) {
                if(TypeInfo.isAggregate(field.type)) {
                    Symbol sym = field.type.sym;
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
                    if(d.sym.isConstant() && TypeInfo.isPrimitive(d.sym.type)) {
                        this.primitiveGlobals.add(d);
                    }
                    else {
                        this.sortedGlobals.add(d);
                    }
                    break;
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
        
        List<Decl> sorted = new ArrayList<>();
        sorted.addAll(this.primitiveGlobals);
        sorted.addAll(this.sortedAggregates);
        sorted.addAll(this.sortedGlobals);
        sorted.addAll(this.sortedDependencies);
        
        return sorted;
    }
}
