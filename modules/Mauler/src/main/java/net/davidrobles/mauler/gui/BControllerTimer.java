package net.davidrobles.mauler.gui;

import javax.swing.*;

public class BControllerTimer extends JLabel
{
    public BControllerTimer() {
        super("5.75s");
    }

    public void setTime() {
        setText("change");
        repaint();
    }
}
