package net.davidrobles.mauler.loa;

import net.davidrobles.mauler.core.MoveObserver;
import net.davidrobles.mauler.core.MatchControllerObserver;
import net.davidrobles.mauler.core.Outcome;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;

public class LOAView extends JPanel implements MoveObserver, MatchControllerObserver<LOA>, MouseListener
{
    private LOA loa;

    // SIZES
    private final int panelWidth, panelHeight, boardWidth, boardHeight;
    private static final int CELL_SIZE = 50;
    private static final int BOARD_X_OFFSET = 30;
    private static final int BOARD_Y_OFFSET = 30;
    private static final int CELL_X_OFFSET = 4;
    private static final int CELL_Y_OFFSET = 4;

    // COLORS
    private static final Color BOARD_BG_COLOR = new Color(0x109d27),
                               BG_COLOR = Color.WHITE,
                               TEXT_COLOR = Color.BLACK,
                               MOVES_COLOR = new Color(0.0f, 0.0f, 0.0f, 0.2f),
                               P1_STONE_COLOR = new Color(204, 0, 0),
                               P2_STONE_COLOR = Color.WHITE,
                               STONE_BORDER_COLOR = Color.BLACK;
    private static final Color[] CELL_COLORS = { new Color(0xffce9e), new Color(0xd18b47) };
    private static final Stroke STONE_STROKE = new BasicStroke(2.0f);

    // FONTS
    private static final Font font = new Font("sansserif", Font.BOLD, 16);
    private FontMetrics fm;

    public LOAView(LOA loa)
    {
        this.loa = loa;
        this.panelWidth = LOA.SIDE_SIZE * CELL_SIZE + BOARD_X_OFFSET * 2 + 1;
        this.panelHeight = LOA.SIDE_SIZE * CELL_SIZE + BOARD_Y_OFFSET * 2 + 1;
        this.boardWidth = LOA.SIDE_SIZE * CELL_SIZE + 1;
        this.boardHeight = LOA.SIDE_SIZE * CELL_SIZE + 1;
        loa.registerMoveObserver(this);
        addMouseListener(this);
        setPreferredSize(new Dimension(panelWidth, panelHeight));
    }

    private void drawStones(Graphics2D g)
    {
        for (int row = 0; row < LOA.SIDE_SIZE; row++)
        {
            for (int col = 0; col < LOA.SIDE_SIZE; col++)
            {
                g.setPaint(CELL_COLORS[(row + col) % 2]);
                g.fill(new Rectangle2D.Double(col * CELL_SIZE + BOARD_X_OFFSET,
                                row * CELL_SIZE + BOARD_Y_OFFSET, CELL_SIZE, CELL_SIZE));

                if (loa.getCell(row, col) != LOA.Cell.EMPTY)
                {
                    if (loa.getCell(row, col) == LOA.Cell.BLACK)
                        g.setPaint(P1_STONE_COLOR);
                    else if (loa.getCell(row, col) == LOA.Cell.WHITE)
                        g.setPaint(P2_STONE_COLOR);

                    Arc2D stone = new Arc2D.Double(col * CELL_SIZE + BOARD_X_OFFSET +  CELL_X_OFFSET,
                                                   row * CELL_SIZE + BOARD_Y_OFFSET + CELL_Y_OFFSET,
                                                   CELL_SIZE - (CELL_X_OFFSET * 2),
                                                   CELL_SIZE - (CELL_Y_OFFSET * 2),
                                                   0, 360, Arc2D.OPEN);
                    g.fill(stone);
                    g.setStroke(STONE_STROKE);
                    g.setPaint(STONE_BORDER_COLOR);
                    g.draw(stone);
                }
            }
        }
    }

    private void drawLetters(Graphics2D g)
    {
        g.setColor(TEXT_COLOR);

        for (int col = 0; col < LOA.SIDE_SIZE; col++)
        {
            Rectangle2D rect = fm.getStringBounds(String.valueOf((char) ('a' + (col))), g);
            int textHeight = (int)(rect.getHeight());
            int textWidth  = (int)(rect.getWidth());
            int x = (CELL_SIZE  - textWidth)  / 2;
            int y = (BOARD_Y_OFFSET - textHeight) / 2  + fm.getAscent();
            g.drawString(String.valueOf((char) ('a' + (col))), BOARD_X_OFFSET + col * CELL_SIZE + x, y);
        }

        for (int row = 0; row < LOA.SIDE_SIZE; row++)
        {
            String str = String.valueOf(LOA.SIDE_SIZE - row);
            Rectangle2D rect = fm.getStringBounds(str, g);
            int textHeight = (int)(rect.getHeight());
            int textWidth  = (int)(rect.getWidth());
            int x = (BOARD_X_OFFSET  - textWidth) / 2;
            int y = (CELL_SIZE - textHeight) / 2  + fm.getAscent();
            g.drawString(str, x, BOARD_Y_OFFSET + row * CELL_SIZE + y);
        }
    }

