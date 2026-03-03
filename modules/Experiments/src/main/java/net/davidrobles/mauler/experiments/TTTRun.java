package net.davidrobles.mauler.experiments;

import net.davidrobles.mauler.core.Series;
import net.davidrobles.mauler.core.Strategy;
import net.davidrobles.mauler.strategies.RandomStrategy;
import net.davidrobles.mauler.strategies.TerminalEvaluator;
import net.davidrobles.mauler.strategies.minimax.AlphaBeta;
import net.davidrobles.mauler.tictactoe.TicTacToe;

import java.util.List;

public class TTTRun
{
    public static void main(String[] args)
    {
        Strategy<TicTacToe> p1 = new AlphaBeta<TicTacToe>(TerminalEvaluator.minimax());
        Strategy<TicTacToe> p2 = new RandomStrategy<>();

        Series<TicTacToe> series = new Series<>(TicTacToe::new, 1000, List.of(p2, p1));
        series.run();
    }
}
