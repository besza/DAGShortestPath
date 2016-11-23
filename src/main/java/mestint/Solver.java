package mestint;

import org.jgrapht.*;
import org.jgrapht.traverse.*;

import java.util.*;
import java.util.stream.*;

public class Solver {

    private Game game;

    private Map<StarSystem, Resources> opt;

    private Map<StarSystem, StarSystem> pred;

    private List<StarSystem> topoOrder;

    public Solver(Game game) {
        this.game = Objects.requireNonNull(game);

        int size = game.getGraph().vertexSet().size();

        pred = new HashMap<>(size);
        opt = new HashMap<>(size);
        topoOrder = new ArrayList<>();

        game.getGraph().vertexSet().forEach(node -> {
            if (node.getId() == game.getStartingStarSystemId()) {
                opt.put(node, Resources.of(node.getTitanium(), game.getUraniumCapacity()));
            } else {
                opt.put(node, new Resources(-1, -1));
            }
            pred.put(node, node);
        });

        Iterator<StarSystem> iterator = new TopologicalOrderIterator<StarSystem, Wormhole>(game.getGraph());
        iterator.forEachRemaining(topoOrder::add);

    }

    private boolean isNodeBeforeStartInTopoOrder(StarSystem node) {
        StarSystem start = game.getStarSystemById(game.getStartingStarSystemId());
        return topoOrder.indexOf(start) > topoOrder.indexOf(node);
    }

    public int getMaxReachableTitanium() {

        StarSystem goal = game.getStarSystemById(game.getGoalStarSystemId());
        StarSystem start = game.getStarSystemById(game.getStartingStarSystemId());

        for (int i = topoOrder.indexOf(start); i <= topoOrder.indexOf(goal); i++) {
            StarSystem starSystem = topoOrder.get(i);
            if (game.getGraph().inDegreeOf(starSystem) != 0 && game.getGraph().outDegreeOf(starSystem) != 0) {
                Resources max = new Resources(-2, -2);
                StarSystem parent = null;
                for (StarSystem star : Graphs.predecessorListOf(game.getGraph(), starSystem).stream().filter(v -> !isNodeBeforeStartInTopoOrder(v)).collect(Collectors.toList())) {
                    final Resources best = opt.get(star);
                    Resources current = Resources.of(best.getTitanium(), best.getUranium());
                    int cost = game.getGraph().getEdge(star, starSystem).getWeight();
                    //ignore edges which has cost more than the capacity
                    if (cost > game.getUraniumCapacity()) continue;
                    //substract the travel cost
                    current = Resources.of(current.getTitanium(), current.getUranium() - cost);

                    //we didn't have enough uranium to travel this edge, trade 1 titanium to refill
                    if (current.getUranium() < 0) {
                        current = Resources.of(current.getTitanium() - 1, game.getUraniumCapacity() - cost);
                    }

                    if (current.compareTo(max) > 0) {
                        max = Resources.of(current.getTitanium(), current.getUranium());
                        parent = star;
                    }
                }

                if (max.getTitanium() >= 0) {
                    max = Resources.of(max.getTitanium() + starSystem.getTitanium(), max.getUranium() + starSystem.getUranium());
                    if (max.getUranium() > game.getUraniumCapacity()) {
                        max = Resources.of(max.getTitanium(), game.getUraniumCapacity());
                    }
                    opt.replace(starSystem, max);
                    pred.replace(starSystem, parent);
                }
            }
        }

        return opt.get(goal).getTitanium();
    }
}
