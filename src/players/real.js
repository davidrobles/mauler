var Mauler = Mauler || {};
Mauler.Players = Mauler.Players || {};

Mauler.Players.Real = function() {
    this.move = 0;
};

Mauler.Players.Real.prototype = {

    constructor: Mauler.Players.Random,

    move: function(game) {
        return this.move;
    }

};