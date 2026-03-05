package net.davidrobles.mauler.othello;

import java.util.List;
import net.davidrobles.mauler.core.Series;
import net.davidrobles.mauler.core.Strategy;
import net.davidrobles.mauler.strategies.Evaluator;
import net.davidrobles.mauler.strategies.RandomStrategy;
import net.davidrobles.mauler.strategies.minimax.AlphaBeta;

public class OthelloRun {
    // Disc-count heuristic: returns a score in [-1, 1] from the given player's perspective.
    // At terminal nodes returns +1/0/-1; at non-terminal nodes returns the disc advantage.
    private static final Evaluator<Othello> DISC_COUNT =
            (game, player) -> {
                int mine = game.getNumDiscs(player);
                int theirs = game.getNumDiscs(1 - player);
                if (game.isOver()) return mine > theirs ? 1.0 : theirs > mine ? -1.0 : 0.0;
                return (mine - theirs) / 64.0;
            };

    public static void main(String[] args) {
        Strategy<Othello> p1 = new AlphaBeta<Othello>(DISC_COUNT, 4);
        Strategy<Othello> p2 = new RandomStrategy<>();

        Series<Othello> series = new Series<>(Othello::new, 100, List.of(p1, p2));
        series.run();
    }
}
