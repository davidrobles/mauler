mauler.RestartView = function(options) {
    this.controller = options.controller;
    this.el = options.el;
    this.addListener();
};

mauler.RestartView.prototype = {

    constructor: mauler.RestartView,

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