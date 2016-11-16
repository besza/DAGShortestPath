package mestint;

import com.mxgraph.layout.*;
import com.mxgraph.swing.*;
import org.jgrapht.ext.*;
import org.jgrapht.graph.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;

public class Main extends JApplet {
    
    private static final Dimension DEFAULT_SIZE = new Dimension(800,600);

    public static void main(String[] args) {
        Main applet = new Main();
        applet.init();
        
        JFrame frame = new JFrame();
        frame.getContentPane().add(applet);
        frame.setTitle("Mest. int. graf demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void init() {

        File resourcesDirectory = new File("src\\main\\resources");
        Path path = Paths.get(resourcesDirectory.getAbsolutePath() + "\\be0.txt");
        Game game = Importer.importGame(path);
        
        ListenableDirectedWeightedGraph<StarSystem, Wormhole> graph = 
                new ListenableDirectedWeightedGraph<>(game.getGraph());

        JGraphXAdapter<StarSystem, Wormhole> jGraphXAdapter = new JGraphXAdapter<>(graph);
        
        getContentPane().add(new mxGraphComponent(jGraphXAdapter));
        
        resize(DEFAULT_SIZE);
        
        mxCircleLayout layout = new mxCircleLayout(jGraphXAdapter);
        
        layout.execute(jGraphXAdapter.getDefaultParent());
    }
}
