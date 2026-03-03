package net.davidrobles.mauler.strategies.mcts.amaf;

import net.davidrobles.mauler.core.IncrementalGame;
import net.davidrobles.mauler.core.Strategy;

import java.util.Random;

public class AMAF<GAME extends IncrementalGame<GAME>>/* extends AbstractMC<GAME>*/ implements Strategy<GAME>
{
    private int nSims = -1;

    public AMAF(int nSims, Random rng) {
//        super(rng);
        this.nSims = nSims;
    }

    ////////////
    // Strategy //
    ////////////

    @Override
    public boolean isDeterministic()
    {
        return false;
    }

    @Override
    public int move(GAME game) {
        int numMoves = game.getNumMoves();
        if (numMoves == 1)
            return 0;
        int[] allCells = new int[game.getNumCells()];
        double[] outcomes = new double[numMoves];
        for (int i = 0; i < nSims; i++) {
            GAME newGame = game.copy();
            int move = i % numMoves;
            newGame.makeMove(move);
            while (!newGame.isOver()) {
//                move = rng.nextInt(newGame.getNumMoves());
//                allCells[newGame.getCellMoves()[move]];
                newGame.makeMove(move);
            }
//            outcomes[move] += utilFunc.evaluate(newGame, game.getCurPlayer());
        }
        return 0;
    }

    @Override
    public int move(GAME game, int timeout)
    {
        return 0;
    }

//    @Override
//    public int move(GAME game) {
//        int numMoves = game.getNumMoves();
//        if (numMoves == 1)
//            return 0;
//        double[] outcomes = new double[numMoves];
//        for (int i = 0; i < nSims; i++) {
//            GAME newGame = game.copy();
//            int move = i % numMoves;
//            newGame.makeMove(move);
//            while (!newGame.isOver())
//                newGame.makeMove(rand.nextInt(newGame.getNumMoves()));
//            outcomes[move] += utilFunc.evaluate(newGame, game.getCurPlayer());
//        }
//        return DRUtil.argMax(outcomes);
//    }
}
