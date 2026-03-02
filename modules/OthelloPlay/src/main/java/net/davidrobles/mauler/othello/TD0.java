package net.davidrobles.mauler.othello;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.players.PlayersUtil;

import java.util.Random;

// TODO: allow a termination condition

/**
 * TD(0) algorithm with Linear Function Approximation using self-play.
 */
public class TD0<GAME extends Game<GAME>>
{
    private int curEpisode;

    private GAME game;
    private LinearEF<GAME> evalFunc;
    private int episodes;
    private double learningRate;
    private double discountFactor;
    private double epsilon;

    private int decrementPeriod = -1;
    private double decrementFactor = 0.95;
    private boolean verbose = false;
    private Random rng;

    public TD0(GAME game, LinearEF<GAME> evalFunc, int episodes, double learningRate, double discountFactor,
               double epsilon)
    {
        this(game, evalFunc, episodes, learningRate, discountFactor, epsilon, new Random());
    }

    public TD0(GAME game, LinearEF<GAME> evalFunc, int episodes, double learningRate, double discountFactor,
               double epsilon, Random rng)
    {
        this.game = game.newInstance();
        this.evalFunc = evalFunc;
        this.episodes = episodes;
        this.learningRate = learningRate;
        this.discountFactor = discountFactor;
        this.epsilon = epsilon;
        this.rng = rng;
    }

    public void setDecrementPeriod(int decrementPeriod)
    {
        this.decrementPeriod = decrementPeriod;
    }

    public void setDecrementFactor(double decrementFactor)
    {
        this.decrementFactor = decrementFactor;
    }

    public int getCurEpisode()
    {
        return curEpisode;
    }

    public int getNumEpisodes()
    {
        return episodes;
    }

    public void setVerbose(boolean verbose)
    {
        this.verbose = verbose;
    }

    public void learn()
    {
        if (verbose)
        {
            System.out.printf("%15s:  %,d\n", "Num. episodes", episodes);
            System.out.printf("%15s:  %.3f\n", '\u03B1', learningRate);
            System.out.printf("%15s:  %.3f\n", '\u03B3', discountFactor);
            System.out.printf("%15s:  %.3f\n", '\u03B5', epsilon);
            System.out.println();
            System.out.print("Episodes: ");
        }

        while (curEpisode < episodes)
            iteration();

        if (verbose)
            System.out.println("\n");
    }

    /**
     * One iteration of the learning algorithm.
     */
    public void iteration()
    {
        simEpisode();

        if (curEpisode % decrementPeriod == 0)
        {
            if (verbose)
                System.out.printf("%,d ", curEpisode);

//            learningRate *= decrementFactor;
        }
    }

    /**
     * Simulates one episode. All the evaluation functions score
     * the value from the point of view of the first player.
     */
    private void simEpisode()
    {
        game.reset();

        while (!game.isOver())
        {
            GAME prevGame = game.copy();
            game.makeMove(PlayersUtil.epsilonGreedyMove(game, evalFunc, epsilon, rng));
            double current = evalFunc.eval(prevGame, 0);
            double next = evalFunc.eval(game, 0);
            double tdError = learningRate * (discountFactor * next - current) * (1 - current * current);
            evalFunc.updateWeights(prevGame, tdError);
        }

        curEpisode++;
    }
}
