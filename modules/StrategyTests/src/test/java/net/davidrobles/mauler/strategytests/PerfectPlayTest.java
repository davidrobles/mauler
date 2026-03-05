package net.davidrobles.mauler.strategytests;

import static org.junit.Assert.assertEquals;

import java.util.List;
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

/**
 * Competence tests for exact-search strategies (Minimax, Negamax, AlphaBeta, PVS).
 *
 * <p>With no depth limit, all four strategies perform a full-tree search on TicTacToe and therefore
 * play perfectly. Perfect play against any opponent must never lose as either player, and perfect
 * play against perfect play must always draw.
 */
public class PerfectPlayTest {
    private static final int N_GAMES = 1_000;

    /**
     * Asserts that {@code strategy} never loses against a random opponent in {@code N_GAMES} games
     * as player 0, then again as player 1.
     */
    private static void assertNeverLoses(Strategy<TicTacToe> strategy, String name) {
        Series<TicTacToe> asP0 =
                new Series<>(TicTacToe::new, N_GAMES, List.of(strategy, new RandomStrategy<>()));
        asP0.setVerbose(false);
        asP0.run();
        assertEquals(name + " lost as player 0", 0, asP0.getLosses(0));

        Series<TicTacToe> asP1 =
                new Series<>(TicTacToe::new, N_GAMES, List.of(new RandomStrategy<>(), strategy));
        asP1.setVerbose(false);
        asP1.run();
        assertEquals(name + " lost as player 1", 0, asP1.getLosses(1));
    }

    @Test
    public void minimax_neverLoses_vsRandom() {
        assertNeverLoses(new Minimax<TicTacToe>(TerminalEvaluator.minimax()), "Minimax");
    }

    @Test
    public void negamax_neverLoses_vsRandom() {
        assertNeverLoses(new Negamax<TicTacToe>(TerminalEvaluator.minimax()), "Negamax");
    }

    @Test
    public void alphaBeta_neverLoses_vsRandom() {
        assertNeverLoses(new AlphaBeta<TicTacToe>(TerminalEvaluator.minimax()), "AlphaBeta");
    }

    @Test
    public void pvs_neverLoses_vsRandom() {
        assertNeverLoses(new PVS<TicTacToe>(TerminalEvaluator.minimax()), "PVS");
    }

    @Test
    public void twoPerfectPlayers_alwaysDraw() {
        Strategy<TicTacToe> alphaBeta = new AlphaBeta<TicTacToe>(TerminalEvaluator.minimax());
        Strategy<TicTacToe> minimax = new Minimax<TicTacToe>(TerminalEvaluator.minimax());

        Series<TicTacToe> series =
                new Series<>(TicTacToe::new, N_GAMES, List.of(alphaBeta, minimax));
        series.setVerbose(false);
        series.run();

        assertEquals("AlphaBeta vs Minimax should always draw", N_GAMES, series.getDraws());
    }
}
