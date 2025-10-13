import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.awt.event.*;

public class Plotter extends JPanel {
    private List<Point2D> path;
    private Point2D start, goal;
    private static final int GRID_SIZE = 144;
    private static final int SCALE = 1;
    private final Image backgroundImage;
    Set<Point2D> obstacles;

    public Plotter() {
        this.start = new Point2D((int)(Math.random() *144), (int)(Math.random()*144));
        this.goal = new Point2D((int)(Math.random() *144), (int)(Math.random()*144));

        obstacles = new HashSet<Point2D>();
        //this.navigator = new Navigator();
        //this.path = this.navigator.getFullPath(start, goal);
        DStarLite planner = new DStarLite();
        PathSmoother pathSmoother = new PathSmoother(planner);
        planner.initializePath(start, goal);
        this.path = pathSmoother.computeSmoothPath(obstacles);

        setPreferredSize(new Dimension(GRID_SIZE * SCALE, GRID_SIZE * SCALE));
        backgroundImage = new ImageIcon("decode_144sq.png").getImage();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                int gridX = e.getX() / SCALE;
                int gridY = e.getY() / SCALE;
                
                for(int i = gridX - 9; i < gridX + 10; i++){
                    for(int j = gridY - 9; j < gridY + 10; j++){
                        obstacles.add(new Point2D(i, j));
                    }
                }
                path = pathSmoother.computeSmoothPath(obstacles);
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

        // Draw path
        if (path != null && path.size() > 1) {
            g2d.setColor(new Color(0, 120, 215));
            g2d.setStroke(new BasicStroke(2));
            for (int i = 0; i < path.size(); i++) {
                Point2D p1 = path.get(i);
                g2d.fillOval(p1.x * SCALE, p1.y * SCALE, SCALE, SCALE);
            }
        }

        g2d.setColor(Color.red);
        for (Point2D obs : obstacles) {
           g2d.fillRect(obs.x * SCALE, obs.y * SCALE, SCALE, SCALE);
        }

        // Draw start point
        g2d.setColor(new Color(0, 200, 0));
        g2d.fillOval(start.x * SCALE - 4, start.y * SCALE - 4, 8, 8);
        g2d.setColor(new Color(0, 150, 0));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(start.x * SCALE - 4, start.y * SCALE - 4, 8, 8);
        
        // Draw goal point
        g2d.setColor(new Color(0, 150, 0));
        g2d.fillOval(goal.x * SCALE - 4, goal.y * SCALE - 4, 8, 8);
        g2d.setColor(new Color(0, 100, 0));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(goal.x * SCALE - 4, goal.y * SCALE - 4, 8, 8);
    }

    public static void main(String[] args) {
        System.out.println("Starting...");
        JFrame frame = new JFrame("D* Lite Interactive Path Plotter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        SwingUtilities.invokeLater(() -> {
        frame.add(new Plotter());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        });
    }
}