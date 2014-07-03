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
    var stats = {
        oneWins: 0,
        twoWins: 0,
        draws: 0
    };
    var newGame = game.copy();
    for (var i = 0; i < numGames; i++) {
        newGame.reset();
        while (!newGame.isOver()) {
            var curPlayer = players[newGame.curPlayer()];
            var move = curPlayer(newGame);
            newGame.move(move);
        }
        var outcomes = newGame.outcomes();
        if (outcomes[0] === 'WIN') {
            stats.oneWins++;
        } else if (outcomes[1] === 'WIN') {
            stats.twoWins++;
        } else {
            stats.draws++;
        }
    }
    return stats;
};