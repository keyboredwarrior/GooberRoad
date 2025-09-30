import java.util.*;
public class Navigator {
    private List<Node> path;
    private List<Node> significantPoints;
    private Set<Node> obstacles;
    private DStarLite dLite;
    private Node start, goal;
    private static final int GRID_SIZE = 144;

    public Navigator(int sx, int sy, int gx, int gy){
        this.start = new Node(sx, sy);
        this.goal = new Node(gx, gy);
        initObstacles();

        this.dLite = new DStarLite(this.start, this.goal, GRID_SIZE, GRID_SIZE, obstacles);
        this.path = dLite.computePath();

        this.significantPoints = extractSignificantPoints();
    }

    private void initObstacles(){
        this.obstacles = new HashSet();
        for(int i = 0; i < 144; i++){

        }
    }

    private List<Node> extractSignificantPoints() {
        List<Node> sigPoints = this.path;
        Node successor, current, previous;
        double slope1, slope2;
        for(int i = 1; i < this.path.size() - 1; i++){
            successor = this.path.get(i+1);
            current = this.path.get(i);
            previous = this.path.get(i-1);

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
            }
        }
        return sigPoints;
    }
}
