package net.davidrobles.mauler.players.mc;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.players.Player;
import net.davidrobles.mauler.players.UtilFunc;
import net.davidrobles.util.DRUtil;

import java.util.Random;

/**
 * Flat Monte Carlo algorithm.
 */
public class MonteCarlo<GAME extends Game<GAME>> implements Player<GAME>
{
    private int nSims = -1;
    protected UtilFunc<GAME> utilFunc = new UtilFunc<GAME>();
    protected Random rand;

    public MonteCarlo()
    {
        this(new Random());
    }

    public MonteCarlo(Random rand)
    {
        this.rand = rand;
    }

    public MonteCarlo(int nSims)
    {
        this(nSims, new Random());
    }

    public MonteCarlo(int nSims, Random rand)
    {
        if (nSims <= 0)
            throw new IllegalArgumentException();

        this.nSims = nSims;
        this.rand = rand;
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
    public int move(GAME game)
    {
        if (nSims <= 0)
            throw new IllegalArgumentException();

        int numMoves = game.getNumMoves();

        if (numMoves == 1)
            return 0;

        double[] outcomes = new double[numMoves];

        for (int i = 0; i < nSims; i++)
        {
            GAME newGame = game.copy();
            int move = i % numMoves;
            newGame.makeMove(move);

            while (!newGame.isOver())
                newGame.makeMove(rand.nextInt(newGame.getNumMoves()));

            outcomes[move] += utilFunc.eval(newGame, game.getCurPlayer());
        }

        return DRUtil.argMax(outcomes);
    }

    @Override
    public int move(GAME game, int timeout)
    {
        int numMoves = game.getNumMoves();

        if (numMoves == 1)
            return 0;

        double[] outcomes = new double[numMoves];
        long timeDue = System.currentTimeMillis() + timeout;

        while (System.currentTimeMillis() < timeDue)
        {
            for (int move = 0; move < numMoves; move++)
            {
                GAME newGame = game.copy();
                newGame.makeMove(move);

                while (!newGame.isOver())
                    newGame.makeMove(rand.nextInt(newGame.getNumMoves()));

                outcomes[move] += utilFunc.eval(newGame, game.getCurPlayer());
            }
        }

        return DRUtil.argMax(outcomes);
    }

    ////////////
    // Object //
    ////////////

    @Override
    public String toString()
    {
        if (nSims <= 0)
            return String.format("<MonteCarlo>", nSims);
        else
            return String.format("<MonteCarlo nSims: %d>", nSims);
    }
}
