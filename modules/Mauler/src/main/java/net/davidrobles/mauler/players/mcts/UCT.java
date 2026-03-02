package net.davidrobles.mauler.players.mcts;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.players.Player;
import net.davidrobles.mauler.players.RandPlayer;
import net.davidrobles.mauler.players.mcts.tree.UCB1;

/**
 * Upper Confidence Bounds for Trees. It uses the UCB1 tree policy
 * and the uniformly random default policy.
 */
public class UCT<GAME extends Game<GAME>> extends MCTS<GAME>
{
    public UCT(double c)
    {
        super(new UCB1<GAME>(c), new RandPlayer<GAME>());
    }

    public UCT(double c, int nSims)
    {
        super(new UCB1<GAME>(c), new RandPlayer<GAME>(), nSims);
        this.nSims = nSims;
    }

    public UCT(Player<GAME> defPolicy, double c)
    {
        super(new UCB1<GAME>(c), defPolicy);
    }

    public UCT(Player<GAME> defPolicy, double c, int nSims)
    {
        super(new UCB1<GAME>(c), defPolicy, nSims);
        this.nSims = nSims;
    }
}
