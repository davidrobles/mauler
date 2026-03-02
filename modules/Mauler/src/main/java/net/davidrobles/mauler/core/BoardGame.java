package net.davidrobles.mauler.core;

public interface BoardGame<GAME extends Game<GAME>> extends Game<GAME>
{
    int[] getCellMoves();
    int getNumCells();
}
