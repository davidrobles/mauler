(function() {

    var InfoView = function(options) {
        this.model = options.model;
        this.el = options.el;
        this.update(null, this.model);
    };

    InfoView.prototype = {

        constructor: InfoView,

        update: function(event, model) {
            this.model = model;
            if (this.model.isGameOver()) {
                var outcomes = this.model.outcomes();
                if (outcomes[0] === "WIN") {
                    this.el.innerHTML = "Player 1 Wins!";
                } else if (outcomes[1] === "WIN") {
                    this.el.innerHTML = "Player 2 Wins!";
                } else {
                    this.el.innerHTML = "Draw!";
                }
            } else {
                var curPlayer = this.model.currentPlayer() + 1;
                this.el.innerHTML = "Turn: Player " + curPlayer;
            }
        }

    };

    mauler.views.InfoView = InfoView;

}());