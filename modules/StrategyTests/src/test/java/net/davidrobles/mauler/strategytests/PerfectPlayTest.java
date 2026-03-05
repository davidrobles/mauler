package net.davidrobles.mauler.strategytests;

import net.davidrobles.mauler.core.Series;
import net.davidrobles.mauler.core.Strategy;
import net.davidrobles.mauler.strategies.RandomStrategy;
import net.davidrobles.mauler.strategies.TerminalEvaluator;
import net.davidrobles.mauler.strategies.minimax.AlphaBeta;
import net.davidrobles.mauler.strategies.minimax.Minimax;
import net.davidrobles.mauler.strategies.minimax.Negamax;
import net.davidrobles.mauler.strategies.minimax.PVS;
import net.davidrobles.mauler.tictactoe.TicTacToe;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Competence tests for exact-search strategies (Minimax, Negamax, AlphaBeta, PVS).
 *
 * <p>With no depth limit, all four strategies perform a full-tree search on
 * TicTacToe and therefore play perfectly. Perfect play against any opponent
 * must never lose, and perfect play against perfect play must always draw.
 */
public class PerfectPlayTest
{
    private static final int N_GAMES = 1_000;

    private static Series<TicTacToe> seriesVsRandom(Strategy<TicTacToe> strategy)
    {
        Series<TicTacToe> series = new Series<>(TicTacToe::new, N_GAMES,
                List.of(strategy, new RandomStrategy<>()));
        series.setVerbose(false);
        return series;
    }

    @Test
    public void minimax_neverLoses_vsRandom()
    {
        Series<TicTacToe> series = seriesVsRandom(new Minimax<TicTacToe>(TerminalEvaluator.minimax()));
        series.run();
        assertEquals("Minimax lost a game it should not have lost", 0, series.getLosses(0));
    }

    @Test
    public void negamax_neverLoses_vsRandom()
    {
        Series<TicTacToe> series = seriesVsRandom(new Negamax<TicTacToe>(TerminalEvaluator.minimax()));
        series.run();
        assertEquals("Negamax lost a game it should not have lost", 0, series.getLosses(0));
    }

    @Test
    public void alphaBeta_neverLoses_vsRandom()
    {
        Series<TicTacToe> series = seriesVsRandom(new AlphaBeta<TicTacToe>(TerminalEvaluator.minimax()));
        series.run();
        assertEquals("AlphaBeta lost a game it should not have lost", 0, series.getLosses(0));
    }

    @Test
    public void pvs_neverLoses_vsRandom()
    {
        Series<TicTacToe> series = seriesVsRandom(new PVS<TicTacToe>(TerminalEvaluator.minimax()));
        series.run();
        assertEquals("PVS lost a game it should not have lost", 0, series.getLosses(0));
    }

    @Test
    public void twoPerfectPlayers_alwaysDraw()
    {
        Strategy<TicTacToe> alphaBeta = new AlphaBeta<TicTacToe>(TerminalEvaluator.minimax());
        Strategy<TicTacToe> minimax   = new Minimax<TicTacToe>(TerminalEvaluator.minimax());

        Series<TicTacToe> series = new Series<>(TicTacToe::new, N_GAMES,
                List.of(alphaBeta, minimax));
        series.setVerbose(false);
        series.run();

        assertEquals("AlphaBeta vs Minimax should always draw", N_GAMES, series.getDraws());
    }
}
