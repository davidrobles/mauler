package net.davidrobles.mauler.strategytests;

import net.davidrobles.mauler.core.Series;
import net.davidrobles.mauler.strategies.RandomStrategy;
import net.davidrobles.mauler.strategies.mcts.UCT;
import net.davidrobles.mauler.tictactoe.TicTacToe;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Competence tests for UCT (Upper Confidence Bounds for Trees).
 *
 * <p>UCT is stochastic, so exact win counts cannot be asserted. The threshold
 * is conservative — well below the true win rate — to avoid flakiness while
 * still catching regressions (e.g. a broken implementation that always picks
 * move 0 would win ~50% vs random, well below the threshold).
 *
 * <p>Note: UCT vs MonteCarlo comparisons are not included here because both
 * strategies reach near-perfect play on TicTacToe with reasonable simulation
 * counts, making the head-to-head result essentially a coin flip on this game.
 */
public class UCTTest
{
    private static final int    N_GAMES          = 1_000;
    private static final int    UCT_SIMS         = 1_000;
    private static final double MIN_WIN_RATE     = 0.85;

    @Test
    public void uct_winsOverwhelmingly_vsRandom()
    {
        Series<TicTacToe> series = new Series<>(TicTacToe::new, N_GAMES,
                List.of(new UCT<>(Math.sqrt(2), UCT_SIMS), new RandomStrategy<>()));
        series.setVerbose(false);
        series.run();

        double winRate = series.getWinsAvg(0);
        assertTrue(
                String.format("UCT(%d) win rate %.1f%% is below the %.0f%% threshold",
                        UCT_SIMS, winRate * 100, MIN_WIN_RATE * 100),
                winRate >= MIN_WIN_RATE);
    }
}
