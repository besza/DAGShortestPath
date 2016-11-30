package gui;

import mestint.*;
import org.jgrapht.graph.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.regex.*;

public class Main extends JPanel {

    private List<Node> nodes;
    private List<Edge> edges;

    private static int COUNTER = 0;
    
    private final InputVerifier numberVerifier;
    
    private Node previousNode;

    private enum ActiveButton {
        NODE, EDGE;
    }

    private ActiveButton activeButton;

    private static final Dimension DIMENSION = new Dimension(800, 600);

    public Main() {
        setBackground(Color.WHITE);
        setPreferredSize(DIMENSION);

        nodes = new ArrayList<>();
        edges = new ArrayList<>();

        numberVerifier = new NumberVerifier();

        JButton newNodeButton = new JButton("Add node");
        JButton newEdgeButton = new JButton("Add edge");
        JButton solveButton = new JButton("Solve!");
        JButton clearButton = new JButton("Clear");

        newNodeButton.addActionListener((event) -> {
            this.activeButton = ActiveButton.NODE;
        });

        newEdgeButton.addActionListener((event) -> {
            this.activeButton = ActiveButton.EDGE;
        });

        clearButton.addActionListener((event) -> {
            COUNTER = 0;
            nodes.clear();
            edges.clear();
            repaint();
        });

        solveButton.addActionListener((event) -> {
            //since this GUI is just a prototype presentation for the underlying algorithm impl. we purposely don't check for any errors
            //we assume the input is correct and we just give it to the solver
            JTextField uraniumCapacityField = new JTextField(10);
            uraniumCapacityField.setInputVerifier(numberVerifier);
            JPanel panel = new JPanel();
            panel.add(new JLabel("Uranium capacity:")); // TODO: refactor
            panel.add(uraniumCapacityField);
            int result = JOptionPane.showConfirmDialog(null, panel, "Please enter additional info", JOptionPane.OK_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                //TODO: refactor so we can reuse existing StarSystem and Wormhole implementation
                int uraniumCapacity = Integer.parseInt(uraniumCapacityField.getText());
                int startingStarSystemId = 0;
                int goalStarSystemId = 0;
                
                Optional<Node> start = nodes.stream().filter(n -> n.getType() == Node.NodeType.START).findAny();
                Optional<Node> goal = nodes.stream().filter(n -> n.getType() == Node.NodeType.GOAL).findAny();
                if (start.isPresent() && goal.isPresent()) {
                    startingStarSystemId = start.get().getId();
                    goalStarSystemId = goal.get().getId();
                }
                SimpleDirectedWeightedGraph<StarSystem, Wormhole> graph = new SimpleDirectedWeightedGraph<StarSystem, Wormhole>(Wormhole.class);
                for (Node node : nodes) {
                    graph.addVertex(new StarSystem(node.getTitanium(), node.getUranium(), node.getId()));
                }
                
                Set<StarSystem> vertices = graph.vertexSet();
                for (Edge edge : edges) {
                    Wormhole wormhole = graph.addEdge(vertices.stream().filter(v -> v.getId() == edge.getFrom().getId()).findFirst().get(),
                            vertices.stream().filter(u -> u.getId() == edge.getTo().getId()).findFirst().get());
                    wormhole.setWeight(edge.getWeight());
                }
                
                Solver solver = new Solver(new Game(startingStarSystemId, goalStarSystemId, uraniumCapacity, graph));
                int optimumTitanium = solver.getOptimumTitanium();
                if (optimumTitanium != -1) {
                    JTextArea textArea = new JTextArea(20, 50);
                    textArea.setEditable(false);
                    textArea.append("Optimum titanium amount = " + optimumTitanium + "\n");
                    StringBuilder stringBuilder = new StringBuilder();
                    solver.getOptimumPath().forEach(v -> stringBuilder.append(v.getId()).append(","));
                    textArea.append("Optimum path :" + stringBuilder.toString() + "\n");
                    JOptionPane.showMessageDialog(null, textArea, "Solution", JOptionPane.NO_OPTION);
                } else {
                    JOptionPane.showMessageDialog(null, "There is no solution.", "Solution", JOptionPane.ERROR_MESSAGE);
                }
                
            }
        });
        
        add(newNodeButton);
        add(newEdgeButton);
        add(solveButton);
        add(clearButton);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (activeButton == ActiveButton.NODE) {
                    //read additional input before creating the vertex
                    JTextField titaniumField = new JTextField(10);
                    titaniumField.setInputVerifier(numberVerifier);
                    JTextField uraniumField = new JTextField(10);
                    uraniumField.setInputVerifier(numberVerifier);
                    JComboBox<Node.NodeType> nodeTypes = new JComboBox<>(new Node.NodeType[]{Node.NodeType.NORMAL, Node.NodeType.GOAL, Node.NodeType.START});

                    JPanel inputPanel = new JPanel();
                    inputPanel.add(new JLabel("Titanium:"));
                    inputPanel.add(titaniumField);
                    inputPanel.add(Box.createHorizontalStrut(15));
                    inputPanel.add(new JLabel("Uranium:"));
                    inputPanel.add(uraniumField);
                    inputPanel.add(Box.createHorizontalStrut(15));
                    inputPanel.add(new JLabel("Node type:"));
                    inputPanel.add(nodeTypes);

                    int result = JOptionPane.showConfirmDialog(null, inputPanel, "Please enter additional info", JOptionPane.OK_CANCEL_OPTION);

                    if (result == JOptionPane.OK_OPTION) {
                        Node node = new Node(e.getX(), e.getY(),  ++COUNTER, (Node.NodeType) nodeTypes.getSelectedItem(), Integer.parseInt(titaniumField.getText()), Integer.parseInt(uraniumField.getText()));
                        nodes.add(node);
                        repaint();
                    }


                } else if (activeButton == ActiveButton.EDGE) {
                    double minDistance = Double.POSITIVE_INFINITY;
                    Node closestNode = null;
                    for (Node node : nodes) {
                        double distance = Util.distance(node.getX(), node.getY(), e.getX(), e.getY());
                        if (distance < minDistance) {
                            minDistance = distance;
                            closestNode = node;
                        }
                    }

                    if (previousNode != null) {
                        //create a directed edge from previousNode to closestNode
                        JTextField weightField = new JTextField(10);
                        weightField.setInputVerifier(numberVerifier);

                        JPanel inputPanel = new JPanel();
                        inputPanel.add(new JLabel("Weight (integer):"));
                        inputPanel.add(weightField);

                        int result = JOptionPane.showConfirmDialog(null, inputPanel, "Please enter additional info", JOptionPane.OK_CANCEL_OPTION);

                        if (result == JOptionPane.OK_OPTION) {
                            Edge edge = new Edge(previousNode, closestNode, Integer.parseInt(weightField.getText()));
                            edges.add(edge);
                            repaint();
                            previousNode = null;
                        }
                    } else {
                        previousNode = closestNode;
                    }
                }
            }
        });

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Node node : nodes) {
            switch (node.getType()) {
                case NORMAL:
                    g2.setColor(Color.DARK_GRAY);
                    break;
                case START:
                    g2.setColor(Color.PINK);
                    break;
                case GOAL:
                    g2.setColor(Color.GREEN);
                    break;
            }
            g2.fillOval(node.getX() - (node.getRADIUS() / 2), node.getY() - (node.getRADIUS() / 2), node.getRADIUS(), node.getRADIUS());
        }

        g2.setColor(Color.BLACK);
        for (Edge edge : edges) {
            Util.drawArrowLine(g, edge.getFrom().getX(), edge.getFrom().getY(), edge.getTo().getX(), edge.getTo().getY(), 5, 5);
        }

    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("demo");
            frame.add(new Main());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });
    }


    private class NumberVerifier extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            JTextField textField = (JTextField) input;
            return Pattern.matches("\\d+", textField.getText());
        }
    }
}
