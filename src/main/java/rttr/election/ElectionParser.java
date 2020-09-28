package rttr.election;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ElectionParser {

    public static ElectionTest loadProblem(int year) {
        Path path = Path.of("input", year + ".csv");
        try( Scanner scan = new Scanner(path.toFile())) {
            List<WinningThePresidency.State> result = new ArrayList<>();

            /* Confirm that the year matches and extract the reference solution. */
            String firstLine = scan.nextLine();
            String[] parts = firstLine.split(",");
            int fileYear = Integer.parseInt(parts[0]);
            int votesNeeded = Integer.parseInt(parts[1]);
            int votesNeededSimplified = Integer.parseInt(parts[2]);

            /* Parse each line of the file. */
            while(scan.hasNextLine()) {
                String line = scan.nextLine();
                result.add( parseState(line) );
            }

            return new ElectionTest(fileYear, result, votesNeeded);
        } catch(FileNotFoundException e) {
            throw new RuntimeException("File " + path.toString() + " not found");
        }
    }

    private static WinningThePresidency.State parseState(String line) {
        String[] pieces = line.split(",");
        if( pieces.length != 4 ) {
            throw new RuntimeException("Wrong number of entries on this line: " + line);
        }

        return new WinningThePresidency.State(
                pieces[0],
                Integer.parseInt(pieces[2]),
                Integer.parseInt(pieces[3])
        );
    }
}
