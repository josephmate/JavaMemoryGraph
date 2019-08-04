import static guru.nidi.graphviz.model.Factory.*;

import guru.nidi.graphviz.attribute.Color;
import javafx.application.Application;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.*;

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
        MutableGraph g = mutGraph("example1").setDirected(true).add(
                mutNode("a").add(Color.RED).addLink(mutNode("b")));
        try {
            Graphviz.fromGraph(g).width(200).render(Format.PNG).toOutputStream(byteArrayOut);


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
        ImageView iv1 = new ImageView();
        iv1.setImage(image);

        Group root = new Group();
        Scene scene = new Scene(root);
        scene.setFill(javafx.scene.paint.Color.WHITE);
        root.getChildren().add(iv1);

        stage.setTitle("ImageView");
        stage.setWidth(415);
        stage.setHeight(200);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }
}
