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

            for (int i = 0; i < starSystems; ++i) {
                int uranium = scanner.nextInt();
                int titanium = scanner.nextInt();
                graph.addVertex(new StarSystem(uranium, titanium));
            }

            Set<StarSystem> vertexSet = graph.vertexSet();
            for (int j = 0; j < wormHoles; ++j) {
                int sourceId = scanner.nextInt();
                int targetId = scanner.nextInt();
                int weight = scanner.nextInt();
                //no need to do ifPresent, these vertices definitely exist
                Wormhole edge = graph.addEdge(vertexSet.stream().filter(v -> v.getId() == sourceId).findFirst().get(),
                        vertexSet.stream().filter(u -> u.getId() == targetId).findFirst().get());
                graph.setEdgeWeight(edge, weight);
            }

            return new Game(startingStarSystemId, goalStarSystemId, uraniumMaxCapacity, graph);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
