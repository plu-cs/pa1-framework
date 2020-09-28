package rttr.election;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;


public class WinningThePresidencyTests {

    @Test
    // Provided Test: One state.
    public void oneState() {
        List<WinningThePresidency.State> states = List.of(
                new WinningThePresidency.State("Denial", 1, 1)
        );

        WinningThePresidency.MinInfo soln = WinningThePresidency.minPopularVoteToWin(states);
        assertEquals(1, soln.popularVotesNeeded);
        assertEquals(states, soln.statesUsed);
    }

    @Test
    // Provided Test: States empty
    public void noStates() {
        List<WinningThePresidency.State> states = new ArrayList<>();

        WinningThePresidency.MinInfo soln = WinningThePresidency.minPopularVoteToWin(states);
        assertEquals(WinningThePresidency.NOT_POSSIBLE, soln.popularVotesNeeded);
        assertEquals(states, soln.statesUsed);
    }


    @Test
    //Provided Test: Two states
    public void twoStates() {
        List<WinningThePresidency.State> states = List.of(
                new WinningThePresidency.State( "A", 4, 500 ),
                new WinningThePresidency.State( "B", 3, 400 )
        );

        WinningThePresidency.MinInfo soln = WinningThePresidency.minPopularVoteToWin(states);
        assertEquals(251, soln.popularVotesNeeded);
        assertEquals(1, soln.statesUsed.size());
        assertTrue(soln.statesUsed.contains(states.get(0)));
    }

    @Test
    // Provided Test: Three states
    public void threeStates() {
        List<WinningThePresidency.State> states = List.of(
                new WinningThePresidency.State( "A", 4, 500 ),
                new WinningThePresidency.State( "B", 3, 499 ),
                new WinningThePresidency.State( "C", 2, 100 )
        );

        WinningThePresidency.MinInfo soln = WinningThePresidency.minPopularVoteToWin(states);
        assertEquals(301, soln.popularVotesNeeded);
        assertEquals(2, soln.statesUsed.size());
        assertTrue(soln.statesUsed.contains(states.get(1)));
        assertTrue(soln.statesUsed.contains(states.get(2)));
    }
}
