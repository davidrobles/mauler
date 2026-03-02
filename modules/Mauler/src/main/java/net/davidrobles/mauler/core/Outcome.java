package net.davidrobles.mauler.core;

public enum Outcome {

    WIN ('W'), LOSS ('L'), DRAW ('D'), NA ('N');

    private final char c;

    private Outcome(char c) {
        this.c = c;
    }

    public char getChar() {
        return c;
    }

    public Outcome flip() {
        switch (this) {
            case WIN:  return LOSS;
            case LOSS: return WIN;
            default:   return this;
        }
    }

}
