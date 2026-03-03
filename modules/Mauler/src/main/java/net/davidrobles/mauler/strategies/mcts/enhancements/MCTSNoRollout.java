package net.davidrobles.mauler.strategies.mcts.enhancements;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.strategies.Evaluator;
import net.davidrobles.mauler.strategies.mcts.MCTS;
import net.davidrobles.mauler.strategies.mcts.MCTSNode;
import net.davidrobles.mauler.strategies.mcts.tree.SelectionPolicy;

public class MCTSNoRollout<GAME extends Game<GAME>> extends MCTS<GAME>
{
    private Evaluator<GAME> evalFunc;

    public MCTSNoRollout(SelectionPolicy<GAME> treePolicy, Evaluator<GAME> evalFunc)
    {
        super(treePolicy, null);
        this.evalFunc = evalFunc;
    }

    public MCTSNoRollout(SelectionPolicy<GAME> treePolicy, Evaluator<GAME> evalFunc, int nSims)
    {
        super(treePolicy, null, nSims);
        this.evalFunc = evalFunc;
    }

    //////////////////
    // AbstractMCTS //
    //////////////////

    @Override
    protected double rollout(MCTSNode<GAME> node)
    {
        return evalFunc.evaluate(node.getGame(), node.getGame().getCurPlayer());
    }

    ////////////
    // Object //
    ////////////

    @Override
    public String toString()
    {
        if (nSims > 0)
            return String.format("<MCTSNoRollout selectionPolicy: %s, evalFunc: %s, nSims: %d>",
                    selectionPolicy, evalFunc, nSims);
        else
            return String.format("<MCTSNoRollout selectionPolicy: %s, evalFunc: %s>",
                    selectionPolicy, evalFunc);
    }
}
