package rttr.disaster;

import rttr.disaster.DisasterTest;

import java.awt.*;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DisasterParser {

    public static DisasterTest loadDisaster(Path file) throws FileNotFoundException {
        DisasterTest result = new DisasterTest();

        try( Scanner scan = new Scanner( file.toFile() ) ) {
            while( scan.hasNextLine()) {
                String line = scan.nextLine().trim();

                /* Skip blank lines or comments. */
                if (line.isEmpty() || line.startsWith("#")) continue;

                parseCityLine(line, result);
            }

            addReverseEdges(result);
        }

        return result;
    }

    /* Given a graph in which all forward edges have been added, adds
     * the reverse edges to the graph.
     */
    private static void addReverseEdges(DisasterTest result) {
        for (String source: result.network.keySet()) {
            for (String dest: result.network.get(source)) {
                if (!result.network.containsKey(dest)) {
                    throw new RuntimeException("Outgoing link found to nonexistent city '" + dest + "'");
                }
                result.network.get(dest).add(source);
            }
        }
    }

    private static void parseCityLine(String line, DisasterTest result) {
        /* Search for a colon on the line. The split function will only return a
         * single component if there are no outgoing links specified.
         */
        if (! line.contains(":")) {
            throw new RuntimeException("Each data line should have exactly one colon on it.");
        }

        /* Split the line into the city name/location and the list
         * of outgoing cities.
         */
        String[] components = line.split(":");
        if (components.length == 0) {
            throw new RuntimeException("Data line appears to have no city information.");
        }

        String name = parseCity(components[0], result);
        if( components.length == 1 || components[1].trim().isEmpty() ) {
            // No outgoing links
            result.network.put(name, new HashSet<>());
        } else {
            parseLinks(name, components[1], result);
        }
    }

    /* Reads the links out of the back half of the line of a file,
     * adding them to the road network.
     */
    private static void parseLinks(String cityName, String linksStr, DisasterTest result) {
        String[] components = linksStr.split(",");
        Set<String> cityLinks = result.network.get(cityName);
        for (String dest : components) {
            /* Clean up all whitespace and make sure that we didn't
             * discover an empty entry.
             */
            String cleanName = dest.trim();
            if (cleanName.isEmpty()) {
                throw new RuntimeException("Blank name in list of outgoing cities?");
            }

            /* Confirm this isn't a dupe. */
            if (cityLinks.contains(cleanName)) {
                throw new RuntimeException("City appears twice in outgoing list?");
            }

            cityLinks.add(cleanName);
        }
    }

    /* Given city information in the form
     *
     *     CityName (X, Y)
     *
     * Parses out the name and the X/Y coordinate, returning the
     * name, and filling in the rttr.disaster.DisasterTest with what's found.
     */
    private static String parseCity( String cityInfo, DisasterTest result ) {
        /* Split on all the delimiters and confirm we've only got
         * three components.
         */
        Pattern pattern = Pattern.compile("^([A-Za-z0-9 .\\-]+)\\(\\s*(-?[0-9]+(?:\\.[0-9]+)?)\\s*,\\s*(-?[0-9]+(?:\\.[0-9]+)?)\\s*\\)$");
        String toMatch = cityInfo.trim();

        Matcher match = pattern.matcher(toMatch);
        if (!match.matches()) {
            throw new RuntimeException("Can't parse this data; is it city info? " + cityInfo);
        }

        if (match.groupCount() != 3) {
            throw new RuntimeException("Could not find all components?  Found: " + match.groupCount());
        }

        /* We're going to get back some extra leading or trailing
         * whitespace here, so peel it off.*/
        String name = match.group(1).trim();
        if (name.isEmpty())
            throw new RuntimeException("City names can't be empty.");

        int x = Integer.parseInt(match.group(2));
        int y = Integer.parseInt(match.group(3));

        /* Insert the city location */
        result.cityLocations.put(name, new Point(x, y));

        /* Insert an entry for the city into the road network. */
        result.network.put(name, new HashSet<>());

        return name;
    }
}
