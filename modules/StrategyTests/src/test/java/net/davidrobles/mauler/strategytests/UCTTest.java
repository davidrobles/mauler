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
 * still catching regressions. Tests are run as both player 0 and player 1.
 */
public class UCTTest
{
    private static final int    N_GAMES      = 1_000;
    private static final int    UCT_SIMS     = 1_000;
    private static final double MIN_WIN_RATE = 0.85;

    private static void assertWinsOverwhelmingly(int player)
    {
        UCT<TicTacToe> uct = new UCT<>(Math.sqrt(2), UCT_SIMS);
        Series<TicTacToe> series = new Series<>(TicTacToe::new, N_GAMES,
                player == 0
                        ? List.of(uct, new RandomStrategy<>())
                        : List.of(new RandomStrategy<>(), uct));
        series.setVerbose(false);
        series.run();

        double winRate = series.getWinsAvg(player);
        assertTrue(
                String.format("UCT(%d) win rate as player %d: %.1f%% < %.0f%% threshold",
                        UCT_SIMS, player, winRate * 100, MIN_WIN_RATE * 100),
                winRate >= MIN_WIN_RATE);
    }

    @Test
    public void uct_winsOverwhelmingly_vsRandom_asFirstPlayer()
    {
        assertWinsOverwhelmingly(0);
    }

    @Test
    public void uct_winsOverwhelmingly_vsRandom_asSecondPlayer()
    {
        assertWinsOverwhelmingly(1);
    }
}
