import javax.swing.*;

import PathCreation.DStarLite;
import PathCreation.PathSmoother;
import PathCreation.Point2D;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.awt.event.*;
import java.awt.geom.*;

public class Plotter extends JPanel {
    private List<Point2D> path;
    private Point2D start, goal;
    private static final int GRID_SIZE = 144;
    private static final double SCALE = 10.0;
    private final Image backgroundImage;
    Set<Point2D> obstacles;

    public Plotter() {
        this.start = new Point2D(120, 15);
        this.goal = new Point2D(50, 120);

        obstacles = new HashSet<Point2D>();
        DStarLite planner = new DStarLite();
        PathSmoother pathSmoother = new PathSmoother(planner);
        planner.initializePath(start, goal);
        this.path = pathSmoother.computeSmoothPath(obstacles);

        setPreferredSize(new Dimension((int)(GRID_SIZE * SCALE), (int)(GRID_SIZE * SCALE)));
        backgroundImage = new ImageIcon("decode_144sq.png").getImage();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                int gridX = (int)(e.getX() / SCALE);
                int gridY = (int)(e.getY() / SCALE);
                
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

        // Draw path with floating point precision
        if (path != null && path.size() > 1) {
            g2d.setColor(new Color(0, 120, 215));
            g2d.setStroke(new BasicStroke(2));
            
            for (int i = 0; i < path.size(); i++) {
                Point2D p1 = path.get(i);
                double screenX = p1.x * SCALE;
                double screenY = p1.y * SCALE;
                
                Ellipse2D.Double point = new Ellipse2D.Double(
                    screenX - SCALE/2, 
                    screenY - SCALE/2, 
                    1, 
                    1
                );
                g2d.fill(point);
            }
        }

        // Draw obstacles
        g2d.setColor(Color.red);
        for (Point2D obs : obstacles) {
            Rectangle2D.Double obstacle = new Rectangle2D.Double(
                obs.x * SCALE, 
                obs.y * SCALE, 
                SCALE, 
                SCALE
            );
            g2d.fill(obstacle);
        }

        // Draw start point
        double startScreenX = start.x * SCALE;
        double startScreenY = start.y * SCALE;
        
        g2d.setColor(new Color(0, 200, 0));
        Ellipse2D.Double startFill = new Ellipse2D.Double(
            startScreenX - 4, 
            startScreenY - 4, 
            8, 
            8
        );
        g2d.fill(startFill);
        
        g2d.setColor(new Color(0, 150, 0));
        g2d.setStroke(new BasicStroke(2));
        Ellipse2D.Double startOutline = new Ellipse2D.Double(
            startScreenX - 4, 
            startScreenY - 4, 
            8, 
            8
        );
        g2d.draw(startOutline);
        
        // Draw goal point
        double goalScreenX = goal.x * SCALE;
        double goalScreenY = goal.y * SCALE;
        
        g2d.setColor(new Color(0, 150, 0));
        Ellipse2D.Double goalFill = new Ellipse2D.Double(
            goalScreenX - 4, 
            goalScreenY - 4, 
            8, 
            8
        );
        g2d.fill(goalFill);
        
        g2d.setColor(new Color(0, 100, 0));
        g2d.setStroke(new BasicStroke(2));
        Ellipse2D.Double goalOutline = new Ellipse2D.Double(
            goalScreenX - 4, 
            goalScreenY - 4, 
            8, 
            8
        );
        g2d.draw(goalOutline);
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