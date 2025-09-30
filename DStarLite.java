import java.util.*;

public class DStarLite {

    // This hot little number basically just helps determine the priority order of selected
    // nodes in the path. AKA the logic behind the queue, based on gCost, rhsCost of nodes
    private static class Key implements Comparable<Key> {
        double first, second;
        
        Key(double first, double second) {
            this.first = first;
            this.second = second;
        }
        
        @Override
        public int compareTo(Key other) {
            if (Math.abs(this.first - other.first) > 1e-9) {
                return Double.compare(this.first, other.first);
            }
            return Double.compare(this.second, other.second);
        }
    }
    
    private Node start, goal;
    private int gridWidth, gridHeight; // Generally 144x144 for FTC field
    private Set<Node> obstacles; // List of nodes declared as obstacles
    private Map<Node, Double> g, rhs;
    private Map<Node, Key> U;
    private double km;

    private static final int[][] MOTIONS = {
        {0, 1}, {0, -1}, {1, 0}, {-1, 0},      // cardinal directions
        {1, 1}, {1, -1}, {-1, 1}, {-1, -1}     // diagonal directions
    };
    
    public DStarLite(Node start, Node goal, int gridWidth, int gridHeight, Set<Node> obstacles) {
        this.start = start;
        this.goal = goal;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.obstacles = new HashSet<>(obstacles);
        this.g = new HashMap<>();
        this.rhs = new HashMap<>();
        this.U = new HashMap<>();
        this.km = 0.0;
        
        // Initialize all nodes in a 144x144 grid
        for (int i = 0; i < gridWidth; i++) {
            for (int j = 0; j < gridHeight; j++) {
                Node node = new Node(i, j);
                g.put(node, Double.POSITIVE_INFINITY);
                rhs.put(node, Double.POSITIVE_INFINITY);
            }
        }
        
        rhs.put(goal, 0.0);
        U.put(goal, calculateKey(goal));
    }
    
    public List<Node> computePath() {
        while (true) {
            Map.Entry<Node, Key> minEntry = getMinKey();
            if (minEntry == null) break;
            
            Node s = minEntry.getKey();
            Key v = minEntry.getValue();
            
            Key startKey = calculateKey(start);
            if (v.compareTo(startKey) >= 0 && 
                Math.abs(rhs.get(start) - g.get(start)) < 1e-9) {
                break;
            }
            
            Key kOld = v;
            U.remove(s);
            
            if (kOld.compareTo(calculateKey(s)) < 0) {
                U.put(s, calculateKey(s));
            } else if (g.get(s) > rhs.get(s)) {
                g.put(s, rhs.get(s));
                for (Node neighbor : getNeighbors(s)) {
                    updateVertex(neighbor);
                }
            } else {
                g.put(s, Double.POSITIVE_INFINITY);
                updateVertex(s);
                for (Node neighbor : getNeighbors(s)) {
                    updateVertex(neighbor);
                }
            }
        }
        
        return extractPath();
    }
    
    private void updateVertex(Node s) {
        if (!s.equals(goal)) {
            double minRhs = Double.POSITIVE_INFINITY;
            for (Node neighbor : getNeighbors(s)) {
                double cost = g.get(neighbor) + cost(s, neighbor);
                minRhs = Math.min(minRhs, cost);
            }
            rhs.put(s, minRhs);
        }
        
        U.remove(s);
        
        if (Math.abs(g.get(s) - rhs.get(s)) > 1e-9) {
            U.put(s, calculateKey(s));
        }
    }
    
    private Key calculateKey(Node s) {
        double minG = Math.min(g.get(s), rhs.get(s));
        return new Key(minG + heuristic(start, s) + km, minG);
    }
    
    private Map.Entry<Node, Key> getMinKey() {
        if (U.isEmpty()) return null;
        
        Map.Entry<Node, Key> minEntry = null;
        for (Map.Entry<Node, Key> entry : U.entrySet()) {
            if (minEntry == null || entry.getValue().compareTo(minEntry.getValue()) < 0) {
                minEntry = entry;
            }
        }
        return minEntry;
    }
    
    private double heuristic(Node a, Node b) {
        // Euclidean distance
        return Math.sqrt(Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2));
    }
    
    private double cost(Node a, Node b) {
        if (isCollision(a, b)) {
            return Double.POSITIVE_INFINITY;
        }
        return Math.sqrt(Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2));
    }
    
    private boolean isCollision(Node a, Node b) {
        return obstacles.contains(a) || obstacles.contains(b);
    }
    
    private List<Node> getNeighbors(Node s) {
        List<Node> neighbors = new ArrayList<>();
        for (int[] motion : MOTIONS) {
            int newX = s.x + motion[0];
            int newY = s.y + motion[1];
            
            if (newX >= 0 && newX < gridWidth && newY >= 0 && newY < gridHeight) {
                Node neighbor = new Node(newX, newY);
                if (!obstacles.contains(neighbor)) {
                    neighbors.add(neighbor);
                }
            }
        }
        return neighbors;
    }
    
    private List<Node> extractPath() {
        List<Node> path = new ArrayList<>();
        Node current = start;
        path.add(current);
        
        for (int i = 0; i < 10000; i++) {
            if (current.equals(goal)) break;
            
            Map<Node, Double> gList = new HashMap<>();
            for (Node neighbor : getNeighbors(current)) {
                if (!isCollision(current, neighbor)) {
                    gList.put(neighbor, g.get(neighbor));
                }
            }
            
            if (gList.isEmpty()) break;
            
            current = Collections.min(gList.entrySet(), Map.Entry.comparingByValue()).getKey();
            path.add(current);
        }
        
        return path;
    }
    
    // Update obstacles and replan
    public List<Node> updateObstacles(Set<Node> newObstacles) {
        Set<Node> addedObstacles = new HashSet<>(newObstacles);
        addedObstacles.removeAll(obstacles);
        
        Set<Node> removedObstacles = new HashSet<>(obstacles);
        removedObstacles.removeAll(newObstacles);
        
        obstacles = new HashSet<>(newObstacles);
        
        // Update km
        km += heuristic(start, start);
        
        // Update affected vertices
        Set<Node> affectedNodes = new HashSet<>();
        affectedNodes.addAll(addedObstacles);
        affectedNodes.addAll(removedObstacles);
        
        for (Node node : affectedNodes) {
            if (obstacles.contains(node)) {
                g.put(node, Double.POSITIVE_INFINITY);
                rhs.put(node, Double.POSITIVE_INFINITY);
            } else {
                updateVertex(node);
            }
            
            for (Node neighbor : getNeighbors(node)) {
                updateVertex(neighbor);
            }
        }
        
        return computePath();
    }
}