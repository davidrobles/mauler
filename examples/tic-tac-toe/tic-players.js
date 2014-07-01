var Minotauro = Minotauro || {};

Minotauro.Players = {};

Minotauro.Players.random = function(game) {
    return Math.floor(Math.random() * game.numMoves());
};

var argMax = function(outcomes) {
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

var utilFunc = function(game, player) {
    var winValue = 1.0,
        lossValue = -1.0,
        drawValue = 0.0;
    if (game.isOver()) {
        var outcomes = game.outcomes();
        switch (outcomes[player]) {
            case "WIN":
                return winValue;
            case "LOSS":
                return lossValue;
            case "DRAW":
                return drawValue;
        }
    }
};

Minotauro.Players.monteCarlo = function(game) {
    var nSims = 1000;
    var numMoves = game.numMoves();
    if (numMoves === 1) {
        return 0;
    }
    var outcomes = [0, 0, 0];
    for (var i = 0; i < nSims; i++) {
        var newGame = game.copy(); // TODO refactor copy method
        var move = i % numMoves;
        newGame.move(move);
        while (!newGame.isOver()) {
            var randMove = Minotauro.Players.random(newGame);
            newGame.move(randMove);
        }
        outcomes[move] += utilFunc(newGame, game.curPlayer());
    }
    return argMax(outcomes);
};

Minotauro.Players.minimax = function(game) {

};

Minotauro.Players.alphaBeta = function(game) {
    var ab = function(game, maxDepth, curDepth, alpha, beta) {
        if (game.isOver() || curDepth === maxDepth) {
            return { move: utilFunc(game, game.curPlayer()), score: -1 }
        }
        var bestMove = -1,
            bestScore = -10000;
        for (var move = 0; move < game.numMoves(); move++) {
            var newGame = game.copy();
            newGame.move(move);
            var curMoveScore = ab(newGame, maxDepth, curDepth + 1, -beta, -Math.max(alpha, bestScore)),
                curScore = -curMoveScore.score;
            if (curScore > bestScore) {
                bestScore = curScore;
                bestMove = move;
                if (bestScore >= beta) {
                    return { move: bestMove, score: bestScore };
                }
            }
        }
        return { move: bestMove, score: bestScore };
    };
    return ab(game, 10000, 0, -100000, 1000000).move;
};