package net.davidrobles.mauler.gui;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.core.MoveObservable;
import net.davidrobles.mauler.core.MatchController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BoardApp<GAME extends Game<GAME> & MoveObservable> extends JFrame
{
    private JPanel gameView;
    private MatchController<GAME> controller;
    private MatchControllerButtonsView<GAME> buttonsView;
    private MatchControllerSliderView<GAME> sliderView;
    private JFileChooser fc = new JFileChooser();

    public BoardApp(JPanel gameView, final MatchController<GAME> controller,
                    MatchControllerButtonsView<GAME> buttonsView, MatchControllerSliderView<GAME> sliderView,
                    JPanel movesView)
    {
        // Menu bar
        JMenuBar menuBar = new JMenuBar();

        // Menus

        JMenu fileMenu = new JMenu("File");
            final JMenuItem fileMenuNewItem = new JMenuItem("New...");
            fileMenuNewItem.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if (e.getSource() == fileMenuNewItem)
                        controller.reset();
                }
            });
            fileMenu.add(fileMenuNewItem);
            final JMenuItem fileMenuSaveGameItem = new JMenuItem("Save...");
            fileMenuSaveGameItem.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if (e.getSource() == fileMenuSaveGameItem)
                        controller.reset();
                }
            });
            fileMenu.add(fileMenuSaveGameItem);
        menuBar.add(fileMenu);

        JMenu controlMenu = new JMenu("Control");

        menuBar.add(controlMenu);

        // Add menus

        setJMenuBar(menuBar);



        JPanel panel = new JPanel();

        this.gameView = gameView;
        this.controller = controller;
        this.buttonsView = buttonsView;
        this.sliderView = sliderView;
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        panel.add(buttonsView, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        panel.add(sliderView, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 3;
        panel.add(gameView, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 3;
        panel.add(movesView, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        panel.add(new BControllerTimer(), c);

        getContentPane().add(panel);
        pack();
        setVisible(true);
    }
}
