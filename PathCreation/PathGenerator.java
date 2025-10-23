package PathCreation;
import java.util.*;

public class PathGenerator{

    private static List<Point2D> rawPath = null;
    private static PathPoint[] smoothedPath;
    private static PathPoint[] straightPath;
    private static List<Point2D> significantPoints;
    private static DStarLite dStarLite;
    private static Set<Point2D> obstacles;

    public PathGenerator() {
        PathGenerator.dStarLite = new DStarLite();
        PathGenerator.smoothedPath = new PathPoint[200];
    }



    public Path genSmoothPath(Point2D start, Point2D goal, Set<Point2D> obstacles) {
        PathGenerator.dStarLite.initializePath(start, goal);
        PathGenerator.obstacles = obstacles;
        computeSmoothPath();
        return new Path(PathGenerator.smoothedPath);
    }

    public Path replanSmoothPath(Set<Point2D> obstacles) {
        PathGenerator.obstacles = obstacles;
        computeSmoothPath();
        return new Path(PathGenerator.smoothedPath);
    }



    public PathPoint[] genStraightPath() {
        return PathGenerator.straightPath;
    }

    private void computeRawPath() {
        if(PathGenerator.rawPath == null) {
            rawPath = dStarLite.computePath(PathGenerator.obstacles);
        } else {
            rawPath = dStarLite.replanPath(PathGenerator.obstacles);
        }
    }



    public void computeSmoothPath() {
        computeRawPath();
        extractSignificantPoints();

        int degree = 3;
        double[] U = uniformClampedKnots(significantPoints.size(), degree);
        BSpline spline = new BSpline(significantPoints, degree, U);
        Point2D p;


        List<Point2D> derivativeCntrlPts = new ArrayList<Point2D>();
        double denominator;
        float x, y;
        for(int i = 0; i < significantPoints.size() - 1; i++){
            denominator = U[i+degree+1] - U[i+1];
            x = (degree * (significantPoints.get(i+1).x - significantPoints.get(i).x) / (float)denominator);
            y = (degree * (significantPoints.get(i+1).y - significantPoints.get(i).y) / (float)denominator);
            derivativeCntrlPts.add(new Point2D(x, y));
        }

        BSpline derivSpline = new BSpline(derivativeCntrlPts, degree-1, U);
        Point2D tangent;


        double uStart = U[degree];
        double uEnd = U[U.length - degree - 1];
        float t;
        Pose pose;
        for(int i = 0; i < 200; i++){
            t = (float)uStart + (float)(uEnd - uStart) * (i / 200.0f);
            p = spline.deBoor(t);
            tangent = derivSpline.deBoor(t);

            pose = new Pose(p.x, p.y, (float)Math.atan(tangent.y / tangent.x));

            PathGenerator.smoothedPath[i] = new PathPoint(t, pose);
        }
    }

    public void computeStraightPath(Point2D start, Point2D goal) {
        if(start.x != goal.x){

        } else {
            if(start.y < goal.y){

            } else {

            }
        }
    }



    private void extractSignificantPoints() {
        List<Point2D> sigPoints = new ArrayList<Point2D>();
        Point2D successor, current, previous;
        double slope1, slope2;

        sigPoints.add(rawPath.getFirst());
        for(int i = 1; i < PathGenerator.rawPath.size() - 1; i++){
            successor = PathGenerator.rawPath.get(i+1);
            current = PathGenerator.rawPath.get(i);
            previous = PathGenerator.rawPath.get(i-1);

            if(current.x != previous.x){
                slope1 = (current.y - previous.y) / (current.x - previous.x);
            } else {
                slope1 = Double.POSITIVE_INFINITY;
            }
            if(current.x != successor.x){
                slope2 = (successor.y - current.y) / (successor.x - current.x);
            } else {
                slope2 = Double.POSITIVE_INFINITY;
            }

            if(slope1 != slope2 || i % 10 == 0){
                sigPoints.add(current);
            }
        }
        sigPoints.add(rawPath.getLast());
        
        PathGenerator.significantPoints = sigPoints;
    }

    public static double[] uniformClampedKnots(int numCtrlPts, int degree){
        int m = numCtrlPts + degree + 1;
        double[] U = new double[m];
        int n = numCtrlPts - 1;

        for(int i = 0; i <= degree; i++) {U[i] = 0.0;}

        for(int i = degree + 1; i < m - degree - 1; i++){
            U[i] = (double)(i - degree) / (n - degree + 1);
        }

        for(int i = m - degree - 1; i < m; i++) {U[i] = 1.0;}

        return U;
    }
}