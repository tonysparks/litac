/*
 * see license.txt
 */
package litac;

import java.io.*;
import java.nio.file.*;
import java.util.List;

import org.hjson.JsonValue;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.*;

import litac.checker.PhaseResult;
import litac.checker.PhaseResult.PhaseError;

/**
 * Runs a set of tests
 * 
 * @author Tony
 *
 */
public class TestSuite {

    public static class TestCase {
        public String name;
        public String code;
        public String error;
    }
    
    public String description;
    public String program;
    public List<TestCase> tests;
    
    
    @Test
    public void test() throws Exception {
        String json = JsonValue.readHjson(new InputStreamReader(TestSuite.class.getResourceAsStream("/declarations.json"))).toString();
        ObjectMapper mapper = new ObjectMapper();
        TestSuite suite = mapper.readValue(json, TestSuite.class);
        
        File outputDir = new File(System.getProperty("user.dir") + "/output_tests");
        if(!outputDir.exists()) {
            assertTrue(outputDir.mkdirs());
        }
        
        System.out.println("Running suite: " + suite.description);
        for(TestCase test: suite.tests) {
            System.out.println("Running test: " + test.name);
            String fullProgram = suite.program.replace("%test%", test.code);
            
            File tmp = new File(outputDir, test.name.replace(" ", "_") + ".lita");            
            Files.write(tmp.toPath(), fullProgram.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            tmp.deleteOnExit();
            
            try {                
                PhaseResult result = LitaC.compile(tmp);
                
                if(result.hasErrors()) {
                    for(PhaseError error : result.getErrors()) {
                        Errors.typeCheckError(error.stmt, error.message);
                    }            
                } 
                
                if(result.hasErrors()) {
                    assertNotNull(test.error);
                    
                    for(PhaseError error : result.getErrors()) {
                        Errors.typeCheckError(error.stmt, error.message);
                        assertTrue(error.message.contains(test.error));        
                    }            
                } 
            }
            catch(Exception e) {
                if(test.error == null) {
                    throw e;
                }
                
                
                assertTrue(e.getMessage().contains(test.error));
            }
        }
    }

}
