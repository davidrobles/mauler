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
//        this.el.disabled = this.controller.isStart();
    }

};