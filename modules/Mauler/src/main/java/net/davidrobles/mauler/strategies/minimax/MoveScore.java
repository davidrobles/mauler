package net.davidrobles.mauler.strategies.minimax;

/**
 * Bundles a move index with its evaluated score, used as the return type of the recursive minimax
 * search helpers.
 *
 * <p>Package-private: not part of the public API.
 */
class MoveScore {
    private final int move;
    private final double score;

    MoveScore(double score, int move) {
        this.move = move;
        this.score = score;
    }

    int getMove() {
        return move;
    }

    double getScore() {
        return score;
    }

    @Override
    public String toString() {
        return String.format("MoveScore(move=%d, score=%.4f)", move, score);
    }
}
