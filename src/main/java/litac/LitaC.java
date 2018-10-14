/*
 * see license.txt
 */
package litac;

import java.io.File;
import java.io.FileReader;

import litac.ast.Stmt.ProgramStmt;
import litac.c.Transpiler;
import litac.c.Transpiler.TranspilerOptions;
import litac.parser.Parser;
import litac.parser.Scanner;
import litac.parser.Source;

/**
 * @author Tony
 *
 */
public class LitaC {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        if(args.length < 1) {
            System.out.println("<usage> litac.exe [source files]");
            return;
        }
        
        Scanner scanner = new Scanner(new Source(new FileReader(new File(args[0]))));
        Parser parser = new Parser(scanner);
        ProgramStmt program = parser.parseProgram();
        
        
        Transpiler.transpile(program, new TranspilerOptions());
        

    }

}
