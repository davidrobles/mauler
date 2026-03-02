package net.davidrobles.mauler.othello.ef;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.players.EvalFunc;

import java.util.Random;

public class RandomEF<GAME extends Game<GAME>> implements EvalFunc<GAME>
{
    private Random rng = new Random();

    @Override
    public double eval(GAME game, int player)
    {
        return rng.nextDouble() * 2 - 1;
    }
}
