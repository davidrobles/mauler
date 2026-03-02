package net.davidrobles.gridworld.view;

import net.davidrobles.gridworld.GWAction;
import net.davidrobles.gridworld.GWState;
import net.davidrobles.gridworld.GridWorldEnv;
import net.davidrobles.gridworld.GridWorldMDP;
import net.davidrobles.rl.valuefunctions.QFunction;
import net.davidrobles.rl.valuefunctions.QFunctionObserver;
import edu.mines.jtk.awt.ColorMap;

import java.awt.*;

public class GWViewQValues extends GridWorldView implements QFunctionObserver<GWState, GWAction>
{
    private QFunction<GWState, GWAction> qFunction;

    public GWViewQValues(GridWorldMDP gridWorld, int cellWidth, int cellHeight, GridWorldEnv environment)
    {
        super(gridWorld, cellWidth, cellHeight, environment);
    }

    @Override
    public void drawValues(Graphics g)
    {
        if (qFunction != null)
        {
            float max = Float.MIN_VALUE;
            float min = Float.MAX_VALUE;

            for (GWState state : gw.getStates()) {
                for (GWAction action : gw.getActions(state)) {
                    float colorValue = (float) qFunction.getValue(state, action);
                    if (colorValue < min) {
                        min = colorValue;
                    }
                    if (colorValue > max) {
                        max = colorValue;
                    }
                }
            }

            ColorMap colorMap = new ColorMap(min, max, ColorMap.getJet());

            for (GWState s : gw.getStates())
            {
                for (GWAction action : gw.getActions(s))
                {
                    GWState state = (GWState) s;
                    g.setColor(colorMap.getColor(qFunction.getValue(state, action)));
//                    g.setColor(colorMap.getColor(qFunction.getValue(state, action)));
//                    g.fillRect(state.getX() * cellWidth, state.getY() * cellHeight, cellWidth, cellHeight);

                    // draw values
//                    if (valuesEnabled) {
//                        g.setColor(Color.WHITE);
//                        String t = String.format("%.1f", (float) qFunction.getValue(state, action));
//                        g.drawString(t, state.getX() * cellWidth, state.getY() * cellHeight + cellHeight / 2);
//                    }

                    if (action == GWAction.UP) {
                        // Top
                        g.fillRect(state.getX() * cellWidth + cellWidth / 3, state.getY() * cellHeight,
                                    cellWidth / 3, cellHeight / 3);
                    }

                    if (action == GWAction.DOWN) {
                        // Bottom
                        g.fillRect(state.getX() * cellWidth + cellWidth / 3, state.getY() * cellHeight
                                + (cellHeight / 3) * 2, cellWidth / 3, cellHeight / 3);
                    }

                    if (action == GWAction.LEFT) {
                        // Left
                        g.fillRect(state.getX() * cellWidth, state.getY() * cellHeight + (cellHeight / 3),
                                cellWidth / 3, cellHeight / 3);
                    }

                    if (action == GWAction.RIGHT) {
                        // Right
                        g.fillRect(state.getX() * cellWidth + (cellWidth / 3) * 2, state.getY() * cellHeight
                                + (cellHeight / 3), cellWidth / 3, cellHeight / 3);
                    }
                }
            }
        }
    }

    @Override
    public void qFunctionUpdated(QFunction<GWState, GWAction> qFunction) {
        this.qFunction = qFunction;
        repaint();
    }
}
