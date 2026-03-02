package net.davidrobles.mauler.othello.ef;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.players.EvalFunc;
import net.davidrobles.mauler.players.UtilFunc;

public class UtilityWrapper<GAME extends Game<GAME>> implements EvalFunc<GAME>
{
    private EvalFunc<GAME> evalFunc;
    private UtilFunc<GAME> utilFunc;

    public UtilityWrapper(EvalFunc<GAME> evalFunc) {
        this(evalFunc, new UtilFunc<GAME>());
    }

    public UtilityWrapper(EvalFunc<GAME> evalFunc, UtilFunc<GAME> utilFunc) {
        this.evalFunc = evalFunc;
        this.utilFunc = utilFunc;
    }

    @Override
    public double eval(GAME game, int player) {
        return (game.isOver() ? utilFunc : evalFunc).eval(game, player);
    }
}
