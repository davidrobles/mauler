mauler.players.Minimax = function(options) {
    options = options || {};
    this.maxDepth = options.maxDepth || Number.MAX_VALUE;
    this.utilFunc = options.utilFunc || new mauler.utils.UtilFunc();
};

mauler.players.Minimax.prototype = {

    constructor: mauler.players.AlphaBeta,

    minimax: function(game, player, curDepth) {
        if (game.isOver() || curDepth === this.maxDepth) {
            return { move: -1, score: this.utilFunc.eval(game, player) };
        }
        var bestMove = -1,
            bestScore = game.curPlayer() === player ? -Number.MAX_VALUE : Number.MAX_VALUE; // TODO change max and min values
        for (var move = 0; move < game.numMoves(); move++) { // TODO use 'n' variable
            var newGame = game.copy();
            newGame.move(move);
            var curMoveScore = this.minimax(newGame, player, curDepth + 1);
            if (game.curPlayer() === player) {
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
        return this.minimax(game.copy(), game.curPlayer(), 0).move;
    }

};