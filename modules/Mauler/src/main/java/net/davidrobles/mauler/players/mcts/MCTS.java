package net.davidrobles.mauler.players.mcts;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.players.Player;
import net.davidrobles.mauler.players.UtilFunc;
import net.davidrobles.mauler.players.mcts.tree.TreePolicy;

import java.util.LinkedList;
import java.util.List;

/**
 * Monte Carlo Tree Search (MCTS).
 *
 * <p>Each simulation consists of four phases:
 * <ol>
 *   <li><b>Selection</b> — walk the existing tree using the {@link TreePolicy}
 *       until an unexpanded node or terminal state is reached.</li>
 *   <li><b>Expansion</b> — expand the first unvisited node by creating its children.</li>
 *   <li><b>Simulation</b> — run a random rollout to a terminal state using the
 *       default policy (typically a {@link net.davidrobles.mauler.players.RandPlayer}).</li>
 *   <li><b>Backpropagation</b> — propagate the outcome back up through all
 *       visited nodes via a running-mean update.</li>
 * </ol>
 *
 * <p>Two operational modes are supported, mirroring {@link net.davidrobles.mauler.players.mc.MonteCarlo}:
 * <ul>
 *   <li><b>Simulation-count</b> — construct with {@code nSims}; runs exactly that
 *       many simulations per {@link #move(Game)} call.</li>
 *   <li><b>Time-based</b> — construct without {@code nSims} ({@code nSims = 0});
 *       use {@link #move(Game, int)} with a millisecond budget.</li>
 * </ul>
 *
 * @param <GAME> the game type
 */
public class MCTS<GAME extends Game<GAME>> implements Player<GAME>
{
    protected int nSims;
    protected TreePolicy<GAME> treePolicy;
    protected Player<GAME> defPolicy;
    private UtilFunc<GAME> utilFunc = new UtilFunc<>(1.0, -1.0, 0.0);

    /**
     * Creates a time-based MCTS instance (use {@link #move(Game, int)}).
     *
     * @param treePolicy the selection/final-move policy
     * @param defPolicy  the rollout (default) policy
     */
    public MCTS(TreePolicy<GAME> treePolicy, Player<GAME> defPolicy)
    {
        this(treePolicy, defPolicy, 0);
    }

    /**
     * Creates a simulation-count MCTS instance.
     *
     * @param treePolicy the selection/final-move policy
     * @param defPolicy  the rollout (default) policy
     * @param nSims      number of simulations per move (must be non-negative)
     * @throws IllegalArgumentException if {@code nSims} is negative
     */
    public MCTS(TreePolicy<GAME> treePolicy, Player<GAME> defPolicy, int nSims)
    {
        if (nSims < 0)
            throw new IllegalArgumentException("nSims must be non-negative, got: " + nSims);

        this.treePolicy = treePolicy;
        this.defPolicy  = defPolicy;
        this.nSims      = nSims;
    }

    /**
     * Replaces the utility function used to score terminal rollout positions.
     * The default is the standard minimax convention: win=+1, draw=0, loss=-1.
     */
    public void setUtilFunc(UtilFunc<GAME> utilFunc)
    {
        this.utilFunc = utilFunc;
    }

    /** Returns a new MCTS with the same configuration, including the current utility function. */
    public MCTS<GAME> copy()
    {
        MCTS<GAME> copy = new MCTS<>(treePolicy, defPolicy, nSims);
        copy.utilFunc = this.utilFunc;
        return copy;
    }

    // -------------------------------------------------------------------------
    // MCTS phases
    // -------------------------------------------------------------------------

    /**
     * Runs one full simulation: selection → expansion → rollout → backpropagation.
     *
     * @param root   the root node of the search tree
     * @param player the player whose win/loss perspective to back up
     */
    public void simulate(MCTSNode<GAME> root, int player)
    {
        LinkedList<MCTSNode<GAME>> path = selectAndExpand(root, player);
        backup(path, rollout(path.getLast(), player));
    }

    /**
     * Selection + expansion: walks the tree via the tree policy until an
     * unexpanded node or terminal state is reached, then expands it.
     */
    private LinkedList<MCTSNode<GAME>> selectAndExpand(MCTSNode<GAME> root, int player)
    {
        LinkedList<MCTSNode<GAME>> path = new LinkedList<>();
        MCTSNode<GAME> node = root;

        while (!node.getGame().isOver())
        {
            path.add(node);
            if (node.getCount() == 0)
            {
                expand(node, player);
                return path;
            }
            node = node.getChild(treePolicy.move(node, player));
        }

        path.add(node);     // terminal node
        return path;
    }

    /**
     * Expansion: creates child nodes for all legal moves from {@code node}.
     * Subclasses may override to attach prior knowledge or other metadata.
     */
    protected void expand(MCTSNode<GAME> node, int player)
    {
        node.init();
    }

    /**
     * Simulation (default policy): plays moves uniformly at random on a copy
     * of the node's game until a terminal state is reached, then scores it.
     * A copy is used so the node's game state is not modified.
     */
    protected double rollout(MCTSNode<GAME> node, int player)
    {
        GAME copy = node.getGame().copy();
        while (!copy.isOver())
            copy.makeMove(defPolicy.move(copy));
        return utilFunc.eval(copy, player);
    }

    /** Backpropagation: propagates {@code outcome} to every node on the path. */
    private void backup(List<MCTSNode<GAME>> path, double outcome)
    {
        for (MCTSNode<GAME> node : path)
            node.update(outcome);
    }

    // -------------------------------------------------------------------------
    // Player
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
        int player = game.getCurPlayer();

        for (int i = 0; i < nSims; i++)
            simulate(root, player);

        return treePolicy.move(root, player);
    }

    /** Runs simulations within the time budget and returns the best move. */
    @Override
    public int move(GAME game, int timeout)
    {
        MCTSNode<GAME> root = new MCTSNode<>(game.copy());
        int player = game.getCurPlayer();
        long timeDue = System.currentTimeMillis() + timeout;

        while (System.currentTimeMillis() < timeDue)
            simulate(root, player);

        return treePolicy.move(root, player);
    }

    // -------------------------------------------------------------------------
    // Object
    // -------------------------------------------------------------------------

    @Override
    public String toString()
    {
        if (nSims > 0)
            return String.format("<MCTS treePolicy=%s defPolicy=%s nSims=%d>",
                    treePolicy, defPolicy, nSims);
        else
            return String.format("<MCTS treePolicy=%s defPolicy=%s>",
                    treePolicy, defPolicy);
    }
}
