package gui;

import lombok.*;

@Data
final class Edge{
    private final Node from;
    private final Node to;
    private final int weight;

}
