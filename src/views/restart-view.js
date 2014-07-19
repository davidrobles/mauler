(function() {
    mauler.views = mauler.views || {};

    mauler.views.RestartView = function(options) {
        this.match = options.match;
        this.el = options.el;
        this.addListener();
    };

    mauler.views.RestartView.prototype = {

        constructor: mauler.views.RestartView,

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
}());