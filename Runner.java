import java.util.HashSet;

import PathCreation.DStarLite;
import PathCreation.PathGenerator;
import PathCreation.Point2D;

public class Runner {
    public static void main(String[] args){
        DStarLite dStarLite = new DStarLite();
        PathGenerator pathgen = new PathGenerator();

        Point2D start = new Point2D(120, 120);
        Point2D end = new Point2D(70, 50);
        dStarLite.initializePath(start, end);

        HashSet<Point2D> obstacles = new HashSet<>();
        for(int i = 0; i < 144; i++){
            for(int j = 0; j < 144; j++){
                if(!((i==start.x && j==start.y) || (i==end.x && j==end.y)) && Math.random() > 0.67){
                    obstacles.add(new Point2D((float)i, (float)j));
                }
            }
        }
        pathgen.computeSmoothPath();
    }
}
