package net.davidrobles.rl.policies;

import net.davidrobles.rl.RLEnv;
import net.davidrobles.rl.valuefunctions.QFunction;
import net.davidrobles.rl.valuefunctions.VFunction;

import java.util.HashMap;
import java.util.Map;

public class TabularPolicy<S, A> implements RLPolicy<S, A>, StochasticPolicy<S, A>
{
    private Map<S, A> map = new HashMap<S, A>();

    public A getAction(S state)
    {
        return map.get(state);
    }

    public void setAction(S state, A action)
    {
        map.put(state, action);
    }

    @Override
    public A getAction(RLEnv<S, A> env, QFunction<S, A> qf)
    {
        return map.get(env.getCurrentState());
    }

    @Override
    public A getAction(RLEnv<S, A> env, VFunction<S> vf)
    {
        return map.get(env.getCurrentState());
    }

    @Override
    public double getProbability(S state, A action)
    {
        return map.get(state).equals(action) ? 1.0 : 0.0;
    }
}
