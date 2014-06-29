var Minotauro = Minotauro || {};

Minotauro.Util = {};

Minotauro.Util.playRandomGame = function(game) {
    console.log(game.toString());
    while (!game.isOver()) {
        game.move();
        console.log(game.toString());
    }
};