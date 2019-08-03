import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.netbeans.lib.profiler.heap.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Uses GraphStream to display the graph.
 * http://graphstream-project.org/gs-talk/demos.html#/title-slide
 *
 * Alternative graph visualizations to explore:
 * - Gephi https://gephi.org/toolkit/
 * - JUNG http://jung.sourceforge.net/
 * - jgraphx https://github.com/jgraph/jgraphx
 * - graphviz-java https://github.com/nidi3/graphviz-java
 *
 */
public class App {
	
	@Option(name="-hprof", usage="The filesystem path pointing to the hprof file to dump", required=true)
	private String hprofPath;
	
    public static void main(String[] args) throws IOException, InterruptedException {
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

    private static class HeapGraph {
		private Map<String, Map<String, Long>> edgeCount = new HashMap<>();
		private Map<String, Long> nodeCount = new HashMap<>();

		public void addNode(String className) {
			nodeCount.put(className, nodeCount.getOrDefault(className, 0L) + 1L);
		}

		public void addEdge(String fromClass, String toClass) {
			Map<String, Long> subMap = edgeCount.get(fromClass);
			if(subMap == null) {
				subMap = new HashMap<>();
				edgeCount.put(fromClass, subMap);
			}
			subMap.put(toClass, subMap.getOrDefault(toClass, 0L) + 1L);
		}
	}

	private HeapGraph generateGraph() throws IOException {
		HeapGraph heapGraph = new HeapGraph();
		Heap heap = HeapFactory.createHeap(new File(hprofPath));

		@SuppressWarnings("unchecked")
		List<JavaClass> javaClasses = heap.getAllClasses();
		for(JavaClass javaClass : javaClasses) {
			String fromClass = javaClass.getName();
			@SuppressWarnings("unchecked")
			List<Instance> instances = javaClass.getInstances();
			for(Instance instance : instances) {
				heapGraph.addNode(fromClass);
				@SuppressWarnings("unchecked")
				List<FieldValue> fieldValues = instance.getFieldValues();
				for(FieldValue fieldValue : fieldValues) {
					String toClass = fieldValue.getField().getType().getName();
					if ("object".equals(toClass)) {
						Instance fieldInstance = getInstanceFromFieldValue(fieldValue);
						if(fieldInstance != null && fieldInstance.getJavaClass() != null) {
							toClass = fieldInstance.getJavaClass().getName();
						} else {
							toClass = "unhandled case";
							heapGraph.addNode(toClass);
						}
					} else {
						// primative, so it will have no out going edges
						heapGraph.addNode(toClass);
					}
					heapGraph.addEdge(fromClass, toClass);
				}
			}
		}

    	return heapGraph;
	}

	private void displayHeapGraph(HeapGraph heapGraph) {
		Graph graph = new SingleGraph("Memory Graph");
		//graph.setStrict(false);
		graph.display();

		for(Map.Entry<String,Long> entry : heapGraph.nodeCount.entrySet()) {
			String className = entry.getKey();
			long count = entry.getValue();
			Node node = graph.addNode(entry.getKey());
			node.addAttribute("ui.label", className + ": " + count);
		}

		for(Map.Entry<String, Map<String, Long>> edgeEntry : heapGraph.edgeCount.entrySet()) {
			String fromClass = edgeEntry.getKey();
			Map<String, Long> subMap = edgeEntry.getValue();

			for(Map.Entry<String,Long> entry :subMap.entrySet()) {
				String toClass = entry.getKey();
				long count = entry.getValue();
				String edgeId = fromClass + "->" + toClass;
				System.out.println(edgeId + ", count: " + count);
				Edge edge = graph.addEdge(edgeId, fromClass, toClass, true);
				System.out.println("edge: " + edge);
				edge.setAttribute("ui.label", count);
			}
		}

		graph.display();
	}

    public void run() throws IOException, InterruptedException {
    	displayHeapGraph(generateGraph());
    }
    
    private Instance getInstanceFromFieldValue(FieldValue fieldValue) {
    	return ((ObjectFieldValue)fieldValue).getInstance();
    }
}
