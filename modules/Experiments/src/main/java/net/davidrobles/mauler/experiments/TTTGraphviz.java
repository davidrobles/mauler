package net.davidrobles.mauler.experiments;

import net.davidrobles.mauler.strategies.mcts.GraphvizMCTSObserver;
import net.davidrobles.mauler.strategies.mcts.MCTSNode;
import net.davidrobles.mauler.strategies.mcts.UCT;
import net.davidrobles.mauler.tictactoe.TicTacToe;

import java.io.File;

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
        for (int i = 0; i < 5; i++)
            game.makeMove(0);   // fills cells 0-5 alternating X/O

        System.out.println(game);

        UCT<TicTacToe> uct = new UCT<TicTacToe>(Math.sqrt(2), 1_000);
        uct.addObserver(new GraphvizMCTSObserver<>(new File("tree.dot"), TTTGraphviz::tttLabel));

        int move = uct.move(game);
        System.out.printf("UCT chose move index %d (cell %d).%n", move, move == 0 ? 6 : move == 1 ? 7 : 8);
        System.out.println("Search tree written to tree.dot");
    }

    /**
     * Styled HTML label: 3×3 board with colored cells and a dark stats footer.
     *
     * <ul>
     *   <li>X — deep red on rose background</li>
     *   <li>O — deep blue on sky background</li>
     *   <li>Empty — off-white</li>
     *   <li>Warm gray grout shows through the cell gaps</li>
     * </ul>
     */
    private static String tttLabel(MCTSNode<TicTacToe> node)
    {
        TicTacToe g = node.getGame();
        StringBuilder html = new StringBuilder();

        // Warm gray background bleeds through CELLSPACING gaps, acting as grout
        html.append("<<TABLE BORDER=\"0\" CELLBORDER=\"0\" CELLSPACING=\"3\" CELLPADDING=\"10\" BGCOLOR=\"#BDB5AC\">");

        for (int row = 0; row < TicTacToe.SIZE; row++)
        {
            html.append("<TR>");
            for (int col = 0; col < TicTacToe.SIZE; col++)
            {
                TicTacToe.Cell cell = g.getCell(row, col);
                if (cell == TicTacToe.Cell.CROSS)
                    html.append("<TD BGCOLOR=\"#FDECEA\">")
                        .append("<FONT FACE=\"Helvetica\" COLOR=\"#C0392B\" POINT-SIZE=\"22\"><B>X</B></FONT>")
                        .append("</TD>");
                else if (cell == TicTacToe.Cell.NOUGHT)
                    html.append("<TD BGCOLOR=\"#EAF4FB\">")
                        .append("<FONT FACE=\"Helvetica\" COLOR=\"#1A5276\" POINT-SIZE=\"22\"><B>O</B></FONT>")
                        .append("</TD>");
                else
                    html.append("<TD BGCOLOR=\"#FAF9F7\">")
                        .append("<FONT POINT-SIZE=\"22\"> </FONT>")
                        .append("</TD>");
            }
            html.append("</TR>");
        }

        // Stats footer: dark charcoal bar spanning all three columns
        html.append("<TR>")
            .append(String.format(
                "<TD COLSPAN=\"3\" BGCOLOR=\"#2C3E50\" CELLPADDING=\"4\">" +
                "<FONT FACE=\"Helvetica\" COLOR=\"#BDC3C7\" POINT-SIZE=\"9\">v = %d   q = %.3f</FONT></TD>",
                node.getVisits(), node.getValue()))
            .append("</TR>");

        html.append("</TABLE>>");
        return html.toString();
    }
}
