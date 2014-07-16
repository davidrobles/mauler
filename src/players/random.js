var mauler = mauler || {};
mauler.players = mauler.players || {};

mauler.players.Random = function() {

};

mauler.players.Random.prototype = {

    constructor: mauler.players.Random,

    move: function(game) {
        return Math.floor(Math.random() * game.numMoves());
    }

};