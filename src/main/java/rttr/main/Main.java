package rttr.main;

import rttr.gui.MainGUI;

import java.awt.*;

public class Main {

    public static void main(String[] args) {
        final MainGUI gui = new MainGUI();
        EventQueue.invokeLater( () -> gui.setVisible(true) );
    }

}
