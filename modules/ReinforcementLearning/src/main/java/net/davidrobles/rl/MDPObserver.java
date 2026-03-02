package net.davidrobles.rl;

/**
 * A class can implement the MDPObserver interface when it wants
 * to be informed of changed in the current state of an MDP.
 *
 * @author David Robles
 */
public interface MDPObserver
{
    /**
     * This method is called whenever the current state of the
     * observed MDP is changed.
     */
    void currentStateChanged();
}
