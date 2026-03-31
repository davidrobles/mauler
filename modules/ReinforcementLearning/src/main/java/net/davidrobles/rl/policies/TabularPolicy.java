package net.davidrobles.rl.policies;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.davidrobles.rl.valuefunctions.QFunction;
import net.davidrobles.rl.valuefunctions.VFunction;

public class TabularPolicy<S, A> implements RLPolicy<S, A>, StochasticPolicy<S, A> {
    private Map<S, A> map = new HashMap<S, A>();

    public A getAction(S state) {
        return map.get(state);
    }

    public void setAction(S state, A action) {
        map.put(state, action);
    }

    @Override
    public A getAction(S state, List<A> actions, QFunction<S, A> qf) {
        return map.get(state);
    }

    @Override
    public A getAction(S state, List<A> actions, VFunction<S> vf) {
        return map.get(state);
    }

    @Override
    public double getProbability(S state, A action) {
        return map.get(state).equals(action) ? 1.0 : 0.0;
    }
}
