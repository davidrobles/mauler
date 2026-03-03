package net.davidrobles.mauler.core.util;

import com.google.common.base.Stopwatch;
import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.core.Strategy;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for measuring game and strategy throughput.
 *
 * <p>Utility class — not instantiable.
 */
public final class SpeedTest
{
    private SpeedTest() {}

    /**
     * Measures how many complete games a strategy can play per second.
     *
     * @param game     the game to benchmark (reset and modified in place)
     * @param strategy the strategy under test
     * @param timeout  measurement duration in seconds
     * @return games per second
     */
    public static <GAME extends Game<GAME>> double playerSpeed(GAME game, Strategy<GAME> strategy, int timeout)
    {
        Stopwatch stopwatch = Stopwatch.createStarted();
        int nGames = 0;

        while (stopwatch.elapsed(TimeUnit.SECONDS) < timeout)
        {
            game.reset();
            while (!game.isOver())
                game.makeMove(strategy.move(game));
            nGames++;
        }

        return nGames / (double) timeout;
    }

    /**
     * Measures how many complete games can be played per second using random moves.
     *
     * @param game    the game to benchmark (reset and modified in place)
     * @param timeout measurement duration in seconds
     * @return games per second
     */
    public static double gameSpeed(Game<?> game, int timeout)
    {
        Random rand = new Random();
        Stopwatch stopwatch = Stopwatch.createStarted();
        int nGames = 0;

        while (stopwatch.elapsed(TimeUnit.SECONDS) < timeout)
        {
            game.reset();
            while (!game.isOver())
                game.makeMove(rand.nextInt(game.getNumMoves()));
            nGames++;
        }

        return nGames / (double) timeout;
    }
}
