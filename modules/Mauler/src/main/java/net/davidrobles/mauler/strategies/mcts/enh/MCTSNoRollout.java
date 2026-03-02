package net.davidrobles.mauler.strategies.mcts.enh;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.strategies.Evaluator;
import net.davidrobles.mauler.strategies.mcts.MCTS;
import net.davidrobles.mauler.strategies.mcts.MCTSNode;
import net.davidrobles.mauler.strategies.mcts.tree.TreePolicy;

public class MCTSNoRollout<GAME extends Game<GAME>> extends MCTS<GAME>
{
    private Evaluator<GAME> evalFunc;

    public MCTSNoRollout(TreePolicy<GAME> treePolicy, Evaluator<GAME> evalFunc)
    {
        super(treePolicy, null);
        this.evalFunc = evalFunc;
    }

    public MCTSNoRollout(TreePolicy<GAME> treePolicy, Evaluator<GAME> evalFunc, int nSims)
    {
        super(treePolicy, null, nSims);
        this.evalFunc = evalFunc;
    }

    //////////////////
    // AbstractMCTS //
    //////////////////

    @Override
    protected double rollout(MCTSNode<GAME> node, int player)
    {
        return evalFunc.evaluate(node.getGame(), player);
    }

    ////////////
    // Object //
    ////////////

    @Override
    public String toString()
    {
        if (nSims > 0)
            return String.format("<MCTSNoRollout treePolicy: %s, defaultPolicy: %s, evalFunc: %s, nSims: %d>",
                    treePolicy, defPolicy, evalFunc, nSims);
        else
            return String.format("<MCTSNoRollout treePolicy: %s, defaultPolicy: %s, evalFunc: %s>",
                    treePolicy, defPolicy, evalFunc);
    }
}
