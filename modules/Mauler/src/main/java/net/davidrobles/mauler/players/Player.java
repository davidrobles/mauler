package net.davidrobles.mauler.players;

import net.davidrobles.mauler.core.Game;

public interface Player<GAME extends Game<GAME>>
{
    boolean isDeterministic();
    int move(GAME game);
    int move(GAME game, int timeout);
}
