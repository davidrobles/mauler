var Mauler = Mauler || {};
Mauler.Players = Mauler.Players || {};

Mauler.Players.AlphaBeta = function() {

};

Mauler.Players.AlphaBeta.prototype = {

    constructor: Mauler.Players.AlphaBeta,

    ab: function(game, maxDepth, curDepth, alpha, beta) {
        if (game.isOver() || curDepth === maxDepth) {
            return { move: -1, score: utilFunc(game, game.curPlayer()) }; // TODO remove move? or change to null?
        }
        var bestMove = -1,
            bestScore = -Number.MAX_VALUE;
        for (var move = 0; move < game.numMoves(); move++) {
            var newGame = game.copy();
            newGame.move(move);
            var curMoveScore = ab(newGame, maxDepth, curDepth + 1, -beta, -Math.max(alpha, bestScore)),
                curScore = -curMoveScore.score;
            if (curScore > bestScore) {
                bestMove = move;
                bestScore = curScore;
                if (bestScore >= beta) {
                    return { move: bestMove, score: bestScore };
                }
            }
        }
        return { move: bestMove, score: bestScore };
    },

    move: function(game) {
        return this.ab(game.copy(), Number.MAX_VALUE, 0, -Number.MAX_VALUE, Number.MAX_VALUE).move;
    }

};