package Body;

import java.util.ArrayList;
import java.util.List;

public class Snake {

    protected Direction direction;
    protected Point headLocation = new Point(0, 0);
    protected List<Point> tail = new ArrayList<Point>();
    protected int height;
    protected int width;
    protected int blockSize;
    protected boolean isCollidedWithWall = false;

    public Snake(int width, int height, int blockSize) {
        this.width = width;
        this.height = height;
        this.blockSize = blockSize;
        this.direction = Direction.RIGHT;
    }
}