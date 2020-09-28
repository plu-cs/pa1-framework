package rttr.election;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class WinningThePresidencyFileTests {

    // Tests on years that are possible to run in a reasonable time without memoization
    @Parameterized.Parameters
    public static Object[] data() {
        return new Object[] { 1828, 1832, 1836, 1840, 1844, 1848, 1864 };
    }

    // Un-comment this (and comment the above method) if you want to run tests on ALL years.
//    @Parameterized.Parameters
//    public static Object[] data() {
//        List<Integer> testYears = new ArrayList<>();
//        for (int year = 1828; year <= 2016; year+=4) testYears.add(year);
//        return testYears.toArray();
//    }

    private int year;

    public WinningThePresidencyFileTests(int yr) {
        year = yr;
    }

    private int electoralVote(List<WinningThePresidency.State> states) {
        int result = 0;
        for (WinningThePresidency.State state: states) {
            result += state.electoralVotes;
        }
        return result;
    }

    private int popularVote(List<WinningThePresidency.State> states) {
        int result = 0;
        for (WinningThePresidency.State state: states) {
            result += state.popularVotes;
        }
        return result;
    }

    @Test
    public void testMinPopularVoteToWin() {
        ElectionTest test = ElectionParser.loadProblem(year);
        WinningThePresidency.MinInfo studentResult = WinningThePresidency.minPopularVoteToWin(test.states);

        int totalVotes = popularVote(test.states);
        int electoralUsed = electoralVote(studentResult.statesUsed);
        int electoralTotal = electoralVote(test.states);

        // Test that min votes is correct
        String error = String.format( "Year %d: You should need %d votes out of %d total votes, but " +
                "your algorithm returned %d",
                test.year, test.minVotesNeeded, totalVotes, studentResult.popularVotesNeeded);
        assertEquals(error, test.minVotesNeeded, studentResult.popularVotesNeeded );

        // Test that the total of the electoral votes in the states used is enough
        error = String.format("Year %d: You got %d electoral votes out of %d, which isn't"
            + " a majority", test.year, electoralUsed, electoralTotal);
        assertTrue(error, electoralUsed * 2 > electoralTotal);
    }
}
