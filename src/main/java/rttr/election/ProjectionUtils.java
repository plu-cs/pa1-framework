package rttr.election;

import java.awt.geom.Point2D;

public class ProjectionUtils {

    /* The math behind this function is shamelessly lifted (with love) from Wikipedia:
     *
     *   https://en.wikipedia.org/wiki/Mollweide_projection#Mathematical_formulation
     *
     * The inverse formula is way easier than the forward formula. :-)
     *
     * The output of this function is a pair of real numbers in the range [-2, +2] x [-1, 1],
     * which then needs to be scaled and translated based on the window size.
     */
    public static Point2D mollweideProjectionOf(double latitude, double longitude,
                                                double longitudeOffset, double latitudeOffset) {
        /* Adjust longitude to fit map. */
        longitude -= longitudeOffset;
        if (longitude < -180) longitude += 360;
        if (longitude > 180)  longitude -= 360;

        latitude -= latitudeOffset;
        if (latitude < -90)   latitude += 180;
        if (latitude >  90)   latitude  -= 180;

        /* Convert from degrees (what we get back from USGS) to radians. */
        longitude *= Math.PI / 180;
        latitude  *= Math.PI / 180;

        /* There isn't a closed-form solution to work out the coordinates, so we'll
         * use Newton's method to try to get close to one. Thanks, calculus!
         */
        final int kNumIterations = 100;
        double theta = latitude;
        for (int i = 0; i < kNumIterations; i++) {
            theta = theta - (2 * theta + Math.sin(2 * theta) - Math.PI * Math.sin(latitude)) / (2 + 2 * Math.cos(2 * theta));
        }

        /* Armed with theta, we can work out the x and y coordinates. */
        double x = 2 * Math.cos(theta) * longitude / Math.PI;
        double y = Math.sin(theta);

        return new Point2D.Double(x, y);
    }
}
