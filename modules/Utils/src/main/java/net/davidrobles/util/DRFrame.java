package net.davidrobles.util;

import javax.swing.*;

public class DRFrame extends JFrame
{
    private JPanel panel;

    public DRFrame(JPanel panel) {
        this(panel, "");
    }

    public DRFrame(JPanel panel, String title) {
        super(title);
        this.panel = panel;
        add(panel);
        pack();
//        DRUtil.centerComponent(this);
        setVisible(true);
    }
    
}
