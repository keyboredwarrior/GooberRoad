import java.util.*;
public class Navigator {
    
    private Set<Point2D> obstacles;
    private DStarLite dLite;
    private PathSmoother pathSmoother;

    public Navigator(Set<Point2D> obstacles, Point2D start, Point2D goal){
        this.dLite = new DStarLite(start, goal, 144, 144, obstacles);
        this.pathSmoother = new PathSmoother(dLite);
    }

    public List<Pose> getFullPath(Point2D start, Point2D goal){
        List<Point2D> smoothPath = pathSmoother.computeSmoothPath();
        
        float[] headings = new float[smoothPath.size()];
        int x, y, nextX, nextY;
        for(int i = 0; i < smoothPath.size() - 1; i++){
            //  LATER: Make option to follow path at an angle separate from tangent to path
            x = smoothPath.get(i).x;
            y = smoothPath.get(i).y;

            nextX = smoothPath.get(i+1).x;
            nextY = smoothPath.get(i+1).y;

            headings[i] = (float)Math.atan((nextY - y) / (nextX - x));
        }
        headings[headings.length-1] = 90.0f;

        ArrayList<Pose> finalPath = new ArrayList<Pose>();
        for(int i = 0; i < headings.length; i++){
            finalPath.add(new Pose(smoothPath.get(i).x, smoothPath.get(i).y, headings[i]));
        }
        return finalPath;
    }

    public void changeObstacles(Set<Point2D> newObstacles){
        this.obstacles = new HashSet<>(newObstacles);
    }
}
