var Mauler = Mauler || {};

Mauler.Util = {};

Mauler.Util.playRandomGame = function(game) {
    console.log(game.toString());
    while (!game.isOver()) {
        game.move();
        console.log(game.toString());
    }
};

Mauler.Util.playNGames = function(game, players, numGames) {
    var stats = {
        oneWins: 0,
        twoWins: 0,
        draws: 0
    };
    for (var i = 0; i < numGames; i++) {
        var newGame = game.copy();
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

Mauler.Util.windowToCanvas = function(canvas, x, y) {
    var bbox = canvas.getBoundingClientRect();
    return {
        x: x - bbox.left * (canvas.width / bbox.width),
        y: y - bbox.top * (canvas.height / bbox.height)
    };
};