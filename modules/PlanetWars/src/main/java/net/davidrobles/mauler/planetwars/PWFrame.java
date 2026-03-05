package net.davidrobles.mauler.planetwars;

import java.awt.*;
import javax.swing.*;

public class PWFrame extends JFrame {

    public PWFrame(JPanel panel) throws HeadlessException {
        add(panel);
        setVisible(true);
        pack();
    }
}
