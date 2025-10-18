package Navigation;
import java.util.*;

import PathCreation.DStarLite;
import PathCreation.PathSmoother;
import PathCreation.Point2D;
public class Navigator {
    
    private Set<Point2D> obstacles;
    private DStarLite dLite;
    private PathSmoother pathSmoother;

    public Navigator(){
        this.dLite = new DStarLite();
        this.pathSmoother = new PathSmoother(dLite);
    }

    public Navigator(Object VisionPipeline){
        // this.VisionPipeline = VisionPipeline
    }

    public void updateObstacles() {
        // pull obstacles from vision pipeline
        
    }
}
