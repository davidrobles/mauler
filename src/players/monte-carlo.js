mauler.players.MonteCarlo = function(options) {
    options = options || {};
    this.numSims = options.numSims || 5000;
    this.utilFunc = options.utilFunc || mauler.utils.utilFunc;
};

mauler.players.MonteCarlo.prototype = {

    constructor: mauler.players.MonteCarlo,

    move: function(game) {
        var numMoves = game.numMoves();
        if (numMoves === 1) {
            return 0;
        }
        var outcomes = Array.apply(null, new Array(numMoves)).map(Number.prototype.valueOf, 0);
        for (var i = 0; i < this.numSims; i++) {
            var newGame = game.copy(); // TODO refactor copy method
            var move = i % numMoves;
            newGame.move(move);
            while (!newGame.isGameOver()) {
                var randMove = Math.floor(Math.random() * newGame.numMoves());
                newGame.move(randMove);
            }
            outcomes[move] += this.utilFunc(newGame, game.currentPlayer());
        }
        return mauler.utils.argMax(outcomes);
    }

};