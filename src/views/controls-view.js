(function() {

    ControlsView = function(options) {
        this.match = options.match;
        this.initElements();
        this.addListeners();
    };

    ControlsView.prototype = {

        constructor: ControlsView,

        initElements: function() {
            this.el = document.createElement("div");
            this.buttons = {
                start: document.createElement("button"),
                prev: document.createElement("button"),
                next: document.createElement("button"),
                end: document.createElement("button")
            };
            this.buttons.start.innerHTML = "|&#60;";
            this.buttons.prev.innerHTML = "&#60;";
            this.buttons.next.innerHTML = "&#62;";
            this.buttons.end.innerHTML = "&#62;|";
            this.el.appendChild(this.buttons.start);
            this.el.appendChild(this.buttons.prev);
            this.el.appendChild(this.buttons.next);
            this.el.appendChild(this.buttons.end);
        },

        addListeners: function () {
            this.buttons.start.addEventListener("click", function() {
                this.match.start();
            }.bind(this));
            this.buttons.prev.addEventListener("click", function() {
                this.match.prev();
            }.bind(this));
            this.buttons.next.addEventListener("click", function() {
                this.match.next();
            }.bind(this));
            this.buttons.end.addEventListener("click", function() {
                this.match.end();
            }.bind(this));
        },

        render: function() {
            return this.el;
        },

        update: function() {
            this.buttons.start.disabled = !this.match.isStart();
            this.buttons.prev.disabled = !this.match.isPrev();
            this.buttons.next.disabled = !this.match.isNext();
            this.buttons.end.disabled = !this.match.isEnd();
        }

    };

    mauler.views.ControlsView = ControlsView;

}());