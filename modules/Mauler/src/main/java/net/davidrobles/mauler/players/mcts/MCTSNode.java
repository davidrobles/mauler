package net.davidrobles.mauler.players.mcts;

import net.davidrobles.mauler.core.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * A node in a Monte Carlo Tree Search tree.
 *
 * <p>Each node stores a game state and accumulates statistics across all
 * simulations that pass through it: visit count N(s) and mean action-value
 * Q(s,a) from the perspective of the current player at that state.
 *
 * <p>Children are created lazily via {@link #init()}, which expands one level
 * of successors for the stored game state.
 */
public class MCTSNode<GAME extends Game<GAME>>
{
    private final GAME game;
    private int count = 0;                              // N(s): number of visits to this node
    private double value = 0.0;                         // Q(s,a): mean outcome for the current player
    private final List<MCTSNode<GAME>> children = new ArrayList<>();

    public MCTSNode(GAME game)
    {
        this.game = game;
    }

    /**
     * Expands this node by creating one child per legal move.
     *
     * <p>Called externally rather than from the constructor so that MCTS can
     * control when expansion happens — expanding only the node chosen by the
     * tree policy, not every node at construction time.
     */
    public void init()
    {
        for (int move = 0; move < game.getNumMoves(); move++)
        {
            GAME newGame = game.copy();
            newGame.makeMove(move);
            children.add(new MCTSNode<>(newGame));
        }
    }

    /**
     * Updates this node's visit count and running mean value with {@code outcome}.
     *
     * @param outcome the result of the simulation, from the perspective of the
     *                node's current player (typically in [0, 1])
     */
    public void update(double outcome)
    {
        count++;
        value += (outcome - value) / count;
    }

    /** Returns N(s,a): the number of times {@code move} has been taken from this node. */
    public int getActionCount(int move)
    {
        return children.get(move).getCount();
    }

    /** Returns Q(s,a): the mean outcome after taking {@code move} from this node. */
    public double getActionValue(int move)
    {
        return children.get(move).getValue();
    }

    /////////////////////////
    // Getters and Setters //
    /////////////////////////

    public MCTSNode<GAME> getChild(int move)
    {
        return children.get(move);
    }

    public List<MCTSNode<GAME>> getChildren()
    {
        return children;
    }

    public int getCount()
    {
        return count;
    }

    /**
     * Overrides the visit count. Used by prior-knowledge initialisation
     * ({@code MCTSPrior}) and parallel root parallelisation ({@code MCTSRootP}).
     */
    public void setCount(int count)
    {
        this.count = count;
    }

    public GAME getGame()
    {
        return game;
    }

    public double getValue()
    {
        return value;
    }

    /**
     * Overrides the stored value. Used by prior-knowledge initialisation
     * ({@code MCTSPrior}).
     */
    public void setValue(double value)
    {
        this.value = value;
    }

    ////////////
    // Object //
    ////////////

    @Override
    public String toString()
    {
        return String.format("<MCTSNode count: %d, value: %f>", count, value);
    }
}
