mauler.players.MonteCarlo = function(options) {
    options = options || {};
    this.numSims = options.numSims || 5000;
    this.utilFunc = options.utilFunc || mauler.utils.utilFunc;
};

mauler.players.MonteCarlo.prototype = {

    constructor: mauler.players.MonteCarlo,

    move: function(game) {
        var moves = game.moves();
        if (moves.length === 1) {
            return 0;
        }
        var outcomes = Array.apply(null, new Array(moves.length)).map(Number.prototype.valueOf, 0);
        for (var i = 0; i < this.numSims; i++) {
            var newGame = game.copy();
            var move = i % moves.length;
            newGame.move(move);
            while (!newGame.isGameOver()) {
                var randMove = Math.floor(Math.random() * newGame.moves().length);
                newGame.move(randMove);
            }
            outcomes[move] += this.utilFunc(newGame, game.currentPlayer());
        }
        return mauler.utils.argMax(outcomes);
    }

};