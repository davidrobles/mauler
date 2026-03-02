package net.davidrobles.mauler.gui;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.core.MoveObservable;
import net.davidrobles.mauler.core.MatchController;
import net.davidrobles.mauler.core.MatchControllerObserver;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.Hashtable;

public class MatchControllerSliderView<GAME extends Game<GAME> & MoveObservable> extends JPanel
        implements MatchControllerObserver<GAME>
{
    private MatchController<GAME> controller;
    private JSlider slider = new JSlider();
    private Hashtable<Integer, JComponent> labelTable = new Hashtable<Integer, JComponent>();

    public MatchControllerSliderView(MatchController<GAME> controller)
    {
        this.controller = controller;
        this.controller.registerObserver(this);
        this.slider.setSnapToTicks(true);
        addSlider();
        addSliderListener();
        update(controller.getGame());
    }

    private void addSlider()
    {
        add(slider);
    }

    private void addSliderListener()
    {
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                if (source.isFocusOwner()) {
                    controller.setChange(source.getValue());
                }
            }
        });
    }

    @Override
    public void update(GAME game)
    {
        slider.setMinimum(0);
        slider.setMaximum(controller.getSize() - 1);
        slider.setValue(controller.getCurrentIndex());
        labelTable.clear();
        labelTable.put(0, new JLabel("0"));
        labelTable.put(controller.getSize() - 1, new JLabel(String.valueOf(controller.getSize() - 1)));
        slider.setLabelTable( labelTable );
        slider.setPaintLabels(true);
    }
}
