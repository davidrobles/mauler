package net.davidrobles.mauler.core.util;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.core.GameResult;
import net.davidrobles.mauler.players.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static net.davidrobles.mauler.core.util.Console.*;

public class GamesUtil
{
    private static final Random RNG = new Random();

    private static final String[] PLAYER_COLORS = { BRIGHT_RED, BRIGHT_BLUE };
    private static final String[] PLAYER_LABELS = { "Player 1", "Player 2" };

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

    public static <GAME extends Game<GAME>> boolean advanceGame(GAME game, int numMoves)
    {
        int i;
        for (i = 0; i < numMoves && !game.isOver(); i++) {
            int rndMove = RNG.nextInt(game.getNumMoves());
            game.makeMove(rndMove);
        }
        return i == numMoves;
    }

    private static String playerLabel(int player)
    {
        return fmt(PLAYER_LABELS[player % 2], BOLD, PLAYER_COLORS[player % 2]);
    }

    private static String outcomeLabel(GameResult outcome)
    {
        switch (outcome) {
            case WIN:  return fmt("WIN",  BOLD, BRIGHT_GREEN);
            case LOSS: return fmt("LOSS", BOLD, BRIGHT_RED);
            case DRAW: return fmt("DRAW", BOLD, BRIGHT_YELLOW);
            default:   return dim("N/A");
        }
    }

    public static <GAME extends Game<GAME>> void playGame(GAME game, List<Player<GAME>> players, boolean alternating,
                                                          boolean copyGame, boolean verbose, int starter)
    {
        if (verbose)
        {
            header(game.getName());
            for (int i = 0; i < players.size(); i++)
                System.out.println("  " + playerLabel(i) + dim("  →  ") + players.get(i));
            System.out.println();
        }

        game.reset();

        int moveNum = 1;

        while (!game.isOver())
        {
            int currentPlayer = game.getCurPlayer();
            int move;

            if (alternating)
                move = players.get((currentPlayer + starter) % 2).move(copyGame ? game.copy() : game);
            else
                move = players.get(currentPlayer).move(copyGame ? game.copy() : game);

            String moveStr = game.getMoves().get(move);
            game.makeMove(move);

            if (verbose)
            {
                System.out.println(dim("Move " + moveNum + "  ")
                        + playerLabel(currentPlayer)
                        + dim("  played  ")
                        + fmt(moveStr, BOLD, BRIGHT_WHITE));
                System.out.println(game);
                separator();
                moveNum++;
            }
        }

        if (verbose)
        {
            System.out.println(bold("\nOutcome"));
            for (int i = 0; i < players.size(); i++)
                System.out.println("  " + playerLabel(i) + "  " + outcomeLabel(game.getOutcome().orElseThrow()[i]));
            System.out.println();
        }
    }

    public static <GAME extends Game<GAME>> void playRandomGame(GAME game)
    {
        header(game.getName());
        System.out.println(game);

        int moveNum = 1;

        while (!game.isOver())
        {
            int currentPlayer = game.getCurPlayer();
            int move = RNG.nextInt(game.getNumMoves());
            String moveStr = game.getMoves().get(move);
            game.makeMove(move);

            System.out.println(dim("Move " + moveNum + "  ")
                    + playerLabel(currentPlayer)
                    + dim("  played  ")
                    + fmt(moveStr, BOLD, BRIGHT_WHITE));
            System.out.println(game);
            separator();
            moveNum++;
        }

        System.out.println(bold("\nOutcome"));
        GameResult[] outcomes = game.getOutcome().orElseThrow();
        for (int i = 0; i < outcomes.length; i++)
            System.out.println("  " + playerLabel(i) + "  " + outcomeLabel(outcomes[i]));
        System.out.println();
    }
}
