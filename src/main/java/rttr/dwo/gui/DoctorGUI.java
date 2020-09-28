package rttr.dwo.gui;

import rttr.dwo.*;
import rttr.gui.TextRenderer;
import rttr.gui.TextUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class DoctorGUI extends JPanel {

    private static final String BASE_PATH = "./input";
    private static final String EXTENSION = ".dwo";

    private class DisplayPanel extends JPanel {
        /* Text box information. */
        private final double BOX_WIDTH       = 250;
        private final double BOX_HEIGHT      = 45;

        /* Global top/bottom padding. */
        private final double VERTICAL_PADDING = BOX_HEIGHT / 2.0;

        /* Spacing between columns. */
        private final double COLUMN_SPACING = 200;

        private final Color UNUSED_BOX_COLOR       = Color.white;
        private final Color UNUSED_BOX_BORDER_COLOR = Color.gray;
        private final Color UNUSED_BOX_FONT_COLOR   = new Color(64, 64, 64 );
        private final Font UNUSED_BOX_FONT          = new Font("SansSerif", Font.BOLD, 13);

        private final Color MATCHED_BOX_COLOR       = new Color(0xF3, 0xE5, 0xAB); // Vanilla
        private final Color MATCHED_BOX_BORDER_COLOR = Color.black;
        private final Color MATCHED_BOX_FONT_COLOR = Color.black;
        private final Font MATCHED_BOX_FONT = new Font("SansSerif", Font.BOLD, 13);

        /* Line information. */
        private final Color MATCHED_LINE_COLOR     = Color.blue;
        private final double MATCHED_LINE_THICKNESS = 5;

        private DisplayPanel() {
            this.setBackground(Color.white);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D)g;

            Map<String, Rectangle2D> nodeLocations = layOutNodes();

            Map<String,String> nodeModifiers = new HashMap<>();
            Set<String> allNames = new HashSet<>();
            for( Doctor d : hospital.doctors ) {
                nodeModifiers.put( d.getName(), "(" + TextUtils.pluralize(d.getHoursFree(), "hour") + " free)" );
                allNames.add(d.getName());
            }
            for( Patient p : hospital.patients ) {
                nodeModifiers.put( p.getName(), "(" + TextUtils.pluralize(p.getHoursNeeded(), "hour") + " needed)" );
                allNames.add(p.getName());
            }

            // Set of matched nodes
            Set<String> matched = new HashSet<>();
            for( Map.Entry<String,Set<String>> e : solution.entrySet() ) {
                matched.add(e.getKey());
                matched.addAll(e.getValue());
            }
            allNames.removeAll(matched);
            Set<String> unmatched = allNames;

            // Draw edges
            drawEdges(g2d, solution, nodeLocations, MATCHED_LINE_COLOR, MATCHED_LINE_THICKNESS);

            // Draw matched nodes
            g2d.setFont(MATCHED_BOX_FONT);
            drawNodes(g2d, matched, nodeLocations, nodeModifiers,
                    MATCHED_BOX_COLOR, MATCHED_BOX_BORDER_COLOR, MATCHED_BOX_FONT_COLOR);

            // Draw unmatched nodes
            g2d.setFont(UNUSED_BOX_FONT);
            drawNodes(g2d, unmatched, nodeLocations, nodeModifiers,
                    UNUSED_BOX_COLOR, UNUSED_BOX_BORDER_COLOR, UNUSED_BOX_FONT_COLOR);
        }

        private void drawNodes(Graphics2D g2d,
                               Set<String> nodes, Map<String, Rectangle2D> nodeLocations,
                               Map<String, String> nodeModifiers,
                               Color boxColor, Color borderColor, Color fontColor) {
            for( String name : nodes ) {
                Rectangle2D box = nodeLocations.get(name);
                g2d.setColor(boxColor);
                g2d.fill(box);
                g2d.setColor(borderColor);
                g2d.draw(box);
                g2d.setColor(fontColor);
                TextRenderer.drawCenteredString(g2d, name + "\n" + nodeModifiers.get(name), box);
            }
        }

        private void drawEdges(Graphics2D g2d,
                               Map<String, Set<String>> edges,
                               Map<String, Rectangle2D> nodeLocations,
                               Color lineColor, double thickness) {
            g2d.setColor(lineColor);
            Stroke oldStroke = g2d.getStroke();
            g2d.setStroke(new BasicStroke((float)thickness));

            for( Map.Entry<String, Set<String>> e : edges.entrySet() ) {
                Rectangle2D from = nodeLocations.get(e.getKey());
                int fromX = (int)from.getMaxX();
                int fromY = (int) ((from.getMaxY() + from.getMinY()) / 2.0);
                for( String toName : e.getValue() ) {
                    Rectangle2D to = nodeLocations.get(toName);
                    int toX = (int)to.getMinX();
                    int toY = (int) ((to.getMaxY() + to.getMinY()) / 2.0);
                    g2d.drawLine(fromX, fromY, toX, toY);
                }
            }
            g2d.setStroke(oldStroke);
        }

        private Map<String, Rectangle2D> layOutNodes() {
            HashMap<String, Rectangle2D> result = new HashMap<>();

            double centerX = this.getWidth() / 2.0;
            double height = this.getHeight();

            layOutColumn( hospital.doctors.stream().map(Doctor::getName).collect(Collectors.toUnmodifiableList()),
                    result, centerX - COLUMN_SPACING / 2.0 - BOX_WIDTH, height);
            layOutColumn( hospital.patients.stream().map(Patient::getName).collect(Collectors.toUnmodifiableList()),
                    result, centerX + COLUMN_SPACING / 2.0, height);

            return result;
        }

        private void layOutColumn( List<String> names, HashMap<String, Rectangle2D> result,
                                   double leftX, double height) {
            if( names.size() == 0 ) return;

            // Special case, center
            if( names.size() == 1 ) {
                result.put( names.get(0), new Rectangle2D.Double(leftX, height / 2.0 - BOX_HEIGHT / 2.0,
                        BOX_WIDTH, BOX_HEIGHT));
                return;
            }

            // At least 2 boxes
            double columnHeight = height - 2.0 * VERTICAL_PADDING;
            double topY = VERTICAL_PADDING;
            /* Compute the spacing between boxes. */
            double boxSpacing = (columnHeight - BOX_HEIGHT * names.size()) / (names.size() - 1);

            for( int i = 0; i < names.size(); i++ ) {
                result.put(names.get(i), new Rectangle2D.Double(
                        leftX, topY + (BOX_HEIGHT + boxSpacing) * i,
                        BOX_WIDTH, BOX_HEIGHT)
                );
            }
        }
    }

    private DisplayPanel displayPanel;
    private JComboBox<String> problemsCb;
    private JButton solveButton;

    private HospitalTestCase hospital;
    private Map<String, Set<String>> solution;

    public DoctorGUI() {
        this.setLayout(new BorderLayout());
        displayPanel = new DisplayPanel();

        List<String> problems = sampleProblems();
        problemsCb = new JComboBox<>(problems.toArray(new String[] {}));
        JPanel southPanel = new JPanel();
        problemsCb.addActionListener((e) -> {
            loadInstance( problemsCb.getItemAt(problemsCb.getSelectedIndex()) );
            displayPanel.repaint();
        });
        southPanel.add(problemsCb);

        solveButton = new JButton("Solve");
        solveButton.addActionListener((e) -> solve());
        southPanel.add(solveButton);

        this.add(displayPanel, BorderLayout.CENTER);
        this.add(southPanel, BorderLayout.SOUTH);

        solution = new HashMap<>();
        loadInstance(problemsCb.getItemAt(problemsCb.getSelectedIndex()));
    }

    private void solve() {
        solution.clear();

        Map<String, Integer> doctors = new HashMap<>();
        Map<String, Integer> patients = new HashMap<>();

        // Make copies to pass to the solver
        for( Doctor d: hospital.doctors ) doctors.put(d.getName(), d.getHoursFree());
        for( Patient p : hospital.patients ) patients.put(p.getName(), p.getHoursNeeded());

        if( DoctorsWithoutOrders.canAllPatientsBeSeen(doctors, patients, solution)) {
            repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Sorry, there's no way for everyone to be seen.");
        }
    }

    private void loadInstance(String selectedItem) {
        Path path = Path.of(BASE_PATH, selectedItem);
        try (Scanner scan = new Scanner(path.toFile())) {
            hospital = DoctorsWithoutOrdersParser.loadHospitalTestCase(scan);
            solution.clear();
        } catch(FileNotFoundException e) {
            System.err.println("File: " + selectedItem + " not found.");
            System.exit(1);
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
