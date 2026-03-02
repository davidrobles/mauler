package net.davidrobles.mauler.tictactoe;

import net.davidrobles.mauler.core.MatchControllerObserver;
import net.davidrobles.mauler.core.MoveObserver;

import javax.swing.*;
import java.awt.*;

public class TicTacToeView extends JPanel implements MoveObserver, MatchControllerObserver<TicTacToe>
{
    private TicTacToe tic;

    // SIZES
    private final int panelWidth, panelHeight, boardWidth, boardHeight;
    private static final int CELL_SIZE = 100,
                             BOARD_X_OFFSET = 30,
                             BOARD_Y_OFFSET = 30,
                             CELL_X_OFFSET = 4,
                             CELL_Y_OFFSET = 4;

    // COLORS
    private static final Color BOARD_BG_COLOR = new Color(0x109d27);
    private static final Color BG_COLOR = Color.WHITE;
    private static final Color GRID_COLOR = Color.BLACK;

    public TicTacToeView(TicTacToe tic)
    {
        this.tic = tic;
        this.panelWidth = TicTacToe.SIZE * CELL_SIZE + BOARD_X_OFFSET * 2 + 1;
        this.panelHeight = TicTacToe.SIZE * CELL_SIZE + BOARD_Y_OFFSET * 2 + 1;
        this.boardWidth = TicTacToe.SIZE * CELL_SIZE + 1;
        this.boardHeight = TicTacToe.SIZE * CELL_SIZE + 1;
        tic.registerMoveObserver(this);
        setPreferredSize(new Dimension(panelWidth, panelHeight));
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2D = (Graphics2D) g;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // background
        g2D.setColor(BG_COLOR);
        g2D.fillRect(0, 0, panelWidth, panelHeight);
        g2D.setColor(BOARD_BG_COLOR);
        g2D.fillRect(BOARD_X_OFFSET, BOARD_Y_OFFSET, boardWidth, boardHeight);

        // stones
        for (int row = 0; row < TicTacToe.SIZE; row++)
        {
            for (int col = 0; col < TicTacToe.SIZE; col++)
            {
                if (tic.getCell(row, col) != TicTacToe.Cell.EMPTY)
                {
                    if (tic.getCell(row, col) == TicTacToe.Cell.CROSS)
                        g2D.setColor(Color.BLACK);
                    else if (tic.getCell(row, col) == TicTacToe.Cell.NOUGHT)
                        g2D.setColor(Color.WHITE);

                    g2D.fillOval(col * CELL_SIZE + BOARD_X_OFFSET + CELL_X_OFFSET,
                                 row * CELL_SIZE + BOARD_Y_OFFSET + CELL_Y_OFFSET,
                                 CELL_SIZE - (CELL_X_OFFSET * 2),
                                 CELL_SIZE - (CELL_Y_OFFSET * 2));
                }
            }
        }

        // grid
        g2D.setColor(GRID_COLOR);

        for (int row = 0; row < TicTacToe.SIZE; row++)
            for (int col = 0; col < TicTacToe.SIZE; col++)
                g2D.drawRect(col * CELL_SIZE + BOARD_X_OFFSET, row * CELL_SIZE + BOARD_Y_OFFSET, CELL_SIZE, CELL_SIZE);
    }

    @Override
    public void moveUpdate()
    {
        repaint();
    }

    @Override
    public void update(TicTacToe tic)
    {
        this.tic = tic;
        repaint();
    }
}
