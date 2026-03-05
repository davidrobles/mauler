package net.davidrobles.mauler.strategies.mcts;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.core.Strategy;
import net.davidrobles.mauler.strategies.TerminalEvaluator;
import net.davidrobles.mauler.strategies.mcts.selection.SelectionPolicy;

/**
 * Monte Carlo Tree Search (MCTS).
 *
 * <p>Each simulation consists of four phases:
 *
 * <ol>
 *   <li><b>Selection</b> — walk the existing tree using the {@link SelectionPolicy} until an
 *       unexpanded node or terminal state is reached.
 *   <li><b>Expansion</b> — expand the first unvisited node by creating its children.
 *   <li><b>Simulation</b> — run a random rollout to a terminal state using the default policy
 *       (typically a {@link net.davidrobles.mauler.strategies.RandomStrategy}).
 *   <li><b>Backpropagation</b> — propagate the outcome back up through all visited nodes via a
 *       running-mean update.
 * </ol>
 *
 * <p>Two operational modes are supported, mirroring {@link
 * net.davidrobles.mauler.strategies.mc.MonteCarlo}:
 *
 * <ul>
 *   <li><b>Simulation-count</b> — construct with {@code nSims}; runs exactly that many simulations
 *       per {@link #move(Game)} call.
 *   <li><b>Time-based</b> — construct without {@code nSims} ({@code nSims = 0}); use {@link
 *       #move(Game, int)} with a millisecond budget.
 * </ul>
 *
 * <p><b>Tree reuse:</b> after each move the subtree rooted at the chosen action is retained. On the
 * next call the search tries to find the opponent's reply among that subtree's children (using
 * {@link Object#equals} on the game state) and continues from there, carrying over all accumulated
 * statistics. Games that do not implement {@code equals()} correctly will always start from a fresh
 * tree.
 *
 * @param <GAME> the game type
 */
public class MCTS<GAME extends Game<GAME>> implements Strategy<GAME> {
    /**
     * Number of simulations between wall-clock checks in time-based search. Must be a power of 2.
     */
    private static final int TIME_CHECK_INTERVAL = 128;

    protected int nSims;
    protected SelectionPolicy<GAME> selectionPolicy;
    protected Strategy<GAME> rolloutPolicy;
    private TerminalEvaluator<GAME> utilFunc = new TerminalEvaluator<>(1.0, -1.0, 0.0);

    private final List<MCTSObserver<GAME>> observers = new CopyOnWriteArrayList<>();

    /**
     * Subtree rooted at the move we played last turn. On the next call to {@code move()}, we search
     * its children for the state the opponent moved to, and reuse that grandchild as the new root
     * if found.
     */
    private MCTSNode<GAME> persistentRoot = null;

    /**
     * Creates a time-based MCTS instance (use {@link #move(Game, int)}).
     *
     * @param selectionPolicy the selection/final-move policy
     * @param rolloutPolicy the rollout (default) policy
     */
    public MCTS(SelectionPolicy<GAME> selectionPolicy, Strategy<GAME> rolloutPolicy) {
        this(selectionPolicy, rolloutPolicy, 0);
    }

    /**
     * Creates a simulation-count MCTS instance.
     *
     * @param selectionPolicy the selection/final-move policy
     * @param rolloutPolicy the rollout (default) policy
     * @param nSims number of simulations per move (must be non-negative)
     * @throws IllegalArgumentException if {@code nSims} is negative
     */
    public MCTS(SelectionPolicy<GAME> selectionPolicy, Strategy<GAME> rolloutPolicy, int nSims) {
        if (nSims < 0)
            throw new IllegalArgumentException("nSims must be non-negative, got: " + nSims);

        this.selectionPolicy = selectionPolicy;
        this.rolloutPolicy = rolloutPolicy;
        this.nSims = nSims;
    }

    /**
     * Replaces the utility function used to score terminal rollout positions. The default is the
     * standard minimax convention: win=+1, draw=0, loss=-1.
     */
    public void setUtilFunc(TerminalEvaluator<GAME> utilFunc) {
        this.utilFunc = utilFunc;
    }

    /** Registers an observer to be notified after each search completes. */
    public void addObserver(MCTSObserver<GAME> observer) {
        observers.add(observer);
    }

    private void notifyObservers(MCTSNode<GAME> root) {
        for (MCTSObserver<GAME> observer : observers) observer.searchFinished(root);
    }

    /** Returns a new MCTS with the same configuration, including the current utility function. */
    public MCTS<GAME> copy() {
        MCTS<GAME> copy = new MCTS<>(selectionPolicy, rolloutPolicy, nSims);
        copy.utilFunc = this.utilFunc;
        return copy;
    }

    // -------------------------------------------------------------------------
    // MCTS phases
    // -------------------------------------------------------------------------

    /**
     * Runs one full simulation: selection → expansion → rollout → backpropagation.
     *
     * @param root the root node of the search tree
     */
    public void simulate(MCTSNode<GAME> root) {
        List<MCTSNode<GAME>> path = selectAndExpand(root);
        backup(path, rollout(path.get(path.size() - 1)));
    }

    /**
     * Selection + expansion: walks the tree via the tree policy until an unexpanded node or
     * terminal state is reached, then expands it.
     */
    private List<MCTSNode<GAME>> selectAndExpand(MCTSNode<GAME> root) {
        List<MCTSNode<GAME>> path = new ArrayList<>();
        MCTSNode<GAME> node = root;

        while (!node.getGame().isOver()) {
            path.add(node);
            if (node.getVisits() == 0) {
                expand(node);
                return path;
            }
            node = node.getChild(selectionPolicy.move(node));
        }

        path.add(node); // terminal node
        return path;
    }

