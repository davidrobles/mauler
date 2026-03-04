package net.davidrobles.mauler.experiments;

import net.davidrobles.mauler.strategies.mcts.GraphvizMCTSObserver;
import net.davidrobles.mauler.strategies.mcts.UCT;
import net.davidrobles.mauler.tictactoe.TicTacToe;

import java.io.File;

/**
 * Plays a single TicTacToe move with UCT (10 000 simulations) and writes the
 * resulting search tree to {@code tree.dot} in the current working directory.
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
        UCT<TicTacToe> uct = new UCT<TicTacToe>(Math.sqrt(2), 10_000);
        uct.addObserver(new GraphvizMCTSObserver<>(new File("tree.dot")));

        TicTacToe game = new TicTacToe();
        int move = uct.move(game);

        System.out.printf("UCT chose move %d on the initial TicTacToe position.%n", move);
        System.out.println("Search tree written to tree.dot");
    }
}
