package net.davidrobles.mauler.players;

import net.davidrobles.mauler.core.Game;

import java.util.Random;

public class RandPlayer<GAME extends Game<GAME>> implements Player<GAME>
{
    private Random rng;

    public RandPlayer()
    {
        this.rng = new Random();
    }

    public RandPlayer(Random rng)
    {
        this.rng = rng;
    }

    ////////////
    // Player //
    ////////////

    @Override
    public boolean isDeterministic()
    {
        return false;
    }

    @Override
    public int move(GAME game)
    {
        return rng.nextInt(game.getNumMoves());
    }

    @Override
    public int move(GAME game, int timeout)
    {
        return move(game);
    }

    ////////////
    // Object //
    ////////////

    @Override
    public String toString()
    {
        return "<Random>";
    }
}
