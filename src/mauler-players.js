var Mauler = Mauler || {};

Mauler.Players = {};

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