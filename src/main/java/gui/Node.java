package gui;

import lombok.*;

@Data
final class Node {
    enum NodeType {
        START, GOAL, NORMAL;
    }
    private final int x;
    private final int y;
    private final int id;
    private final NodeType type;
    
    private final int titanium;
    private final int uranium;

    private final int RADIUS = 20;
    
}
