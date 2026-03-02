package net.davidrobles.mauler.breakthrough;

import net.davidrobles.mauler.breakthrough.Breakthrough.Cell;
import net.davidrobles.mauler.core.MoveObserver;
import net.davidrobles.mauler.core.MatchControllerObserver;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;


public class BreakthroughView extends JPanel implements MoveObserver, MatchControllerObserver<Breakthrough>, MouseListener
{
    private Breakthrough bk;

    // SIZES
    private final int panelWidth, panelHeight, boardWidth, boardHeight;

    private static final int CELL_SIZE = 50,
                             BOARD_X_OFFSET = 30,
                             BOARD_Y_OFFSET = 30,
                             CELL_X_OFFSET = 4,
                             CELL_Y_OFFSET = 4;

    // COLORS
    private static final Color BOARD_BG_COLOR = new Color(0xf8bf6a),
                               BG_COLOR = Color.WHITE,
                               GRID_COLOR = Color.BLACK,
                               TEXT_COLOR = Color.BLACK,
                               MOVES_COLOR = new Color(0.0f, 0.0f, 0.0f, 0.2f);

    // FONTS
    private static final Font font = new Font("sansserif", Font.BOLD, 16);
    private FontMetrics fm = null;

    // MOUSE
    private int row = -1;
    private int col = -1;

    public BreakthroughView(Breakthrough bk)
    {
        this.bk = bk;
        this.panelWidth = bk.getRows() * CELL_SIZE + BOARD_X_OFFSET * 2 + 1;
        this.panelHeight = bk.getCols() * CELL_SIZE + BOARD_Y_OFFSET * 2 + 1;
        this.boardWidth = bk.getRows() * CELL_SIZE + 1;
        this.boardHeight = bk.getCols() * CELL_SIZE + 1;
        bk.registerMoveObserver(this);
        setPreferredSize(new Dimension(panelWidth, panelHeight));
        addMouseListener(this);
    }

    @Override
    protected void paintComponent(Graphics gg)
    {
        super.paintComponent(gg);

        Graphics2D g = (Graphics2D) gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (fm == null)
            fm = g.getFontMetrics(font);

        drawBackground(g);
        drawPieces(g);
        drawLetters(g);
        drawGrid(g);
        drawStatus(g);
        drawLegalMoves(g);

        // draw result
//        if (bk.isOver())
//        {
//            g.setColor(Color.RED);
//            String msg;
//
//            if (bk.getOutcome()[0] == Outcome.WIN)
//                msg = "BLACK wins";
//            else if (bk.getOutcome()[1] == Outcome.WIN)
//                msg = "WHITE wins";
//            else
//                msg = "Draw!";
//
//            g.drawString(msg, 200, 200);
//        }
//        else
//        {
//            // possible moves
//            if (bk.getMoves().size() == 1 && bk.getMoves().get(0).equals("PASS"))
//            {
//                g.setColor(Color.RED);
//                g.drawString("NO MOVES AVAILABLE... PASS!", 200, 200);
//            }
//            else
//            {
//                g.setColor(MOVES_COLOR);
//
//                for (String moveStr : bk.getMoves())
//                {
//                    int col = getCol(moveStr.charAt(0));
//                    int row = Integer.valueOf(moveStr.substring(1, 2)) - 1;
//
//                    g.fillOval(col * CELL_SIZE + BOARD_X_OFFSET + CELL_X_OFFSET,
//                            row * CELL_SIZE + BOARD_Y_OFFSET + CELL_Y_OFFSET, CELL_SIZE - (CELL_X_OFFSET * 2),
//                            CELL_SIZE - (CELL_Y_OFFSET * 2));
//                }
//            }
//        }
    }

    private void drawBackground(Graphics2D g)
    {
        g.setColor(BG_COLOR);
        g.fillRect(0, 0, panelWidth, panelHeight);
        g.setColor(BOARD_BG_COLOR);
        g.fillRect(BOARD_X_OFFSET, BOARD_Y_OFFSET, boardWidth, boardHeight);
    }

    private void drawStatus(Graphics2D g)
    {
        g.setColor(Color.BLACK);
        String pieceCountStr = "Black: " + bk.getCount(0) + " White: " + bk.getCount(1);
        Rectangle2D pieceCountStrRect = fm.getStringBounds(pieceCountStr, g);
        g.drawString(pieceCountStr, BOARD_X_OFFSET, panelHeight - (int) pieceCountStrRect.getHeight() / 2);
        String playerTurnStr = (bk.getCurPlayer() == 0 ? "Black" : "White") + " player's turn";
        Rectangle2D playerTurnRect = fm.getStringBounds(playerTurnStr, g);
        g.drawString(playerTurnStr, panelWidth - (int) playerTurnRect.getWidth(),
                panelHeight - (int) pieceCountStrRect.getHeight() / 2);
    }

