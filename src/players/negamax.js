mauler.players.negamax = function(options) {
    options = options || {};
    var maxDepth = options.maxDepth || Number.MAX_VALUE,
        evalFunc = options.evalFunc || mauler.utils.utilFunc;
    return function(game) {
        return (function negamax(game, curDepth) {
            if (game.isGameOver() || curDepth === maxDepth) {
                return { score: evalFunc(game, game.currentPlayer()) };
            }
            var bestMove = null,
                bestScore = -Number.MAX_VALUE,
                moves = game.moves();
            for (var move = 0; move < moves.length; move++) {
                var moveScore = negamax(game.copy().move(move), curDepth + 1);
                var curScore = -moveScore.score;
                if (curScore > bestScore) {
                    bestMove = move;
                    bestScore = curScore;
                }
            }
            return { move: bestMove, score: bestScore };
        }(game, 0)).move;
    };
};
