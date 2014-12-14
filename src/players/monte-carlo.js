ma.players.monteCarlo = function(options) {
    options = options || {};
    var numSims = options.numSims || 5000,
        evalFunc = options.evalFunc || ma.utilFunc;
    return function(game) {
        var moves = game.moves();
        if (moves.length === 1) {
            return 0;
        }
        var outcomes = Array.apply(null, new Array(moves.length)).map(Number.prototype.valueOf, 0);
        for (var i = 0; i < numSims; i++) {
            var newGame = game.copy();
            var move = i % moves.length;
            newGame.move(move);
            while (!newGame.isGameOver()) {
                var randMove = Math.floor(Math.random() * newGame.moves().length);
                newGame.move(randMove);
            }
            outcomes[move] += evalFunc(newGame, game.currentPlayer());
        }
        return ma.argMax(outcomes);
    };
};
