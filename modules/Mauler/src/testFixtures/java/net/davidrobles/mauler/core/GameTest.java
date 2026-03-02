package net.davidrobles.mauler.core;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public abstract class GameTest<GAME extends Game<GAME>>
{
    protected GAME game;
    protected Random rnd = new Random();

    @Test(timeout = 100)
    public void testGameFinishes()
    {
        while (!game.isOver())
            game.makeMove(rnd.nextInt(game.getNumMoves()));
    }

    @Test
    public void testCopy()
    {
        assertEquals(game, game.copy());

        while (!game.isOver()) {
            game.makeMove(rnd.nextInt(game.getNumMoves()));
            assertEquals(game, game.copy());
        }
    }

    @Test
    public void testNumLegalMovesEqualsListMoves()
    {
        while (!game.isOver()) {
            assertEquals(game.getNumMoves(), game.getMoves().length);
            game.makeMove(rnd.nextInt(game.getNumMoves()));
        }

        assertEquals(0, game.getNumMoves());
        assertEquals(0, game.getMoves().length);
    }

//    @Test
//    public void testOutcomesNotNAWhenGameIsOver()
//    {
//        while (!game.isOver()) {
//            assertEquals(game.getNumMoves(), game.getMoves().length);
//            game.makeMove(rnd.nextInt(game.getNumMoves()));
//        }
//
//        Outcome[] outcomes = game.getOutcome();
//
//        for (int i = 0; i < game.getNumPlayers(); i++)
//            assertFalse(outcomes[i] == Outcome.NA);
//    }
}
