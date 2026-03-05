package net.davidrobles.mauler.strategies.mcts.selection;

import java.util.Random;
import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.strategies.mcts.MCTSNode;

/**
 * UCB1 selection policy for Monte Carlo Tree Search (UCT).
 *
 * <p>At each node, selects the action maximizing:
 *
 * <pre>
 *   UCB1(s, a) = Q(s,a) + c * sqrt( ln(N) / N(s,a) )
 * </pre>
 *
 * where N is the parent visit count, N(s,a) is the child visit count, and Q(s,a) is the child's
 * mean value from the current player's perspective.
 *
 * <p>Any action with N(s,a) = 0 is treated as +infinity (the limit of the formula as N(s,a) &rarr;
 * 0), and one is chosen uniformly at random among them, ensuring every arm is tried before
 * exploitation begins.
 *
 * <p>References:
 *
 * <ul>
 *   <li>Auer, Cesa-Bianchi &amp; Fischer (2002). "Finite-time Analysis of the Multiarmed Bandit
 *       Problem." <em>Machine Learning</em>, 47:235&ndash;256. Proves O(ln n) regret and derives
 *       the theoretical constant c&nbsp;=&nbsp;&radic;2.
 *   <li>Kocsis &amp; Szepesvári (2006). "Bandit based Monte-Carlo Planning." <em>ECML</em>.
 *       Introduces UCT: UCB1 applied at each tree node, with proven convergence to the minimax
 *       value.
 *   <li>Browne et al. (2012). "A Survey of Monte Carlo Tree Search Methods." <em>IEEE TCIAIG</em>,
 *       4(1):1&ndash;43. Covers the exploration constant and empirical tuning strategies in
 *       §3.2&ndash;3.3.
 * </ul>
 */
public class UCB1<GAME extends Game<GAME>> implements SelectionPolicy<GAME> {
    /** Theoretical exploration constant proven to minimize regret (Auer et al., 2002). */
    public static final double C_THEORETICAL = Math.sqrt(2);

    private final double c;
    private final Random rng;

    public UCB1(double c, Random rng) {
        this.c = c;
        this.rng = rng;
    }

    public UCB1(double c) {
        this(c, new Random());
    }

    /** Creates a UCB1 policy using the theoretical exploration constant c = sqrt(2). */
    public UCB1() {
        this(C_THEORETICAL);
    }

    //////////////////////
    // SelectionPolicy  //
    //////////////////////

    @Override
    public int move(MCTSNode<GAME> node) {
        int unvisited = randomUnvisited(node);
        return unvisited != -1 ? unvisited : bestVisited(node);
    }

    /**
     * Returns a uniformly random unvisited arm, or -1 if all arms have been visited. Uses reservoir
     * sampling for uniform selection in a single O(k) pass.
     */
    private int randomUnvisited(MCTSNode<GAME> node) {
        int selected = -1;
        int count = 0;

        for (int move = 0; move < node.getGame().getNumMoves(); move++) {
            if (node.getActionVisits(move) == 0) {
                count++;
                if (rng.nextInt(count) == 0) selected = move;
            }
        }

        return selected;
    }

    /**
     * Returns the UCB1 score for {@code move} from the current node.
     *
     * @param logParentVisits precomputed ln(N) to avoid recomputing it per arm
     */
    private double ucb1Score(MCTSNode<GAME> node, int move, double logParentVisits) {
        // Negate: getActionValue returns Q from the child's (opponent's) perspective;
        // the zero-sum property means the current player's value is its negation.
        double exploitation = -node.getActionValue(move);
        double exploration = c * Math.sqrt(logParentVisits / node.getActionVisits(move));
        return exploitation + exploration;
    }

    /**
     * Returns the arm with the highest UCB1 score. Assumes all arms have been visited at least
     * once. N (parent visits) equals node.getVisits() because each simulation that passes through
     * this node visits exactly one child, keeping the counts in sync.
     */
    private int bestVisited(MCTSNode<GAME> node) {
        int bestMove = 0;
        double bestValue = Double.NEGATIVE_INFINITY;
        double logParentVisits = Math.log(node.getVisits());

        for (int move = 0; move < node.getGame().getNumMoves(); move++) {
            double value = ucb1Score(node, move, logParentVisits);

            if (value > bestValue) {
                bestMove = move;
                bestValue = value;
            }
        }

        return bestMove;
    }

    ////////////
    // Object //
    ////////////

    @Override
    public String toString() {
        return String.format("<UCB1 c=%.2f>", c);
    }
}
