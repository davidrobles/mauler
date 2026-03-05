package net.davidrobles.mauler.strategies.greedy;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.core.Strategy;
import net.davidrobles.mauler.strategies.Evaluator;
import net.davidrobles.mauler.strategies.StrategiesUtil;

/**
 * A player that always picks the move leading to the highest-scored successor state.
 *
 * <p>On each turn, every legal move is tried on a copy of the game; the resulting position is
 * scored with the given {@link Evaluator}, and the move with the highest score is returned. Ties
 * are broken in favor of the first move encountered.
 *
 * @param <GAME> the game type
 */
public class GreedyStrategy<GAME extends Game<GAME>> implements Strategy<GAME> {
    private final Evaluator<GAME> evalFunc;

    /**
     * @param evalFunc the evaluation function used to score successor positions
     */
    public GreedyStrategy(Evaluator<GAME> evalFunc) {
        this.evalFunc = evalFunc;
    }

    // -------------------------------------------------------------------------
    // Strategy
    // -------------------------------------------------------------------------

    @Override
    public boolean isDeterministic() {
        return true;
    }

    @Override
    public int move(GAME game) {
        return StrategiesUtil.greedyMove(game, evalFunc);
    }

    // -------------------------------------------------------------------------
    // Object
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return String.format("<Greedy evalFunc=%s>", evalFunc);
    }
}
