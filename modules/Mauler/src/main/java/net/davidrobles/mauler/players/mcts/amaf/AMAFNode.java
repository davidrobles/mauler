package net.davidrobles.mauler.players.mcts.amaf;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.players.mcts.MCTSNode;

public class AMAFNode<GAME extends Game<GAME>> extends MCTSNode<GAME>
{
    private double amafValue = 0.0; // Q^(s, a)
    private int amafCount = 0; // N^(s): number of visits to this node

    public AMAFNode(GAME game) {
        super(game);
    }
}