    /**
     * Expansion: creates child nodes for all legal moves from {@code node}. Subclasses may override
     * to attach prior knowledge or other metadata.
     */
    protected void expand(MCTSNode<GAME> node) {
        node.expand();
    }

    /**
     * Simulation (default policy): plays moves uniformly at random on a copy of the node's game
     * until a terminal state is reached, then scores it from the perspective of the player to move
     * at {@code node}. A copy is used so the node's game state is not modified.
     */
    protected double rollout(MCTSNode<GAME> node) {
        int leafPlayer = node.getGame().getCurPlayer();
        GAME copy = node.getGame().copy();
        while (!copy.isOver()) copy.makeMove(rolloutPolicy.move(copy));
        return utilFunc.evaluate(copy, leafPlayer);
    }

    /**
     * Final move selection: returns the most-visited child of {@code root}. Using visit count
     * (robust child) rather than the selection policy avoids the exploration bonus influencing the
     * actual move played.
     */
    private int mostVisitedChild(MCTSNode<GAME> root) {
        int bestMove = 0;
        int mostVisits = -1;

        for (int move = 0; move < root.getGame().getNumMoves(); move++) {
            int childVisits = root.getActionVisits(move);
            if (childVisits > mostVisits) {
                mostVisits = childVisits;
                bestMove = move;
            }
        }

        return bestMove;
    }

    /**
     * Backpropagation: walks the path from leaf to root, negating the value at each step so that
     * every node stores the outcome from its own current player's perspective.
     */
    private void backup(List<MCTSNode<GAME>> path, double outcome) {
        double value = outcome;
        for (int i = path.size() - 1; i >= 0; i--) {
            path.get(i).update(value);
            value = -value;
        }
    }

    // -------------------------------------------------------------------------
    // Strategy
    // -------------------------------------------------------------------------

    /** Always {@code false} — rollout and tree policies are typically stochastic. */
    @Override
    public boolean isDeterministic() {
        return false;
    }

    /**
     * Attempts to reuse the subtree from the previous search.
     *
     * <p>After we played our last move, {@code persistentRoot} holds the child node for that move.
     * The opponent has since replied with some move. We scan {@code persistentRoot}'s children for
     * one whose game state equals the current input — that grandchild represents the position after
     * both moves and carries the statistics accumulated during the previous search.
     *
     * <p>Falls back to a fresh root when:
     *
     * <ul>
     *   <li>this is the first call ({@code persistentRoot == null})
     *   <li>{@code persistentRoot} was never visited/expanded during the last search
     *   <li>the game's {@code equals()} is not implemented (new game detected)
     * </ul>
     */
    private MCTSNode<GAME> findOrCreateRoot(GAME game) {
        if (persistentRoot != null) {
            for (MCTSNode<GAME> candidate : persistentRoot.getChildren()) {
                if (candidate.getGame().equals(game)) return candidate;
            }
        }
        return new MCTSNode<>(game.copy());
    }

    /**
     * Runs {@code nSims} simulations from the current position and returns the best move.
     *
     * <p>Reuses the subtree from the previous search when possible; see {@link
     * #findOrCreateRoot(Game)}.
     *
     * @throws IllegalStateException if this instance was configured for time-based search ({@code
     *     nSims=0}); use {@link #move(Game, int)} instead
     */
    @Override
    public int move(GAME game) {
        if (nSims == 0)
            throw new IllegalStateException(
                    "This MCTS instance was configured for time-based search (nSims=0). "
                            + "Use move(game, timeoutMs) instead.");

        MCTSNode<GAME> root = findOrCreateRoot(game);

        for (int i = 0; i < nSims; i++) simulate(root);

        int bestMove = mostVisitedChild(root);
        persistentRoot = root.getChild(bestMove);
        notifyObservers(root);
        return bestMove;
    }

    /**
     * Runs simulations within the time budget and returns the best move.
     *
     * <p>Reuses the subtree from the previous search when possible; see {@link
     * #findOrCreateRoot(Game)}.
     *
     * <p>Time is checked once every {@value #TIME_CHECK_INTERVAL} simulations rather than every
     * simulation to avoid the overhead of frequent {@link System#currentTimeMillis()} calls, which
     * matters for fast games where simulations run in microseconds.
     */
    @Override
    public int move(GAME game, int timeout) {
        MCTSNode<GAME> root = findOrCreateRoot(game);
        long timeDue = System.currentTimeMillis() + timeout;
        int sims = 0;

        while ((++sims & TIME_CHECK_INTERVAL - 1) != 0 || System.currentTimeMillis() < timeDue)
            simulate(root);

        int bestMove = mostVisitedChild(root);
        persistentRoot = root.getChild(bestMove);
        notifyObservers(root);
        return bestMove;
    }

    // -------------------------------------------------------------------------
    // Object
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        if (nSims > 0)
            return String.format(
                    "<MCTS selectionPolicy=%s rolloutPolicy=%s nSims=%d>",
                    selectionPolicy, rolloutPolicy, nSims);
        else
            return String.format(
                    "<MCTS selectionPolicy=%s rolloutPolicy=%s>", selectionPolicy, rolloutPolicy);
    }
}
