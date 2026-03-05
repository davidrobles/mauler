package net.davidrobles.mauler.othello.ef;

import java.util.Random;
import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.strategies.Evaluator;

public class RandomEF<GAME extends Game<GAME>> implements Evaluator<GAME> {
    private Random rng = new Random();

    @Override
    public double evaluate(GAME game, int player) {
        return rng.nextDouble() * 2 - 1;
    }
}
