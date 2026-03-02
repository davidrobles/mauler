package net.davidrobles.util;

import javax.swing.*;
import java.awt.*;

public class DRUtil
{
    public static int argMax(double... sequence) {
        int index = 0;
        double max = sequence[index];
        for (int i = 1; i < sequence.length; i++) {
            if (sequence[i] > max) {
                max = sequence[i];
                index = i;
            }
        }
        return index;
    }

    public static void centerJFrame(JFrame frame)
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((int) (screenSize.getWidth() / 2 - frame.getWidth() / 2),
                          (int) (screenSize.getHeight() / 2 - frame.getHeight() / 2));
    }
}
