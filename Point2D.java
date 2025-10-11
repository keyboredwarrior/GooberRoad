public class Point2D {
    public int x, y;
    
    public Point2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        Point2D Point2D = (Point2D) o;
        return x == Point2D.x && y == Point2D.y;
    }
}