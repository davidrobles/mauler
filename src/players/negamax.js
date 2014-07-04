var Mauler = Mauler || {};
Mauler.Players = Mauler.Players || {};

Mauler.Players.Negamax = function(options) {
    options = options || {};
    this.maxDepth = options.maxDepth || Number.MAX_VALUE;
    this.utilFunc = new Mauler.Util.UtilFunc();
};

Mauler.Players.Negamax.prototype = {

    constructor: Mauler.Players.Negamax,

    negamax: function(game, curDepth) {
        if (game.isOver() || curDepth === this.maxDepth) {
            return { move: -1, score: this.utilFunc.eval(game, game.curPlayer()) }
        }
        var bestMove = -1,
            bestScore = -Number.MAX_VALUE;
        for (var move = 0; move < game.numMoves(); move++) { // TODO use 'n' variable
            var newGame = game.copy();
            newGame.move(move);
            var curMoveScore = this.negamax(newGame, curDepth + 1);
            var curScore = -curMoveScore.score;
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