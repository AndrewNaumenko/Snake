package Logic;

import Body.Direction;
import Body.Point;
import Body.Snake;

import java.util.List;

public class SnakeLogic extends Snake {
    public SnakeLogic(int width, int height, int blockSize) {
        super(width, height, blockSize);
    }

    public void snakeUpdate() {

        if (tail.size() > 0) {
            tail.remove(tail.size() - 1);
            tail.add(0, new Point(headLocation.getX(), headLocation.getY()));
        }

        switch (direction) {
            case UP:
                headLocation.setY(headLocation.getY() - blockSize);
                if (headLocation.getY() < 0) {
                    isCollidedWithWall = true;
                    headLocation.setY(0);
                }
                break;

            case DOWN:
                headLocation.setY(headLocation.getY() + blockSize);
                if (headLocation.getY() >= height) {
                    isCollidedWithWall = true;
                    headLocation.setY(height - blockSize);
                }
                break;

            case LEFT:
                headLocation.setX(headLocation.getX() - blockSize);
                if (headLocation.getX() < 0) {
                    isCollidedWithWall = true;
                    headLocation.setX(0);
                }
                break;

            case RIGHT:
                headLocation.setX(headLocation.getX() + blockSize);
                if (headLocation.getX() >= width) {
                    isCollidedWithWall = true;
                    headLocation.setX(width - blockSize);
                }
                break;

            default:
                break;
        }
    }

    public boolean collidedWithWall() {
        return isCollidedWithWall;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean collidedWithTail() {
        boolean isCollision = false;

        for (Point tailSegment : tail) {
            if (headLocation.equals(tailSegment)) {
                isCollision = true;
                break;
            }
        }

        return isCollision;
    }

    public void addTailSegment() {
        tail.add(0, new Point(headLocation.getX(), headLocation.getY()));
    }

    public void setDirection(Direction myDirection) {
        this.direction = myDirection;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void setHeadLocation(int x, int y) {
        headLocation.setX(x);
        headLocation.setY(y);
    }

    public Point getHeadLocation() {
        return headLocation;
    }

    public List<Point> getTail() {
        return tail;
    }
}
