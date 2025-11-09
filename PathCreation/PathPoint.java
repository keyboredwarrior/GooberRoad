package PathCreation;

public class PathPoint {
    public double t;
    public Pose pose;

    public PathPoint(double t, Pose pose) {
        this.t = t;
        this.pose = pose;
    }

    public double getTValue() {
        return this.t;
    }

    public Pose getPose() {
        return this.pose;
    }
}
