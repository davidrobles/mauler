ma.players.Random = function() {

};

ma.players.Random.prototype = {

    constructor: ma.players.Random,

    move: function(game) {
        return Math.floor(Math.random() * game.numMoves());
    }

};

ma.players.randomFunc = function(game) {
    return Math.floor(Math.random() * game.numMoves());
};