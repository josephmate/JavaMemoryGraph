import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.netbeans.lib.profiler.heap.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Uses GraphStream to display the graph.
 * http://graphstream-project.org/gs-talk/demos.html#/title-slide
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
	    Graph graph = new SingleGraph("Memory Graph");
	    graph.setStrict(false);
	    
		@SuppressWarnings("unchecked")
		List<JavaClass> javaClasses = heap.getAllClasses();
	    for(JavaClass javaClass : javaClasses) {
	    	String fromClass = javaClass.getName();
	    	graph.addNode(fromClass);
			@SuppressWarnings("unchecked")
			List<Instance> instances = javaClass.getInstances();
	    	for(Instance instance : instances) {
	    		@SuppressWarnings("unchecked")
				List<FieldValue> fieldValues = instance.getFieldValues();
	    		for(FieldValue fieldValue : fieldValues) {
	    			String toClass = fieldValue.getField().getType().getName();
	        		if ("object".equals(toClass)) {
	        			Instance fieldInstance = getInstanceFromFieldValue(fieldValue);
	        			if(fieldInstance != null && fieldInstance.getJavaClass() != null) {
	        				toClass = fieldInstance.getJavaClass().getName();
	        			}
	        		}
	    			
	    	    	graph.addNode(toClass);
	    			graph.addEdge(fromClass+toClass, fromClass, toClass, true);
	    		}
	    	}
	    }
	    
	    graph.display();
    }
    
    private Instance getInstanceFromFieldValue(FieldValue fieldValue) {
    	return ((ObjectFieldValue)fieldValue).getInstance();
    }
}
