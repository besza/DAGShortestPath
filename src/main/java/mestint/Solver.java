package mestint;

import org.jgrapht.alg.DirectedNeighborIndex;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.*;

public class Solver {

    private Game game;

    private Map<StarSystem, Resources> opt;

    private Map<StarSystem, StarSystem> pred;

    private final List<StarSystem> topoOrder;

    private final StarSystem start;

    private final StarSystem goal;

    public Solver(Game game) {
        this.game = Objects.requireNonNull(game);

        start = game.getStarSystemById(game.getStartingStarSystemId());

        goal = game.getStarSystemById(game.getGoalStarSystemId());

        topoOrder = new ArrayList<>(game.getGraph().vertexSet().size());

        Iterator<StarSystem> iterator = new TopologicalOrderIterator<>(game.getGraph());
        iterator.forEachRemaining(topoOrder::add);

        pred = new HashMap<>();

        opt = new HashMap<>();

        //initialize the optimum for the starting node
        opt.put(start, Resources.of(start.getTitanium(), game.getUraniumCapacity()));

        solve();
    }

    private void solve() {

        int maxTitanium;
        int maxUranium;
        int currentTitanium;
        int currentUranium;
        int parentStarSystemId = -1;
        int uraniumCapacity = game.getUraniumCapacity();

        DirectedNeighborIndex<StarSystem, Wormhole> neighborIndex = new DirectedNeighborIndex<>(game.getGraph());

        int goalIndex = topoOrder.indexOf(goal);

        for (int i = topoOrder.indexOf(start) + 1; i <= goalIndex; ++i) {

            StarSystem starSystem = topoOrder.get(i);

            if (game.getGraph().inDegreeOf(starSystem) != 0 && game.getGraph().outDegreeOf(starSystem) != 0 || starSystem.equals(goal)) {

                maxTitanium = Integer.MIN_VALUE;
                maxUranium = Integer.MIN_VALUE;

                for (StarSystem star : neighborIndex.predecessorListOf(starSystem)) {

                    Resources optimum = opt.get(star);

                    if (optimum == null) continue;

                    //ignore edges which needs more uranium than the given capacity
                    int cost = game.getGraph().getEdge(star, starSystem).getWeight();

                    currentTitanium = optimum.getTitanium();
                    currentUranium = optimum.getUranium();

                    //substract the travel cost
                    currentUranium -= cost;

                    //if we didn't have enough uranium to travel this edge, trade 1 titanium to refill
                    if (currentUranium < 0) {
                        --currentTitanium;
                        currentUranium = uraniumCapacity - cost;
                    }

                    if (currentTitanium > maxTitanium) {
                        maxTitanium = currentTitanium;
                        maxUranium = currentUranium;
                        parentStarSystemId = star.getId();
                    }
                }

                if (maxTitanium >= 0) {
                    maxTitanium += starSystem.getTitanium();
                    maxUranium += starSystem.getUranium();
                    if (maxUranium > uraniumCapacity) {
                        maxUranium = uraniumCapacity;
                    }

                    opt.put(starSystem, Resources.of(maxTitanium, maxUranium));
                    pred.put(starSystem, game.getStarSystemById(parentStarSystemId));
                }
            }
        }
    }

    public int getOptimumTitanium() {
        Resources result = opt.getOrDefault(goal, Resources.of(-1, -1));
        return result.getTitanium();
    }

    public LinkedList<StarSystem> getOptimumPath() {
        LinkedList<StarSystem> path = new LinkedList<>();
        path.add(goal);
        for (StarSystem star = pred.get(goal); !star.equals(start); star = pred.get(star)) {
            path.addFirst(star);
        }
        path.addFirst(start);
        return path;
    }
}
