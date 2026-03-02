package net.davidrobles.mauler.othello;

import net.davidrobles.mauler.core.MoveObserver;
import net.davidrobles.mauler.core.MatchControllerObserver;
import net.davidrobles.mauler.core.Outcome;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class OthelloView extends JPanel implements MoveObserver, MatchControllerObserver<Othello>
{
    private Othello othello;

    // SIZES
    private final int panelWidth, panelHeight, boardWidth, boardHeight;
    private static final int CELL_SIZE = 50;
    private static final int BOARD_X_OFFSET = 30;
    private static final int BOARD_Y_OFFSET = 30;
    private static final int CELL_X_OFFSET = 4;
    private static final int CELL_Y_OFFSET = 4;

    // COLORS
    private static final Color BOARD_BG_COLOR = new Color(0x109d27);
    private static final Color BG_COLOR = Color.WHITE;
    private static final Color GRID_COLOR = Color.BLACK;
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color MOVES_COLOR = new Color(0.0f, 0.0f, 0.0f, 0.2f);

    // FONTS
    private static final Font font = new Font("sansserif", Font.BOLD, 16);

    public OthelloView(Othello othello)
    {
        this.othello = othello;
        this.panelWidth = Othello.SIZE * CELL_SIZE + BOARD_X_OFFSET * 2 + 1;
        this.panelHeight = Othello.SIZE * CELL_SIZE + BOARD_Y_OFFSET * 2 + 1;
        this.boardWidth = Othello.SIZE * CELL_SIZE + 1;
        this.boardHeight = Othello.SIZE * CELL_SIZE + 1;
        othello.registerMoveObserver(this);
        setPreferredSize(new Dimension(panelWidth, panelHeight));
    }

    @Override
    protected void paintComponent(Graphics gg)
    {
        super.paintComponent(gg);

        Graphics2D g = (Graphics2D) gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        g.setFont(font);

        // background
        g.setColor(BG_COLOR);
        g.fillRect(0, 0, panelWidth, panelHeight);
        g.setColor(BOARD_BG_COLOR);
        g.fillRect(BOARD_X_OFFSET, BOARD_Y_OFFSET, boardWidth, boardHeight);

        // stones
        for (int row = 0; row < Othello.SIZE; row++)
        {
            for (int col = 0; col < Othello.SIZE; col++)
            {
                if (othello.getSquare(row, col) != Othello.Square.EMPTY)
                {
                    if (othello.getSquare(row, col) == Othello.Square.BLACK)
                        g.setColor(Color.BLACK);
                    else if (othello.getSquare(row, col) == Othello.Square.WHITE)
                        g.setColor(Color.WHITE);

                    g.fillOval(col * CELL_SIZE + BOARD_X_OFFSET +  CELL_X_OFFSET, row * CELL_SIZE + BOARD_Y_OFFSET
                            + CELL_Y_OFFSET, CELL_SIZE - (CELL_X_OFFSET * 2), CELL_SIZE - (CELL_Y_OFFSET * 2));
                }
            }
        }





        // letters

        FontMetrics fm   = g.getFontMetrics(font);

        g.setColor(TEXT_COLOR);

        for (int col = 0; col < Othello.SIZE; col++)
        {

            Rectangle2D rect = fm.getStringBounds(String.valueOf((char) ('A' + (col))), g);
            int textHeight = (int)(rect.getHeight());
            int textWidth  = (int)(rect.getWidth());
            int x = (CELL_SIZE  - textWidth)  / 2;
            int y = (BOARD_Y_OFFSET - textHeight) / 2  + fm.getAscent();
            g.drawString(String.valueOf((char) ('A' + (col))), BOARD_X_OFFSET + col * CELL_SIZE + x, y);
//            g.drawString(String.valueOf(Othello.letters[col]), BOARD_X_OFFSET + col * CELL_SIZE + x,
//                    panelHeight - BOARD_Y_OFFSET + y);
        }

        for (int row = 0; row < Othello.SIZE; row++)
        {
            String str = String.valueOf(row + 1);
            Rectangle2D rect = fm.getStringBounds(str, g);
            int textHeight = (int)(rect.getHeight());
            int textWidth  = (int)(rect.getWidth());
            int x = (BOARD_X_OFFSET  - textWidth) / 2;
            int y = (CELL_SIZE - textHeight) / 2  + fm.getAscent();
            g.drawString(str, x, BOARD_Y_OFFSET + row * CELL_SIZE + y);
//            g.drawString(str, panelWidth - BOARD_X_OFFSET + x, BOARD_Y_OFFSET + row * CELL_SIZE + y);
        }

        // grid
        g.setColor(GRID_COLOR);
        for (int col = 0; col < Othello.SIZE; col++) {
            for (int row = 0; row < Othello.SIZE; row++) {
                g.drawRect(col * CELL_SIZE + BOARD_X_OFFSET, row * CELL_SIZE + BOARD_Y_OFFSET, CELL_SIZE, CELL_SIZE);
            }
        }

        g.setColor(Color.BLACK);
        String pieceCountStr = "Black: " + othello.getNumDiscs(0) + " White: " + othello.getNumDiscs(1);
        Rectangle2D pieceCountStrRect = fm.getStringBounds(pieceCountStr, g);
        g.drawString(pieceCountStr, BOARD_X_OFFSET, panelHeight - (int) pieceCountStrRect.getHeight() / 2);
        String playerTurnStr = (othello.getCurPlayer() == 0 ? "Black" : "White") + " player's turn";
        Rectangle2D playerTurnRect = fm.getStringBounds(playerTurnStr, g);
        g.drawString(playerTurnStr, panelWidth - (int) playerTurnRect.getWidth(),
                panelHeight - (int) pieceCountStrRect.getHeight() / 2);

        // draw result
        if (othello.isOver())
        {
            g.setColor(Color.RED);
            String msg;

            if (othello.getOutcome()[0] == Outcome.WIN)
                msg = "BLACK wins";
            else if (othello.getOutcome()[1] == Outcome.WIN)
                msg = "WHITE wins";
            else
                msg = "Draw!";

            g.drawString(msg, 200, 200);
        }
        else
        {
            // possible moves
            if (othello.getMoves().length == 1 && othello.getMoves()[0].equals("PASS"))
            {
                g.setColor(Color.RED);
                g.drawString("NO MOVES AVAILABLE... PASS!", 200, 200);
            }
            else
            {
                g.setColor(MOVES_COLOR);

                for (String moveStr : othello.getMoves())
                {
                    int col = getCol(moveStr.charAt(0));
                    int row = Integer.valueOf(moveStr.substring(1, 2)) - 1;

                    g.fillOval(col * CELL_SIZE + BOARD_X_OFFSET + CELL_X_OFFSET,
                            row * CELL_SIZE + BOARD_Y_OFFSET + CELL_Y_OFFSET, CELL_SIZE - (CELL_X_OFFSET * 2),
                            CELL_SIZE - (CELL_Y_OFFSET * 2));
                }
            }
        }
    }

    private int getCol(char letter)
    {
        for (int i = 0; i < Othello.SIZE; i++)
            if ((char) ('A' + (i)) == letter)
                return i;

        return -838448;
    }

    @Override
    public void moveUpdate()
    {
        repaint();
    }

    @Override
    public void update(Othello othello)
    {
        this.othello = othello;
        repaint();
    }
}
