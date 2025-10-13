import java.util.*;
public class Navigator {
    
    private Set<Point2D> obstacles;
    private DStarLite dLite;
    private PathSmoother pathSmoother;

    public Navigator(){
        this.dLite = new DStarLite();
        this.pathSmoother = new PathSmoother(dLite);
    }

    public List<Pose> getFullPath(Point2D start, Point2D goal){
        this.dLite.initializePath(start, goal);
        this.obstacles = new HashSet<Point2D>();
        List<Point2D> smoothPath = pathSmoother.computeSmoothPath(this.obstacles);
        System.out.println("Path Smoothed");
        float[] headings = new float[smoothPath.size()];
        int x, y, nextX, nextY;
        for(int i = 0; i < smoothPath.size() - 1; i++){
            //  LATER: Make option to follow path at an angle separate from tangent to path
            x = smoothPath.get(i).x;
            y = smoothPath.get(i).y;

            nextX = smoothPath.get(i+1).x;
            nextY = smoothPath.get(i+1).y;

            if(nextX - x != 0){
                headings[i] = (float)(Math.atan((nextY - y) / (nextX - x)));
            } else if(nextY - y > 0){
                headings[i] = 90;
            } else {
                headings[i] = 3.0f * (float)Math.PI / 2.0f;
            }
        
        }
        headings[headings.length-1] = 90.0f;

        ArrayList<Pose> finalPath = new ArrayList<Pose>();
        for(int i = 0; i < headings.length; i++){
            finalPath.add(new Pose(smoothPath.get(i).x, smoothPath.get(i).y, headings[i]));
        }
        return finalPath;
    }

    public void changeObstacles(Set<Point2D> newObstacles){
        this.obstacles = new HashSet<Point2D>(newObstacles);
    }
}
