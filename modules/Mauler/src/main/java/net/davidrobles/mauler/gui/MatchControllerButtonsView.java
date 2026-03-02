package net.davidrobles.mauler.gui;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.core.MoveObservable;
import net.davidrobles.mauler.core.MatchController;
import net.davidrobles.mauler.core.MatchControllerObserver;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MatchControllerButtonsView<GAME extends Game<GAME> & MoveObservable> extends JPanel
        implements MatchControllerObserver<GAME>
{
    private MatchController<GAME> controller;
    private JButton startButton = new JButton("|<");
    private JButton prevButton = new JButton("<");
    private JButton nextButton = new JButton(">");
    private JButton endButton = new JButton(">|");
    private JButton playToEndButton = new JButton("Play to End");

    public MatchControllerButtonsView(MatchController<GAME> controller)
    {
        this.controller = controller;
        this.controller.registerObserver(this);
        addButtons();
        addButtonListeners();
        update(controller.getGame());
    }

    private void addButtons()
    {
        add(startButton);
        add(prevButton);
        add(nextButton);
        add(endButton);
        add(playToEndButton);
    }

    private void addButtonListeners()
    {
        startButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                controller.start();
            }
        });

        prevButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                controller.prev();
            }
        });

        nextButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                controller.next();
            }
        });

        endButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                controller.end();
            }
        });

        playToEndButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                controller.playToEnd();
            }
        });
    }

    /////////////////////////////
    // MatchControllerObserver //
    /////////////////////////////

    @Override
    public void update(GAME game)
    {
        startButton.setEnabled(controller.isStart());
        prevButton.setEnabled(controller.isPrev());
        nextButton.setEnabled(controller.isNext());
        endButton.setEnabled(controller.isEnd());
        playToEndButton.setEnabled(!controller.isOver());
        repaint();
    }
}
