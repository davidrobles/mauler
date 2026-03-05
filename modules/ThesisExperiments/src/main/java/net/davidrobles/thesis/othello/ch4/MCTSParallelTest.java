package net.davidrobles.thesis.othello.ch4;

import java.util.ArrayList;
import java.util.List;
import net.davidrobles.mauler.core.GameResult;
import net.davidrobles.mauler.core.Strategy;
import net.davidrobles.mauler.othello.Othello;
import net.davidrobles.mauler.strategies.mcts.MCTSRootParallel;
import net.davidrobles.mauler.strategies.mcts.UCT;

/** Tests for the parallel implementation of MCTS. */
public class MCTSParallelTest {
    /** Parallel MCTS vs standard MCTS> */
    static void ParallelMCTSvsStandardMCTS() {
        double c = 0.5;
        int nGames = 10;
        int timeout = 1000;
        MCTSRootParallel<Othello> mctsRootP = new MCTSRootParallel<Othello>(new UCT<Othello>(c));
        UCT<Othello> uct = new UCT<Othello>(c);

        List<Strategy<Othello>> players = new ArrayList<Strategy<Othello>>();
        players.add(mctsRootP);
        players.add(uct);

        Othello othello = new Othello();
        int wins = 0;

        for (int i = 0; i < nGames; i++) {
            System.out.println("Game " + i);
            othello.reset();

            while (!othello.isOver()) {
                Strategy<Othello> curPlayer = players.get(othello.getCurPlayer());
                int move = curPlayer.move(othello, timeout);
                othello.makeMove(move);
            }

            if (othello.getOutcome().orElseThrow()[0] == GameResult.WIN) wins++;
        }

        double winRate = wins / (double) nGames;
        System.out.println("Result: " + winRate);
    }

    public static void main(String[] args) {
        ParallelMCTSvsStandardMCTS();
    }
}
