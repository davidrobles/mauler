package net.davidrobles.mauler.strategies.mcts.enhancements;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.core.Strategy;
import net.davidrobles.mauler.strategies.Evaluator;
import net.davidrobles.mauler.strategies.mcts.MCTS;
import net.davidrobles.mauler.strategies.mcts.MCTSNode;
import net.davidrobles.mauler.strategies.mcts.selection.SelectionPolicy;

public class MCTSPrior<GAME extends Game<GAME>> extends MCTS<GAME>
{
    private Evaluator<GAME> priorEF;
    private int initQVisits;

    public MCTSPrior(SelectionPolicy<GAME> selectionPolicy, Strategy<GAME> rolloutPolicy, Evaluator<GAME> priorEF, int initQVisits)
    {
        super(selectionPolicy, rolloutPolicy);
        this.priorEF = priorEF;
        this.initQVisits = initQVisits;
    }

    public MCTSPrior(SelectionPolicy<GAME> selectionPolicy, Strategy<GAME> rolloutPolicy, Evaluator<GAME> priorEF,
                     int initQVisits, int nSims)
    {
        super(selectionPolicy, rolloutPolicy, nSims);
        this.priorEF = priorEF;
        this.initQVisits = initQVisits;
    }

    @Override
    protected void expand(MCTSNode<GAME> node)
    {
        node.expand();
        node.setValue(priorEF.evaluate(node.getGame(), node.getGame().getCurPlayer()));
        node.setVisits(initQVisits);
    }

    ////////////
    // Object //
    ////////////

    @Override
    public String toString()
    {
        if (nSims > 0)
            return String.format("<MCTSPrior selectionPolicy: %s, rolloutPolicy: %s, priorEF: %s, initQVisits: %d, nSims: %d>",
                    selectionPolicy, rolloutPolicy, priorEF, initQVisits, nSims);
        else
            return String.format("<MCTSPrior selectionPolicy: %s, rolloutPolicy: %s, priorEF: %s, initQVisits: %d>",
                    selectionPolicy, rolloutPolicy, priorEF, initQVisits);
    }
}
