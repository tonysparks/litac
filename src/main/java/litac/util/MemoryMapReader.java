/*
 * see license.txt
 */
package litac.util;

import java.io.*;
import java.nio.*;

import litac.util.Profiler.Segment;

/**
 * @author Tony
 *
 */
public class MemoryMapReader implements AutoCloseable {

    
    private ByteBuffer buffer;
    private byte[] cache = new byte[256];
    
    public MemoryMapReader(File file) {
        try {
            //System.out.println("Loading file: " + file);
            //try(Segment s = Profiler.startSegment("LD:" + file.getName())) {
            try(RandomAccessFile raf = new RandomAccessFile(file, "r")) {
                int size = (int)raf.length();
                
                byte[] data = new byte[size];
                int length = raf.read(data, 0, size);
                this.buffer = ByteBuffer.wrap(data, 0, length >= 0 ? length : 0);
                
                // Bug with JVM that doesn't close the byte buffer until GC
                //this.buffer = this.raf.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, this.raf.length());
            }
            //}
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public MemoryMapReader(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    private void ensureCapacity(int newCapacity) {
        if(cache.length >= newCapacity) {
            return;
        }
        byte[] newCache = new byte[newCapacity];
        System.arraycopy(cache, 0, newCache, 0, cache.length);
        cache = newCache;        
    }
    
    public String readLine() throws IOException {        
        int startingPos = this.buffer.position();
        int limit = this.buffer.limit();
        int endPos = limit;
        if(startingPos >= endPos) {
            return null;
        }
        
        int pos = this.buffer.position();
        for(; pos < limit; pos++) {
            int c = this.buffer.get(pos) & 0xFF;
            if(c == 10 || c == 13) {   
                endPos = pos;
                
                if(pos+1 < this.buffer.capacity()) {
                    int nextChar = this.buffer.get(pos+1) & 0xFF;
                    if(nextChar == 10) {
                        pos += 1;
                    }
                }
                break;
            }            
        }
                
        ensureCapacity(endPos - startingPos);
        this.buffer.position(startingPos);        
        this.buffer.get(this.cache, 0, endPos - startingPos);
        if((endPos + 1) <= limit) {
            this.buffer.position(endPos + 1);
        }
        return new String(this.cache, 0, endPos - startingPos);
    }
    
    
    @Override
    public void close() throws IOException {        
    }
}
