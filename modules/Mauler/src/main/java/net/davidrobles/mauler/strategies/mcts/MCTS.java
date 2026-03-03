package net.davidrobles.mauler.strategies.mcts;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.core.Strategy;
import net.davidrobles.mauler.strategies.TerminalEvaluator;
import net.davidrobles.mauler.strategies.mcts.selection.SelectionPolicy;

import java.util.LinkedList;
import java.util.List;

/**
 * Monte Carlo Tree Search (MCTS).
 *
 * <p>Each simulation consists of four phases:
 * <ol>
 *   <li><b>Selection</b> — walk the existing tree using the {@link SelectionPolicy}
 *       until an unexpanded node or terminal state is reached.</li>
 *   <li><b>Expansion</b> — expand the first unvisited node by creating its children.</li>
 *   <li><b>Simulation</b> — run a random rollout to a terminal state using the
 *       default policy (typically a {@link net.davidrobles.mauler.strategies.RandomStrategy}).</li>
 *   <li><b>Backpropagation</b> — propagate the outcome back up through all
 *       visited nodes via a running-mean update.</li>
 * </ol>
 *
 * <p>Two operational modes are supported, mirroring {@link net.davidrobles.mauler.strategies.mc.MonteCarlo}:
 * <ul>
 *   <li><b>Simulation-count</b> — construct with {@code nSims}; runs exactly that
 *       many simulations per {@link #move(Game)} call.</li>
 *   <li><b>Time-based</b> — construct without {@code nSims} ({@code nSims = 0});
 *       use {@link #move(Game, int)} with a millisecond budget.</li>
 * </ul>
 *
 * @param <GAME> the game type
 */
public class MCTS<GAME extends Game<GAME>> implements Strategy<GAME>
{
    protected int nSims;
    protected SelectionPolicy<GAME> selectionPolicy;
    protected Strategy<GAME> rolloutPolicy;
    private TerminalEvaluator<GAME> utilFunc = new TerminalEvaluator<>(1.0, -1.0, 0.0);

    /**
     * Creates a time-based MCTS instance (use {@link #move(Game, int)}).
     *
     * @param selectionPolicy   the selection/final-move policy
     * @param rolloutPolicy the rollout (default) policy
     */
    public MCTS(SelectionPolicy<GAME> selectionPolicy, Strategy<GAME> rolloutPolicy)
    {
        this(selectionPolicy, rolloutPolicy, 0);
    }

    /**
     * Creates a simulation-count MCTS instance.
     *
     * @param selectionPolicy   the selection/final-move policy
     * @param rolloutPolicy the rollout (default) policy
     * @param nSims        number of simulations per move (must be non-negative)
     * @throws IllegalArgumentException if {@code nSims} is negative
     */
    public MCTS(SelectionPolicy<GAME> selectionPolicy, Strategy<GAME> rolloutPolicy, int nSims)
    {
        if (nSims < 0)
            throw new IllegalArgumentException("nSims must be non-negative, got: " + nSims);

        this.selectionPolicy    = selectionPolicy;
        this.rolloutPolicy = rolloutPolicy;
        this.nSims         = nSims;
    }

    /**
     * Replaces the utility function used to score terminal rollout positions.
     * The default is the standard minimax convention: win=+1, draw=0, loss=-1.
     */
    public void setUtilFunc(TerminalEvaluator<GAME> utilFunc)
    {
        this.utilFunc = utilFunc;
    }

    /** Returns a new MCTS with the same configuration, including the current utility function. */
    public MCTS<GAME> copy()
    {
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
    public void simulate(MCTSNode<GAME> root)
    {
        LinkedList<MCTSNode<GAME>> path = selectAndExpand(root);
        backup(path, rollout(path.getLast()));
    }

    /**
     * Selection + expansion: walks the tree via the tree policy until an
     * unexpanded node or terminal state is reached, then expands it.
     */
    private LinkedList<MCTSNode<GAME>> selectAndExpand(MCTSNode<GAME> root)
    {
        LinkedList<MCTSNode<GAME>> path = new LinkedList<>();
        MCTSNode<GAME> node = root;

        while (!node.getGame().isOver())
        {
            path.add(node);
            if (node.getVisits() == 0)
            {
                expand(node);
                return path;
            }
            node = node.getChild(selectionPolicy.move(node));
        }

        path.add(node);     // terminal node
        return path;
    }

    /**
     * Expansion: creates child nodes for all legal moves from {@code node}.
     * Subclasses may override to attach prior knowledge or other metadata.
     */
    protected void expand(MCTSNode<GAME> node)
    {
        node.expand();
    }

    /**
     * Simulation (default policy): plays moves uniformly at random on a copy
     * of the node's game until a terminal state is reached, then scores it
     * from the perspective of the player to move at {@code node}.
     * A copy is used so the node's game state is not modified.
     */
    protected double rollout(MCTSNode<GAME> node)
    {
        int leafPlayer = node.getGame().getCurPlayer();
        GAME copy = node.getGame().copy();
        while (!copy.isOver())
            copy.makeMove(rolloutPolicy.move(copy));
        return utilFunc.evaluate(copy, leafPlayer);
    }

    /**
     * Final move selection: returns the most-visited child of {@code root}.
     * Using visit count (robust child) rather than the selection policy avoids
     * the exploration bonus influencing the actual move played.
     */
    private int mostVisitedChild(MCTSNode<GAME> root)
    {
        int bestMove  = 0;
        int mostVisits = -1;

        for (int move = 0; move < root.getGame().getNumMoves(); move++)
        {
            int childVisits = root.getActionVisits(move);
            if (childVisits > mostVisits)
            {
                mostVisits = childVisits;
                bestMove   = move;
            }
        }

        return bestMove;
    }

    /**
     * Backpropagation: walks the path from leaf to root, negating the value at
     * each step so that every node stores the outcome from its own current
     * player's perspective.
     */
    private void backup(List<MCTSNode<GAME>> path, double outcome)
    {
        double value = outcome;
        for (int i = path.size() - 1; i >= 0; i--)
        {
            path.get(i).update(value);
            value = -value;
        }
    }

    // -------------------------------------------------------------------------
    // Strategy
    // -------------------------------------------------------------------------

    /** Always {@code false} — rollout and tree policies are typically stochastic. */
    @Override
    public boolean isDeterministic()
    {
        return false;
    }

    /** Runs {@code nSims} simulations from the current position and returns the best move. */
    @Override
    public int move(GAME game)
    {
        MCTSNode<GAME> root = new MCTSNode<>(game);

        for (int i = 0; i < nSims; i++)
            simulate(root);

        return mostVisitedChild(root);
    }

    /** Runs simulations within the time budget and returns the best move. */
    @Override
    public int move(GAME game, int timeout)
    {
        MCTSNode<GAME> root = new MCTSNode<>(game.copy());
        long timeDue = System.currentTimeMillis() + timeout;

        while (System.currentTimeMillis() < timeDue)
            simulate(root);

        return mostVisitedChild(root);
    }

    // -------------------------------------------------------------------------
    // Object
    // -------------------------------------------------------------------------

    @Override
    public String toString()
    {
        if (nSims > 0)
            return String.format("<MCTS selectionPolicy=%s rolloutPolicy=%s nSims=%d>",
                    selectionPolicy, rolloutPolicy, nSims);
        else
            return String.format("<MCTS selectionPolicy=%s rolloutPolicy=%s>",
                    selectionPolicy, rolloutPolicy);
    }
}
