package rttr.election;

import java.awt.geom.Point2D;

public class Bounds {
    public Point2D min;
    public Point2D max;
    public Bounds() {
        min = new Point2D.Double(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        max = new Point2D.Double(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
    }

    public Bounds( Point2D min, Point2D max ) {
        this.min = new Point2D.Double(min.getX(), min.getY());
        this.max = new Point2D.Double(max.getX(), max.getY());
    }

    public void add( Point2D pt ) {
        min = new Point2D.Double( Math.min( min.getX(), pt.getX() ),
            Math.min( min.getY(), pt.getY() ));
        max = new Point2D.Double( Math.max(max.getX(), pt.getX()),
                Math.max(max.getY(), pt.getY()));
    }

    public double getWidth() {
        return max.getX() - min.getX();
    }

    public double getHeight() {
        return max.getY() - min.getY();
    }

    public String toString() {
        return String.format("min = (%f,%f) max = (%f,%f)", min.getX(), min.getY(), max.getX(), max.getY());
    }
}
