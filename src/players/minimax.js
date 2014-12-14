ma.players.minimax = function(options) {
    options = options || {};
    var maxDepth = options.maxDepth || Number.MAX_VALUE,
        evalFunc = options.evalFunc || ma.utilFunc;
    return function(game) {
        var player = game.currentPlayer();
        return (function minimax(game, curDepth) {
            if (game.isGameOver() || curDepth === maxDepth) {
                return { score: evalFunc(game, player) };
            }
            var bestMove = null,
                bestScore = game.currentPlayer() === player ? -Number.MAX_VALUE : Number.MAX_VALUE,
                moves = game.moves();
            for (var move = 0; move < moves.length; move++) {
                var moveScore = minimax(game.copy().move(move), curDepth + 1);
                if (game.currentPlayer() === player) {
                    if (moveScore.score > bestScore) {
                        bestMove = move;
                        bestScore = moveScore.score;
                    }
                } else if (moveScore.score < bestScore) {
                    bestMove = move;
                    bestScore = moveScore.score;
                }
            }
            return { move: bestMove, score: bestScore };
        }(game, 0)).move;
    };
};
