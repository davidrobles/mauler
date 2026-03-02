package net.davidrobles.rl.valuefunctions;

public interface QFunctionObserver<S, A>
{
    void qFunctionUpdated(QFunction<S, A> qFunction);
}
