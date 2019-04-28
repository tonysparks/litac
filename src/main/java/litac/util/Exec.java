/*
 * see license.txt
 */
package litac.util;

import java.io.File;

/**
 * @author Tony
 *
 */
public class Exec {

    public static int run(File workingDir, String command) throws Exception {        
        ProcessBuilder pb = new ProcessBuilder();
        pb.command(command.split(" "));
        pb.directory(workingDir);
        pb.inheritIO();
        
        Process process = pb.start();
        int status = process.waitFor();        
                
        return status;
    }

}
