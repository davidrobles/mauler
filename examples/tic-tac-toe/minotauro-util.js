var Minotauro = Minotauro || {};

Minotauro.Util = {};

Minotauro.Util.playRandomGame = function(game) {
    console.log(game.toString());
    while (!game.isOver()) {
        game.move();
        console.log(game.toString());
    }
};

Minotauro.Util.playNGames = function(game, players, numGames) {
    var game = game.newGame();
    var stats = {
        oneWins: 0,
        twoWins: 0,
        draws: 0
    };
    for (var i = 0; i < numGames; i++) {
        while (!game.isOver()) {
            var curPlayer = players[game.curPlayer()];
            var move = curPlayer(game);
            game.move(move);
        }
        var outcomes = game.outcomes();
        if (outcomes['1'] === 'WIN') {
            stats.oneWins++;
        } else if (outcomes['2'] === 'WIN') {
            stats.twoWins++;
        } else {
            stats.draws++;
        }
        game.reset();
    }
    return stats;
};