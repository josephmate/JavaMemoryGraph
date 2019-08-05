import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.Map;

public class GraphStreamDisplayer implements HeapGraphDisplayer {

    public void display(HeapGraph heapGraph) {
        Graph graph = new SingleGraph("Memory Graph");
        graph.display();

        for(Map.Entry<String,Long> entry : heapGraph.getNodeCounts().entrySet()) {
            String className = entry.getKey();
            long count = entry.getValue();
            Node node = graph.addNode(entry.getKey());
            node.addAttribute("ui.label", className + ": " + count);
        }

        for(Map.Entry<String, Map<String, Long>> edgeEntry : heapGraph.getEdgeCounts().entrySet()) {
            String fromClass = edgeEntry.getKey();
            Map<String, Long> subMap = edgeEntry.getValue();

            for(Map.Entry<String,Long> entry :subMap.entrySet()) {
                String toClass = entry.getKey();
                long count = entry.getValue();
                String edgeId = fromClass + "->" + toClass;
                Edge edge = graph.addEdge(edgeId, fromClass, toClass, true);
                edge.setAttribute("ui.label", count);
            }
        }

        graph.display();
    }

}
