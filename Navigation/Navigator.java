package Navigation;
import java.util.*;

import PathCreation.DStarLite;
import PathCreation.PathGenerator;
import PathCreation.Point2D;
public class Navigator {
    
    private Set<Point2D> obstacles;
    private DStarLite dLite;
    private PathGenerator pathSmoother;

    public Navigator(){
        this.dLite = new DStarLite();
        this.pathSmoother = new PathGenerator(dLite);
    }

    public Navigator(Object VisionPipeline){
        // this.VisionPipeline = VisionPipeline
    }

    public void updateObstacles() {
        // pull obstacles from vision pipeline
        
    }
}
