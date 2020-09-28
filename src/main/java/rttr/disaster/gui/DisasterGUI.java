package rttr.disaster.gui;

import rttr.disaster.DisasterParser;
import rttr.disaster.DisasterPlanning;
import rttr.disaster.DisasterTest;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class DisasterGUI extends JPanel {

    private final String EXTENSION = ".dst";
    private final String BASE_PATH = "input";

    /* Lower bound on the width or height of the data range, used for
     * collinear points.
     */
    private final double LOGICAL_PADDING = 1e-5;

    /* Buffer space around the window. */
    private final double BUFFER_SPACE = 60;

    private final float ROAD_WIDTH = 3;
    private final Color LIGHT_ROAD_COLOR = new Color(0xa0, 0xa0, 0xa0);
    private final Color DARK_ROAD_COLOR = Color.black;

    /* Colors to use when drawing cities. */
    private class CityColors {
        public Color borderColor;
        public Color fillColor;
        public Color fontColor;
        public CityColors(Color border, Color fill, Color font) {
            borderColor = border;
            fillColor = fill;
            fontColor = font;
        }
    }

    private enum CityState {
        UNCOVERED, COVERED_DIRECTLY, COVERED_INDIRECTLY
    }
    private final Map<CityState, CityColors> CITY_COLORS = Map.ofEntries(
            Map.entry( CityState.UNCOVERED, new CityColors(
                    new Color(0xa0, 0xa0, 0xa0),
                    new Color(0xc0, 0xc0, 0xc0),
                    new Color(0x80,0x80,0x80))),   // Uncovered
            Map.entry( CityState.COVERED_INDIRECTLY, new CityColors(
                    new Color(0x40, 0x80, 0x80),
                    new Color(0xc0, 0xff, 0xff),
                    new Color(0x80, 0xc0, 0xc0))),   // Indirectly covered
            Map.entry( CityState.COVERED_DIRECTLY, new CityColors(
                    Color.black,
                    new Color( 0xfc, 0xe0, 0x29),
                    Color.black)   )                          // Directly covered
    );

    private final int CITY_RADIUS = 25;

    /* Max length of a string in a label. */
    private final int MAX_LENGTH = 3;

    /* Font to use for city labels. */
    private final Font LABEL_FONT     = new Font("Monospace", Font.BOLD, 12);

    private class DisasterGUIDisplay extends JPanel {

        public DisasterGUIDisplay() {
            this.setBackground(Color.white);
        }

        private class Geometry {
            /* Range of X and Y values in the data set, used for
             * scaling everything.
             */
            private Point2D.Double minData, maxData;

            /* Range of X and Y values to use when drawing everything. */
            private Point2D.Double minDraw, maxDraw;

            public Geometry() {
                minData = new Point2D.Double(Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY);
                maxData = new Point2D.Double(Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY);
                minDraw = new Point2D.Double(Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY);
                maxDraw = new Point2D.Double(Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY);
            }
            public void addDataPoint( Point2D pt ) {
                minData.x = Math.min( minData.x, pt.getX() );
                minData.y = Math.min( minData.y, pt.getY() );
                maxData.x = Math.max( maxData.x, pt.getX() );
                maxData.y = Math.max( maxData.y, pt.getY() );
            }
            public void padData(double value) {
                minData.x -= value;
                minData.y -= value;
                maxData.x += value;
                maxData.y += value;
            }
            public Point2D logicalToPhysical(Point2D pt) {
                double dataWidth = maxData.x - minData.x;
                double dataHeight = maxData.y - minData.y;
                double drawWidth = maxDraw.x - minDraw.x;
                double drawHeight = maxDraw.y - minDraw.y;

                double x = ((pt.getX() - minData.x) / dataWidth) * drawWidth + minDraw.x;
                double y = ((pt.getY() - minData.y) / dataHeight) * drawHeight + minDraw.y;

                return new Point2D.Double( x, y );
            }
        }


        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D)g;

            if (!network.network.isEmpty()) {
                Geometry geo = computeGeometry();

                /* Draw the roads under the cities to avoid weird graphics
                 * artifacts.
                 */
                drawRoads(g2d, geo);
                drawCities(g2d, geo);
            }
        }

        private void drawRoads(Graphics2D g2d, Geometry geo) {
            Stroke oldStroke = g2d.getStroke();
            g2d.setStroke(new BasicStroke(ROAD_WIDTH));

            for (Map.Entry<String, Set<String>> entry : network.network.entrySet()) {
                String source = entry.getKey();
                Point2D src = geo.logicalToPhysical(network.cityLocations.get(source));
                for (String dest: entry.getValue()) {
                    /* Selected roads draw in the dark color; deselected
                     * roads draw in a the light color.
                     */
                    Color lineColor = LIGHT_ROAD_COLOR;
                    if( solution.contains(source) || solution.contains(dest) ) {
                        lineColor = DARK_ROAD_COLOR;
                    }
                    g2d.setColor(lineColor);

                    /* Draw the line, remembering that the coordinates are in
                     * logical rather than physical space.
                     */
                    Point2D dst = geo.logicalToPhysical(network.cityLocations.get(dest));

                    g2d.drawLine((int)src.getX(), (int)src.getY(), (int)dst.getX(), (int)dst.getY() );
                }
            }
            g2d.setStroke(oldStroke);
        }

        private void drawCities(Graphics2D g2d, Geometry geo) {
            g2d.setFont(LABEL_FONT);
            FontRenderContext frc = g2d.getFontRenderContext();

            for( Map.Entry<String, Point2D> entry : network.cityLocations.entrySet() ) {
                String cityName = entry.getKey();
                Point2D location = entry.getValue();
                /* Figure out the center of the city on the screen. */
                Point2D center = geo.logicalToPhysical(location);

                /* See what state the city is in with regards to coverage. */
                CityState state = CityState.UNCOVERED;
                if( solution.contains(cityName) ) state = CityState.COVERED_DIRECTLY;
                else if( !Collections.disjoint(solution, network.network.get(cityName))) state = CityState.COVERED_INDIRECTLY;

                CityColors colors = CITY_COLORS.get(state);
                g2d.setColor(colors.fillColor);
                g2d.fillOval((int)center.getX() - CITY_RADIUS, (int)center.getY() - CITY_RADIUS, 2 * CITY_RADIUS, 2 * CITY_RADIUS);
                g2d.setColor(colors.borderColor);
                g2d.drawOval((int)center.getX() - CITY_RADIUS, (int)center.getY() - CITY_RADIUS, 2 * CITY_RADIUS, 2 * CITY_RADIUS);

                /* Set the label text and color. */
                g2d.setColor(colors.fontColor);
                String label = shorthandName(cityName);
                TextLayout tl = new TextLayout(label, LABEL_FONT, frc);
                Rectangle2D bounds = tl.getBounds();
                g2d.drawString(label, (int)(center.getX() - bounds.getWidth() / 2.0),
                        (int)(center.getY() + tl.getAscent() / 2.0));
            }

        }

        private String shorthandName( String name ) {
            String[] parts = name.split(" ");

            if (parts.length == 1) {
                if (parts[0].length() < MAX_LENGTH) return parts[0];
                else return parts[0].substring(0, 3);
            } else {
                /* Use initials. */
                String result = "";
                for (int i = 0; result.length() < MAX_LENGTH && i < parts.length; i++) {
                    /* Skip empty components, which might exist if there are consecutive spaces in
                     * the name */
                    if (!parts[i].isEmpty()) {
                        result += parts[i].charAt(0);
                    }
                }
                return result;
            }
        }

        private Geometry computeGeometry() {
            Geometry geo = new Geometry();

            for( Point2D pt : network.cityLocations.values() ) {
                geo.addDataPoint(pt);
            }
            geo.padData(LOGICAL_PADDING);

            /* Get the aspect ratio of the window. */
            double winWidth  = this.getWidth()  - 2 * BUFFER_SPACE;
            double winHeight = this.getHeight() - 2 * BUFFER_SPACE;
            double winAspect = winWidth / winHeight;

            /* Get the aspect ratio of the data set. */
            double dataAspect = (geo.maxData.x - geo.minData.x) / (geo.maxData.y - geo.minData.y);

            double dataWidth, dataHeight;

            /* If the data aspect ratio exceeds the window aspect ratio,
             * the limiting factor in the display is going to be the
             * width. Therefore, we'll use that to determine our effective
             * width and height.
             */
            if (dataAspect >= winAspect) {
                dataWidth = winWidth;
                dataHeight = dataWidth / dataAspect;
            } else {
                dataHeight = winHeight;
                dataWidth = dataAspect * dataHeight;
            }

            /* Now, go center that in the window. */
            geo.minDraw.x = (winWidth  -  dataWidth) / 2.0 + BUFFER_SPACE;
            geo.minDraw.y = (winHeight - dataHeight) / 2.0 + BUFFER_SPACE;

            geo.maxDraw.x = geo.minDraw.x + dataWidth;
            geo.maxDraw.y = geo.minDraw.y + dataHeight;

            return geo;
        }
    }

    private DisasterGUIDisplay display;
    private JComboBox<String> problemsCb;
    private JButton solveButton;

    private DisasterTest network;
    private Set<String> solution;

    public DisasterGUI() {
        setLayout( new BorderLayout() );
        display = new DisasterGUIDisplay();

        solution = new HashSet<>();

        List<String> problems = sampleProblems();
        problemsCb = new JComboBox<>(problems.toArray(new String[] {}));
        JPanel southPanel = new JPanel();
        problemsCb.addActionListener((e) -> {
            loadInstance(problemsCb.getItemAt(problemsCb.getSelectedIndex()));
            solution.clear();
            display.repaint();
        });
        southPanel.add(problemsCb);

        solveButton = new JButton("Solve");
        solveButton.addActionListener((e) -> solve());
        southPanel.add(solveButton);

        this.add(display, BorderLayout.CENTER);
        this.add(southPanel, BorderLayout.SOUTH);

        loadInstance(problemsCb.getItemAt(problemsCb.getSelectedIndex()));
    }

    private void solve() {
        solution.clear();
        solveOptimally();
        display.repaint();
    }

    /* Uses binary search to find the optimal number of cities to use for disaster
     * preparedness, populating the result field with the minimum group of cities
     * that ended up being needed.
     */
    private void solveOptimally() {
        /* The variable 'low' is the lowest number that might be feasible.
         * The variable 'high' is the highest number that we know is feasible.
         */
        int low = 0, high = network.network.size();

        /* Begin with a feasible solution that uses as many cities as we'd like. */
        DisasterPlanning.canBeMadeDisasterReady(network.network, high, solution);

        while (low < high) {
            int mid = (high + low) / 2;
            Set<String> thisResult = new HashSet<>();

            /* If this option works, decrease high to it, since we know all is good. */
            if (DisasterPlanning.canBeMadeDisasterReady(network.network, mid, thisResult)) {
                high = mid;
                solution = thisResult; // Remember this result for later.
            }
            /* Otherwise, rule out anything less than or equal to it. */
            else {
                low = mid + 1;
            }
        }
    }

    private void loadInstance(String fileName) {
        try {
            network = DisasterParser.loadDisaster(Path.of(BASE_PATH, fileName));
        } catch( FileNotFoundException e ) {
            throw new RuntimeException("File " + fileName + " not found.");
        }
    }

    private List<String> sampleProblems() {
        try {
            return Files.list(Path.of(BASE_PATH))
                    .filter((p) -> p.getFileName().toString().endsWith(EXTENSION))
                    .map((p) -> p.getFileName().toString())
                    .collect(Collectors.toList());
        } catch(IOException e) {
            return new ArrayList<>();
        }
    }
}
