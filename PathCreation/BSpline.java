package PathCreation;
import java.util.*;

public class BSpline {
    private List<Point2D> controlPoints;
    private double[] knots;
    private final int degree;

    public BSpline(List<Point2D> controlPoints, int degree, double[] knots) {
        this.controlPoints = controlPoints;
        this.degree = degree;
        this.knots = knots;
    }

    public Point2D deBoor(float t) {
        int k = findKnotSpan(t);
        int p = degree;

        Point2D[] d = new Point2D[p+1];
        for(int j = 0; j <= p; j++){
            d[j] = new Point2D(
                controlPoints.get(k - p + j).x,
                controlPoints.get(k - p + j).y
            );
        }

        for(int i = 1; i <= p; i++){
            for(int j = p; j >= i; j--){
                int index = k - p + j;
                float alpha = (t - (float)knots[index]) / (float)(knots[index + p - i + 1] - knots[index]);
                float x = (1 - alpha) * d[j-1].x + alpha * d[j].x;
                float y = (1 - alpha) * d[j-1].y + alpha * d[j].y;
                d[j] = new Point2D(x, y);
            }
        }
        return d[p];
    }

    private int findKnotSpan(float t) {
        int n = controlPoints.size() - 1;
        if(t >= knots[n+1]) {return n;}
        if(t <= knots[degree]) {return degree;}

        int low = degree;
        int high = n + 1;
        int mid = low + high / 2;
        while(t < knots[mid] || t >= knots[mid+1]) {
            if(t < knots[mid]) {high = mid;}
            else {low = mid;}
            mid = (low + high) / 2;
        }
        return mid;
    }
}
