package net.davidrobles.mauler.othello.ef;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.strategies.Evaluator;
import net.davidrobles.mauler.strategies.TerminalEvaluator;

public class UtilityWrapper<GAME extends Game<GAME>> implements Evaluator<GAME> {
    private Evaluator<GAME> evalFunc;
    private TerminalEvaluator<GAME> utilFunc;

    public UtilityWrapper(Evaluator<GAME> evalFunc) {
        this(evalFunc, new TerminalEvaluator<GAME>());
    }

    public UtilityWrapper(Evaluator<GAME> evalFunc, TerminalEvaluator<GAME> utilFunc) {
        this.evalFunc = evalFunc;
        this.utilFunc = utilFunc;
    }

    @Override
    public double evaluate(GAME game, int player) {
        return (game.isOver() ? utilFunc : evalFunc).evaluate(game, player);
    }
}
