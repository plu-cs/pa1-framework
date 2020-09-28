package rttr.disaster;

import java.util.Map;
import java.util.Set;

/**
 * PROBLEM 2 - Disaster Planning
 *
 * Fill in the method canBeMadeDisasterReady below.  To test it you have the following
 * options:
 *   - Use the GUI:  ./gradlew run
 *   - Run the JUnit test suites:
 *         ./gradlew test --tests "rttr.disaster.DisasterPlanningTests"
 *         ./gradlew test --tests "rttr.disaster.DisasterPlanningFileTests"
 *   - Run an individual JUnit test (for example dontBeGreedy1):
 *         ./gradlew test --tests "rttr.disaster.DisasterPlanningTests.dontBeGreedy1"
 *   - Create your own JUnit tests in the above files, you can find those files in: src/test/java/rttr/disaster
 */
public class DisasterPlanning {

    /**
     * Given a road network and the maximum number of cities to use, determines whether or not
     * the network can be made disaster ready using no more than numCities as supply locations.
     * A city is disaster ready if each city is either a supply location
     * or directly adjacent to a city that is a supply location.
     *
     * If it is possible, return true and the parameter supplyLocations will be populated with
     * the names of the cities that are supply locations.  If not, the method should return false.
     *
     * @param roadNetwork the road network as an adjacency list.  The key is the city and the value
     *                    is the set of adjacent cities.  You may assume that this network is undirected.
     *                    That is, if city B is in the adjacency list for city A, then city A is in
     *                    the adjacency list for B.
     * @param numCities the maximum number of cities that can be supply locations.
     * @param supplyLocations this parameter should be filled with the names of the cities that are
     *                        supply locations, if the method returns true
     * @return true if it is possible to make this network disaster ready.
     */
    public static boolean canBeMadeDisasterReady(
            Map<String, Set<String>> roadNetwork,
            int numCities,
            Set<String> supplyLocations) {

        // TODO: fill in this method.

        return false;
    }
}
