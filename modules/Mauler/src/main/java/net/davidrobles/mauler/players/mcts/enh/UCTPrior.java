package net.davidrobles.mauler.players.mcts.enh;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.core.Strategy;
import net.davidrobles.mauler.players.RandPlayer;
import net.davidrobles.mauler.players.Evaluator;
import net.davidrobles.mauler.players.mcts.tree.UCB1;

/**
 * An Upper Confidence Bounds for Trees algorithm with prior knowledge
 * initialisation in the tree policy.
 */
public class UCTPrior<GAME extends Game<GAME>> extends MCTSPrior<GAME>
{
    public UCTPrior(double c, Evaluator<GAME> priorEF, int initQVisits)
    {
        super(new UCB1<GAME>(c), new RandPlayer<GAME>(), priorEF, initQVisits);
    }

    public UCTPrior(double c, Evaluator<GAME> priorEF, int initQVisits, int nSims)
    {
        super(new UCB1<GAME>(c), new RandPlayer<GAME>(), priorEF, initQVisits, nSims);
    }

    public UCTPrior(Strategy<GAME> defPolicy, double c, Evaluator<GAME> priorEF, int initQVisits)
    {
        super(new UCB1<GAME>(c), defPolicy, priorEF, initQVisits);
    }

    public UCTPrior(Strategy<GAME> defPolicy, double c, Evaluator<GAME> priorEF, int initQVisits, int nSims)
    {
        super(new UCB1<GAME>(c), defPolicy, priorEF, initQVisits, nSims);
    }
}
