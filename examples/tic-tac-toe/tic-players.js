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
    var nSims = 10000;
    var numMoves = game.numMoves();
    if (numMoves === 1) {
        return 0;
    }
    var outcomes = Array.apply(null, new Array(numMoves)).map(Number.prototype.valueOf, 0);
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
    return  argMax(outcomes);
};

Minotauro.Players.minimax = function(game) {
    var minmax = function(game, player, curDepth, maxDepth) {
        if (game.isOver() || curDepth === maxDepth) {
            return { move: -1, score: utilFunc(game, player) }
        }
        var bestMove = -1,
            bestScore = game.curPlayer() === player ? -Number.MAX_VALUE : Number.MAX_VALUE; // TODO change max and min values
        for (var move = 0; move < game.numMoves(); move++) { // TODO use 'n' variable
            var newGame = game.copy();
            newGame.move(move);
            var curMoveScore = minmax(newGame, player, curDepth + 1, maxDepth);
            if (game.curPlayer() === player) {
                if (curMoveScore.score > bestScore) {
                    bestMove = move;
                    bestScore = curMoveScore.score;
                }
            } else if (curMoveScore.score < bestScore) {
                bestMove = move;
                bestScore = curMoveScore.score;
            }
        }
        return { move: bestMove, score: bestScore };
    };
    return minmax(game.copy(), game.curPlayer(), 0, Number.MAX_VALUE).move;
};

Minotauro.Players.alphaBeta = function(game) {
    var ab = function(game, maxDepth, curDepth, alpha, beta) {
        if (game.isOver() || curDepth === maxDepth) {
            return { move: -1, score: utilFunc(game, game.curPlayer()) }; // TODO remove move? or change to null?
        }
        var bestMove = -1,
            bestScore = -Number.MAX_VALUE;
        for (var move = 0; move < game.numMoves(); move++) {
            var newGame = game.copy();
            newGame.move(move);
            var curMoveScore = ab(newGame, maxDepth, curDepth + 1, -beta, -Math.max(alpha, bestScore)),
                curScore = -curMoveScore.score;
            if (curScore > bestScore) {
                bestMove = move;
                bestScore = curScore;
                if (bestScore >= beta) {
                    return { move: bestMove, score: bestScore };
                }
            }
        }
        return { move: bestMove, score: bestScore };
    };
    return ab(game.copy(), Number.MAX_VALUE, 0, -Number.MAX_VALUE, Number.MAX_VALUE).move;
};