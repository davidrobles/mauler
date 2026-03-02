package net.davidrobles.gridworld.view;

import net.davidrobles.gridworld.GWState;
import net.davidrobles.gridworld.GridWorldEnv;
import net.davidrobles.gridworld.GridWorldMDP;
import net.davidrobles.rl.MDPObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public abstract class GridWorldView extends JPanel implements MDPObserver, MouseMotionListener, MouseListener
{                              // TODO: remove this
    protected GridWorldMDP gw;
    protected GridWorldEnv env;
    protected int cellWidth = 25;
    protected int cellHeight = 25;
    protected boolean valuesEnabled = false;
    private boolean gridEnabled = false;
    private boolean highlightTerminalStates = true;
    private boolean showCurrentState = false;

    // Colors
    private static final Color CURRENT_STATE_FILL_COLOR = Color.GREEN;
    private static final Color CURRENT_STATE_BORDER_COLOR = Color.BLACK;
    private static final Color TERMINAL_STATE_FILL_COLOR = Color.BLACK;
    private static final Color TERMINAL_STATE_BORDER_COLOR = Color.WHITE;

    public GridWorldView(GridWorldMDP gw, int cellWidth, int cellHeight, GridWorldEnv environment) {
        this.gw = gw;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.env = environment;
        addMouseMotionListener(this);
//        gridWorldEnv.addObserver(this);
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(gw.getCols() * cellWidth, gw.getRows() * cellHeight));
    }

    public void setShowCurrentState(boolean showCurrentState) {
        this.showCurrentState = showCurrentState;
    }

    public void setGridEnabled(boolean gridEnabled) {
        this.gridEnabled = gridEnabled;
    }

    public abstract void drawValues(Graphics g);

//    public void drawValues(Graphics g)
//    {
//        float max = Float.MIN_VALUE;
//        float min = Float.MAX_VALUE;
//
//        for (MDPState state : gridWorld.getStates()) {
//            float colorValue = (float) valueFunction.getValue(state);
//            if (colorValue < min) {
//                min = colorValue;
//            }
//            if (colorValue > max) {
//                max = colorValue;
//            }
//        }
//
//        ColorMap colorMap = new ColorMap(min, max, ColorMap.getJet());
////        ColorMap colorMap = new ColorMap(min, max, ColorMap.getGray());
//
//        for (MDPState s : gridWorld.getStates()) {
//            GWState state = (GWState) s;
////            g.setColor(colorMap.getColor(colorMap.getIndex(valueFunction.getValue(state))));
//            g.setColor(colorMap.getColor(valueFunction.getValue(state)));
//            g.fillRect(state.getX() * cellWidth, state.getY() * cellHeight, cellWidth, cellHeight);
//            // draw values
//            if (valuesEnabled) {
//                g.setColor(Color.WHITE);
//                String t = String.format("%.1f", (float) valueFunction.getValue(state));
////                String t = String.format("%.1f", (float) gridWorld.reward(state));
//                g.drawString(t, state.getX() * cellWidth, state.getY() * cellHeight + cellHeight / 2);
//            }
//        }
//    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

//        if (valueFunction != null) {
            drawValues(g);
//        }

        // Draw grid
        for (GWState gwState : gw.getStates())
        {

//            if (gridWorld.isStateTerminal(gwState)) {
//                g.setColor(Color.GREEN);
//                g.fillRect(gwState.getX() * cellWidth, gwState.getY() * cellHeight, cellWidth, cellHeight);
//                g.setColor(Color.WHITE);
//                String t = String.format("%.1f", (float) gridWorld.reward(gwState));
//                g.drawString(t, gwState.getX() * cellWidth, gwState.getY() * cellHeight + cellHeight / 2);
//            }

            // draw square
            if (gridEnabled) {
                g.setColor(Color.BLACK);
                g.drawRect(gwState.getX() * cellWidth, gwState.getY() * cellHeight, cellWidth, cellHeight);
            }
            // Highlight terminal states
            if (highlightTerminalStates && gw.isTerminal(gwState)) {
                g.setColor(TERMINAL_STATE_FILL_COLOR);
                g.fillRect(gwState.getX() * cellWidth, gwState.getY() * cellHeight, cellWidth, cellHeight);
                g.setColor(TERMINAL_STATE_BORDER_COLOR);
                g.drawRect(gwState.getX() * cellWidth, gwState.getY() * cellHeight, cellWidth, cellHeight);
            }
        }

        // Draw current state
        if (showCurrentState) {
            GWState st = (GWState) env.getCurrentState();
            g.setColor(CURRENT_STATE_FILL_COLOR);
            g.fillRect(st.getX() * cellWidth, st.getY() * cellHeight, cellWidth, cellHeight);
            g.setColor(CURRENT_STATE_BORDER_COLOR);
            g.drawRect(st.getX() * cellWidth, st.getY() * cellHeight, cellWidth, cellHeight);
        }

        // Draw cell info
//        if (xCell != -1 && yCell != -1) {
//            g.setColor(Color.WHITE);
//            g.fillRect(xCell * cellWidth, yCell * cellHeight, 50, 50);
//        }

//        g.setColor(Color.BLACK);

        // draw q values
//        for (MDPState s : gridWorld.getStates())
//        {
//            GWState state = (GWState) s;
//
//            // Top
//            g.fillRect(state.getX() * cellWidth + cellWidth / 3, state.getY() * cellHeight,
//                        cellWidth / 3, cellHeight / 3);
//            // Bottom
//            g.fillRect(state.getX() * cellWidth + cellWidth / 3, state.getY() * cellHeight
//                    + (cellHeight / 3) * 2, cellWidth / 3, cellHeight / 3);
//
//            // Left
//            g.fillRect(state.getX() * cellWidth, state.getY() * cellHeight + (cellHeight / 3),
//                    cellWidth / 3, cellHeight / 3);
//
//            // Right
//            g.fillRect(state.getX() * cellWidth + (cellWidth / 3) * 2, state.getY() * cellHeight
//                    + (cellHeight / 3), cellWidth / 3, cellHeight / 3);
//        }
    }

    private void drawObject(Graphics g, GWState s, Color c) {
        assert s != null;
        int x = s.getX();
        int y = s.getY();
        g.setColor(c);
        g.fillOval(x * cellWidth, y * cellHeight, cellWidth, cellHeight);
    }

    @Override
    public void currentStateChanged() {
        repaint();
    }



    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
//        xCell = e.getX() / cellWidth;
//        yCell = e.getY() / cellHeight;
//        System.out.printf("X: %d, Y: %d\n", xCell, yCell);
//        printCellInfo();
    }

//    public void printCellInfo() {
//        GWState state = gridWorld.getState(xCell, yCell);
//        System.out.println("-----------------------");
//        System.out.printf("State: %d, %d\n", xCell, yCell);
//        System.out.println("-----------------------");
//        System.out.println("Actions:");
//        for (MDPAction action : gridWorld.getActions(state)) {
//            System.out.println("\t" + action);
//            for (MDPState nextState : gridWorld.successors(state, action)) {
//                GWState nState = (GWState) nextState;
//                System.out.printf("\t\tNext state: %d, %d, with probability of %.2f\n", nState.getX(), nState.getY(),
//                        gridWorld.transition(state, action, nextState));
//            }
//        }
//        System.out.printf("Value:  %.4f\n", valueFunction.getValue(state));
//        System.out.printf("Reward: %.1f\n", gridWorld.reward(state));
//        System.out.println();
//    }

    int xCell = -1;
    int yCell = -1;

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {
        xCell = -1;
        yCell = -1;
    }
}