package mestint;

import org.jgrapht.graph.DefaultWeightedEdge;

public class Wormhole extends DefaultWeightedEdge{

    @Override
    public String toString() {
        return this.getSource() + " -> " + this.getTarget() + " with weight = " + this.getWeight();
    }
}
