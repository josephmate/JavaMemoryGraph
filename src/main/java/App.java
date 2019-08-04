import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.netbeans.lib.profiler.heap.*;
import java.io.*;
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

	public enum DisplayType {
		GraphStream,
		GraphViz
	}

	@Option(name="-hprof", usage="The filesystem path pointing to the hprof file to dump", required=true)
	private String hprofPath = null;

	@Option(name="-displayType", usage="Library used to display")
	private DisplayType displayType = DisplayType.GraphViz;

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

    private HeapGraphDisplayer getDisplayer() {
		switch (displayType) {
			case GraphStream:
				return new GraphStreamDisplayer();
			case GraphViz:
				return new GraphVizDisplayer();
			default:
				throw new IllegalArgumentException(displayType + ": is not a supported displayType");
		}
	}

	private void displayHeapGraph(HeapGraph heapGraph) {
		HeapGraphDisplayer displayer;
	}

    public void run() throws IOException {
		getDisplayer().display(HeapGraph.generateGraph(hprofPath));
    }
}
