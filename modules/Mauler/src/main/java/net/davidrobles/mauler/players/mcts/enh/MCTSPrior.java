package net.davidrobles.mauler.players.mcts.enh;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.players.Player;
import net.davidrobles.mauler.players.EvalFunc;
import net.davidrobles.mauler.players.mcts.MCTS;
import net.davidrobles.mauler.players.mcts.MCTSNode;
import net.davidrobles.mauler.players.mcts.tree.TreePolicy;

public class MCTSPrior<GAME extends Game<GAME>> extends MCTS<GAME>
{
    private EvalFunc<GAME> priorEF;
    private int initQVisits;

    public MCTSPrior(TreePolicy<GAME> treePolicy, Player<GAME> defPolicy, EvalFunc<GAME> priorEF, int initQVisits)
    {
        super(treePolicy, defPolicy);
        this.priorEF = priorEF;
        this.initQVisits = initQVisits;
    }

    public MCTSPrior(TreePolicy<GAME> treePolicy, Player<GAME> defPolicy, EvalFunc<GAME> priorEF,
                     int initQVisits, int nSims)
    {
        super(treePolicy, defPolicy, nSims);
        this.priorEF = priorEF;
        this.initQVisits = initQVisits;
    }

    @Override
    protected void newNode(MCTSNode<GAME> node, int player)
    {
        node.init();
        node.setValue(priorEF.eval(node.getGame(), player));
        node.setCount(initQVisits);
    }

    ////////////
    // Object //
    ////////////

    @Override
    public String toString()
    {
        if (nSims > 0)
            return String.format("<MCTSPrior treePolicy: %s, defPolicy: %s, priorEF: %s, initQVisits: %d, nSims: %d>",
                    treePolicy, defPolicy, priorEF, initQVisits, nSims);
        else
            return String.format("<MCTSPrior treePolicy: %s, defPolicy: %s, priorEF: %s, initQVisits: %d>",
                    treePolicy, defPolicy, priorEF, initQVisits);
    }
}
