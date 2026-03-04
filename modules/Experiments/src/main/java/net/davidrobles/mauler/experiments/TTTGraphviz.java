package net.davidrobles.mauler.experiments;

import net.davidrobles.mauler.strategies.mcts.GraphvizMCTSObserver;
import net.davidrobles.mauler.strategies.mcts.MCTSNode;
import net.davidrobles.mauler.strategies.mcts.UCT;
import net.davidrobles.mauler.tictactoe.TicTacToe;

import java.io.File;
import java.util.function.Function;

/**
 * Runs UCT (1 000 simulations) from a near-terminal TicTacToe position and
 * writes the resulting search tree to {@code tree.dot} in the current working
 * directory.
 *
 * <p>The starting position has the first two rows filled and row 2 empty:
 * <pre>
 *   X | O | X
 *  ---+---+---
 *   O | X | O
 *  ---+---+---
 *   _ | _ | _    ← X to move
 * </pre>
 * X has two immediate wins (diagonal 0-4-8 via cell 8, anti-diagonal 2-4-6
 * via cell 6). UCT should discover both and prefer one of them.
 *
 * <p>Render the output with any Graphviz layout engine, e.g.:
 * <pre>
 *   dot -Tpdf tree.dot -o tree.pdf
 * </pre>
 */
public class TTTGraphviz
{
    public static void main(String[] args)
    {
        // Build the near-terminal position: makeMove(0) advances through cells
        // 0-5 in order since each call picks the lowest-indexed empty cell.
        TicTacToe game = new TicTacToe();
        for (int i = 0; i < 6; i++)
            game.makeMove(0);   // fills cells 0-5 alternating X/O

        System.out.println(game);

        UCT<TicTacToe> uct = new UCT<TicTacToe>(Math.sqrt(2), 1_000);
        uct.addObserver(new GraphvizMCTSObserver<>(new File("tree.dot"), TTTGraphviz::tttLabel));

        int move = uct.move(game);
        System.out.printf("UCT chose move index %d (cell %d).%n", move, move == 0 ? 6 : move == 1 ? 7 : 8);
        System.out.println("Search tree written to tree.dot");
    }

    /** HTML label showing the board as a 3×3 table plus visit/value stats. */
    private static String tttLabel(MCTSNode<TicTacToe> node)
    {
        TicTacToe g = node.getGame();
        StringBuilder html = new StringBuilder("<<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\">");
        for (int row = 0; row < TicTacToe.SIZE; row++)
        {
            html.append("<TR>");
            for (int col = 0; col < TicTacToe.SIZE; col++)
            {
                TicTacToe.Cell cell = g.getCell(row, col);
                String symbol;
                if (cell == TicTacToe.Cell.CROSS)        symbol = "X";
                else if (cell == TicTacToe.Cell.NOUGHT)  symbol = "O";
                else                                      symbol = ".";
                html.append("<TD WIDTH=\"20\">").append(symbol).append("</TD>");
            }
            html.append("</TR>");
        }
        html.append(String.format("<TR><TD COLSPAN=\"3\">v=%d q=%.3f</TD></TR>", node.getVisits(), node.getValue()));
        html.append("</TABLE>>");
        return html.toString();
    }
}
