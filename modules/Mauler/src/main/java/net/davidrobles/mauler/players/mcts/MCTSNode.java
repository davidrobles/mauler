package net.davidrobles.mauler.players.mcts;

import net.davidrobles.mauler.core.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * A Monte Carlo Tree Search node.
 */
public class MCTSNode<GAME extends Game<GAME>> {

//    private static int nodeIds = 1;

    // GRAPHVIZ STUFF
//    private int nodeId;

//    public int getNodeId() {
//        return nodeId;
//    }
//
//    public void setNodeId(int nodeId) {
//        this.nodeId = nodeId;
//    }

    private GAME game;
    private int count = 0; // N(s): number of count to this node
    private double value = 0.0; // Q(s, a)
    private List<MCTSNode<GAME>> children = new ArrayList<MCTSNode<GAME>>();

    public MCTSNode(GAME game) {
        this.game = game;
        // remove this, graphviz stuff
        // this wouldn't work if we run this in parallel
//        nodeId = nodeIds;
//        nodeIds++;
    }

    // TODO: why is init called externally instead of from the constructor
    public void init() {
        for (int move = 0; move < game.getNumMoves(); move++) {
            GAME newGame = game.copy();
            newGame.makeMove(move);
            children.add(new MCTSNode<GAME>(newGame));
        }
    }

    public void update(double outcome) {
        count++;
        value += (outcome - value) / count;
    }

    // N(s, a), number of times the given move has been taken in this node
    public int getActionCount(int move) {
        return children.get(move).getCount();
    }

    public double getActionValue(int move) {
        return children.get(move).getValue();
    }

    /////////////////////////
    // Getters and Setters //
    /////////////////////////

    public MCTSNode<GAME> getChild(int move) {
        return children.get(move);
    }

    public List<MCTSNode<GAME>> getChildren() {
        return children;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public GAME getGame() {
        return game;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    ////////////
    // Object //
    ////////////

    @Override
    public String toString() {
        return String.format("<MCTSNode count: %d, value: %f}\n", count, value);
    }
}
