package net.davidrobles.thesis.othello.ch4;

import net.davidrobles.mauler.core.Outcome;
import net.davidrobles.mauler.players.Player;
import net.davidrobles.mauler.othello.Othello;
import net.davidrobles.mauler.players.mcts.UCT;
import net.davidrobles.mauler.players.mcts.parallel.MCTSRootP;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests for the parallel implementation of MCTS.
 */
public class MCTSParallelTest
{
    /**
     * Parallel MCTS vs standard MCTS>
     */
    static void ParallelMCTSvsStandardMCTS()
    {
        double c = 0.5;
        int nGames = 10;
        int timeout = 1000;
        MCTSRootP<Othello> mctsRootP = new MCTSRootP<Othello>(new UCT<Othello>(c));
        UCT<Othello> uct = new UCT<Othello>(c);

        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
        players.add(mctsRootP);
        players.add(uct);

        Othello othello = new Othello();
        int wins = 0;

        for (int i = 0; i < nGames; i++)
        {
            System.out.println("Game " + i);
            othello.reset();

            while (!othello.isOver())
            {
                Player<Othello> curPlayer = players.get(othello.getCurPlayer());
                int move = curPlayer.move(othello, timeout);
                othello.makeMove(move);
            }

            if (othello.getOutcome()[0] == Outcome.WIN)
                wins++;
        }

        double winRate = wins / (double) nGames;
        System.out.println("Result: " + winRate);
    }

    public static void main(String[] args)
    {
        ParallelMCTSvsStandardMCTS();
    }
}
