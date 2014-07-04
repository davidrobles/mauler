var Mauler = Mauler || {};
Mauler.Players = Mauler.Players || {};

Mauler.Players.Random = function() {

};

Mauler.Players.Random.prototype = {

    constructor: Mauler.Players.Random,

    move: function(game) {
        return Math.floor(Math.random() * game.numMoves());
    }

};