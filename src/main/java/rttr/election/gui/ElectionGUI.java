package rttr.election.gui;

import rttr.election.*;
import rttr.gui.TextRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ElectionGUI extends JPanel {

    /* Geographic center of the US. */
    private final double CENTER_LATITUDE  =   44.966666667;
    private final double CENTER_LONGITUDE = -103.766666667;

    /* Year ranges. */
    private final int MIN_YEAR  = 1828;
    private final int MAX_YEAR = 2016;
    private final int YEAR_STEP = 4;

    private final Bounds MAP_BOUNDS = new Bounds(
            new Point2D.Double(-0.7, -0.45),
            new Point2D.Double(0.43, 0.37));

    private final String LOADING_MESSAGE   = "Loading...";
    private final Font LOADING_FONT      = new Font("Serif", Font.BOLD, 36);
    private final Color LOADING_COLOR = Color.black;

    private final Color BACKGROUND_COLOR = new Color(0xad, 0xd8, 0xe6); // Light Blue
    private final Color NO_VOTE_BORDER_COLOR = new Color(0x87, 0xad, 0xb9);
    private final Color NO_VOTE_FILL_COLOR = new Color(0x97, 0xbd, 0xc9);
    private final Color VOTE_AGAINST_BORDER_COLOR = new Color(0x20, 0x20, 0x20);
    private final Color VOTE_AGAINST_FILL_COLOR = new Color(0x56, 0x6b, 0x73);
    private final Color VOTE_FOR_BORDER_COLOR = new Color(0x80, 0x60, 0x30);
    private final Color VOTE_FOR_FILL_COLOR = new Color(0xff, 0xcf, 0x60);

    private final Font LEGEND_FONT = new Font("Serif", Font.BOLD, 18);
    private final Color LEGEND_FONT_COLOR = new Color(0x08, 0x25, 0x67); // Sapphire

    private final Font RESULT_FONT        = new Font("Monospaced", Font.BOLD, 20);
    private final Color RESULT_COLOR = LEGEND_FONT_COLOR;


    private class DisplayPanel extends JPanel {

        public DisplayPanel() {
            setBackground(BACKGROUND_COLOR);
        }

        private boolean firstRun = true;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // Display loading message if data is not yet available.
            if( statePaths == null ) {
                drawLoading(g2d);
                loadGeographyData();
                return;
            }

            for( Map.Entry<String,List<Path2D>> entry : statePaths.entrySet() ) {
                StateResult result = stateResults.get( entry.getKey() );
                List<Path2D> pathList = entry.getValue();
                drawState( g2d, pathList, result );
            }

            drawLegend(g2d);
            if( solution != null ) {
                drawSolution(g2d);
            }

            if( isSolving() ) {
               drawSolving(g2d);
            }

            // Trigger first solution.
            if( firstRun ) {
                firstRun = false;
                solve();
            }
        }

        private void drawSolution(Graphics2D g2d) {
            int votesCast = 0;
            for( WinningThePresidency.State state : allStatesForThisYear ) {
                votesCast += state.popularVotes;
            }
            String results = String.format(
                    "Election Year:      %d\n" +
                    "Popular Votes Cast: %,d\n" +
                    "Minimum to Win:     %,d",
                    yearSlider.getValue(),
                    votesCast, solution.popularVotesNeeded
            );

            g2d.setFont(RESULT_FONT);
            g2d.setColor(RESULT_COLOR);
            TextRenderer.drawCenteredString(g2d, results,
                    new Rectangle(350, 50, 450, 150), true, 10);
        }

        private void drawLegend(Graphics2D g2d) {
            g2d.setFont(LEGEND_FONT);
            g2d.setColor(LEGEND_FONT_COLOR);
            Rectangle rect = new Rectangle(5, 250, 300, 150);
            TextRenderer.drawCenteredString(g2d,
                    "Didn't Participate\nNot Needed To Win\nNeeded To Win",
                    rect, true, 50);
            g2d.draw(rect);

            g2d.setColor(NO_VOTE_FILL_COLOR);
            g2d.fillRect( (int)rect.getX() + 5, (int)rect.getY() + 10, 30, 30 );
            g2d.setColor(VOTE_AGAINST_FILL_COLOR);
            g2d.fillRect( (int)rect.getX() + 5, (int)rect.getY() + 60, 30, 30 );
            g2d.setColor(VOTE_FOR_FILL_COLOR);
            g2d.fillRect( (int)rect.getX() + 5, (int)rect.getY() + 110, 30, 30 );
        }

        private void drawState(Graphics2D g2d, List<Path2D> paths, StateResult result) {
            Color fillColor = NO_VOTE_FILL_COLOR;
            Color borderColor = NO_VOTE_BORDER_COLOR;
            switch(result) {
                case VOTE_AGAINST:
                    fillColor = VOTE_AGAINST_FILL_COLOR;
                    borderColor = VOTE_AGAINST_BORDER_COLOR;
                    break;
                case VOTE_FOR:
                    fillColor = VOTE_FOR_FILL_COLOR;
                    borderColor = VOTE_FOR_BORDER_COLOR;
                    break;
            }
            g2d.setColor(fillColor);
            for( Path2D p : paths ) g2d.fill(p);
            g2d.setColor(borderColor);
            for(Path2D p: paths) g2d.draw(p);
        }

        private void loadGeographyData() {
            // Load the geography data in a worker thread.
            ExecutorService service = Executors.newSingleThreadExecutor();
            Executors.callable(new LoadStateDataRunnable());
            service.submit( new LoadStateDataRunnable() );
            service.shutdown();
        }

        private void drawLoading(Graphics2D g2d) {
            g2d.setFont(LOADING_FONT);
            g2d.setColor(LOADING_COLOR);
            TextRenderer.drawCenteredString(
                    g2d, LOADING_MESSAGE, new Rectangle(0, 0, this.getWidth(), this.getHeight())
            );
        }

        private void drawSolving(Graphics2D g2d) {
            g2d.setColor(new Color(255,255,255,150));
            g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
            g2d.setFont(LOADING_FONT);
            g2d.setColor(LOADING_COLOR);
            int year = yearSlider.getValue();
            TextRenderer.drawCenteredString(
                    g2d, "Solving " + year + "...", new Rectangle(0, 0, this.getWidth(), this.getHeight())
            );
        }
    }

    private enum StateResult {
        NO_VOTE, VOTE_AGAINST, VOTE_FOR
    }

    private final String STATE_SHAPE_FILE = "us-borders.txt";

    private DisplayPanel displayPanel;
    private Map<String, List<Path2D>> statePaths;

    private WinningThePresidency.MinInfo solution;
    private Map<String, StateResult> stateResults;
    private List<WinningThePresidency.State> allStatesForThisYear;

    private JSlider yearSlider;
    private ExecutorService solverService;
    private Future solverFuture;

    public ElectionGUI() {
        setLayout(new BorderLayout());
        displayPanel = new DisplayPanel();

        yearSlider = new JSlider(MIN_YEAR, MAX_YEAR, MIN_YEAR);
        yearSlider.setMajorTickSpacing(YEAR_STEP * 4);
        yearSlider.setMinorTickSpacing(YEAR_STEP);
        yearSlider.setSnapToTicks(true);
        yearSlider.setPaintTicks(true);
        yearSlider.setPaintLabels(true);
        yearSlider.addChangeListener((event) -> {
            if( !yearSlider.getValueIsAdjusting() )
                solve();
        });

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(yearSlider, BorderLayout.CENTER);

        // We'll load this data upon first repaint
        statePaths = null;
        solution = null;
        allStatesForThisYear = null;
        solverService = Executors.newSingleThreadExecutor();
        stateResults = new HashMap<>();

        add(displayPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }

    private boolean isSolving() {
        return solverFuture != null && !solverFuture.isDone();
    }

    private void solve() {
        // If we're already working on a solution, do nothing.
        if( isSolving() ) return;

        yearSlider.setEnabled(false);
        final int year = yearSlider.getValue();
        solverFuture = solverService.submit( () -> {
            try {
                solveInThread(year);
            } catch(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void solveInThread(int year) {
        // Repaint to show the solving message
        displayPanel.repaint();

        // Solve!
        ElectionTest testCase = ElectionParser.loadProblem(year);
        WinningThePresidency.MinInfo info = WinningThePresidency.minPopularVoteToWin(testCase.states);


        Map<String, StateResult> results = new HashMap<>();
        for( String stateName : statePaths.keySet() ) {
            results.put(stateName, StateResult.NO_VOTE);
        }
        for( WinningThePresidency.State state : testCase.states ) {
            results.put(state.name, StateResult.VOTE_AGAINST);
        }

        if( info != null ) {
            for (WinningThePresidency.State state : info.statesUsed) {
                results.put(state.name, StateResult.VOTE_FOR);
            }
        }

        // Copy over results.
        solution = info;
        stateResults = results;
        allStatesForThisYear = new ArrayList<>(testCase.states);

        // Repaint
        EventQueue.invokeLater(() -> {
            displayPanel.repaint();
            yearSlider.setEnabled(true);
        });
    }

    private class LoadStateDataRunnable implements Runnable {
        @Override
        public void run() {
            statePaths = loadStates();
            displayPanel.repaint();
        }

        private void transformStates(Map<String, List<Path2D>> map) {
            int w = displayPanel.getWidth();
            int h = displayPanel.getHeight();

            AffineTransform m = AffineTransform.getScaleInstance(
                    w / MAP_BOUNDS.getWidth(),
                    -h / MAP_BOUNDS.getHeight());
            m.concatenate(AffineTransform.getTranslateInstance(-MAP_BOUNDS.min.getX(), -MAP_BOUNDS.max.getY()));

            for (List<Path2D> pathList : map.values()) {
                for (Path2D path : pathList) {
                    path.transform(m);
                }
            }
        }

        /* Draws all the states in the indicated window. */
        private Map<String, List<Path2D>> loadStates() {
            stateResults.clear();
            try (Scanner scan = new Scanner(Path.of("input", STATE_SHAPE_FILE).toFile())) {
                Map<String, List<Path2D>> result = new HashMap<>();

                while (scan.hasNextLine()) {
                    String stateName = scan.nextLine();
                    int numShapes = scan.nextInt();
                    scan.nextLine();

                    List<Path2D> shapes = new ArrayList<>();
                    for (int i = 0; i < numShapes; i++) {
                        shapes.add(loadSingleShape(scan));
                    }
                    result.put(stateName, shapes);
                    stateResults.put(stateName, StateResult.NO_VOTE);
                }
                transformStates(result);
                return result;
            } catch (FileNotFoundException e) {
                throw new RuntimeException("File " + STATE_SHAPE_FILE + " not found.");
            }
        }

        private Path2D loadSingleShape(Scanner scan) {
            Path2D result = new Path2D.Double();

            /* Skip the first line, since it's in the middle of the region. */
            scan.nextLine();

            /* Parse the remaining lines until we get to a blank line. */
            String line = scan.nextLine().trim();
            boolean first = true;
            while (!line.isEmpty()) {
                String[] parts = line.split("\\s+");
                double longitude = Double.parseDouble(parts[0]);
                double latitude = Double.parseDouble(parts[1]);
                Point2D pt = ProjectionUtils.mollweideProjectionOf(latitude, longitude,
                        CENTER_LONGITUDE, CENTER_LATITUDE);

                if (first) {
                    result.moveTo(pt.getX(), pt.getY());
                    first = false;
                } else {
                    result.lineTo(pt.getX(), pt.getY());
                }
                line = scan.nextLine().trim();
            }

            result.closePath();
            return result;
        }
    }
}
