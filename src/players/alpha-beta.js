mauler.players.alphaBeta = function(options) {
    options = options || {};
    var maxDepth = options.maxDepth || Number.MAX_VALUE,
        evalFunc = options.evalFunc || mauler.utils.utilFunc;
    return function(game) {
        return (function alphaBeta(game, curDepth, alpha, beta) {
            if (game.isGameOver() || curDepth === maxDepth) {
                return { score: evalFunc(game, game.currentPlayer()) };
            }
            var bestMove = null,
                bestScore = -Number.MAX_VALUE,
                moves = game.moves();
            for (var move = 0; move < moves.length; move++) {
                var moveScore = alphaBeta(game.copy().move(move), curDepth + 1, -beta, -Math.max(alpha, bestScore));
                var curScore = -moveScore.score;
                if (curScore > bestScore) {
                    bestMove = move;
                    bestScore = curScore;
                    if (bestScore >= beta) {
                        return { move: bestMove, score: bestScore };
                    }
                }
            }
            return { move: bestMove, score: bestScore };
        }(game, 0, -Number.MAX_VALUE, Number.MAX_VALUE)).move;
    };
};
