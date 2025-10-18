package PathCreation;
import java.util.*;

public class PathSmoother{
    private List<Point2D> rawPath = null, smoothedPath;
    private List<Point2D> significantPoints;
    private DStarLite dStarLite;

    public PathSmoother(DStarLite dLite) {
        this.dStarLite = dLite;
    }
    
    private void computeRawPath(Set<Point2D> obstacles) {
        if(this.rawPath == null) {
            rawPath = dStarLite.computePath(obstacles);
        } else {
            rawPath = dStarLite.replanPath(obstacles);
        }
    }

    public List<Point2D> getRawPath() {
        return this.rawPath;
    }

    public List<Point2D> computeSmoothPath(Set<Point2D> obstacles) {
        computeRawPath(obstacles);
        extractSignificantPoints();
        this.smoothedPath = new ArrayList<Point2D>();

        int degree = 3;
        double[] U = uniformClampedKnots(significantPoints.size(), degree);
        BSpline spline = new BSpline(significantPoints, degree, U);

        double uStart = U[degree];
        double uEnd = U[U.length - degree - 1];
        for(float i = 0; i <= 1000; i++){
            float t = (float)uStart + (float)(uEnd - uStart) * (i / 1000.0f);
            Point2D p = spline.deBoor(t);
            smoothedPath.add(p);
        }

        return this.smoothedPath;
    }

    private void extractSignificantPoints() {
        List<Point2D> sigPoints = new ArrayList<Point2D>();
        Point2D successor, current, previous;
        double slope1, slope2;

        sigPoints.add(rawPath.getFirst());
        for(int i = 1; i < this.rawPath.size() - 1; i++){
            successor = this.rawPath.get(i+1);
            current = this.rawPath.get(i);
            previous = this.rawPath.get(i-1);

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
        
        this.significantPoints = sigPoints;
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