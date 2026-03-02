package net.davidrobles.mauler.core.util;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.players.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GamesUtil
{
    private static final Random RNG = new Random();

    private GamesUtil() { }

    public static void makeRandomMove(Game<?> game)
    {
        game.makeMove(RNG.nextInt(game.getNumMoves()));
    }

    public double evaluate(double[] weights, double[] features)
    {
        double value = 0;

        for (int i = 0; i < weights.length; i++)
            value += weights[i] * features[i];

        return value;
    }

    public static <GAME extends Game<GAME>> boolean advanceGame(GAME game, int numMoves) {
        int i;
        for (i = 0; i < numMoves && !game.isOver(); i++) {
            int rndMove = RNG.nextInt(game.getNumMoves());
            game.makeMove(rndMove);
        }
        return i == numMoves;
    }

    public static <GAME extends Game<GAME>> void playGame(GAME game, List<Player<GAME>> players, boolean alternating,
                                                          boolean copyGame, boolean verbose, int starter)
    {
        if (verbose)
            System.out.println(game + "\n");

        if (verbose)
            for (int i = 0; i < players.size(); i++)
                System.out.format("Player %d: %s\n", i, players.get(i));

        if (verbose)
            System.out.println("\n" + game);

        game.reset();

        while (!game.isOver())
        {
            int currentPlayer = game.getCurPlayer();
            int move;

            if (alternating)
                move = players.get((game.getCurPlayer() + starter) % 2).move(copyGame ? game.copy() : game);
            else
                move = players.get(game.getCurPlayer()).move(copyGame ? game.copy() : game);

            String moveStr = game.getMoves()[move];
            game.makeMove(move);

            if (verbose)
            {
                System.out.format("Player %d made move {%d => %s}\n", currentPlayer, move, moveStr);
                System.out.println(game);
            }
        }

        if (verbose)
            for (int i = 0; i < players.size(); i++)
                System.out.format("Player %d => %s: %s\n", i, players.get(i), game.getOutcome()[i]);
    }

    public static <GAME extends Game<GAME>> void playRandomGame(GAME game)
    {
        System.out.println(game);

        while (!game.isOver())
        {
            game.makeMove(RNG.nextInt(game.getNumMoves()));
            System.out.println(game);
        }

        System.out.println(Arrays.toString(game.getOutcome()));
    }
}
