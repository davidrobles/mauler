package net.davidrobles.mauler.core;

import net.davidrobles.mauler.players.Player;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

public class Match<GAME extends Game<GAME>> implements Callable<Outcome[]>
{
    private GAME game;
    private List<Player<GAME>> players;
    private int starter;
    private int timeout = -1;
    private boolean deterministic = false;
    private Random rnd = new Random();

    public Match(GAME game, List<Player<GAME>> players, int starter, int timeout)
    {
        if (timeout <= 0)
            throw new IllegalArgumentException();

        this.game = game;
        this.players = players;
        this.starter = starter;
        this.timeout = timeout;

        if (players.get(0).isDeterministic() && players.get(1).isDeterministic())
            deterministic = true;
        else
            deterministic = false;
    }

    public Match(GAME game, List<Player<GAME>> players, int starter)
    {
        this.game = game;
        this.players = players;
        this.starter = starter;
    }

    //////////////
    // Callable //
    //////////////

    @Override
    public Outcome[] call()
    {
        game = game.newInstance();

        if (deterministic)
        {
            while (!game.isOver())
            {
                int move;

                if (rnd.nextDouble() < 0.1)
                    move = rnd.nextInt(game.getNumMoves());
                else
                {
                    int playerToMove = (game.getCurPlayer() + starter) % 2;
                    Player<GAME> player = players.get(playerToMove);

                    if (timeout > 0)
                        move = player.move(game, timeout);
                    else
                        move = player.move(game);
                }

                game.makeMove(move);
            }
        }
        else
        {
            while (!game.isOver())
            {
                int move;
                int playerToMove = (game.getCurPlayer() + starter) % 2;
                Player<GAME> player = players.get(playerToMove);

                if (timeout > 0)
                    move = player.move(game, timeout);
                else
                    move = player.move(game);

                game.makeMove(move);
            }
        }

        Outcome[] outcomes = game.getOutcome();

        if (starter == 1)
            outcomes = new Outcome[] { outcomes[1], outcomes[0] };

        game = null;
//        System.out.println("Match: " + increaseCounter());

        return outcomes;
    }

    static int counter = 0;

    public synchronized int increaseCounter()
    {
        return counter++;
    }
}
