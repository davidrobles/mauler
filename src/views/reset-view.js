mauler.ResetView = function(options) {
    this.controller = options.controller;
    this.el = options.el;
    this.addListener();
};

mauler.ResetView.prototype = {

    constructor: mauler.ResetView,

    addListener: function () {
        this.el.addEventListener("click", function() {
            this.controller.reset();
        }.bind(this));
    },

    update: function() {
        if (!this.controller.isStart()) {
            this.el.style.display = "none";
        } else {
            this.el.style.display = "block";
        }

    }

};