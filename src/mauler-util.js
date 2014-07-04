var Mauler = Mauler || {};

Mauler.Util = {};

Mauler.Util.argMax = function(outcomes) {
    var maxArg = 0,
        maxValue = outcomes[0];
    for (var i = 1; i < outcomes.length; i++) {
        if (outcomes[i] > maxValue) {
            maxArg = i;
            maxValue = outcomes[i];
        }
    }
    return maxArg;
};

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
            var move = curPlayer.move(newGame);
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

Mauler.Util.UtilFunc = function(options) {
    options = options || {};
    this.win  = options.win  ||  1.0;
    this.draw = options.draw ||  0.0;
    this.loss = options.loss || -1.0;
};

Mauler.Util.UtilFunc.prototype.eval = function(game, player) {
    if (game.isOver()) {
        var outcomes = game.outcomes();
        switch (outcomes[player]) {
            case "WIN":
                return this.win;
            case "DRAW":
                return this.draw;
            case "LOSS":
                return this.loss;
        }
    }
};