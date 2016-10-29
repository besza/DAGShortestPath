package mestint;

import lombok.Data;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.Objects;

@Data
public class Game {
    private int startingStarSystemId;
    private int goalStarSystemId;
    private int uraniumCapacity;

    private SimpleDirectedWeightedGraph<StarSystem, Wormhole> graph;

    public Game(int startingStarSystemId, int goalStarSystemId, int uraniumCapacity, SimpleDirectedWeightedGraph<StarSystem, Wormhole> graph) {
        Objects.requireNonNull(graph);

        if (uraniumCapacity < 1)
            throw new IllegalArgumentException(("Uranium capacity must be at least 1!"));


        this.startingStarSystemId = startingStarSystemId;
        this.goalStarSystemId = goalStarSystemId;
        this.uraniumCapacity = uraniumCapacity;
        this.graph = graph;
    }
}
