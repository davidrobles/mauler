mauler.RestartView = function(options) {
    this.match = options.match;
    this.el = options.el;
    this.addListener();
};

mauler.RestartView.prototype = {

    constructor: mauler.RestartView,

    addListener: function () {
        this.el.addEventListener("click", function() {
            this.match.reset();
        }.bind(this));
    },

    // Match Events

    update: function() {
        if (!this.match.isStart()) {
            this.el.style.display = "none";
        } else {
            this.el.style.display = "block";
        }
    }

};