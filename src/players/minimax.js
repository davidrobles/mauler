mauler.players.Minimax = function(options) {
    options = options || {};
    this.maxDepth = options.maxDepth || Number.MAX_VALUE;
    this.utilFunc = options.utilFunc || mauler.utils.utilFunc;
};

mauler.players.Minimax.prototype = {

    constructor: mauler.players.Minimax,

    minimax: function(game, player, curDepth) {
        if (game.isGameOver() || curDepth === this.maxDepth) {
            return { move: -1, score: this.utilFunc(game, player) };
        }
        var bestMove = -1,
            bestScore = game.currentPlayer() === player ? -Number.MAX_VALUE : Number.MAX_VALUE,
            moves = game.moves();
        for (var move = 0; move < moves.length; move++) {
            var newGame = game.copy();
            newGame.move(move);
            var curMoveScore = this.minimax(newGame, player, curDepth + 1);
            if (game.currentPlayer() === player) {
                if (curMoveScore.score > bestScore) {
                    bestMove = move;
                    bestScore = curMoveScore.score;
                }
            } else if (curMoveScore.score < bestScore) {
                bestMove = move;
                bestScore = curMoveScore.score;
            }
        }
        return { move: bestMove, score: bestScore };
    },

    move: function(game) {
        return this.minimax(game.copy(), game.currentPlayer(), 0).move;
    }

};

mauler.players.minimax = function(options) {
    options = options || {};
    var maxDepth = options.maxDepth || Number.MAX_VALUE,
        evalFunc = options.evalFunc || mauler.utils.utilFunc;
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
