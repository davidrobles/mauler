package net.davidrobles.mauler.players.mcts.tree;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.players.mcts.MCTSNode;

/**
 * A tree policy makes a move by looking at the information
 * in the MCTS node.
 */
public interface TreePolicy<GAME extends Game<GAME>>
{
    // TODO: do i really need the player parameter?
    int move(MCTSNode<GAME> node, int player);
}
