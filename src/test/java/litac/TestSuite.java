/*
 * see license.txt
 */
package litac;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.hjson.JsonValue;
import org.junit.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.*;

import litac.LitaOptions.TypeInfoOption;
import litac.compiler.PhaseResult;
import litac.compiler.PhaseResult.PhaseError;
import litac.util.Profiler;
import litac.util.Profiler.Segment;

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
    private List<File> filesToBeDeleted = new ArrayList<>();
    
    @After
    public void cleanup() {
        for(File tmp : filesToBeDeleted) {
            try {
                Files.deleteIfExists(tmp.toPath());
            }
            catch(Exception e) {                
            }
        }
    }
    
    private void runTestSuite(TestSuite suite, File outputDir, ByteArrayOutputStream errorStream) throws Exception {
        System.out.println("\n\n\n");
        System.out.println("Running suite: " + suite.description);        
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
                    filesToBeDeleted.add(tmp);
                }
            }
            
            String fullProgram = suite.program
                                        .replace("%definitions%", test.definitions)
                                        .replace("%test%", test.code);
            
            File tmp = new File(outputDir, test.name.replace(" ", "_") + ".lita");            
            Files.write(tmp.toPath(), fullProgram.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            filesToBeDeleted.add(tmp);
            
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
                
                //System.out.println("build command: '" + options.cOptions.compileCmd + "'");
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
        singleFileTest("/aggregates.json");        
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
    
    
    @Test
    public void profileTest() throws Exception {
        int count = 1;//5_000;
        
        LitaOptions options = new LitaOptions();
        options.buildFile = new File("C:\\Users\\antho\\git\\realm\\src\\main.lita");
        options.cOnly = true;
        options.debugMode = true;
        options.outputDir = new File("C:\\Users\\antho\\eclipse-workspace\\litac\\output_tests");
        options.profile = true;
        for(int i = 0; i < count; i++) {
            //System.out.println("Building instance: " + i);
            //Segment s = Profiler.startSegment("Build #" + i);
            PhaseResult result = LitaC.compile(options);
            if(result.hasErrors()) {
                for(PhaseError error : result.getErrors()) {
                    Errors.typeCheckError(error.pos, error.message);
                }            
            } 
            
            printProfileResults();
            //s.close();
            Profiler.clear();
            //long msec = s.getDeltaTimeNSec() / 1_000_000;
            //System.out.printf("Building %s %d\n", s.name, msec);
        }
    }
    
    private static void printProfileResults() {
        long totalTime = 0;
        for(Segment s : Profiler.profiledSegments()) {
            totalTime += s.getDeltaTimeNSec();                                     
        }
        
        System.out.printf("\n");
        System.out.printf("%-20s %-20s %10s\n", "Segment", "Time (NanoSec)", "% of Total");
        System.out.printf("======================================================\n");
        for(Segment s : Profiler.profiledSegments()) {
            long delta = s.getDeltaTimeNSec();
            int percentage = 0;
            if(totalTime > 0) {
                percentage = (int) (((double)delta / (double)totalTime) * 100);
            }
            
            if(s.isTop) {
                System.out.printf("%-20s %15d %10d%%\n", s.name, delta, percentage);
            }
            
            if(s.children.isEmpty()) {
                continue;
            }
            
            long tt = 0;            
            for(Segment c : s.children) {
                tt += c.getDeltaTimeNSec();
            }
            
            for(Segment c : s.children) {
                long childDelta = c.getDeltaTimeNSec();
                int childPercentage = 0;
                if(tt > 0) {
                    childPercentage = (int) (((double)childDelta / (double)tt) * 100);
                }
                
                System.out.printf(" %-19s %15d %10d%%\n", c.name, childDelta, childPercentage);
            }
            
            System.out.printf("%20s %15d ns (%d ms)\n", "Total Time:", tt, tt / 1_000_000);
        }
        
        long totalTimeForLoad = 0;
        for(Segment s : Profiler.profiledSegments()) {
            if(!s.name.startsWith("LD:")) {
                continue;
            }
            totalTimeForLoad += s.getDeltaTimeNSec();                        
        }
        
        long totalTimeForParser = 0;
        for(Segment s : Profiler.profiledSegments()) {
            if(!s.name.startsWith("PR:")) {
                continue;
            }
            totalTimeForParser += s.getDeltaTimeNSec();                        
        }
        
        long totalTimeForLex = 0;
        for(Segment s : Profiler.profiledSegments()) {
            if(!s.name.startsWith("LX:")) {
                continue;
            }
            totalTimeForLex += s.getDeltaTimeNSec();                        
        }
                
        System.out.println();
        System.out.printf("%20s %15d ns (%d ms)\n", "Total Time:", totalTime, totalTime / 1_000_000);
        System.out.printf("%20s %15d ns (%d ms)\n", "Total Load Time:", totalTimeForLoad, totalTimeForLoad / 1_000_000);
        System.out.printf("%20s %15d ns (%d ms)\n", "Total Parser Time:", totalTimeForParser, totalTimeForParser / 1_000_000);
        System.out.printf("%20s %15d ns (%d ms)\n", "Total Lex Time:", totalTimeForLex, totalTimeForLex / 1_000_000);
    }
}
