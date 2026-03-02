package net.davidrobles.mauler.players;

import net.davidrobles.mauler.core.Game;

import java.util.Random;

/**
 * Player utilities.
 */
public class PlayersUtil
{
    public static <GAME extends Game<GAME>> int epsilonGreedyMove(GAME game, EvalFunc<GAME> evalFunc,
                                                                  double epsilon, Random rng)
    {
        if (rng.nextDouble() > epsilon)
            return greedyMove(game, evalFunc);  // greedy

        return rng.nextInt(game.getNumMoves()); // uniform random
    }

    public static <GAME extends Game<GAME>> int epsilonGreedyMove(GAME game, EvalFunc<GAME> evalFunc,
                                                                   double epsilon)
    {
        return epsilonGreedyMove(game, evalFunc, epsilon, new Random());
    }

    public static <GAME extends Game<GAME>> int greedyMove(GAME game, EvalFunc<GAME> evalFunc)
    {
        if (game.getNumMoves() == 1)
            return 0;

        double bestValue = Double.NEGATIVE_INFINITY;
        int bestMove = -1;

        for (int move = 0; move < game.getNumMoves(); move++)
        {
            GAME gameCopy = game.copy();
            gameCopy.makeMove(move);
            double value = evalFunc.eval(gameCopy, game.getCurPlayer());

            if (value > bestValue)
            {
                bestValue = value;
                bestMove = move;
            }
        }

        return bestMove;
    }

    public static <GAME extends Game<GAME>> double utility(GAME game, int player, double win, double loss, double draw)
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
