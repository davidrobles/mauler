package net.davidrobles.mauler.strategies.mcts;

import net.davidrobles.mauler.core.Game;

/**
 * Observer notified at the end of an MCTS search.
 *
 * <p>Implementations receive the root node of the completed search tree, giving access to the full
 * tree structure and all accumulated statistics (visit counts, mean values, children).
 *
 * @param <GAME> the game type
 * @see GraphvizMCTSObserver
 */
@FunctionalInterface
public interface MCTSObserver<GAME extends Game<GAME>> {
    /**
     * Called when an MCTS search has finished.
     *
     * @param root the root node of the search tree after all simulations
     */
    void searchFinished(MCTSNode<GAME> root);
}
