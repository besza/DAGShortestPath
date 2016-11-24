package mestint;

import org.jgrapht.*;
import org.jgrapht.traverse.*;

import java.util.*;
import java.util.stream.*;

public class Solver {

    private Game game;

    private Map<StarSystem, Resources> opt;

    private Map<StarSystem, StarSystem> pred;

    private final List<StarSystem> topoOrder;

    private List<StarSystem> topoOrderView;

    private final StarSystem start;

    private final StarSystem goal;

    public Solver(Game game) {
        this.game = Objects.requireNonNull(game);

        start = game.getStarSystemById(game.getStartingStarSystemId());

        goal = game.getStarSystemById(game.getGoalStarSystemId());

        topoOrder = new ArrayList<>();
        Iterator<StarSystem> iterator = new TopologicalOrderIterator<StarSystem, Wormhole>(game.getGraph());
        iterator.forEachRemaining(topoOrder::add);

        pred = new HashMap<>();
        opt = new HashMap<>();

        topoOrderView = topoOrder.subList(topoOrder.indexOf(start), topoOrder.indexOf(goal) + 1);
        topoOrderView.forEach(node -> {
            opt.put(node, new Resources(-1, -1));
            pred.put(node, node);
        });

        //initialize the optimum for the starting node
        opt.replace(start, Resources.of(start.getTitanium(), game.getUraniumCapacity()));

        solve();
    }

    private boolean isNodeBeforeStartInTopoOrder(StarSystem node) {
        return topoOrder.indexOf(start) > topoOrder.indexOf(node);
    }

    private void solve() {

        for (StarSystem starSystem : topoOrderView) {
            // TODO: make sure to calculate the opt for the goal vertex, even if it has d(v) = 0 (where d is the outdegree)
            if (game.getGraph().inDegreeOf(starSystem) != 0 && game.getGraph().outDegreeOf(starSystem) != 0) {
                Resources max = new Resources(-2, -2);
                StarSystem parent = null;
                for (StarSystem star : Graphs.predecessorListOf(game.getGraph(), starSystem).stream().filter(v -> !isNodeBeforeStartInTopoOrder(v)).collect(Collectors.toList())) {
                    //ignore edges which needs more uranium than the given capacity
                    int cost = game.getGraph().getEdge(star, starSystem).getWeight();
                    if (cost > game.getUraniumCapacity()) continue;

                    final Resources best = opt.get(star);

                    //create a copy
                    Resources current = Resources.of(best.getTitanium(), best.getUranium());

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
    }
    
    public int getOptimumTitanium() {
        return opt.get(goal).getTitanium();
    }
}
