package Body;

public class Food {
    private Point location;

    public Food(int x, int y) {
        location = new Point(x, y);
    }

    public Point getLocation() {
        return location;
    }

}