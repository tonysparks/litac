/*
 * see license.txt
 */
package litac.util;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;


/**
 * Simple profile timer, used to capture run times of the compiler
 */
public class Profiler {

    public static class Segment implements AutoCloseable {
        public final String name;
        private long startTime;
        private long endTime;
        public List<Segment> children;
        public boolean isTop;
        
        Segment(String name) {
            this.name = name;
            this.children = new ArrayList<>();
            this.isTop = false;
        }
        
        public void start() {           
            this.startTime = System.nanoTime();
        }
        
        @Override
        public void close() { 
            this.endTime = System.nanoTime();
            
            if(!segments.isEmpty()) {
                if(segments.peek() == this) {
                    segments.pop();
                }
            }
            profiled.add(this);
        }
        
        public long getDeltaTimeNSec() {
            return this.endTime - this.startTime;
        }
    }
    
    private static Queue<Segment> profiled = new ConcurrentLinkedDeque<>();
    private static Deque<Segment> segments = new ConcurrentLinkedDeque<>(); 
    
    /**
     * Prepares the profiler to be ran again.  Removes any previous stored state
     */
    public static void clear() {
        profiled.clear();
        segments.clear();
    }
    
    /**
     * @return the profiled code segments
     */
    public static Collection<Segment> profiledSegments() {
        return new ArrayList<>(profiled);
    }
    
    
    public static Segment startSegment(String segmentName) {
        Segment s = new Segment(segmentName);
        if(segments.isEmpty()) {
            s.isTop = true;
            segments.push(s);
        }
        else {
            segments.peek().children.add(s);
        }
        s.start();
        return s;
    }
    
//    public static void endSegment() {
//        Segment s = segments.pop();
//        s.close();
//    }
}
