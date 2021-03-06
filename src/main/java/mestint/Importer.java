package mestint;

import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.Set;


public class Importer {

    private Importer() {
    }

    public static Game importGame(Path path) {
        try (Scanner scanner = new Scanner(path)) {
            int starSystems = scanner.nextInt();
            int wormHoles = scanner.nextInt();
            int startingStarSystemId = scanner.nextInt();
            int goalStarSystemId = scanner.nextInt();
            int uraniumMaxCapacity = scanner.nextInt();

            SimpleDirectedWeightedGraph<StarSystem, Wormhole> graph = new SimpleDirectedWeightedGraph<>(Wormhole.class);

            for (int i = 1; i <= starSystems; ++i) {
                int titanium = scanner.nextInt();
                int uranium = scanner.nextInt();
                graph.addVertex(new StarSystem(titanium, uranium, i));
            }

            Set<StarSystem> vertexSet = graph.vertexSet();
            for (int j = 0; j < wormHoles; ++j) {
                int sourceId = scanner.nextInt();
                int targetId = scanner.nextInt();
                int weight = scanner.nextInt();
                //early check for edges that cost more than our capacity
                if (weight <= uraniumMaxCapacity) {
                    //no need to do ifPresent, these vertices definitely exist
                    Wormhole edge = graph.addEdge(vertexSet.stream().filter(v -> v.getId() == sourceId).findFirst().get(),
                            vertexSet.stream().filter(u -> u.getId() == targetId).findFirst().get());
                    edge.setWeight(weight);
                }
            }

            return new Game(startingStarSystemId, goalStarSystemId, uraniumMaxCapacity, graph);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