    private void drawStatus(Graphics2D g)
    {
        g.setColor(Color.BLACK);
        String pieceCountStr = "Black: " + loa.getCount(0) + " White: " + loa.getCount(1);
        Rectangle2D pieceCountStrRect = fm.getStringBounds(pieceCountStr, g);
        g.drawString(pieceCountStr, BOARD_X_OFFSET, panelHeight - (int) pieceCountStrRect.getHeight() / 2);
        String playerTurnStr = (loa.getCurPlayer() == 0 ? "Black" : "White") + " player's turn";
        Rectangle2D playerTurnRect = fm.getStringBounds(playerTurnStr, g);
        g.drawString(playerTurnStr, panelWidth - (int) playerTurnRect.getWidth(),
                panelHeight - (int) pieceCountStrRect.getHeight() / 2);
    }

    private void drawBackground(Graphics2D g)
    {
        g.setColor(BG_COLOR);
        g.fillRect(0, 0, panelWidth, panelHeight);
        g.setColor(BOARD_BG_COLOR);
        g.fillRect(BOARD_X_OFFSET, BOARD_Y_OFFSET, boardWidth, boardHeight);
    }

    private void drawMessages(Graphics2D g)
    {
        if (loa.isOver())
        {
            g.setColor(Color.RED);
            String msg;

            if (loa.getOutcome()[0] == Outcome.WIN)
                msg = "Player 1 wins";
            else if (loa.getOutcome()[1] == Outcome.WIN)
                msg = "Player 2 wins";
            else
                msg = "Draw!";

            g.drawString(msg, 200, 200);
        }
    }

    private void drawLegalMoves(Graphics2D g)
    {
        if (row != -1 && col != -1)
        {
            g.setColor(MOVES_COLOR);
            g.fillOval(col * CELL_SIZE + BOARD_X_OFFSET + CELL_X_OFFSET,
                       row * CELL_SIZE + BOARD_Y_OFFSET + CELL_Y_OFFSET,
                       CELL_SIZE - (CELL_X_OFFSET * 2),
                       CELL_SIZE - (CELL_Y_OFFSET * 2));

            for (String moveStr : loa.filterMoves(row, col))
            {
                Point dstPoint = loa.dstMoveToPoint(moveStr);
                g.setColor(new Color(0, 255, 0, 150));
                g.fillOval(dstPoint.x * CELL_SIZE + BOARD_X_OFFSET + CELL_X_OFFSET,
                           dstPoint.y * CELL_SIZE + BOARD_Y_OFFSET + CELL_Y_OFFSET,
                           CELL_SIZE - (CELL_X_OFFSET * 2),
                           CELL_SIZE - (CELL_Y_OFFSET * 2));
            }
        }
    }

    private int getCol(char letter)
    {
        for (int i = 0; i < LOA.SIDE_SIZE; i++)
            if ((char) ('a' + (i)) == letter)
                return i;

        return -838448;
    }

    private boolean coordValid(int row, int col)
    {
        return row < LOA.SIDE_SIZE && col < LOA.SIDE_SIZE;
    }

    ////////////////
    // JComponent //
    ////////////////

    @Override
    protected void paintComponent(Graphics graphics)
    {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (fm == null)
            fm = g.getFontMetrics(font);

        drawBackground(g);
        drawStones(g);
        drawLetters(g);
        drawStatus(g);
        drawMessages(g);
        drawLegalMoves(g);
    }

    //////////////////
    // MoveObserver //
    //////////////////

    @Override
    public void moveUpdate()
    {
        repaint();
    }

    /////////////////////////////
    // MatchControllerObserver //
    /////////////////////////////

    @Override
    public void update(LOA loa)
    {
        this.loa = loa;
        repaint();
    }

    ///////////////////
    // MouseListener //
    ///////////////////

    // MOUSE
    private int row = -1;
    private int col = -1;

    @Override
    public void mouseClicked(MouseEvent e)
    {

    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        Point point = e.getPoint();
        row = (((int) point.getY()) - BOARD_Y_OFFSET) / CELL_SIZE;
        col = (((int) point.getX()) - BOARD_X_OFFSET) / CELL_SIZE;

//        if (coordValid(row, col))
//        {
//            System.out.println("index " + LOA.cellToString(row, col));
//            System.out.println(Arrays.toString(loa.filterMoves(row, col).toArray()));
//        }

        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        row = col = -1;
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {

    }

    @Override
    public void mouseExited(MouseEvent e)
    {

    }
}
