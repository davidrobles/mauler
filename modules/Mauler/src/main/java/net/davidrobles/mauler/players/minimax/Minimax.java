package net.davidrobles.mauler.players.minimax;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.players.EvalFunc;

public class Minimax<GAME extends Game<GAME>> implements DepthLimitedSearch<GAME>
{
    private EvalFunc<GAME> evalFunc;
    private final int maxDepth;

    /** Iterative Deepening stuff */
    private static final int initialDepth = 4;
    private static final int stepSize = 2;

    public Minimax(EvalFunc<GAME> evalFunc)
    {
        this(evalFunc, Integer.MAX_VALUE);
    }

    public Minimax(EvalFunc<GAME> evalFunc, int maxDepth)
    {
        this.evalFunc = evalFunc;
        this.maxDepth = maxDepth;
    }

    public MoveScore minimax(GAME game, int player, int currentDepth, int maxDepth)
    {
        if (game.isOver() || currentDepth == maxDepth)
            return new MoveScore(evalFunc.eval(game, player), -1);

        int bestMove = -1;
        double bestScore = game.getCurPlayer() == player ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int move = 0; move < game.getNumMoves(); move++)
        {
            GAME newGame = game.copy();
            newGame.makeMove(move);
            MoveScore currentMoveScore = minimax(newGame, player, currentDepth + 1, maxDepth);

            if (game.getCurPlayer() == player) {
                if (currentMoveScore.getScore() > bestScore) {
                    bestScore = currentMoveScore.getScore();
                    bestMove = move;
                }
            } else if (currentMoveScore.getScore() < bestScore) {
                bestScore = currentMoveScore.getScore();
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
        return minimax(game, game.getCurPlayer(), 0, maxDepth).getMove();
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
        return minimax(game, game.getCurPlayer(), 0, maxDepth).getMove();
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
        return String.format("<Minimax evalFunc: %s>", evalFunc);
    }
}