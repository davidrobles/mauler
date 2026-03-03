package net.davidrobles.mauler.core.util;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.core.GameResult;
import net.davidrobles.mauler.core.Strategy;

import java.util.List;
import java.util.Random;

import static net.davidrobles.mauler.core.util.Console.*;

/**
 * Static utilities for running and displaying games on the console.
 *
 * <p>Utility class — not instantiable.
 */
public final class Games
{
    private static final Random RNG = new Random();

    private static final String[] PLAYER_COLORS = { BRIGHT_RED, BRIGHT_BLUE };
    private static final String[] PLAYER_LABELS = { "Player 1", "Player 2" };

    private Games() {}

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Makes a single random legal move on {@code game}.
     */
    public static void makeRandomMove(Game<?> game)
    {
        game.makeMove(RNG.nextInt(game.getNumMoves()));
    }

    /**
     * Advances {@code game} by up to {@code numMoves} random moves,
     * stopping early if the game ends.
     *
     * @return {@code true} if all {@code numMoves} moves were made,
     *         {@code false} if the game ended before reaching that count
     */
    public static <GAME extends Game<GAME>> boolean advanceRandom(GAME game, int numMoves)
    {
        int i;
        for (i = 0; i < numMoves && !game.isOver(); i++)
            game.makeMove(RNG.nextInt(game.getNumMoves()));
        return i == numMoves;
    }

    /**
     * Plays a complete game between the given strategies, printing every move
     * and the final outcome to stdout.
     *
     * <p>The game is reset before play begins. Each strategy receives a
     * defensive copy of the current state so it cannot mutate the live game.
     *
     * @param game       the game to play (reset and modified in place)
     * @param strategies one strategy per player; index must match player index
     */
    public static <GAME extends Game<GAME>> void play(GAME game, List<Strategy<GAME>> strategies)
    {
        game.reset();

        header(game.getName());
        for (int i = 0; i < strategies.size(); i++)
            System.out.println("  " + playerLabel(i) + dim("  →  ") + strategies.get(i));
        System.out.println();

        int moveNum = 1;

        while (!game.isOver())
        {
            int player     = game.getCurPlayer();
            int move       = strategies.get(player).move(game.copy());
            String moveStr = game.getMoves().get(move);
            game.makeMove(move);

            System.out.println(dim("Move " + moveNum++ + "  ")
                    + playerLabel(player)
                    + dim("  played  ")
                    + fmt(moveStr, BOLD, BRIGHT_WHITE));
            System.out.println(game);
            separator();
        }

        printOutcome(game);
    }

    /**
     * Plays a complete game with uniformly random moves for all players,
     * printing every move and the final outcome to stdout.
     *
     * <p>The game is reset before play begins.
     *
     * @param game the game to play (reset and modified in place)
     */
    public static <GAME extends Game<GAME>> void playRandom(GAME game)
    {
        game.reset();

        header(game.getName());
        System.out.println(game);

        int moveNum = 1;

        while (!game.isOver())
        {
            int player     = game.getCurPlayer();
            int move       = RNG.nextInt(game.getNumMoves());
            String moveStr = game.getMoves().get(move);
            game.makeMove(move);

            System.out.println(dim("Move " + moveNum++ + "  ")
                    + playerLabel(player)
                    + dim("  played  ")
                    + fmt(moveStr, BOLD, BRIGHT_WHITE));
            System.out.println(game);
            separator();
        }

        printOutcome(game);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static String playerLabel(int player)
    {
        return fmt(PLAYER_LABELS[player % 2], BOLD, PLAYER_COLORS[player % 2]);
    }

    private static String outcomeLabel(GameResult outcome)
    {
        switch (outcome)
        {
            case WIN:  return fmt("WIN",  BOLD, BRIGHT_GREEN);
            case LOSS: return fmt("LOSS", BOLD, BRIGHT_RED);
            case DRAW: return fmt("DRAW", BOLD, BRIGHT_YELLOW);
            default:   return dim("N/A");
        }
    }

    private static void printOutcome(Game<?> game)
    {
        GameResult[] outcomes = game.getOutcome().orElseThrow();
        System.out.println(bold("\nOutcome"));
        for (int i = 0; i < outcomes.length; i++)
            System.out.println("  " + playerLabel(i) + "  " + outcomeLabel(outcomes[i]));
        System.out.println();
    }
}
