package net.davidrobles.thesis.othello.ch4;

import net.davidrobles.mauler.core.Strategy;
import net.davidrobles.mauler.core.util.SpeedTest;
import net.davidrobles.mauler.othello.Othello;
import net.davidrobles.mauler.players.EpsilonGreedy;
import net.davidrobles.mauler.players.Evaluator;

import java.util.HashMap;
import java.util.Map;

import static net.davidrobles.thesis.othello.ch4.OthelloVF.*;

public class NTSSpeedTests
{
    static void RunAll()
    {
        Othello othello = new Othello();
        int timeoutInSecs = 10;
        double epsilon = 0.1;

        Map<String, Evaluator<Othello>> players = new HashMap<String, Evaluator<Othello>>();
        players.put("DR-WPC", WPC_SYM);
        players.put("FIRST-NTS", NTS_RND);
        players.put("RS-NTS", NTS_RS);
        players.put("LOG-NTS", NTS_LOG);
        players.put("EVO-NTS", NTS_EVO);

        for (Map.Entry<String, Evaluator<Othello>> entry : players.entrySet())
        {
            Strategy<Othello> player = new EpsilonGreedy<Othello>(entry.getValue(), epsilon);
            double ntsGames = SpeedTest.playerSpeed(othello, player, timeoutInSecs);
            System.out.println(entry.getValue());
            System.out.format("%s %.1f mauler per second.\n", entry.getKey(), ntsGames);
            System.out.println("-------------------");
        }
    }

    public static void main(String[] args)
    {
        RunAll();
    }
}
