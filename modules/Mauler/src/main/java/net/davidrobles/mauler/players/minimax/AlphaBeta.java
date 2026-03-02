package net.davidrobles.mauler.players.minimax;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.players.EvalFunc;

public class AlphaBeta<GAME extends Game<GAME>> implements DepthLimitedSearch<GAME>
{
    private EvalFunc<GAME> evalFunc;
    private final int maxDepth;

    public AlphaBeta(EvalFunc<GAME> evalFunc)
    {
        this(evalFunc, Integer.MAX_VALUE);
    }

    public AlphaBeta(EvalFunc<GAME> evalFunc, int maxDepth)
    {
        this.evalFunc = evalFunc;
        this.maxDepth = maxDepth;
    }

    public MoveScore abNegamax(GAME game, int maxDepth, int curDepth, int alpha, int beta)
    {
        if (game.isOver() || curDepth == maxDepth)
            return new MoveScore(evalFunc.eval(game, game.getCurPlayer()), -1);

        int bestMove = -1;
        double bestScore = Integer.MIN_VALUE;
        assert game.getNumMoves() > 0 : game;

        for (int move = 0; move < game.getNumMoves(); move++)
        {
            GAME newGame = game.copy();
            newGame.makeMove(move);
            MoveScore curMoveScore = abNegamax(newGame, maxDepth,  curDepth + 1, -beta, (int) -Math.max(alpha, bestScore));
            double curScore = -curMoveScore.getScore();

            if (curScore > bestScore)
            {
                bestScore = curScore;
                bestMove = move;

                if (bestScore >= beta)
                    return new MoveScore(bestScore, bestMove);
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
        return abNegamax(game, maxDepth, 0, Integer.MIN_VALUE, Integer.MAX_VALUE).getMove();
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
        return abNegamax(game, maxDepth, 0, Integer.MIN_VALUE, Integer.MAX_VALUE).getMove();
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
        return String.format("<Alpha-Beta maxDepth: %d, evalFunc: %s>", maxDepth, evalFunc);
    }
}
