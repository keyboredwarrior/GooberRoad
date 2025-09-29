import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class Plotter extends JPanel {
    private List<Node> path;
    private Set<Node> obstacles;
    private final DStarLite dstar;
    private final Node start, goal;
    private static final int GRID_SIZE = 144;
    private static final int SCALE = 5;

    public Plotter(Node start, Node goal, Set<Node> initialObstacles) {
        this.start = start;
        this.goal = goal;
        this.obstacles = new HashSet<>(initialObstacles);
        this.dstar = new DStarLite(start, goal, GRID_SIZE, GRID_SIZE, obstacles);
        this.path = dstar.computePath();
        
        setPreferredSize(new Dimension(GRID_SIZE * SCALE, GRID_SIZE * SCALE));
        
        // Add mouse listener for generating random obstacle patterns
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Generate random obstacles across entire grid
                obstacles.clear();
                Random rand = new Random();
                int obstacleCount = 0;
                
                for (int x = 0; x < GRID_SIZE; x++) {
                    for (int y = 0; y < GRID_SIZE; y++) {
                        Node node = new Node(x, y);
                        
                        // Skip start and goal positions
                        if (node.equals(start) || node.equals(goal)) {
                            continue;
                        }
                        
                        // 1/3 chance of becoming an obstacle
                        if (rand.nextDouble() < 1.0 / 3.0) {
                            obstacles.add(node);
                            obstacleCount++;
                        }
                    }
                }
                
                System.out.println("Generated " + obstacleCount + " random obstacles");
                
                // Recompute path with updated obstacles
                long startTime = System.currentTimeMillis();
                path = dstar.updateObstacles(obstacles);
                long endTime = System.currentTimeMillis();
                
                System.out.println("Path recomputed in " + (endTime - startTime) + "ms");
                System.out.println("New path length: " + (path != null ? path.size() : 0) + " nodes");
                
                if (path == null || path.isEmpty() || !path.get(path.size() - 1).equals(goal)) {
                    System.out.println("WARNING: No valid path found!");
                }
                
                repaint();
            }
        });
        
        // Add mouse motion listener to show grid coordinates
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int gridX = e.getX() / SCALE;
                int gridY = e.getY() / SCALE;
                setToolTipText("Grid: (" + gridX + ", " + gridY + ")");
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw grid
        g2d.setColor(new Color(240, 240, 240));
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
        Node start = new Node((int)(Math.random()*144), (int)(Math.random()*144));
        Node goal = new Node((int)(Math.random()*144), (int)(Math.random()*144));
        
        Set<Node> obstacles = new HashSet<>();
        
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("D* Lite Interactive Path Plotter");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            Plotter plotter = new Plotter(start, goal, obstacles);
            frame.add(plotter);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}