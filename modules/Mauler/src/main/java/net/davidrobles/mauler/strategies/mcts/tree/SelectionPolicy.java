package net.davidrobles.mauler.strategies.mcts.tree;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.strategies.mcts.MCTSNode;

/**
 * A selection policy chooses which child to descend into during the tree
 * traversal phase of MCTS. Scores are always from the perspective of the
 * current player at the node, so the policy always maximises.
 */
public interface SelectionPolicy<GAME extends Game<GAME>>
{
    int move(MCTSNode<GAME> node);
}
