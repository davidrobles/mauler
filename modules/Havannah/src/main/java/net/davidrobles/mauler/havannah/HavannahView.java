package net.davidrobles.mauler.havannah;

import net.davidrobles.mauler.core.MatchControllerObserver;
import net.davidrobles.mauler.core.MoveObserver;

import javax.swing.*;
import java.awt.*;

public class HavannahView extends JPanel implements MoveObserver, MatchControllerObserver<Havannah>
{
    private Havannah havannah;

    // SIZES
    private final int panelWidth, panelHeight, boardWidth, boardHeight;
    private static final int CELL_SIZE = 20;
    private static final int BOARD_X_OFFSET = 30;
    private static final int BOARD_Y_OFFSET = 30;
    private static final int CELL_X_OFFSET = 4;
    private static final int CELL_Y_OFFSET = 4;
    private static final int STONE_SIZE = 25;

    // COLORS
    private static final Color BOARD_BG_COLOR = new Color(0x109d27);
    private static final Color BG_COLOR = Color.WHITE;
    private static final Color GRID_COLOR = Color.BLACK;
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color MOVES_COLOR = new Color(0.0f, 0.0f, 0.0f, 0.2f);

    // FONTS
    private static final Font font = new Font("sansserif", Font.BOLD, 16);

    public HavannahView(Havannah havannah)
    {
        this.havannah = havannah;
        this.panelWidth = havannah.getBoardLength() * CELL_SIZE + BOARD_X_OFFSET * 2 + 1;
        this.panelHeight = havannah.getBoardLength() * CELL_SIZE + BOARD_Y_OFFSET * 2 + 1;
        this.boardWidth = havannah.getBoardLength() * CELL_SIZE + 1;
        this.boardHeight = havannah.getBoardLength() * CELL_SIZE + 1;
        havannah.registerMoveObserver(this);
        setPreferredSize(new Dimension(600, 600));
    }

    @Override
    protected void paintComponent(Graphics gg)
    {
        super.paintComponent(gg);

        Graphics2D g = (Graphics2D) gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // background
//        g.setColor(BG_COLOR);
//        g.fillRect(0, 0, panelWidth, panelHeight);
//        g.setColor(BOARD_BG_COLOR);
//        g.fillRect(BOARD_X_OFFSET, BOARD_Y_OFFSET, boardWidth, boardHeight);

        g.setColor(Color.BLACK);

        // TODO: draw stones

        // TODO: draw letters

        // TODO: draw grid
//        System.out.println(havannah);

        for (int row = 0; row < havannah.getBoardLength(); row++)
        {
            int startX = (int) (300 - (row * (30.0)));
            int startY = (int) (500 - (row * (17.0)));

            for (int col = 0; col < havannah.getBoardLength(); col++)
            {
                Cell cell = havannah.getCell(row, col);

                if (cell != Cell.ILLEGAL)
                {
                    int x = (int) (startX + (col * 30.0));
                    int y = (int) (startY - (col * 17.0));

                    // draw hexagon
                    Color hexFillColor = Color.YELLOW;

//                    if (havannah.getSelectedCell().row == row && havannah.getSelectedCell().col == col)
//                        hexFillColor = Color.BLUE;
//                    else if (havannah.getSelectedAdjs().contains(new HCell(row, col)))
//                        hexFillColor = Color.RED;

                    drawHexagon(x, y, hexFillColor, g);

                    // draw stone
                    Color color = (cell == Cell.BLACK ? Color.BLACK : Color.WHITE);
                    drawStone(x, y, havannah.getCell(row, col), color, g);
                }

//                if (row % 2 == 0) {
//                    x = col * (CELL_SIZE + CELL_SIZE * 2);
//                System.out.println("X: " + x);
//                System.out.println("Y: " + y);
//                }
// else {
////                    x = col * (CELL_SIZE + CELL_SIZE * 2);
////                    y = row * (CELL_SIZE +  CELL_SIZE * 2);
//                }
            }
        }
    }

    private void drawStone(int x, int y, Cell cell, Color color, Graphics2D g2d)
    {
        if (cell != Cell.EMPTY)
        {
            g2d.setColor(color);
            g2d.fillOval(x - STONE_SIZE / 2, y - STONE_SIZE / 2, STONE_SIZE, STONE_SIZE);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(x - STONE_SIZE / 2, y - STONE_SIZE / 2, STONE_SIZE, STONE_SIZE);
        }
    }

    private void drawHexagon(int x, int y, Color color, Graphics2D g2d)
    {
        int[] xPoints = new int[6];
        int[] yPoints = new int[6];

        for (int i = 0; i < 6; i++)
        {
            xPoints[i] = x + (int) (CELL_SIZE * Math.cos(Math.toRadians(i * 60))); // TODO: change cell size
            yPoints[i] = y - (int) (CELL_SIZE * Math.sin(Math.toRadians(i * 60)));
        }

//        g2d.drawPolygon(xPoints, yPoints, 6);
        g2d.setColor(color);
        g2d.fillPolygon(xPoints, yPoints, 6);
        g2d.setColor(Color.BLACK);
        g2d.drawPolygon(xPoints, yPoints, 6);
//        g2d.drawString();
//        int zero = x + 50;
//        int next = x + (int) (50 * Math.cos(Math.toRadians(60)));
//        int partesita = (int) (CELL_SIZE * Math.sin(Math.toRadians(60)));
//        System.out.println("parte: " + partesita);
//        g2d.drawLine(x + 50, y, next, y);
    }

    @Override
    public void moveUpdate() {
        repaint();
    }

    @Override
    public void update(Havannah havannah) {
        this.havannah = havannah;
        repaint();
    }
}
