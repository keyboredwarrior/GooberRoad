package Navigation;
public class Pose {
    private final float x;
    private final float y;
    private final float heading;


    public Pose(float x, float y, float heading) {
        this.x = x;
        this.y = y;
        this.heading = heading;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getHeading() {
        return this.heading;
    }
}
