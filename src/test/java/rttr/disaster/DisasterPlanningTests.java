package rttr.disaster;

import org.junit.Test;
import rttr.disaster.DisasterPlanning;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DisasterPlanningTests {

    @Test
    // Provided Test: Works for map with one city.
    public void oneCity1() {
        Map<String, Set<String>> map = Map.ofEntries(
                Map.entry( "Solipsist", new HashSet<>() )
        );

        HashSet<String> locations = new HashSet<>();
        assertFalse(DisasterPlanning.canBeMadeDisasterReady(map, 0, locations));
        assertTrue(DisasterPlanning.canBeMadeDisasterReady(map, 1, locations));
        assertTrue(DisasterPlanning.canBeMadeDisasterReady(map, 2, locations));
    }

    @Test
    // Provided Test: Works for map with one city, and produces output.
    public void oneCity2() {
        Map<String, Set<String>> map = Map.ofEntries(
                Map.entry( "Solipsist", new HashSet<>() )
        );

        HashSet<String> locations0 = new HashSet<>();
        HashSet<String> locations1 = new HashSet<>();
        HashSet<String> locations2 = new HashSet<>();
        assertFalse(DisasterPlanning.canBeMadeDisasterReady(map, 0, locations0));
        assertTrue(DisasterPlanning.canBeMadeDisasterReady(map, 1, locations1));
        assertTrue(DisasterPlanning.canBeMadeDisasterReady(map, 2, locations2));

        /* Don't check locations0; since the function returned false, the values there
         * can be anything. */
        Set<String> expected = Set.of( "Solipsist" );
        assertEquals(expected, locations1);
        assertEquals(expected, locations2);
    }

    @Test
    // Provided Test: Works for map with two linked cities.
    public void twoLinkedCities() {
        Map<String, Set<String>> map = Map.ofEntries(
                Map.entry("A", Set.of("B") ),
                Map.entry("B", Set.of("A") )
        );

        Set<String> locations0 = new HashSet<>(), locations1 = new HashSet<>(), locations2 = new HashSet<>();
        assertFalse(DisasterPlanning.canBeMadeDisasterReady(map, 0, locations0));
        assertTrue(DisasterPlanning.canBeMadeDisasterReady(map, 1, locations1));
        assertTrue(DisasterPlanning.canBeMadeDisasterReady(map, 2, locations2));
    }

    @Test
    // Provided Test: Works for map with two linked cities, and produces output.
    public void twoLinkedCities2() {
        Map<String, Set<String>> map = Map.ofEntries(
                Map.entry("A", Set.of("B") ),
                Map.entry("B", Set.of("A") )
        );

        Set<String> locations0 = new HashSet<>(), locations1 = new HashSet<>(), locations2 = new HashSet<>();
        assertFalse(DisasterPlanning.canBeMadeDisasterReady(map, 0, locations0));
        assertTrue(DisasterPlanning.canBeMadeDisasterReady(map, 1, locations1));
        assertTrue(DisasterPlanning.canBeMadeDisasterReady(map, 2, locations2));

        assertEquals(1, locations1.size());
        assertTrue(Set.of("A","B").containsAll(locations1));

        assertTrue(locations2.size() <= 2);
        assertTrue(Set.of("A","B").containsAll(locations2));
    }

    @Test
    // Provided Test: Works for four cities in a line.
    public void fourCitiesInALine1() {
        Map<String, Set<String>> map = Map.ofEntries(
                Map.entry("A", Set.of("B")),
                Map.entry("B", Set.of("C", "A")),
                Map.entry("C", Set.of("D","B")),
                Map.entry("D", Set.of("C"))
        );

        Set<String>
                locations0 = new HashSet<>(),
                locations1 = new HashSet<>(),
                locations2 = new HashSet<>(),
                locations3 = new HashSet<>(),
                locations4 = new HashSet<>();
        assertFalse(DisasterPlanning.canBeMadeDisasterReady(map, 0, locations0));
        assertFalse(DisasterPlanning.canBeMadeDisasterReady(map, 1, locations1));
        assertTrue(DisasterPlanning.canBeMadeDisasterReady(map, 2, locations2));
        assertTrue(DisasterPlanning.canBeMadeDisasterReady(map, 3, locations3));
        assertTrue(DisasterPlanning.canBeMadeDisasterReady(map, 4, locations4));
    }

    @Test
    // Provided Test: Works for four cities in a line, and produces output.
    public void fourCitiesInALine2() {
        Map<String, Set<String>> map = Map.ofEntries(
                Map.entry("A", Set.of("B")),
                Map.entry("B", Set.of("C", "A")),
                Map.entry("C", Set.of("D","B")),
                Map.entry("D", Set.of("C"))
        );

        Set<String>
                locations0 = new HashSet<>(),
                locations1 = new HashSet<>(),
                locations2 = new HashSet<>(),
                locations3 = new HashSet<>(),
                locations4 = new HashSet<>();
        assertFalse(DisasterPlanning.canBeMadeDisasterReady(map, 0, locations0));
        assertFalse(DisasterPlanning.canBeMadeDisasterReady(map, 1, locations1));
        assertTrue(DisasterPlanning.canBeMadeDisasterReady(map, 2, locations2));
        assertTrue(DisasterPlanning.canBeMadeDisasterReady(map, 3, locations3));
        assertTrue(DisasterPlanning.canBeMadeDisasterReady(map, 4, locations4));

        Set<String> all = Set.of("A", "B", "C", "D");
        assertTrue(locations2.size() <= 2);
        assertTrue(locations3.size() <= 3);
        assertTrue(locations4.size() <= 4);
        assertTrue(all.containsAll(locations2));
        assertTrue(all.containsAll(locations3));
        assertTrue(all.containsAll(locations4));

        /* Check if locations2 is a solution. (locations3 and locations4 must be) */
        assertTrue(isCovered("A", map, locations2));
        assertTrue(isCovered("B", map, locations2));
        assertTrue(isCovered("C", map, locations2));
        assertTrue(isCovered("D", map, locations2));
    }

    @Test
    // Provided Test: Works for four disconnected cities.
    public void fourDisconnectedCities1() {
        Map<String, Set<String>> map = Map.ofEntries(
                Map.entry("A", new HashSet<>()),
                Map.entry("B", new HashSet<>()),
                Map.entry("C", new HashSet<>()),
                Map.entry("D", new HashSet<>())
        );

        Set<String>
                locations0 = new HashSet<>(),
                locations1 = new HashSet<>(),
                locations2 = new HashSet<>(),
                locations3 = new HashSet<>(),
                locations4 = new HashSet<>();
        assertFalse(DisasterPlanning.canBeMadeDisasterReady(map, 0, locations0));
        assertFalse(DisasterPlanning.canBeMadeDisasterReady(map, 1, locations1));
        assertFalse(DisasterPlanning.canBeMadeDisasterReady(map, 2, locations2));
        assertFalse(DisasterPlanning.canBeMadeDisasterReady(map, 3, locations3));
        assertTrue(DisasterPlanning.canBeMadeDisasterReady(map, 4, locations4));
    }

    @Test
    //Provided Test: Works for four disconnected cities, and produces output.
    public void fourDisconnectedCities2() {
        Map<String, Set<String>> map = Map.ofEntries(
                Map.entry("A", new HashSet<>()),
                Map.entry("B", new HashSet<>()),
                Map.entry("C", new HashSet<>()),
                Map.entry("D", new HashSet<>())
        );

        Set<String>
                locations0 = new HashSet<>(),
                locations1 = new HashSet<>(),
                locations2 = new HashSet<>(),
                locations3 = new HashSet<>(),
                locations4 = new HashSet<>();
        assertFalse(DisasterPlanning.canBeMadeDisasterReady(map, 0, locations0));
        assertFalse(DisasterPlanning.canBeMadeDisasterReady(map, 1, locations1));
        assertFalse(DisasterPlanning.canBeMadeDisasterReady(map, 2, locations2));
        assertFalse(DisasterPlanning.canBeMadeDisasterReady(map, 3, locations3));
        assertTrue(DisasterPlanning.canBeMadeDisasterReady(map, 4, locations4));

        Set<String> expected = Set.of("A", "B", "C", "D");
        assertEquals(expected, locations4);
    }

    @Test
    // Provided Test: Works on a 3x3 grid.
    public void grid3x3_1() {
        /* Make a 3x3 grid of cities. */
        Map<String, Set<String>> map = Map.ofEntries(
                Map.entry("A1", Set.of("A2", "B1")),
                Map.entry( "A2", Set.of("A3", "B2", "A1") ),
                Map.entry( "A3", Set.of("B3", "A2"      ) ),
                Map.entry( "B1", Set.of("B2", "C1", "A1") ),
                Map.entry( "B2", Set.of("B3", "C2", "A2", "B1") ),
                Map.entry( "B3", Set.of("C3", "A3", "B2"      ) ),
                Map.entry( "C1", Set.of("C2", "B1") ),
                Map.entry( "C2", Set.of("C3", "B2", "C1") ),
                Map.entry( "C3", Set.of("C2", "B3") )
        );

        Set<String>
                locations0 = new HashSet<>(),
                locations1 = new HashSet<>(),
                locations2 = new HashSet<>(),
                locations3 = new HashSet<>();
        assertFalse(DisasterPlanning.canBeMadeDisasterReady(map, 0, locations0));
        assertFalse(DisasterPlanning.canBeMadeDisasterReady(map, 1, locations1));
        assertFalse(DisasterPlanning.canBeMadeDisasterReady(map, 2, locations2));
        assertTrue( DisasterPlanning.canBeMadeDisasterReady(map, 3, locations3));
    }

    @Test
    // Provided Test: Works on a 3x3 grid, and produces output.
    public void grid3x3_2() {
        /* Make a 3x3 grid of cities. */
        Map<String, Set<String>> map = Map.ofEntries(
                Map.entry("A1", Set.of("A2", "B1")),
                Map.entry( "A2", Set.of("A3", "B2", "A1") ),
                Map.entry( "A3", Set.of("B3", "A2"      ) ),
                Map.entry( "B1", Set.of("B2", "C1", "A1") ),
                Map.entry( "B2", Set.of("B3", "C2", "A2", "B1") ),
                Map.entry( "B3", Set.of("C3", "A3", "B2"      ) ),
                Map.entry( "C1", Set.of("C2", "B1") ),
                Map.entry( "C2", Set.of("C3", "B2", "C1") ),
                Map.entry( "C3", Set.of("C2", "B3") )
        );

        Set<String>
                locations0 = new HashSet<>(),
                locations1 = new HashSet<>(),
                locations2 = new HashSet<>(),
                locations3 = new HashSet<>();
        assertFalse(DisasterPlanning.canBeMadeDisasterReady(map, 0, locations0));
        assertFalse(DisasterPlanning.canBeMadeDisasterReady(map, 1, locations1));
        assertFalse(DisasterPlanning.canBeMadeDisasterReady(map, 2, locations2));
        assertTrue( DisasterPlanning.canBeMadeDisasterReady(map, 3, locations3));

        /* Check that we have a solution. */
        assertTrue(isCovered("A1", map, locations3));
        assertTrue(isCovered("A2", map, locations3));
        assertTrue(isCovered("A3", map, locations3));
        assertTrue(isCovered("B1", map, locations3));
        assertTrue(isCovered("B2", map, locations3));
        assertTrue(isCovered("B3", map, locations3));
        assertTrue(isCovered("C1", map, locations3));
        assertTrue(isCovered("C2", map, locations3));
        assertTrue(isCovered("C3", map, locations3));
    }

    @Test
    // Provided Test: Can solve ethane example with two cities.
    public void ethane1() {
        Map<String, Set<String>> map = Map.ofEntries(
                Map.entry( "C1", Set.of("H1", "H3", "H5", "C2") ),
                Map.entry( "C2", Set.of("H2", "H4", "H6", "C1") ),
                Map.entry( "H1", Set.of("C1") ),
                Map.entry( "H2", Set.of("C2") ),
                Map.entry( "H3", Set.of("C1") ),
                Map.entry( "H4", Set.of("C2") ),
                Map.entry( "H5", Set.of("C1") ),
                Map.entry( "H6", Set.of("C2") )
        );

        Set<String> locations = new HashSet<>();
        assertTrue(DisasterPlanning.canBeMadeDisasterReady(map, 2, locations));
    }

    @Test
    // Provided Test: Can solve ethane example with two cities, and produces output.
    public void ethane2() {
        Map<String, Set<String>> map = Map.ofEntries(
                Map.entry( "C1", Set.of("H1", "H3", "H5", "C2") ),
                Map.entry( "C2", Set.of("H2", "H4", "H6", "C1") ),
                Map.entry( "H1", Set.of("C1") ),
                Map.entry( "H2", Set.of("C2") ),
                Map.entry( "H3", Set.of("C1") ),
                Map.entry( "H4", Set.of("C2") ),
                Map.entry( "H5", Set.of("C1") ),
                Map.entry( "H6", Set.of("C2") )
        );

        Set<String> locations = new HashSet<>();
        assertTrue(DisasterPlanning.canBeMadeDisasterReady(map, 2, locations));

        assertEquals(Set.of("C1", "C2"), locations);
    }

    @Test
    // Provided Test: Solves "Don't be Greedy" from the handout.
    public void dontBeGreedy1() {
        Map<String, Set<String>> map = Map.ofEntries(
                Map.entry( "A", Set.of( "B" ) ),
                Map.entry( "B", Set.of( "C", "D", "A" ) ),
                Map.entry( "C", Set.of( "D", "B" ) ),
                Map.entry( "D", Set.of( "G", "F", "B", "C" ) ),
                Map.entry( "E", Set.of( "F" ) ),
                Map.entry( "F", Set.of( "G", "D", "E" ) ),
                Map.entry( "G", Set.of( "D", "F"))
        );

        Set<String>
                locations0 = new HashSet<>(),
                locations1 = new HashSet<>(),
                locations2 = new HashSet<>();

        assertFalse(DisasterPlanning.canBeMadeDisasterReady(map, 0, locations0));
        assertFalse(DisasterPlanning.canBeMadeDisasterReady(map, 1, locations1));
        assertTrue(DisasterPlanning.canBeMadeDisasterReady(map, 2, locations2));
    }

    @Test
    // Provided Test: Solves "Don't be Greedy" from the handout, and produces output.
    public void dontBeGreedy2() {
        Map<String, Set<String>> map = Map.ofEntries(
                Map.entry( "A", Set.of( "B" ) ),
                Map.entry( "B", Set.of( "C", "D", "A" ) ),
                Map.entry( "C", Set.of( "D", "B" ) ),
                Map.entry( "D", Set.of( "G", "F", "B", "C" ) ),
                Map.entry( "E", Set.of( "F" ) ),
                Map.entry( "F", Set.of( "G", "D", "E" ) ),
                Map.entry( "G", Set.of( "D", "F"))
        );

        Set<String>
                locations0 = new HashSet<>(),
                locations1 = new HashSet<>(),
                locations2 = new HashSet<>();

        assertFalse(DisasterPlanning.canBeMadeDisasterReady(map, 0, locations0));
        assertFalse(DisasterPlanning.canBeMadeDisasterReady(map, 1, locations1));
        assertTrue(DisasterPlanning.canBeMadeDisasterReady(map, 2, locations2));

        Set<String> expected = Set.of("B", "F");
        assertEquals(expected, locations2);
    }

    @Test
    // Provided Test: Stress test: 6 x 6 grid. (This should take at most a few seconds.)
    public void stressTest6x6_1() {
        Map<String, Set<String>> grid = new HashMap<>();

        /* Build the grid. */
        char maxRow = 'F';
        int  maxCol = 6;
        for (char row = 'A'; row <= maxRow; row++) {
            for (int col = 1; col <= maxCol; col++) {
                String cellName = "" + row + col;
                Set<String> set = new HashSet<>();
                grid.put(cellName, set);

                if (row != maxRow) {
                    set.add( "" + (char)(row+1) + col);
                }
                if (col != maxCol) {
                    set.add("" + row + (col + 1) );
                }
            }
        }

        // Make symmetric
        for (String from: grid.keySet()) {
            for (String to: grid.get(from)) {
                Set<String> toSet = grid.get(to);
                toSet.add(from);
            }
        }

        Set<String> locations = new HashSet<>();
        assertTrue(DisasterPlanning.canBeMadeDisasterReady(grid, 10, locations));
    }

    @Test
    // Provided Test: Stress test: 6 x 6 grid with output. (This should take at most a few seconds.)
    public void stressTest6x6_2() {
        Map<String, Set<String>> grid = new HashMap<>();

        /* Build the grid. */
        char maxRow = 'F';
        int  maxCol = 6;
        for (char row = 'A'; row <= maxRow; row++) {
            for (int col = 1; col <= maxCol; col++) {
                String cellName = "" + row + col;
                Set<String> set = new HashSet<>();
                grid.put(cellName, set);

                if (row != maxRow) {
                    set.add( "" + (char)(row+1) + col);
                }
                if (col != maxCol) {
                    set.add("" + row + (col + 1) );
                }
            }
        }

        // Make symmetric
        for (String from: grid.keySet()) {
            for (String to: grid.get(from)) {
                Set<String> toSet = grid.get(to);
                toSet.add(from);
            }
        }

        Set<String> locations = new HashSet<>();
        assertTrue(DisasterPlanning.canBeMadeDisasterReady(grid, 10, locations));

        for (char row = 'A'; row <= maxRow; row++) {
            for (int col = 1; col <= maxCol; col++) {
                assertTrue(isCovered("" + row + col, grid, locations));
            }
        }
    }

    private boolean isCovered(String city, Map<String, Set<String>> roadNetwork, Set<String> supplyLocations) {
        if (supplyLocations.contains(city)) return true;

        for (String neighbor: roadNetwork.get(city)) {
            if (supplyLocations.contains(neighbor)) return true;
        }

        return false;
    }

}
