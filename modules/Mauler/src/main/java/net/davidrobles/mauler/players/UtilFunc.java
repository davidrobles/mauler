package net.davidrobles.mauler.players;

import net.davidrobles.mauler.core.Game;

/**
 * Scores terminal game positions only.
 */
public class UtilFunc<GAME extends Game<GAME>> implements EvalFunc<GAME>
{
    private final double win, loss, draw;

    public UtilFunc()
    {
        this(1.0, -1.0, 0.0);
    }

    public UtilFunc(double win, double loss, double draw)
    {
        this.win = win;
        this.loss = loss;
        this.draw = draw;
    }

    @Override
    public double eval(GAME game, int player)
    {
        if (game.isOver())
        {
            switch (game.getOutcome()[player])
            {
                case WIN:  return win;
                case LOSS: return loss;
                case DRAW: return draw;
            }
        }

        throw new Error("Utility Function called before the end of the game.");
    }
}
