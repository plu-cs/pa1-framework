package rttr.gui;

import rttr.disaster.gui.DisasterGUI;
import rttr.dwo.gui.DoctorGUI;
import rttr.election.gui.ElectionGUI;

import javax.swing.*;
import java.awt.*;

public class MainGUI extends JFrame {

    private JTabbedPane tabbedPane;

    public MainGUI() {
        setTitle("Recursion to the Rescue!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(900,700));
        tabbedPane.addTab("Doctors Without Orders", new DoctorGUI());
        tabbedPane.addTab("Disaster Planning", new DisasterGUI());
        tabbedPane.addTab("Election", new ElectionGUI());

        this.add(tabbedPane, BorderLayout.CENTER);
        pack();
    }
}
