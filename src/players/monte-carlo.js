var Mauler = Mauler || {};
Mauler.Players = Mauler.Players || {};

Mauler.Players.MonteCarlo = function(options) {
    options = options || {};
    this.numSims = options.numSims || 100;
};

Mauler.Players.MonteCarlo.prototype = {

    constructor: Mauler.Players.MonteCarlo,

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
            while (!newGame.isOver()) {
                var randMove = Math.floor(Math.random() * newGame.numMoves());
                newGame.move(randMove);
            }
            outcomes[move] += utilFunc(newGame, game.curPlayer());
        }
        return Mauler.Util.argMax(outcomes);
    }

};