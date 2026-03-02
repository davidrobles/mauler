package net.davidrobles.mauler.players.minimax;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.players.EvalFunc;

/**
 * Negamax player.
 */
public class Negamax<GAME extends Game<GAME>> implements DepthLimitedSearch<GAME>
{
    private EvalFunc<GAME> evalFunc;
    private final int maxDepth;

    /** Iterative Deepening stuff */
    private static final int initialDepth = 4;
    private static final int stepSize = 2;

    public Negamax(EvalFunc<GAME> evalFunc)
    {
        this(evalFunc, Integer.MAX_VALUE);
    }

    public Negamax(EvalFunc<GAME> evalFunc, int maxDepth)
    {
        this.evalFunc = evalFunc;
        this.maxDepth = maxDepth;
    }

    private MoveScore negamax(GAME game, int currentDepth, int maxDepth)
    {
        if (game.isOver() || currentDepth == maxDepth)
            return new MoveScore(evalFunc.eval(game, game.getCurPlayer()), -1);

        int bestMove = -1;
        double bestScore = Integer.MIN_VALUE;

        for (int move = 0; move < game.getNumMoves(); move++)
        {
            GAME newGame = game.copy();
            newGame.makeMove(move);
            MoveScore recursedMoveScore = negamax(newGame, currentDepth + 1, maxDepth);
            double currentScore = -recursedMoveScore.getScore();

            if (currentScore > bestScore)
            {
                bestScore = currentScore;
                bestMove = move;
            }
        }

        return new MoveScore(bestScore, bestMove);
    }

    ///////////////////
    // MinimaxPlayer //
    ///////////////////

    @Override
    public int move(int maxDepth, GAME game)
    {
        return negamax(game, 0, maxDepth).getMove();
    }

    ////////////
    // Player //
    ////////////

    @Override
    public boolean isDeterministic()
    {
        return true;
    }

    @Override
    public int move(GAME game)
    {
        return negamax(game, 0, maxDepth).getMove();
    }

    @Override
    public int move(GAME game, int timeout)
    {
        return IterDeep.move(game, this, timeout);
    }

    ////////////
    // Object //
    ////////////

    @Override
    public String toString()
    {
        return String.format("<Negamax evalFunc: %s>", evalFunc);
    }
}
