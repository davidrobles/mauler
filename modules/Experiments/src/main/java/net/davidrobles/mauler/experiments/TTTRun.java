package net.davidrobles.mauler.experiments;

import net.davidrobles.mauler.core.Series;
import net.davidrobles.mauler.core.Strategy;
import net.davidrobles.mauler.strategies.greedy.EpsilonGreedyStrategy;
import net.davidrobles.mauler.strategies.greedy.GreedyStrategy;
import net.davidrobles.mauler.strategies.RandomStrategy;
import net.davidrobles.mauler.strategies.TerminalEvaluator;
import net.davidrobles.mauler.strategies.mc.MonteCarlo;
import net.davidrobles.mauler.strategies.mcts.UCT;
import net.davidrobles.mauler.strategies.mcts.UCTNoRollout;
import net.davidrobles.mauler.strategies.mcts.MCTSRootParallel;
import net.davidrobles.mauler.strategies.minimax.AlphaBeta;
import net.davidrobles.mauler.strategies.minimax.Minimax;
import net.davidrobles.mauler.strategies.minimax.Negamax;
import net.davidrobles.mauler.strategies.minimax.PVS;
import net.davidrobles.mauler.tictactoe.TicTacToe;

import java.util.List;

public class TTTRun
{
    // -------------------------------------------------------------------------
    // Evaluator
    // -------------------------------------------------------------------------

    // Terminal-only evaluator: works for full-depth search and MC rollouts.
    // For depth-limited search or greedy strategies, supply a heuristic instead.
    static final TerminalEvaluator<TicTacToe> terminalEval = TerminalEvaluator.minimax();

    // -------------------------------------------------------------------------
    // Random
    // -------------------------------------------------------------------------

    static final Strategy<TicTacToe> random = new RandomStrategy<>();

    // -------------------------------------------------------------------------
    // Minimax family — no depth limit → perfect play for TicTacToe
    // -------------------------------------------------------------------------

    static final Strategy<TicTacToe> minimax   = new Minimax<TicTacToe>(terminalEval);
    static final Strategy<TicTacToe> negamax   = new Negamax<TicTacToe>(terminalEval);
    static final Strategy<TicTacToe> alphaBeta = new AlphaBeta<TicTacToe>(terminalEval);
    static final Strategy<TicTacToe> pvs       = new PVS<TicTacToe>(terminalEval);

    // -------------------------------------------------------------------------
    // Monte Carlo — flat, simulation-count
    // -------------------------------------------------------------------------

    static final Strategy<TicTacToe> monteCarlo100  = new MonteCarlo<TicTacToe>(100);
    static final Strategy<TicTacToe> monteCarlo1000 = new MonteCarlo<TicTacToe>(1000);

    // -------------------------------------------------------------------------
    // UCT — simulation-count (c = √2 is the standard exploration constant)
    // -------------------------------------------------------------------------

    static final Strategy<TicTacToe> uct100  = new UCT<TicTacToe>(Math.sqrt(2), 100);
    static final Strategy<TicTacToe> uct10000 = new UCT<TicTacToe>(Math.sqrt(2), 10000);

    // -------------------------------------------------------------------------
    // UCT with root parallelization
    // -------------------------------------------------------------------------

    static final Strategy<TicTacToe> uctRootP = new MCTSRootParallel<TicTacToe>(new UCT<TicTacToe>(Math.sqrt(2), 1000));

    // -------------------------------------------------------------------------
    // Greedy / ε-Greedy — require a non-terminal heuristic evaluator
    // -------------------------------------------------------------------------

//  static final Strategy<TicTacToe> greedy        = new GreedyStrategy<TicTacToe>(/* heuristic */);
//  static final Strategy<TicTacToe> epsilonGreedy = new EpsilonGreedyStrategy<TicTacToe>(/* heuristic */, 0.1);

    // -------------------------------------------------------------------------
    // UCTNoRollout — replaces rollouts with an evaluator; needs non-terminal heuristic
    // -------------------------------------------------------------------------

//  static final Strategy<TicTacToe> uctNoRollout = new UCTNoRollout<TicTacToe>(Math.sqrt(2), /* heuristic */);

    // -------------------------------------------------------------------------
    // Experiment
    // -------------------------------------------------------------------------

    public static void main(String[] args)
    {
        Series<TicTacToe> series = new Series<>(TicTacToe::new, 1000, List.of(uct10000, random));
        series.run();
    }
}
