import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.netbeans.lib.profiler.heap.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class App {
	
	@Option(name="-hprof", usage="The filesystem path pointing to the hprof file to dump", required=true)
	private String hprofPath;
	
    public static void main(String[] args) throws IOException {
    	App bean = new App();
        CmdLineParser parser = new CmdLineParser(bean);
        try {
                parser.parseArgument(args);
                bean.run();
        } catch (CmdLineException e) {
            // handling of wrong arguments
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
    }
    
    public void run() throws FileNotFoundException, IOException {
	    Heap heap = HeapFactory.createHeap(new File(hprofPath));
    }
    
    private Instance getInstanceFromFieldValue(FieldValue fieldValue) {
    	return ((ObjectFieldValue)fieldValue).getInstance();
    }
}