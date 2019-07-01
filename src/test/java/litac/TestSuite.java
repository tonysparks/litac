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
import litac.compiler.BackendOptions;

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
        public String definitions = "";
        public String error;
        public boolean disabled;
    }
    
    public String description;
    public String program;
    public boolean includeTypeInfos;
    public boolean disabled;
    public List<TestCase> tests;
    
    
    private void runTestSuite(TestSuite suite, File outputDir, ByteArrayOutputStream errorStream) throws Exception {
        System.out.println("\n\n\n");
        System.out.println("*******************************************");
        System.out.println("*******************************************");
        System.out.println("\n");
        System.out.println("Running suite: " + suite.description);
        System.out.println("\n");
        System.out.println("*******************************************");
        System.out.println("*******************************************");
        
        if(suite.disabled) {
            System.out.println("Skipping (is disabled)");
        }
        
        for(TestCase test: suite.tests) {
            System.out.println("Running test: " + test.name);
            
            if(test.disabled) {
                System.out.println("Skipping (is disabled)");
            }
            
            String fullProgram = suite.program
                                        .replace("%definitions%", test.definitions)
                                        .replace("%test%", test.code);
            
            File tmp = new File(outputDir, test.name.replace(" ", "_") + ".lita");            
            Files.write(tmp.toPath(), fullProgram.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            tmp.deleteOnExit();
            
            try {                
                BackendOptions options = new BackendOptions();
                options.buildFile = tmp;
                options.cOptions.symbolPrefix = "";
                options.run = true;
                options.typeInfo = suite.includeTypeInfos;
                
                PhaseResult result = LitaC.compile(options);
                if(result.hasErrors()) {
                    for(PhaseError error : result.getErrors()) {
                        Errors.typeCheckError(error.stmt, error.message);
                    }            
                } 
                
                if(result.hasErrors()) {
                    assertNotNull(test.error);
                    
                    for(PhaseError error : result.getErrors()) {                        
                        assertTrue(error.message.contains(test.error));        
                    }            
                }
                else {
                    assertNull(test.error);
                    assertTrue(errorStream.toString("UTF-8").isEmpty());
                }
            }
            catch(Exception e) {
                if(test.error == null) {
                    throw e;
                }
                
                
                assertTrue(e.getMessage().contains(test.error));
            }
            finally {
                errorStream.reset();
            }
        }
    }
    
    @Test
    public void test() throws Exception {
        File testDir = new File("./src/test/resources");
        
        ObjectMapper mapper = new ObjectMapper();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        PrintStream errStream = System.err;
        OutputStream oStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                errorStream.write(b);
                errStream.write(b);
            }
        };
        PrintStream pStream = new PrintStream(oStream, true, "UTF-8");
        System.setErr(pStream);
        
        File outputDir = new File(System.getProperty("user.dir") + "/output_tests");
        if(!outputDir.exists()) {
            assertTrue(outputDir.mkdirs());
        }
        
        File[] testFiles = testDir.listFiles(file -> file.getName().toLowerCase().endsWith(".json"));
        for(File testFile : testFiles) {
            String json = JsonValue.readHjson(new FileReader(testFile)).toString();
            TestSuite suite = mapper.readValue(json, TestSuite.class);
            
            runTestSuite(suite, outputDir, errorStream);
        }
    }

    @Test
    public void singleTest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        PrintStream errStream = System.err;
        OutputStream oStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                errorStream.write(b);
                errStream.write(b);
            }
        };
        PrintStream pStream = new PrintStream(oStream, true, "UTF-8");
        System.setErr(pStream);
        
        File outputDir = new File(System.getProperty("user.dir") + "/output_tests");
        if(!outputDir.exists()) {
            assertTrue(outputDir.mkdirs());
        }
        
        
        String json = JsonValue.readHjson(new InputStreamReader(TestSuite.class.getResourceAsStream("/singleTest.json"))).toString();
        TestSuite suite = mapper.readValue(json, TestSuite.class);
        
        runTestSuite(suite, outputDir, errorStream);
        
    }
}
