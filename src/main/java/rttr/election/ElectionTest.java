package rttr.election;

import java.util.List;

public class ElectionTest {
    public List<WinningThePresidency.State> states;
    public int minVotesNeeded;
    public int year;

    public ElectionTest( int yr, List<WinningThePresidency.State> st, int minVotes ) {
        states = st;
        year = yr;
        minVotesNeeded = minVotes;
    }
}
