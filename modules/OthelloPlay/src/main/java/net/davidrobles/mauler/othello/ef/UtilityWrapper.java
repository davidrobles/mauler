package net.davidrobles.mauler.othello.ef;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.players.Evaluator;
import net.davidrobles.mauler.players.UtilFunc;

public class UtilityWrapper<GAME extends Game<GAME>> implements Evaluator<GAME>
{
    private Evaluator<GAME> evalFunc;
    private UtilFunc<GAME> utilFunc;

    public UtilityWrapper(Evaluator<GAME> evalFunc) {
        this(evalFunc, new UtilFunc<GAME>());
    }

    public UtilityWrapper(Evaluator<GAME> evalFunc, UtilFunc<GAME> utilFunc) {
        this.evalFunc = evalFunc;
        this.utilFunc = utilFunc;
    }

    @Override
    public double evaluate(GAME game, int player) {
        return (game.isOver() ? utilFunc : evalFunc).evaluate(game, player);
    }
}
