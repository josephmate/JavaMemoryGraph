import static guru.nidi.graphviz.model.Factory.*;

import guru.nidi.graphviz.attribute.Color;
import javafx.application.Application;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.graphstream.graph.Edge;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * JavaFX does not let me pass Objects to the Application instance. It only allows passing
 * String arguments. As a result, I need to use an UGLY static field to pass the reference
 * over.
 *
 * Reference:
 * https://stackoverflow.com/questions/37862579/javafx-pass-value-into-instance-of-view-without-using-static-setter
 * https://stackoverflow.com/questions/54875960/javafx-launching-an-application-with-an-object-parameter-from-a-different-clas
 */
public class GraphVizDisplayer extends Application implements HeapGraphDisplayer  {

    private static ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();

    public void display(HeapGraph heapGraph) {
        MutableGraph graph = mutGraph("example1").setDirected(true);

        Map<String, MutableNode> nodes = new HashMap<>();

        for(Map.Entry<String,Long> entry : heapGraph.getNodeCounts().entrySet()) {
            String className = entry.getKey();
            long count = entry.getValue();
            MutableNode node = mutNode(className);
            graph.add(node);
            nodes.put(className, node);
        }

        for(Map.Entry<String, Map<String, Long>> edgeEntry : heapGraph.getEdgeCounts().entrySet()) {
            String fromClass = edgeEntry.getKey();
            Map<String, Long> subMap = edgeEntry.getValue();

            for(Map.Entry<String,Long> entry :subMap.entrySet()) {
                String toClass = entry.getKey();
                long count = entry.getValue();
                String edgeId = fromClass + "->" + toClass;

                MutableNode fromNode = nodes.get(fromClass);
                MutableNode toNode = nodes.get(toClass);

                graph.addLink(fromNode.addLink(toNode));
            }
        }

        try {
            Graphviz.fromGraph(graph)
                    .render(Format.PNG)
                    .toOutputStream(byteArrayOut);


            Application.launch(GraphVizDisplayer.class);
        } catch(IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        // load the image
        Image image = new Image(new ByteArrayInputStream(byteArrayOut.toByteArray()));

        // simple displays ImageView the image as is
        ImageView imageView = new ImageView();
        imageView.setImage(image);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(imageView);

        Scene scene = new Scene(scrollPane);
        scene.setFill(javafx.scene.paint.Color.WHITE);

        stage.setTitle("Heap Graph Summary");
        stage.setWidth(500);
        stage.setHeight(500);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }
}
