package mestint;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class StarSystem {

    private static int count = 0;

    @Getter
    private final int id;

    @Getter
    private int uranium;

    @Getter
    private int titanium;

    public StarSystem(int uranium, int titanium) {
        if (uranium < 0 || titanium < 0)
            throw new IllegalArgumentException("Star system cannot have negative resources!");

        this.uranium = uranium;
        this.titanium = titanium;
        this.id = ++count;
    }

    public void removeUranium(int amount) {
        if (amount > this.uranium)
            throw new IllegalArgumentException("Cannot remove more uranium than what we have!");

        uranium -= amount;
    }

    public void removeTitanium(int amount) {
        if (amount > this.titanium)
            throw new IllegalArgumentException("Cannot remove more titanium than what we have!");

        titanium -= amount;
    }

    @Override
    public String toString() {
        return "StarSystem{" +
                "id=" + id +
                ", uranium=" + uranium +
                ", titanium=" + titanium +
                '}';
    }
}
