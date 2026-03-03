package net.davidrobles.mauler.strategies.mcts.enhancements;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.strategies.Evaluator;
import net.davidrobles.mauler.strategies.mcts.tree.UCB1;

/**
 * An Upper Confidence Bounds for Trees algorithm that replaces
 * the default policy with an evaluation function.
 */
public class UCTNoRollout<GAME extends Game<GAME>> extends MCTSNoRollout<GAME>
{
    public UCTNoRollout(double c, Evaluator<GAME> evalFunc)
    {
        super(new UCB1<GAME>(c), evalFunc);
    }

    public UCTNoRollout(double c, Evaluator<GAME> evalFunc, int nSims)
    {
        super(new UCB1<GAME>(c), evalFunc, nSims);
    }
}
