package net.davidrobles.mauler.players;

import net.davidrobles.mauler.core.Game;

/**
 * A player that takes greedy actions by scoring the next states
 * from the given evaluation function.
 */
public class GreedyPlayer<GAME extends Game<GAME>> implements Player<GAME>
{
    private EvalFunc<GAME> evalFunc;

    public GreedyPlayer(EvalFunc<GAME> evalFunc)
    {
        this.evalFunc = evalFunc;
    }

    ////////////
    // Player //
    ////////////

    @Override
    public boolean isDeterministic()
    {
        return true;
    }

    @Override
    public int move(GAME game)
    {
        return PlayersUtil.greedyMove(game, evalFunc);
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
        return String.format("<Greedy evalFunc: %s>", evalFunc);
    }
}
