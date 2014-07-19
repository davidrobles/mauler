mauler.Tic = mauler.Tic || {};

mauler.Tic.InfoView = function(options) {
    this.model = options.model;
    this.el = options.el;
};

mauler.Tic.InfoView.prototype = {

    constructor: mauler.Tic.InfoView,

    update: function(event, model) {
        this.model = model;
        if (this.model.isOver()) {
            var outcomes = this.model.outcomes();
            if (outcomes[0] === "WIN") {
                this.el.innerHTML = "Player 1 Wins!";
            } else if (outcomes[1] === "WIN") {
                this.el.innerHTML = "Player 2 Wins!";
            } else {
                this.el.innerHTML = "Draw!";
            }
        } else {
            var curPlayer = this.model.curPlayer() + 1;
            this.el.innerHTML = "Turn: Player " + curPlayer;
        }
    }

};