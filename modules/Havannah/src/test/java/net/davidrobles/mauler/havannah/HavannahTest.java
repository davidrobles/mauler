package net.davidrobles.mauler.havannah;

import static org.junit.Assert.*;

import java.util.Random;
import net.davidrobles.mauler.core.GameResult;
import org.junit.Before;
import org.junit.Test;

public class HavannahTest {
    private Havannah game;
    private Random rnd = new Random();

    @Before
    public void init() {
        game = new Havannah(4);
    }

    @Test
    public void testNumberOfPlayers() {
        assertEquals(2, game.getNumPlayers());
    }

    @Test
    public void testOutcomeEmptyWhenNotOver() {
        assertFalse(game.getOutcome().isPresent());
    }

    @Test(timeout = 2000)
    public void testOutcomeConsistentWithIsOver() {
        while (!game.isOver()) {
            assertFalse(game.getOutcome().isPresent());
            game.makeMove(rnd.nextInt(game.getNumMoves()));
        }
        assertTrue(game.getOutcome().isPresent());
    }

    @Test(timeout = 2000)
    public void testOutcomeHasValidResult() {
        while (!game.isOver()) game.makeMove(rnd.nextInt(game.getNumMoves()));
        GameResult[] outcome = game.getOutcome().get();
        assertEquals(2, outcome.length);
        boolean winLoss =
                (outcome[0] == GameResult.WIN && outcome[1] == GameResult.LOSS)
                        || (outcome[0] == GameResult.LOSS && outcome[1] == GameResult.WIN);
        boolean draw = outcome[0] == GameResult.DRAW && outcome[1] == GameResult.DRAW;
        assertTrue(winLoss || draw);
    }
}
