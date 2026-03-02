package net.davidrobles.mauler.players.mcts;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.players.Player;
import net.davidrobles.mauler.players.UtilFunc;
import net.davidrobles.mauler.players.mcts.tree.TreePolicy;

import java.util.LinkedList;
import java.util.List;

/**
 * Monte Carlo Tree Search.
 */
public class MCTS<GAME extends Game<GAME>> implements Player<GAME> {

    protected int nSims;
    protected TreePolicy<GAME> treePolicy;
    protected Player<GAME> defPolicy;
    private UtilFunc<GAME> utilFunc = new UtilFunc<GAME>(1.0, -1.0, 0.0);
//    private List<MCTSObserver> mctsObservers = new ArrayList<MCTSObserver>();
//    private final boolean NOTIFY_OBSERVERS = false;

    public MCTS(TreePolicy<GAME> treePolicy, Player<GAME> defPolicy) {
        this(treePolicy, defPolicy, 0);
    }

//    public void addMctsObserver(MCTSObserver<GAME> observer) {
//        mctsObservers.add(observer);
//    }

    public MCTS(TreePolicy<GAME> treePolicy, Player<GAME> defPolicy, int nSims) {
        this.treePolicy = treePolicy;
        this.defPolicy = defPolicy;
        if (nSims < 0) {
            throw new IllegalArgumentException();
        }
        this.nSims = nSims;
    }

    public MCTS<GAME> copy() {
        return new MCTS<GAME>(treePolicy, defPolicy, nSims);
    }

    /** Runs one full simulation, from the current state to a terminal state. */
    public void simulate(MCTSNode<GAME> curPos, int player) // TODO: can i remove the player?
    {
        LinkedList<MCTSNode<GAME>> visitedNodes = simTree(curPos, player);
        backup(visitedNodes, simDefault(visitedNodes.getLast(), player));
    }

    /** Tree policy stage */
    private LinkedList<MCTSNode<GAME>> simTree(MCTSNode<GAME> curPos, int player) {
        LinkedList<MCTSNode<GAME>> nodes = new LinkedList<MCTSNode<GAME>>();
        MCTSNode<GAME> curNode = curPos;
        while (!curNode.getGame().isOver()) {
            nodes.add(curNode);
            if (nodes.getLast().getCount() == 0) {
                newNode(nodes.getLast(), player);
                return nodes;
            }
            int move = treePolicy.move(nodes.getLast(), player);
            curNode = curNode.getChild(move);
        }
        nodes.add(curNode);
        return nodes;
    }

    /** Default policy */
    protected double simDefault(MCTSNode<GAME> node, int player) {
        GAME copy = node.getGame().copy(); // TODO: why the copy?
        while (!copy.isOver()) {
            copy.makeMove(defPolicy.move(copy));
        }
        return utilFunc.eval(copy, player);
    }

    /** Backup of the values. */
    private void backup(List<MCTSNode<GAME>> visitedNodes, double outcome) {
        for (MCTSNode<GAME> node : visitedNodes) {
            node.update(outcome);
        }
    }

    protected void newNode(MCTSNode<GAME> node, int player) {
        node.init();
    }

    public void setUtilFunc(UtilFunc<GAME> utilFunc) {
        this.utilFunc = utilFunc;
    }

    ////////////
    // Player //
    ////////////

    @Override
    public boolean isDeterministic() {
        return false; // TODO: check tree and default policies
    }

    @Override
    public int move(GAME game) {
        MCTSNode<GAME> root = new MCTSNode<GAME>(game);
        int curPlayer = game.getCurPlayer();
        for (int i = 0; i < nSims; i++) {
            simulate(root, curPlayer);
        }

//        if (NOTIFY_OBSERVERS) {
//            notifyObserver(root);
//        }

        return treePolicy.move(root, curPlayer);
    }

//    private void notifyObserver(MCTSNode<GAME> node) {
//        for (MCTSObserver mctsObserver : mctsObservers) {
//            mctsObserver.simulationFinished(node);
//        }
//    }

    @Override
    public int move(GAME game, int timeout) {
        game = game.copy();
        MCTSNode<GAME> root = new MCTSNode<GAME>(game);
        int currentPlayer = game.getCurPlayer();
        long timeDue = System.currentTimeMillis() + timeout;
        while (System.currentTimeMillis() < timeDue) {
            simulate(root, currentPlayer);
        }
        return treePolicy.move(root, currentPlayer);
    }

    ///////////
    // Object //
    ///////////

    @Override
    public String toString() {
        if (nSims > 0) {
            return String.format("<MCTS treePolicy: %s, defPolicy: %s, nSims: %d>", treePolicy, defPolicy, nSims);
        } else {
            return String.format("<MCTS treePolicy: %s, defPolicy: %s>", treePolicy, defPolicy);
        }
    }
}
