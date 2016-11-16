package mestint;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class StarSystem {

    @Getter
    private final int id;

    @Getter
    private int uranium;

    @Getter
    private int titanium;

    public StarSystem(int titanium, int uranium, int id) {
        if (uranium < 0 || titanium < 0)
            throw new IllegalArgumentException("Star system cannot have negative resources!");

        this.titanium = titanium;
        this.uranium = uranium;
        this.id = id;
    }

    @Override
    public String toString() {
        return "StarSystem{" +
                "id=" + id +
                ", titanium=" + titanium +
                ", uranium=" + uranium +
                '}';
    }
}
