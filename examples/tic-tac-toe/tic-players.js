var Minotauro = Minotauro || {};

Minotauro.Players = {};

Minotauro.Players.random = function(game) {
    return Math.floor(Math.random() * game.numMoves());
};

Minotauro.Players.monteCarlo = function(game) {
    var numMoves = game.numMoves();
    if (numMoves === 1) {
        return 0;
    }
    var outcomes = [];

    for (var i = 0; i < nSims; i++) {
        while (game.isOver()) {
            var move = Minotauro.Players.random(game);
            game.move(move);
        }

    }

    return null;
};

Minotauro.Players.minimax = function(game) {

};

Minotauro.Players.alphaBeta = function(game) {

};