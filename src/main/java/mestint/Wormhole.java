package mestint;

import lombok.*;
import org.jgrapht.graph.*;

public class Wormhole extends DefaultEdge {

    @Getter
    @Setter
    private int weight = 0;
    
    @Override
    public String toString() {
        return this.getSource() + " -> " + this.getTarget() + " with weight = " + weight;
    }
}
