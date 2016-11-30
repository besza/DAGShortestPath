package mestint;

import lombok.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Main extends JPanel {
    private Map<Integer, Node> points;

    private JButton node;
    private JButton edge;
    private JButton solve;
    
    private static int COUNTER = 0;
    
    private final int RADIUS = 20;
    
    private static final Dimension DIMENSION = new Dimension(800, 600);

    public Main() {
        setBackground(Color.WHITE);
        setPreferredSize(DIMENSION);
        setLayout(new BorderLayout());
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    points.put(new Point(e.getX(), e.getY()), Color.MAGENTA);
                } else if (e.getButton() == MouseEvent.BUTTON2) {
                    points.put(new Point(e.getX(), e.getY()), Color.CYAN);
                }
                repaint();
            }
        });
        JButton button = new JButton("Add node");
        button.addActionListener((event) -> {
            System.out.println("lofasz");    
            }
        );
        add(button, BorderLayout.NORTH);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (Map.Entry<Point, Color> entry : points.entrySet()) {
            g2.setColor(entry.getValue());
            g2.fillOval(entry.getKey().x, entry.getKey().y, RADIUS, RADIUS);
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

    @Data
    private final class Node {
        public enum NodeType {
            START, GOAL, NORMAL;
        }
        private final int x;
        private final int y;
        private final int id;
        
    }
}
