package mestint;

import lombok.*;
import org.jgrapht.*;
import org.jgrapht.traverse.*;

import java.util.*;

public class Solver {

    private Game game;

    private Map<StarSystem, Resources> opt;

    private Map<StarSystem, StarSystem> pred;

    public Solver(Game game) {
        this.game = Objects.requireNonNull(game);

        int size = game.getGraph().vertexSet().size();

        pred = new HashMap<>(size);
        opt = new HashMap<>(size);

        game.getGraph().vertexSet().forEach(node -> {
            opt.put(node, null);
            pred.put(node, null);
        });
    }

    public int getMaxReachableTitanium() {

        Iterator<StarSystem> iterator = new TopologicalOrderIterator<StarSystem, Wormhole>(game.getGraph());
        while (iterator.hasNext()) {
            StarSystem starSystem = iterator.next();
            if (game.getGraph().inDegreeOf(starSystem) > 0) {
                Resources max = new Resources(Integer.MIN_VALUE, Integer.MIN_VALUE);
                StarSystem parent = null;
                for (StarSystem star : Graphs.predecessorListOf(game.getGraph(), starSystem)) {
                    Resources res = opt.get(star);
                    int cost = game.getGraph().getEdge(star, starSystem).getWeight();
                    //ignore edges which has cost more than the capacity
                    if (cost > game.getUraniumCapacity()) continue;
                    //substract the travel cost
                    res.uranium -= cost;

                    //we didn't have enough uranium to travel this edge, trade 1 titanium to refill
                    if (res.uranium < 0) {
                        --res.titanium;
                        res.uranium = game.getUraniumCapacity() - cost;
                    }

                    if (res.titanium > max.titanium) {
                        max.titanium = res.titanium;
                        max.uranium = res.uranium;
                        parent = star;
                    }
                }

                if (max.titanium >= 0) {
                    max.titanium += starSystem.getTitanium();
                    max.uranium += starSystem.getUranium();
                    if (max.uranium > game.getUraniumCapacity())
                        max.uranium = game.getUraniumCapacity();
                    opt.replace(starSystem, max);
                    pred.replace(starSystem, parent);
                }

            } else {
                //initialize trivially
                opt.replace(starSystem, new Resources(starSystem.getTitanium(), game.getUraniumCapacity()));
                pred.replace(starSystem, starSystem);
            }
        }

        StarSystem goal = game.getStarSystemById(game.getGoalStarSystemId()).get();
        StarSystem start = game.getStarSystemById(game.getStartingStarSystemId()).get();

        System.out.println(opt.get(goal).getTitanium() + " - " + opt.get(start).getTitanium() + " + " + start.getTitanium());
        
        return opt.get(goal).titanium - opt.get(start).titanium + start.getTitanium();
    }

    /**
     * Helper class to store resource pair.
     */
    private class Resources {
        @Getter
        @Setter
        protected int titanium;

        @Getter
        @Setter
        protected int uranium;

        Resources(int t, int u) {
            titanium = t;
            uranium = u;
        }

        @Override
        public String toString() {
            return "Resources{" +
                    "titanium=" + titanium +
                    ", uranium=" + uranium +
                    '}';
        }
    }
}
