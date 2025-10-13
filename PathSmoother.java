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

        for(float t = 0; t < (float)significantPoints.size() - 3.0; t += 0.005){
            this.smoothedPath.add(getSplinePoint(t));
        }

        return this.smoothedPath;
    }

    private Point2D getSplinePoint(float t) {
        int p0, p1, p2, p3;
        p1 = (int)t + 1;
        p2 = p1 + 1;
        p3 = p2 + 1;
        p0 = p1 - 1;

        t = t - (int)t;

        float tt = t * t;
        float ttt = tt * t;

        float q1 = -1 * ttt + 2.0f*tt - t;
        float q2 = 3.0f * ttt - 5.0f * tt + 2.0f;
        float q3 = -3.0f * ttt + 4.0f * tt + t;
        float q4 = ttt - tt;

        float tx = significantPoints.get(p0).x * q1 + significantPoints.get(p1).x * q2 + significantPoints.get(p2).x * q3 + significantPoints.get(p3).x * q4;
        float ty = significantPoints.get(p0).y * q1 + significantPoints.get(p1).y * q2 + significantPoints.get(p2).y * q3 + significantPoints.get(p3).y * q4;
        
        tx *= 0.5;
        ty *= 0.5;
        
        return new Point2D((int)tx, (int)ty);
    }

    private void extractSignificantPoints() {
        List<Point2D> sigPoints = this.rawPath;
        Point2D successor, current, previous;
        double slope1, slope2;
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

            if(slope1 == slope2){
                sigPoints.remove(current);
                i--;
            }
        }
        
        int startX = rawPath.get(0).x;
        int startY = rawPath.get(0).y;

        int xDif1 = startX - rawPath.get(1).x;
        int yDif1 = startY - rawPath.get(1).y;

        sigPoints.add(0, new Point2D(startX + xDif1, startY + yDif1));

        int endX = rawPath.getLast().x;
        int endY = rawPath.getLast().y;

        int xDif2 = endX - rawPath.get(rawPath.size()-2).x;
        int yDif2 = endY - rawPath.get(rawPath.size()-2).y;

        sigPoints.add(new Point2D(endX + xDif2, endY + yDif2));
        this.significantPoints = sigPoints;
    }
}