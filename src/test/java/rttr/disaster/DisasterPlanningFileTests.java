package rttr.disaster;

import org.junit.Test;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class DisasterPlanningFileTests {

    @Test
    //  numCities = 0
    public void zeroCities() throws Exception {
        DisasterTest testCase = getTest("CentralEurope.dst");
        HashSet<String> stockpiles = new HashSet<>();
        boolean result = DisasterPlanning.canBeMadeDisasterReady(testCase.network, 0, stockpiles);
        assertFalse(result);
    }

    @Test
    //  BritishIsles.dst
    public void britishIsles() throws Exception {
        DisasterTest testCase = getTest("BritishIsles.dst");
        int minCities = 4;
        runTest(testCase, minCities);
    }

    @Test
    //  CentralEurope.dst
    public void centralEurope() throws Exception {
        DisasterTest testCase = getTest("CentralEurope.dst");
        int minCities = 6;

        runTest(testCase, minCities);
    }

    @Test
    //  Ethane.dst
    public void ethane() throws Exception {
        DisasterTest testCase = getTest("Ethane.dst");
        int minCities = 2;

        runTest(testCase, minCities);
    }

    @Test
    //  IberianPeninsula.dst
    public void iberianPeninsula() throws Exception {
        DisasterTest testCase = getTest("IberianPeninsula.dst");
        int minCities = 7;

        runTest(testCase, minCities);
    }

    @Test
    //  NortheastUS.dst
    public void northeastUS() throws Exception {
        DisasterTest testCase = getTest("NortheastUS.dst");
        int minCities = 7;

        runTest(testCase, minCities);
    }

    @Test
    //  SouthernNigeria.dst
    public void southernNigeria() throws Exception {
        DisasterTest testCase = getTest("SouthernNigeria.dst");
        int minCities = 8;

        runTest(testCase, minCities);
    }

    @Test
    //  SouthernSouthKorea.dst
    public void southernSouthKorea() throws Exception {
        DisasterTest testCase = getTest("SouthernSouthKorea.dst");
        int minCities = 11;

        runTest(testCase, minCities);
    }

    @Test
    //  SouthernUS.dst
    public void southernUs() throws Exception {
        DisasterTest testCase = getTest("SouthernUS.dst");
        int minCities = 8;

        runTest(testCase, minCities);
    }

    @Test
    //  VeryHardNortheastUS.dst
    public void veryHardNortheastUs() throws Exception {
        DisasterTest testCase = getTest("VeryHardNortheastUS.dst");
        int minCities = 9;

        runTest(testCase, minCities);
    }

    @Test
    //  VeryHardSouthernUS.dst - should take about a minute
    public void veryHardSouthernUs() throws Exception {
        DisasterTest testCase = getTest("VeryHardSouthernUS.dst");
        int minCities = 10;

        runTest(testCase, minCities);
    }

    @Test
    //  WesternUS.dst
    public void westernUs() throws Exception {
        DisasterTest testCase = getTest("WesternUS.dst");
        int minCities = 5;

        runTest(testCase, minCities);
    }

    private void runTest(DisasterTest testCase, int minCities) {
        HashSet<String> stockpiles = new HashSet<>();
        assertFalse(DisasterPlanning.canBeMadeDisasterReady(testCase.network, minCities - 1, stockpiles));
        stockpiles.clear();
        assertTrue(DisasterPlanning.canBeMadeDisasterReady(testCase.network, minCities, stockpiles));
        validateAnswer(testCase, minCities, stockpiles);
    }

    private DisasterTest getTest(String file) throws Exception {
        return DisasterParser.loadDisaster(Path.of("input", file));
    }

    private boolean allCitiesCovered(Set<String> stockpileCities,
                                     Map<String, Set<String>> network) {
        Set<String> covered = new HashSet<>( stockpileCities );
        for (String city: stockpileCities) {
            covered.addAll(network.get(city));
        }

        Set<String> allCities = new HashSet<>(network.keySet());
        return allCities.equals(covered);
    }

    private void validateAnswer(DisasterTest test, int minCities, Set<String> stockpiles) {
        if (stockpiles.size() > minCities) {
            String msg = "ERROR: Student's solution used " + stockpiles.size() +
                    " cities but should have only used " + minCities + " cities.";
            fail(msg);
        }
        if (!allCitiesCovered(stockpiles, test.network)) {
            fail("ERROR: Student's solution does not provide disaster coverage to all cities.");
        }
    }


}
