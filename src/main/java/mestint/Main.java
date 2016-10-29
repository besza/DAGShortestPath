package mestint;

import org.jgrapht.traverse.TopologicalOrderIterator;

import java.io.File;
import java.util.Iterator;

public class Main {
    public static void main(String[] args) {
        Game game = Importer.importGame(new File("src\\test\\resources\\be9.txt"));

        Iterator<StarSystem> iter = new TopologicalOrderIterator<>(game.getGraph());
        StarSystem vertex;
        while (iter.hasNext()) {
            vertex = iter.next();
            System.out.println(
                    "Vertex " + vertex.toString() + " is connected to: "
                            + game.getGraph().edgesOf(vertex).toString());
        }
    }
}
