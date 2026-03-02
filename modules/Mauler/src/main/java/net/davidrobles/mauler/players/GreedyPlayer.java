package net.davidrobles.mauler.players;

import net.davidrobles.mauler.core.Game;

/**
 * A player that always picks the move leading to the highest-scored successor state.
 *
 * <p>On each turn, every legal move is tried on a copy of the game; the resulting
 * position is scored with the given {@link EvalFunc}, and the move with the highest
 * score is returned. Ties are broken in favour of the first move encountered.
 *
 * @param <GAME> the game type
 */
public class GreedyPlayer<GAME extends Game<GAME>> implements Player<GAME>
{
    private final EvalFunc<GAME> evalFunc;

    /**
     * @param evalFunc the evaluation function used to score successor positions
     */
    public GreedyPlayer(EvalFunc<GAME> evalFunc)
    {
        this.evalFunc = evalFunc;
    }

    // -------------------------------------------------------------------------
    // Player
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // Object
    // -------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return String.format("<Greedy evalFunc=%s>", evalFunc);
    }
}
