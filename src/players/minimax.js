mauler.players.Minimax = function(options) {
    options = options || {};
    this.maxDepth = options.maxDepth || Number.MAX_VALUE;
    this.utilFunc = options.utilFunc || mauler.utils.utilFunc;
};

mauler.players.Minimax.prototype = {

    constructor: mauler.players.Minimax,

    minimax: function(game, player, curDepth) {
        if (game.isOver() || curDepth === this.maxDepth) {
            return { move: -1, score: this.utilFunc(game, player) };
        }
        var bestMove = -1,
            bestScore = game.currentPlayer() === player ? -Number.MAX_VALUE : Number.MAX_VALUE;
        for (var move = 0; move < game.numMoves(); move++) { // TODO use 'n' variable
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