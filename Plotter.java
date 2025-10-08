import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class Plotter extends JPanel {
    private List<Node> path;
    private Set<Node> obstacles;
    private final Node start, goal;
    private static final int GRID_SIZE = 144;
    private static final int SCALE = 5;
    private final Image backgroundImage;
    private final Navigator navigator;

    public Plotter(Node start, Node goal, Set<Node> initialObstacles) {
        this.start = start;
        this.goal = goal;
        this.obstacles = new HashSet<>(initialObstacles);
        this.navigator = new Navigator(start.x, start.y, goal.x, goal.y);
        this.path = navigator.getPath();
        
        setPreferredSize(new Dimension(GRID_SIZE * SCALE, GRID_SIZE * SCALE));
        backgroundImage = new ImageIcon("decode_144sq.png").getImage();

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int gridX = e.getX() / SCALE;
                int gridY = e.getY() / SCALE;
                setToolTipText("Grid: (" + gridX + ", " + gridY + ")");
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                int gridX = e.getX() / SCALE;
                int gridY = e.getY() / SCALE;
                
                for(int i = gridX - 9; i < gridX + 10; i++){
                    for(int j = gridY - 9; j < gridY + 10; j++){
                        obstacles.add(new Node(i, j));
                    }
                }
                
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        // Draw grid
        g2d.setColor(new Color(40, 40, 40, 50));
        for (int i = 0; i <= GRID_SIZE; i++) {
           g2d.drawLine(i * SCALE, 0, i * SCALE, GRID_SIZE * SCALE);
           g2d.drawLine(0, i * SCALE, GRID_SIZE * SCALE, i * SCALE);
        }

        // Draw obstacles
        g2d.setColor(new Color(100, 100, 100));
        for (Node obs : obstacles) {
           g2d.fillRect(obs.x * SCALE, obs.y * SCALE, SCALE, SCALE);
        }

        // Draw path
        if (path != null && path.size() > 1) {
            g2d.setColor(new Color(0, 120, 215));
            g2d.setStroke(new BasicStroke(2));
            for (int i = 0; i < path.size() - 1; i++) {
                Node p1 = path.get(i);
                Node p2 = path.get(i + 1);
                g2d.drawLine(p1.x * SCALE + SCALE/2, p1.y * SCALE + SCALE/2, 
                          p2.x * SCALE + SCALE/2, p2.y * SCALE + SCALE/2);
            }
            
            // Draw path nodes
            g2d.setColor(new Color(0, 120, 215, 100));
            for (int i = 1; i < path.size() - 1; i++) {
                Node p = path.get(i);
                g2d.fillOval(p.x * SCALE + SCALE/2 - 2, p.y * SCALE + SCALE/2 - 2, 4, 4);
            }
        }

        // Draw start point
        g2d.setColor(new Color(0, 200, 0));
        g2d.fillOval(start.x * SCALE - 4, start.y * SCALE - 4, 8, 8);
        g2d.setColor(new Color(0, 150, 0));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(start.x * SCALE - 4, start.y * SCALE - 4, 8, 8);
        
        // Draw goal point
        g2d.setColor(new Color(255, 50, 50));
        g2d.fillOval(goal.x * SCALE - 4, goal.y * SCALE - 4, 8, 8);
        g2d.setColor(new Color(200, 0, 0));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(goal.x * SCALE - 4, goal.y * SCALE - 4, 8, 8);
    }

    public static void main(String[] args) {
        System.out.println("Starting...");
        
        Node start = new Node(126, 135);
        Node goal = new Node(70, 50);
        
        Set<Node> obstacles = new HashSet<>();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("D* Lite Interactive Path Plotter");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            frame.add(new Plotter(start, goal, obstacles));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}