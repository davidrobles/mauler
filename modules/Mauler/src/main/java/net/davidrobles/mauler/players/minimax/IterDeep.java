package net.davidrobles.mauler.players.minimax;

import net.davidrobles.mauler.core.Game;

/**
 * Iterative deepening for Minimax players.
 */
public class IterDeep
{
    public static <GAME extends Game<GAME>> int move(GAME game, DepthLimitedSearch<GAME> minimax, int timeout)
    {
        int bestMove = 0;

        if (game.getNumMoves() == 1)
            return bestMove;

        final int MAX_DEPTH = 2;
        int maxDepth = MAX_DEPTH;
        long timeDue = System.currentTimeMillis() + timeout;

        while (true)
        {
            int move = minimax.move(maxDepth++, game.copy());

            if (System.currentTimeMillis() < timeDue)
                bestMove = move;
            else {
//                if (maxDepth > 7)
//                    System.out.println(maxDepth);
                break;
            }
        }

        return bestMove;
    }
}
