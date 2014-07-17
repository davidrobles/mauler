var Tic = Tic || {};

Tic.InfoView = function(options) {
    this.model = options.model;
    this.el = options.el;
};

Tic.InfoView.prototype = {

    constructor: Tic.InfoView,

    update: function(model) {
        this.model = model;

        if (this.model.isOver()) {
            this.el.innerHTML = "Game Over!"
        } else {
            var curPlayer = this.model.curPlayer() + 1;
            this.el.innerHTML = "Turn: Player " + curPlayer;
        }
    }

};