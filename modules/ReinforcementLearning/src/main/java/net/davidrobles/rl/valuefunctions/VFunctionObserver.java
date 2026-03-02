package net.davidrobles.rl.valuefunctions;

public interface VFunctionObserver<S>
{
    /**
     * This method is called when the observable State Value Function changes.
     * @param vFunction the new state value function
     */
    void valueFunctionChanged(VFunction<S> vFunction);
}
