package net.davidrobles.mauler.strategies.mcts;

import net.davidrobles.mauler.core.Game;

public interface MCTSObserver<GAME extends Game<GAME>>
{
    void simulationFinished(MCTSNode<GAME> game);
}
