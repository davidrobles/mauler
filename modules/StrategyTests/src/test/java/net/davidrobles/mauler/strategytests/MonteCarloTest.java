package net.davidrobles.mauler.strategytests;

import net.davidrobles.mauler.core.Series;
import net.davidrobles.mauler.strategies.RandomStrategy;
import net.davidrobles.mauler.strategies.mc.MonteCarlo;
import net.davidrobles.mauler.tictactoe.TicTacToe;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Competence tests for the flat Monte Carlo strategy.
 *
 * <p>MonteCarlo is stochastic, so exact win counts cannot be asserted.
 * Instead these tests verify that the strategy wins significantly more often
 * than a random opponent — a threshold well below the true win rate, chosen
 * to be robust against natural variance while still catching regressions
 * (e.g., a broken implementation that always picks move 0).
 */
public class MonteCarloTest
{
    private static final int N_GAMES    = 1_000;
    private static final int N_SIMS     = 100;
    private static final double MIN_WIN_RATE = 0.75;

    @Test
    public void monteCarlo_winsOverwhelmingly_vsRandom()
    {
        Series<TicTacToe> series = new Series<>(TicTacToe::new, N_GAMES,
                List.of(new MonteCarlo<>(N_SIMS), new RandomStrategy<>()));
        series.setVerbose(false);
        series.run();

        double winRate = series.getWinsAvg(0);
        assertTrue(
                String.format("MonteCarlo(%d) win rate %.1f%% is below the %.0f%% threshold",
                        N_SIMS, winRate * 100, MIN_WIN_RATE * 100),
                winRate >= MIN_WIN_RATE);
    }
}
