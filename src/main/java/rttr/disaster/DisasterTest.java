package rttr.disaster;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DisasterTest {
    public Map<String, Set<String>> network;  // The road network
    public Map<String, Point2D> cityLocations;
    public DisasterTest() {
        network = new HashMap<>();
        cityLocations = new HashMap<>();
    }
}