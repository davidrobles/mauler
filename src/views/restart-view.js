(function() {

    var RestartView = function(options) {
        this.match = options.match;
        this.el = options.el;
        this.update();
        this.addListener();
    };

    RestartView.prototype = {

        constructor: RestartView,

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

    mauler.views.RestartView = RestartView;

}());