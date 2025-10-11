import javax.swing.*;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Plotter extends JPanel {
    private List<Pose> path;
    private Point2D start, goal;
    private static final int GRID_SIZE = 144;
    private static final int SCALE = 5;
    private final Image backgroundImage;
    private Navigator navigator;

    public Plotter() {
        this.start = new Point2D((int)(Math.random() *144), (int)(Math.random()*144));
        this.goal = new Point2D((int)(Math.random() *144), (int)(Math.random()*144));

        Set<Point2D> obstacles = new HashSet<Point2D>();
        for(int i = 0; i < 2000; i++){
            int oX = (int)(Math.random() * 144);
            int oY = (int)(Math.random() * 144);

            if((oX != start.x || oY != start.y) && (oX != goal.x || oY != goal.y)){
                obstacles.add(new Point2D(oX, oY));
            }
        }
        this.navigator = new Navigator(obstacles, start, goal);
        this.path = this.navigator.getFullPath(start, goal);

        setPreferredSize(new Dimension(GRID_SIZE * SCALE, GRID_SIZE * SCALE));
        backgroundImage = new ImageIcon("decode_144sq.png").getImage();
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
            for (int i = 0; i < path.size() - 1; i++) {
                Pose p1 = path.get(i);
                Pose p2 = path.get(i + 1);
                g2d.drawLine((int)p1.getX() * SCALE + SCALE/2, (int)p1.getY() * SCALE + SCALE/2, 
                          (int)p2.getX() * SCALE + SCALE/2, (int)p2.getY() * SCALE + SCALE/2);
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
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("D* Lite Interactive Path Plotter");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            frame.add(new Plotter());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}