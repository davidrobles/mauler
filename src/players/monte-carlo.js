var mauler = mauler || {};
mauler.players = mauler.players || {};

mauler.players.MonteCarlo = function(options) {
    options = options || {};
    this.numSims = options.numSims || 100;
    this.utilFunc = options.utilFunc || new mauler.utils.UtilFunc();
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
            while (!newGame.isOver()) {
                var randMove = Math.floor(Math.random() * newGame.numMoves());
                newGame.move(randMove);
            }
            outcomes[move] += this.utilFunc.eval(newGame, game.curPlayer());
        }
        return mauler.utils.argMax(outcomes);
    }

};