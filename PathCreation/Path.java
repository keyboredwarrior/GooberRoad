package PathCreation;

public class Path {
    // basically a list of PathPoints but with additional constraints regarding deceleration etc
    private PathPoint[] PoseSequence;
    private Pose closestPose;


    public Path(PathPoint[] poseSequence){
        this.PoseSequence = poseSequence;
    }

    public Pose getClosestPathPose(Pose currentPose){
        closestPose = PoseSequence[0].pose;
        for(PathPoint point : PoseSequence){
            if(euclideanDistance(closestPose, currentPose) > euclideanDistance(currentPose, point.pose)){
                closestPose = point.pose;
            }
        }
        return closestPose;
    }

    private float euclideanDistance(Pose p1, Pose p2){
        return (float)Math.sqrt( Math.pow((p2.getX() - p1.getX()), 2) + Math.pow((p2.getY() - p1.getY()), 2) );
    }
}