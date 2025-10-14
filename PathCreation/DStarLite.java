package PathCreation;
import java.util.*;


public class DStarLite {
    // the key for comparing two nodes when ordering the queue/path
    private static class Key implements Comparable<Key> {
        double first, second;

        Key(double first, double second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public int compareTo(Key other) {
            if(Math.abs(this.first - other.first) > 1e-9){
                return Double.compare(this.first, other.first);
            }
            return Double.compare(this.second, other.second);
        }
    }

    private Point2D start, goal;
    private Point2D lastStart; // Track previous start position for km calculation
    private final int SIDE_LENGTH = 144;
    private Set<Point2D> obstacles = null;
    private final Map<Point2D, Double> gCost, rhsCost;
    private final Map<Point2D, Key> queue;
    private double km;

    private static final int[][] MOTIONS = {
        {0, 1}, {0, -1}, {1, 0}, {-1, 0},      // cardinal directions
        {1, 1}, {1, -1}, {-1, 1}, {-1, -1}     // diagonal directions
    };

    public DStarLite() {
        this.gCost = new HashMap<Point2D, Double>();
        this.rhsCost = new HashMap<Point2D, Double>();
        this.queue = new HashMap<Point2D, Key>();
        this.km = 0.0;
        
        Point2D node;
        for(int i = 0; i < SIDE_LENGTH; i++){
            for(int j = 0; j < SIDE_LENGTH; j++){
                node = new Point2D(i, j);
                gCost.put(node, Double.POSITIVE_INFINITY);
                rhsCost.put(node, Double.POSITIVE_INFINITY);
            }
        }
    }

    public void initializePath(Point2D start, Point2D goal) {
        this.start = start;
        this.goal = goal;
        this.lastStart = start;
        
        // CRITICAL: Initialize the goal node
        rhsCost.put(goal, 0.0);
        queue.put(goal, calculateKey(goal));
    }

    public List<Point2D> computePath(Set<Point2D> obstacles) {
        
        this.obstacles = new HashSet<>(obstacles);

        while(true) {
            Map.Entry<Point2D, Key> minEntry = getMinKey();
            if(minEntry == null) {break;}

            Point2D node = minEntry.getKey();
            Key k = minEntry.getValue();

            Key startKey = calculateKey(start);
            if(k.compareTo(startKey) >= 0 && Math.abs(rhsCost.get(start) - gCost.get(start)) < 1e-9) {
                break;
            }

            Key kOld = k;
            queue.remove(node);

            if(kOld.compareTo(calculateKey(node)) < 0) {
                queue.put(node, calculateKey(node));
            } else if(gCost.get(node) > rhsCost.get(node)) {
                gCost.put(node, rhsCost.get(node));
                for(Point2D neighbor : getNeighbors(node)) {
                    updateVertex(neighbor);
                }
            } else {
                gCost.put(node, Double.POSITIVE_INFINITY);
                updateVertex(node);
                for(Point2D neighbor : getNeighbors(node)) {
                    updateVertex(neighbor);
                }
            }
        }

        return extractPath();
    }

    private void updateVertex(Point2D node) {
        if(!node.equals(goal)) {
            double minRhsCost = Double.POSITIVE_INFINITY;
            for(Point2D neighbor : getNeighbors(node)) {
                double cost = gCost.get(neighbor) + cost(node, neighbor);
                minRhsCost = Math.min(minRhsCost, cost);
            }
            rhsCost.put(node, minRhsCost);
        }

        queue.remove(node);

        if(Math.abs(gCost.get(node) - rhsCost.get(node)) > 1e-9) {
            queue.put(node, calculateKey(node));
        }
    }

    private Key calculateKey(Point2D node) {
        double minGCost = Math.min(gCost.get(node), rhsCost.get(node));
        return new Key(minGCost + heuristic(start, node) + km, minGCost);
    }

    private Map.Entry<Point2D, Key> getMinKey() {
        if(queue.isEmpty()) {return null;}

        Map.Entry<Point2D, Key> minEntry = null;
        for(Map.Entry<Point2D, Key> entry : queue.entrySet()) {
            if(minEntry == null || entry.getValue().compareTo(minEntry.getValue()) < 0) {
                minEntry = entry;
            }
        }

        return minEntry;
    }

    // euclidean distance
    private double heuristic(Point2D a, Point2D b) {
        return Math.sqrt(Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2));
    }

    private double cost(Point2D a, Point2D b) {
        if(isCollision(a,b)) {
            return Double.POSITIVE_INFINITY;
        }
        return Math.sqrt(Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2));
    }

    private boolean isCollision(Point2D a, Point2D b) {
        return obstacles.contains(a) || obstacles.contains(b);
    }

    private List<Point2D> getNeighbors(Point2D node) {
        List<Point2D> neighbors = new ArrayList<>();
        for(int[] motion : MOTIONS){
            float newX = node.x + motion[0];
            float newY = node.y + motion[1];

            if(newX >= 0 && newX < SIDE_LENGTH && newY >= 0 && newY < SIDE_LENGTH){
                Point2D neighbor = new Point2D(newX, newY);
                neighbors.add(neighbor);
            }
        }

        return neighbors;
    }

    private List<Point2D> extractPath() {
        List<Point2D> path = new ArrayList<Point2D>();
        Point2D current = this.start;
        path.add(current);
        
        for(int i = 0; i < 10000; i++) {
            if(current.equals(this.goal)){break;}

            Map<Point2D, Double> gList = new HashMap<Point2D, Double>();

            for(Point2D neighbor : getNeighbors(current)) {
                if(!isCollision(current, neighbor)) {
                    double cost = gCost.getOrDefault(neighbor, Double.POSITIVE_INFINITY);
                    gList.put(neighbor, cost);
                }
            }

            if(gList.isEmpty()){break;}

            current = Collections.min(gList.entrySet(), Map.Entry.comparingByValue()).getKey();
            path.add(current);
            gList.clear();
        }
        return path;
    }

    public List<Point2D> replanPath(Set<Point2D> newObstacles){
        // Update km with distance robot has moved
        km += heuristic(lastStart, start);
        lastStart = start;

        // Find newly added obstacles
        Set<Point2D> addedObstacles = new HashSet<>(newObstacles);
        addedObstacles.removeAll(obstacles);

        // Find removed obstacles
        Set<Point2D> removedObstacles = new HashSet<>(obstacles);
        removedObstacles.removeAll(newObstacles);

        // Update obstacles to reflect new state
        obstacles = new HashSet<>(newObstacles);

        // Process all affected cells and their neighbors
        Set<Point2D> affectedCells = new HashSet<>();
        affectedCells.addAll(addedObstacles);
        affectedCells.addAll(removedObstacles);

        for(Point2D cell : affectedCells) {
            // Update the affected cell itself
            updateVertex(cell);
            
            // Update all neighbors of the affected cell
            for(Point2D neighbor : getNeighbors(cell)) {
                updateVertex(neighbor);
            }
        }

        // Recompute the path
        return computePath(obstacles);
    }
    
    // Method to update the robot's current position (call this as robot moves)
    public void updateStart(Point2D newStart) {
        this.start = newStart;
    }
}