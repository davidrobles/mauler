package net.davidrobles.mauler.players.mcts.enh;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.players.Player;
import net.davidrobles.mauler.players.RandPlayer;
import net.davidrobles.mauler.players.EvalFunc;
import net.davidrobles.mauler.players.mcts.tree.UCB1;

/**
 * An Upper Confidence Bounds for Trees algorithm with prior knowledge
 * initialisation in the tree policy.
 */
public class UCTPrior<GAME extends Game<GAME>> extends MCTSPrior<GAME>
{
    public UCTPrior(double c, EvalFunc<GAME> priorEF, int initQVisits)
    {
        super(new UCB1<GAME>(c), new RandPlayer<GAME>(), priorEF, initQVisits);
    }

    public UCTPrior(double c, EvalFunc<GAME> priorEF, int initQVisits, int nSims)
    {
        super(new UCB1<GAME>(c), new RandPlayer<GAME>(), priorEF, initQVisits, nSims);
    }

    public UCTPrior(Player<GAME> defPolicy, double c, EvalFunc<GAME> priorEF, int initQVisits)
    {
        super(new UCB1<GAME>(c), defPolicy, priorEF, initQVisits);
    }

    public UCTPrior(Player<GAME> defPolicy, double c, EvalFunc<GAME> priorEF, int initQVisits, int nSims)
    {
        super(new UCB1<GAME>(c), defPolicy, priorEF, initQVisits, nSims);
    }
}
