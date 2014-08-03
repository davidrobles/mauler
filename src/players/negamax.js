mauler.players.Negamax = function(options) {
    options = options || {};
    this.maxDepth = options.maxDepth || Number.MAX_VALUE;
    this.utilFunc = options.utilFunc || mauler.utils.utilFunc;
};

mauler.players.Negamax.prototype = {

    constructor: mauler.players.Negamax,

    negamax: function(game, curDepth) {
        if (game.isGameOver() || curDepth === this.maxDepth) {
            return { move: -1, score: this.utilFunc(game, game.currentPlayer()) };
        }
        var bestMove = -1,
            bestScore = -Number.MAX_VALUE;
        for (var move = 0; move < game.numMoves(); move++) { // TODO use 'n' variable
            var newGame = game.copy();
            newGame.move(move);
            var curMoveScore = this.negamax(newGame, curDepth + 1),
                curScore = -curMoveScore.score;
            if (curScore > bestScore) {
                bestMove = move;
                bestScore = curScore;
            }
        }
        return { move: bestMove, score: bestScore };
    },

    move: function(game) {
        return this.negamax(game.copy(), 0).move;
    }

};