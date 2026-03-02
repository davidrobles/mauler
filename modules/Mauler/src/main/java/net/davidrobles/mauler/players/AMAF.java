package net.davidrobles.mauler.players;

import net.davidrobles.mauler.core.IncrementalGame;

import java.util.Random;

public class AMAF<GAME extends IncrementalGame<GAME>>/* extends AbstractMC<GAME>*/ implements Player<GAME>
{
    private int nSims = -1;

    public AMAF(int nSims, Random rng) {
//        super(rng);
        this.nSims = nSims;
    }

    ////////////
    // Player //
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
//            outcomes[move] += utilFunc.eval(newGame, game.getCurPlayer());
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
//            outcomes[move] += utilFunc.eval(newGame, game.getCurPlayer());
//        }
//        return DRUtil.argMax(outcomes);
//    }
}
