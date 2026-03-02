package net.davidrobles.mauler.gui;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame
{
    public GameFrame(JPanel panel, String title) {
        super("Minotauro - " + title);
        add(panel);
        pack();
        centerComponent(this);
        setVisible(true);
    }

    public static void centerComponent(Component component) {
        // Get the size of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        // Determine the new location of the window
        int w = component.getSize().width;
        int h = component.getSize().height;
        int x = (dim.width-w)/2;
        int y = (dim.height-h)/2;
        // Move the window
        component.setLocation(x, y);
    }
}
