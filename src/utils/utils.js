(function() {

    ma = ma || {};

    ma.argMax = function(outcomes) {
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

    ma.playRandomGame = function(game) {
        console.log(game.toString());
        while (!game.isGameOver()) {
            game.move();
            console.log(game.toString());
        }
    };

    ma.playNGames = function(game, players, numGames) {
        var stats = {
            oneWins: 0,
            twoWins: 0,
            draws: 0
        };
        for (var i = 0; i < numGames; i++) {
            var newGame = game.copy();
            while (!newGame.isGameOver()) {
                var curPlayer = players[newGame.currentPlayer()];
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

    ma.windowToCanvas = function(canvas, x, y) {
        var bbox = canvas.getBoundingClientRect();
        return {
            x: x - bbox.left * (canvas.width / bbox.width),
            y: y - bbox.top * (canvas.height / bbox.height)
        };
    };

    ma.utilFunc = function(game, player) {
        if (game.isGameOver()) {
            var outcomes = game.outcomes();
            switch (outcomes[player]) {
                case 'WIN':
                    return 1.0;
                case 'DRAW':
                    return 0.0;
                case 'LOSS':
                    return -1.0;
            }
        }
    };

}());