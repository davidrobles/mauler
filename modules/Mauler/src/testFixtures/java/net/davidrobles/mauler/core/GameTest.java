package net.davidrobles.mauler.core;

import static org.junit.Assert.*;

import java.util.Random;
import org.junit.Test;

public abstract class GameTest<GAME extends Game<GAME>> {
    protected GAME game;
    protected Random rnd = new Random();

    @Test(timeout = 100)
    public void testGameFinishes() {
        while (!game.isOver()) game.makeMove(rnd.nextInt(game.getNumMoves()));
    }

    @Test
    public void testCopy() {
        assertEquals(game, game.copy());

        while (!game.isOver()) {
            game.makeMove(rnd.nextInt(game.getNumMoves()));
            assertEquals(game, game.copy());
        }
    }

    @Test
    public void testHashCode() {
        assertEquals(game.hashCode(), game.copy().hashCode());

        while (!game.isOver()) {
            game.makeMove(rnd.nextInt(game.getNumMoves()));
            assertEquals(game.hashCode(), game.copy().hashCode());
        }
    }

    @Test
    public void testNumLegalMovesEqualsListMoves() {
        while (!game.isOver()) {
            assertEquals(game.getNumMoves(), game.getMoves().size());
            game.makeMove(rnd.nextInt(game.getNumMoves()));
        }

        assertEquals(0, game.getNumMoves());
        assertEquals(0, game.getMoves().size());
    }

    //    @Test
    //    public void testOutcomesNotNAWhenGameIsOver()
    //    {
    //        while (!game.isOver()) {
    //            assertEquals(game.getNumMoves(), game.getMoves().length);
    //            game.makeMove(rnd.nextInt(game.getNumMoves()));
    //        }
    //
    //        GameResult[] outcomes = game.getOutcome();
    //
    //        for (int i = 0; i < game.getNumPlayers(); i++)
    //            assertFalse(outcomes[i] == GameResult.NA);
    //    }
}
