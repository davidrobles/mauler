package net.davidrobles.mauler.core.util;

import com.google.common.base.Stopwatch;
import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.players.Player;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SpeedTest
{
    /**
     * Calculates the number of dr.mauler per second that a player can play in a game.
     * The timeout should be given in seconds.
     */
    public static <GAME extends Game<GAME>> double playerSpeed(GAME game, Player<GAME> player, int timeout)
    {
//        Stopwatch stopwatch = new Stopwatch();
//        stopwatch.start();
//        int nGames = 0;
//
//        while (stopwatch.elapsedTime(TimeUnit.SECONDS) < timeout)
//        {
//            game.reset();
//
//            while (!game.isOver())
//                game.makeMove(player.move(game));
//
//            nGames++;
//        }
//
//        return nGames / (double) timeout;

        return 0.0;
    }

    /**
     * Calculates the number of games per second that can be played in a game..
     * The timeout should be given in seconds.
     */
    public static double gameSpeed(Game<?> game, int timeout) {
        Random rand = new Random();
        Stopwatch stopWatch = Stopwatch.createStarted();
        int nGames = 0;
        while (stopWatch.elapsed(TimeUnit.SECONDS) < timeout) {
            game.reset();
            while (!game.isOver()) {
                game.makeMove(rand.nextInt(game.getNumMoves()));
            }
            nGames++;
        }
        return nGames / (double) timeout;
    }
}
