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

    render: function() {
        return this.el;
    },

    update: function() {
//        this.buttons.start.disabled = !this.controller.isStart();
//        this.buttons.prev.disabled = !this.controller.isPrev();
//        this.buttons.next.disabled = !this.controller.isNext();
//        this.buttons.end.disabled = !this.controller.isEnd();
    }

};