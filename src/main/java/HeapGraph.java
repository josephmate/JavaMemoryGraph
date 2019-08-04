import org.netbeans.lib.profiler.heap.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tracks the aggregated memory graph in our own data structure to make
 * using multiple display tools easier.
 */
public class HeapGraph {

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

    public Map<String, Map<String, Long>> getEdgeCounts() {
        return edgeCount;
    }

    public  Map<String, Long> getNodeCounts() {
        return nodeCount;
    }


    public static HeapGraph generateGraph(String hprofPath) throws IOException {
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

    private static Instance getInstanceFromFieldValue(FieldValue fieldValue) {
        return ((ObjectFieldValue)fieldValue).getInstance();
    }
}
