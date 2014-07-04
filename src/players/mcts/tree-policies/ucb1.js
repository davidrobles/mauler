var Mauler = Mauler || {};
Mauler.Players = Mauler.Players || {};

Mauler.Players.UCB1 = function(options) {
    options = options || {};
    this.c = options.c; // TODO add random number generator
};

Mauler.Players.UCB1.prototype = {

    constructor: Mauler.Players.UCB1,

    move: function(node, player) {
        var bestMove = -1,
            max = node.game.curPlayer() === player,
            bestValue = max ? -Number.MAX_VALUE : Number.MAX_VALUE,
            nb = 0;
        for (var move = 0; move < node.game.numMoves(); move++) {
            nb += node.actionCount(move);
        }
        for (var move = 0; move < node.game.numMoves(); move++) {
            var value = 0;

            // ensures that each arm is selected once before further exploration
            if (node.actionCount(move) === 0)
            {
                var bias = (Math.random() * 1000) + 10;
                value = max ? (100000000 - bias) : (-100000000 + bias); // TODO: refactor
            }
            else
            {
                var exploitation = node.actionValue(move);
                var exploration = this.c * Math.sqrt(Math.log(nb) / node.actionCount(move));
                value += exploitation;
                value += max ? exploration : -exploration;
            }

            if (max)
            {
                if (value > bestValue) {
                    bestMove = move;
                    bestValue = value;
                }
            }
            else if (value < bestValue) { // min
                bestMove = move;
                bestValue = value;
            }
        }
        return bestMove;
    }

};