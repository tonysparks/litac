/*
 * see license.txt
 */
package litac;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.hjson.JsonValue;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.*;

import litac.LitaOptions.TypeInfoOption;
import litac.compiler.PhaseResult;
import litac.compiler.PhaseResult.PhaseError;

/**
 * Runs a set of tests
 * 
 * @author Tony
 *
 */
public class TestSuite {

    public static class TestModule {
        public String name;
        public String program;        
    }
    
    public static class TestCase {
        public String name;
        public String code;
        public String definitions = "";
        public String error;
        public boolean disabled;
        public Boolean debug;
        public Boolean disableLines;
        public String symbolPrefix = "";
        public boolean genDoc = false;
        public TestModule[] modules;
    }
    
    public String description;
    public String program;
    public boolean includeTypeInfos;
    public boolean disabled;
    public Boolean debug;
    public Boolean disableLines = false;
    public List<TestCase> tests;
    
    private int numberOfTestsRan;
    
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
            return;
        }
        
        for(TestCase test: suite.tests) {
            System.out.println("Running test: " + test.name);
            
            if(test.disabled) {
                System.out.println("Skipping (is disabled)");
                continue;
            }
            
            if(test.modules != null) {
                for(TestModule tm : test.modules) {
                    File tmp = new File(outputDir, tm.name.replace(" ", "_") + ".lita");            
                    Files.write(tmp.toPath(), tm.program.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                    tmp.deleteOnExit();
                }
            }
            
            String fullProgram = suite.program
                                        .replace("%definitions%", test.definitions)
                                        .replace("%test%", test.code);
            
            File tmp = new File(outputDir, test.name.replace(" ", "_") + ".lita");            
            Files.write(tmp.toPath(), fullProgram.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            tmp.deleteOnExit();
            
            try {                
                LitaOptions options = new LitaOptions();
                options.buildFile = tmp;
                options.cOptions.symbolPrefix = test.symbolPrefix;
                options.run = true;
                options.generateDocs = test.genDoc;
                options.typeInfo = suite.includeTypeInfos ? TypeInfoOption.All : TypeInfoOption.None;
                options.disableLines = suite.disableLines;
                options.debugMode = test.debug != null  
                                         ? test.debug  
                                         : suite.debug != null ? suite.debug : false;
                
                String compileCmd = System.getProperty("buildCmd");
                if(compileCmd != null && !compileCmd.isEmpty()) {
                    options.cOptions.compileCmd = compileCmd;
                }
                
                System.out.println("build command: '" + options.cOptions.compileCmd + "'");
                //options.cOptions.compileCmd =
                //        "clang.exe -g -fsanitize=undefined,address -o \"%output%\" \"%input%\" -D_CRT_SECURE_NO_WARNINGS";
                //+= " -g -fsanitize=undefined,address ";
                numberOfTestsRan++;
                
                PhaseResult result = LitaC.compile(options);
                if(result.hasErrors()) {
                    for(PhaseError error : result.getErrors()) {
                        Errors.typeCheckError(error.pos, error.message);
                    }            
                } 
                
                if(result.hasErrors()) {
                    assertNotNull(test.error);
                    
                    assertTrue(result.getErrors().get(0).message.contains(test.error));
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
        
        System.out.println("Number of tests ran: " + this.numberOfTestsRan);
    }

    @Ignore
    private void singleFileTest(String filename) throws Exception {
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
        
        
        String json = JsonValue.readHjson(new InputStreamReader(TestSuite.class.getResourceAsStream(filename))).toString();
        TestSuite suite = mapper.readValue(json, TestSuite.class);
        
        runTestSuite(suite, outputDir, errorStream);
        
    }
    
    @Test
    public void singleTest() throws Exception {
        singleFileTest("/singleTest.json");        
    }
    
    @Test
    public void fileTest() throws Exception {
        singleFileTest("/json.json");        
    }
    
    
    @Test
    public void generateStdLibDocumentation() throws Exception {
        File libDir = new File("./lib");                
        File outputDir = new File(System.getProperty("user.dir") + "/output_tests");
        if(!outputDir.exists()) {
            assertTrue(outputDir.mkdirs());
        }
        
        final String program = "%definitions%\n\nfunc main(n: i32, args:**char) {}";
        Set<String> ignoreModules = new HashSet<>();
        ignoreModules.add("opengl.lita");
        ignoreModules.add("glad.lita");
        ignoreModules.add("thread.lita");
        ignoreModules.add("thread_win.lita");
        
        StringBuilder sb = new StringBuilder();
        File[] moduleFiles = libDir.listFiles(file -> file.getName().toLowerCase().endsWith(".lita"));
        
        int id = 0;
        for(File file : moduleFiles) {
            if(ignoreModules.contains(file.getName())) {
                continue;
            }
            sb.append("import \"").append(file.getName().replace(".lita", ""))
                .append("\" as id").append(id++).append("\n");
        }
        
        String fullProgram = program.replace("%definitions%", sb.toString());

        File tmp = new File(outputDir, "documentationGenerator.lita");            
        Files.write(tmp.toPath(), fullProgram.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        tmp.deleteOnExit();

                        
        LitaOptions options = new LitaOptions();
        options.buildFile = tmp;            
        options.generateDocs = true;
        options.outputDocDir = new File("./doc");                        
        String compileCmd = System.getProperty("buildCmd");
        if(compileCmd != null && !compileCmd.isEmpty()) {
            options.cOptions.compileCmd = compileCmd;
        }
        
        System.out.println("build command: '" + options.cOptions.compileCmd + "'");
        
        PhaseResult result = LitaC.compile(options);
        if(result.hasErrors()) {
            for(PhaseError error : result.getErrors()) {
                Errors.typeCheckError(error.pos, error.message);
            }            
        } 
        
    }
}