    private void drawGrid(Graphics2D g)
    {
        g.setColor(GRID_COLOR);

        for (int col = 0; col < bk.getCols(); col++)
            for (int row = 0; row < bk.getRows(); row++)
                g.drawRect(col * CELL_SIZE + BOARD_X_OFFSET, row * CELL_SIZE + BOARD_Y_OFFSET, CELL_SIZE, CELL_SIZE);
    }

    private void drawLegalMoves(Graphics2D g)
    {
        if (row != -1 && col != -1)
        {
            g.setColor(MOVES_COLOR);
            List<String> mv = filterMoves(row, col);

            for (String moveStr : mv)
            {
                Point point = bk.getPoint(moveStr);
                int pointCol = point.x;
                int pointRow = point.y;
                g.fillOval(pointCol * CELL_SIZE + BOARD_X_OFFSET + CELL_X_OFFSET,
                        pointRow * CELL_SIZE + BOARD_Y_OFFSET + CELL_Y_OFFSET, CELL_SIZE - (CELL_X_OFFSET * 2),
                        CELL_SIZE - (CELL_Y_OFFSET * 2));
            }
        }
    }

    private void drawLetters(Graphics2D g)
    {
        g.setColor(TEXT_COLOR);

        for (int col = 0; col < bk.getCols(); col++)
        {
            Rectangle2D rect = fm.getStringBounds(String.valueOf((char) ('a' + (col))), g);
            int textHeight = (int)(rect.getHeight());
            int textWidth  = (int)(rect.getWidth());
            int x = (CELL_SIZE  - textWidth)  / 2;
            int y = (BOARD_Y_OFFSET - textHeight) / 2  + fm.getAscent();
            g.drawString(String.valueOf((char) ('a' + (col))), BOARD_X_OFFSET + col * CELL_SIZE + x, y);
        }

        for (int row = 0; row < bk.getRows(); row++)
        {
            String str = String.valueOf(row + 1);
            Rectangle2D rect = fm.getStringBounds(str, g);
            int textHeight = (int)(rect.getHeight());
            int textWidth  = (int)(rect.getWidth());
            int x = (BOARD_X_OFFSET  - textWidth) / 2;
            int y = (CELL_SIZE - textHeight) / 2  + fm.getAscent();
            g.drawString(str, x, BOARD_Y_OFFSET + row * CELL_SIZE + y);
        }
    }

    private void drawPieces(Graphics2D g)
    {
        for (int row = 0; row < bk.getRows(); row++)
        {
            for (int col = 0; col < bk.getCols(); col++)
            {
                if (bk.getCell(row, col) != Cell.EMPTY)
                {
                    if (bk.getCell(row, col) == Cell.BLACK)
                        g.setColor(Color.BLACK);
                    else if (bk.getCell(row, col) == Cell.WHITE)
                        g.setColor(Color.WHITE);

                    g.fillOval(col * CELL_SIZE + BOARD_X_OFFSET +  CELL_X_OFFSET, row * CELL_SIZE + BOARD_Y_OFFSET
                            + CELL_Y_OFFSET, CELL_SIZE - (CELL_X_OFFSET * 2), CELL_SIZE - (CELL_Y_OFFSET * 2));
                }
            }
        }
    }

    private List<String> filterMoves(int row, int col)
    {
        List<String> newMoves = new ArrayList<String>();

        for (String move : bk.getMoves())
            if (bk.cellToString(row, col).equals(move.substring(0, 2)))
                newMoves.add(move);

        return newMoves;
    }

    private boolean coordValid(int row, int col)
    {
        return row < bk.getRows() && col < bk.getCols();
    }

    @Override
    public void moveUpdate()
    {
        repaint();
    }

    @Override
    public void update(Breakthrough bk)
    {
        this.bk = bk;
        repaint();
    }

    ///////////////////
    // MouseListener //
    ///////////////////

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

        if (coordValid(row, col))
            System.out.println("index " + bk.cellToString(row, col));

        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        row = col = -1;
        repaint();
        System.out.println("released");
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
