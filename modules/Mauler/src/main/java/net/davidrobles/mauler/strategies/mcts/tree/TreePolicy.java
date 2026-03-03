package net.davidrobles.mauler.strategies.mcts.tree;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.strategies.mcts.MCTSNode;

/**
 * A tree policy selects a move from a node's children based on accumulated
 * visit and value statistics. Scores are always from the perspective of the
 * current player at the node, so the policy always maximises.
 */
public interface TreePolicy<GAME extends Game<GAME>>
{
    int move(MCTSNode<GAME> node);
}
