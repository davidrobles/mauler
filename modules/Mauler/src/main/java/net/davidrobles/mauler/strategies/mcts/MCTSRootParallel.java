package net.davidrobles.mauler.strategies.mcts;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.core.Strategy;

/**
 * MCTS with root parallelization.
 *
 * <p>Spawns {@code nThreads} independent MCTS workers, each starting from the same root state and
 * building a private search tree — no shared state, no synchronization. After all workers finish,
 * their per-move statistics are aggregated: visit counts are summed and values are combined as a
 * weighted mean. The final move is selected by the robust-child criterion (most-visited child).
 *
 * <p>Root parallelization is the simplest of the three main parallel MCTS strategies (leaf, root,
 * tree). Its strength is zero synchronization overhead; its weakness is that threads cannot share
 * discoveries mid-search.
 *
 * <p>References:
 *
 * <ul>
 *   <li>Chaslot et al. (2008). "Parallel Monte-Carlo Tree Search." <em>Computers and Games (CG
 *       2008)</em>, LNCS 5131:60&ndash;71. Introduces and evaluates leaf, root, and tree
 *       parallelization.
 *   <li>Browne et al. (2012). "A Survey of Monte Carlo Tree Search Methods." <em>IEEE TCIAIG</em>,
 *       4(1):1&ndash;43. §8 covers parallel MCTS and the trade-offs between strategies.
 * </ul>
 *
 * @param <GAME> the game type
 */
public class MCTSRootParallel<GAME extends Game<GAME>> implements Strategy<GAME> {
    private final MCTS<GAME> mcts;
    private final int nThreads;

    /**
     * Creates a root-parallelized MCTS using one thread per available CPU core.
     *
     * @param mcts the base MCTS configuration; each thread receives an independent copy
     */
    public MCTSRootParallel(MCTS<GAME> mcts) {
        this(mcts, Runtime.getRuntime().availableProcessors());
    }

    /**
     * Creates a root-parallelized MCTS with an explicit thread count.
     *
     * @param mcts the base MCTS configuration; each thread receives an independent copy
     * @param nThreads number of parallel threads
     */
    public MCTSRootParallel(MCTS<GAME> mcts, int nThreads) {
        this.mcts = mcts;
        this.nThreads = nThreads;
    }

    // -------------------------------------------------------------------------
    // Merging and final selection
    // -------------------------------------------------------------------------

    /**
     * Merges independently built root nodes by summing child visit counts and computing a weighted
     * mean of child values:
     *
     * <pre>
     *   Q_merged(a) = sum_i( Q_i(a) * N_i(a) ) / sum_i( N_i(a) )
     * </pre>
     */
    private MCTSNode<GAME> mergeRoots(List<MCTSNode<GAME>> roots) {
        MCTSNode<GAME> merged = new MCTSNode<>(roots.get(0).getGame().copy());
        merged.expand();

        for (int move = 0; move < merged.getGame().getNumMoves(); move++) {
            int totalVisits = 0;
            double weightedValueSum = 0.0;

            for (MCTSNode<GAME> root : roots) {
                MCTSNode<GAME> child = root.getChild(move);
                int visits = child.getVisits();
                totalVisits += visits;
                weightedValueSum += child.getValue() * visits;
            }

            if (totalVisits > 0) {
                merged.getChild(move).setVisits(totalVisits);
                merged.getChild(move).setValue(weightedValueSum / totalVisits);
            }
        }

        return merged;
    }

    /**
     * Returns the most-visited child index (robust-child criterion). Robust child is less
     * susceptible to statistical noise than selecting the highest-value child.
     */
    private int mostVisitedChild(MCTSNode<GAME> node) {
        int bestMove = 0;
        int mostVisits = -1;

        for (int move = 0; move < node.getGame().getNumMoves(); move++) {
            int visits = node.getChild(move).getVisits();

            if (visits > mostVisits) {
                mostVisits = visits;
                bestMove = move;
            }
        }

        return bestMove;
    }

    // -------------------------------------------------------------------------
    // Worker task
    // -------------------------------------------------------------------------

    /**
     * A single parallel MCTS worker. Each task receives an independent copy of the MCTS
     * configuration and builds a private search tree from scratch.
     */
    private class MCTSTask implements Callable<MCTSNode<GAME>> {
        private final GAME game;
        private final int nSims; // 0 = time-based mode
        private final long timeDue; // used when nSims == 0

        /** Simulation-count constructor: runs exactly {@code nSims} simulations. */
        MCTSTask(GAME game, int nSims) {
            this.game = game;
            this.nSims = nSims;
            this.timeDue = 0;
        }

        /** Time-based constructor: runs until the wall clock reaches {@code timeDue}. */
        MCTSTask(GAME game, long timeDue) {
            this.game = game;
            this.nSims = 0;
            this.timeDue = timeDue;
        }

        @Override
        public MCTSNode<GAME> call() {
            MCTS<GAME> worker = mcts.copy();
            MCTSNode<GAME> root = new MCTSNode<>(game);

            if (nSims > 0) {
                for (int i = 0; i < nSims; i++) worker.simulate(root);
            } else {
                while (System.currentTimeMillis() < timeDue) worker.simulate(root);
            }

            return root;
        }
    }

    // -------------------------------------------------------------------------
    // Shared execution logic
    // -------------------------------------------------------------------------

    private int runParallel(List<MCTSTask> tasks) {
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        List<MCTSNode<GAME>> roots = new ArrayList<>();

        try {
            for (Future<MCTSNode<GAME>> future : executor.invokeAll(tasks)) roots.add(future.get());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException("MCTS worker failed", e.getCause());
        } finally {
            executor.shutdown();
        }

        return mostVisitedChild(mergeRoots(roots));
    }

    // -------------------------------------------------------------------------
    // Strategy
    // -------------------------------------------------------------------------

    @Override
    public boolean isDeterministic() {
        return false;
    }

    /**
     * Runs {@code mcts.nSims} simulations on each of the {@code nThreads} workers and returns the
     * most-visited move after merging.
     *
     * <p>Total simulations = {@code nSims * nThreads}. Each thread receives the full simulation
     * budget independently, exploiting all available cores.
     *
     * @throws IllegalStateException if the base MCTS was configured for time-based operation (nSims
     *     == 0); use {@link #move(Game, int)} instead
     */
    @Override
    public int move(GAME game) {
        if (mcts.nSims <= 0)
            throw new IllegalStateException(
                    "move(game) requires a simulation-count MCTS; use move(game, timeout) instead");

        List<MCTSTask> tasks = new ArrayList<>();

        for (int i = 0; i < nThreads; i++) tasks.add(new MCTSTask(game.copy(), mcts.nSims));

        return runParallel(tasks);
    }

    /**
     * Runs each of the {@code nThreads} workers for {@code timeout} milliseconds simultaneously,
     * then merges results and returns the most-visited move.
     */
    @Override
    public int move(GAME game, int timeout) {
        long timeDue = System.currentTimeMillis() + timeout;
        List<MCTSTask> tasks = new ArrayList<>();

        for (int i = 0; i < nThreads; i++) tasks.add(new MCTSTask(game.copy(), timeDue));

        return runParallel(tasks);
    }

    // -------------------------------------------------------------------------
    // Object
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return String.format("<MCTSRootParallel mcts=%s nThreads=%d>", mcts, nThreads);
    }
}
