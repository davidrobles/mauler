package net.davidrobles.mauler.planetwars;

import javax.swing.*;
import java.awt.*;

public class PWFrame extends JFrame {

    public PWFrame(JPanel panel) throws HeadlessException {
        add(panel);
        setVisible(true);
        pack();
    }
}
