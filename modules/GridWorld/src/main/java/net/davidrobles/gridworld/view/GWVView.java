package net.davidrobles.gridworld.view;

import net.davidrobles.gridworld.GWState;
import net.davidrobles.gridworld.GridWorldEnv;
import net.davidrobles.gridworld.GridWorldMDP;
import net.davidrobles.rl.valuefunctions.VFunction;
import net.davidrobles.rl.valuefunctions.VFunctionObserver;
import edu.mines.jtk.awt.ColorMap;

import java.awt.*;

public class GWVView extends GridWorldView implements VFunctionObserver<GWState>
{
    private VFunction<GWState> vFunction;

    public GWVView(GridWorldMDP gridWorld, int cellWidth, int cellHeight, GridWorldEnv env)
    {
        super(gridWorld, cellWidth, cellHeight, env);
    }

    @Override
    public void drawValues(Graphics g)
    {
        if (vFunction != null)
        {
            float max = Float.MIN_VALUE;
            float min = Float.MAX_VALUE;

            // calculate max and min color values
            for (GWState state : gw.getStates())
            {
                float colorValue = (float) vFunction.getValue(state);

                if (colorValue < min)
                    min = colorValue;

                if (colorValue > max)
                    max = colorValue;
            }

            ColorMap colorMap = new ColorMap(min, max, ColorMap.getJet());

            // draw states
            for (GWState state : gw.getStates())
            {
                g.setColor(colorMap.getColor(vFunction.getValue(state)));
                g.fillRect(state.getX() * cellWidth, state.getY() * cellHeight, cellWidth, cellHeight);

                // draw values
                if (valuesEnabled)
                {
                    g.setColor(Color.WHITE);
                    String t = String.format("%.1f", (float) vFunction.getValue(state));
                    g.drawString(t, state.getX() * cellWidth, state.getY() * cellHeight + cellHeight / 2);
                }
            }
        }

        // draw current state
        g.setColor(Color.RED);
        g.fillRect(env.getCurrentState().getX() * cellWidth, env.getCurrentState().getY() * cellHeight,
                        cellWidth, cellHeight);
    }

    @Override
    public void valueFunctionChanged(VFunction<GWState> vFunction)
    {
        this.vFunction = vFunction;
        repaint();
    }
}
