package mestint;

import lombok.*;

@EqualsAndHashCode
class Resources implements Comparable<Resources> {
    @Getter
    private final int titanium;

    @Getter
    private final int uranium;

    public Resources(int t, int u) {
        titanium = t;
        uranium = u;
    }
    
    public static Resources of(int t, int u) {
        return new Resources(t, u);
    }
    
    @Override
    public String toString() {
        return "R(" + titanium + "," + uranium + ")";
    }

    @Override
    public int compareTo(Resources o) {
        return Integer.compare(titanium, o.titanium);
    }
}
