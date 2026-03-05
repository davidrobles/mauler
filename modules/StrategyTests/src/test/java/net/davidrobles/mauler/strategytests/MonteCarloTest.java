package net.davidrobles.mauler.strategytests;

import static org.junit.Assert.assertTrue;

import java.util.List;
import net.davidrobles.mauler.core.Series;
import net.davidrobles.mauler.strategies.RandomStrategy;
import net.davidrobles.mauler.strategies.mc.MonteCarlo;
import net.davidrobles.mauler.tictactoe.TicTacToe;
import org.junit.Test;

/**
 * Competence tests for the flat Monte Carlo strategy.
 *
 * <p>MonteCarlo is stochastic, so exact win counts cannot be asserted. Instead these tests verify
 * that the strategy wins significantly more often than a random opponent as both player 0 and
 * player 1 — a threshold well below the true win rate, chosen to be robust against natural variance
 * while still catching regressions.
 */
public class MonteCarloTest {
    private static final int N_GAMES = 1_000;
    private static final int N_SIMS = 100;
    private static final double MIN_WIN_RATE = 0.75;

    private static void assertWinsOverwhelmingly(int player) {
        MonteCarlo<TicTacToe> mc = new MonteCarlo<>(N_SIMS);
        Series<TicTacToe> series =
                new Series<>(
                        TicTacToe::new,
                        N_GAMES,
                        player == 0
                                ? List.of(mc, new RandomStrategy<>())
                                : List.of(new RandomStrategy<>(), mc));
        series.setVerbose(false);
        series.run();

        double winRate = series.getWinsAvg(player);
        assertTrue(
                String.format(
                        "MonteCarlo(%d) win rate as player %d: %.1f%% < %.0f%% threshold",
                        N_SIMS, player, winRate * 100, MIN_WIN_RATE * 100),
                winRate >= MIN_WIN_RATE);
    }

    @Test
    public void monteCarlo_winsOverwhelmingly_vsRandom_asFirstPlayer() {
        assertWinsOverwhelmingly(0);
    }

    @Test
    public void monteCarlo_winsOverwhelmingly_vsRandom_asSecondPlayer() {
        assertWinsOverwhelmingly(1);
    }
}
