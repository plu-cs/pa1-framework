package rttr.election;

import java.util.ArrayList;
import java.util.List;

/**
 * PROBLEM 3 - Winning the Presidency
 *
 * Fill in the method minPopularVoteToWin below.  To test it you have the following
 * options:
 *   - Use the GUI:  ./gradlew run
 *   - Run the JUnit test suites:
 *         ./gradlew test --tests "rttr.election.WinningThePresidencyTests"
 *         ./gradlew test --tests "rttr.election.WinningThePresidencyFileTests"
 *   - Run an individual JUnit test (for example twoStates):
 *         ./gradlew test --tests "rttr.election.WinningThePresidencyTests.twoStates"
 *   - Create your own JUnit tests in the above files, you can find those files in: src/test/java/rttr/election
 */
public class WinningThePresidency {

    /**
     * A simple class representing a state in an election.
     */
    public static class State {
        public String name;         // The name of the state
        public int electoralVotes;  // How many electors it has
        public int popularVotes;    // The number of people in that state who voted
        public State(String n, int eVotes, int pVotes) {
            name = n;
            electoralVotes = eVotes;
            popularVotes = pVotes;
        }
    }

    /**
     * A class representing information about how to win an election with the fewest number
     * of popular votes.
     */
    public static class MinInfo {
        public final int popularVotesNeeded;   // How many popular votes you'd need.
        public final List<State> statesUsed;   // Which states you'd carry in the course of doing so.
        public MinInfo() {
            popularVotesNeeded = 0;
            statesUsed = new ArrayList<>();
        }
        public MinInfo(int votes, List<State> states) {
            popularVotesNeeded = votes;
            statesUsed = new ArrayList<>(states);
        }
        public MinInfo( MinInfo other ) {
            popularVotesNeeded = other.popularVotesNeeded;
            statesUsed = new ArrayList<>(other.statesUsed);
        }
    }

    /**
     * Use this value for the popular votes needed when it is not possible to win.
     */
    public static final int NOT_POSSIBLE = 1000000000;

    /**
     * Given a list of states, returns information about the minimum number of
     * popular votes needed to be elected President of the United States.
     *
     * @param states all the states that participated in the election.
     * @return Information about the minimum number of votes you'd need to be elected.
     */
    public static MinInfo minPopularVoteToWin(List<State> states) {

        // TODO: fill in this function, I suggest you call the (recursive) helper function
        //  below (minPopularVoteToGetAtLeast with appropriate parameters)

        return null;
    }

    /**
     * Given a list of states, a target number of electoral votes, and a start index into that list
     * of states, returns the minimum number of popular votes you'd need to get that many electoral
     * votes, along with which states you'd use to make that target.
     *
     * If it's not possible to hit the target number of electoral votes, you should return a MinInfo
     * struct that has the number of popular votes set to the constant NOT_POSSIBLE. The set of
     * associated states can be whatever you'd like it to be in that case.
     *
     * @param electoralVotes The target number of electoral votes to hit.
     * @param startIndex The starting index into the List of which states you're allowed to use.
     * @param states All the states participating in the election.
     * @return Information about the fewest number of popular votes needed to hit the target number
     *         of electoral votes using only states from index startIndex and beyond.
     */

    public static MinInfo minPopularVoteToGetAtLeast(
            int electoralVotes,
            int startIndex,
            List<State> states
    ) {
        // TODO: fill in this function

        return null;
    }

}
