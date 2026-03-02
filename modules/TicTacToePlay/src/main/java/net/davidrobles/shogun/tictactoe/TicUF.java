package net.davidrobles.shogun.tictactoe;

import net.davidrobles.mauler.players.EvalFunc;
import net.davidrobles.mauler.tictactoe.TicTacToe;

import static net.davidrobles.mauler.core.Outcome.*;

public class TicUF implements EvalFunc<TicTacToe> {

    private double win = 1.0, draw = 0.0, loss = -1.0;

    public TicUF() {

    }

    public TicUF(double win, double draw, double loss) {
        this.win = win;
        this.draw = draw;
        this.loss = loss;
    }

    @Override
    public double eval(TicTacToe ticTacToe, int player) {
        if (ticTacToe.isOver()) {
            switch (ticTacToe.getOutcome()[player]) {
                case WIN:  return win;
                case DRAW: return draw;
                case LOSS: return loss;
            }
        }
        throw new Error("The utility function must be called only at the end of the game.");
    }

    @Override
    public String toString() {
        return "Tic-Tac-Toe Utility Function";
    }

}
