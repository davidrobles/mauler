mauler.ControlsView = function(options) {
    this.controller = options.controller;
    this.initElements();
    this.addListeners();
};

mauler.ControlsView.prototype = {

    constructor: mauler.ControlsView,

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
            this.controller.start();
        }.bind(this));
        this.buttons.prev.addEventListener("click", function() {
            this.controller.prev();
        }.bind(this));
        this.buttons.next.addEventListener("click", function() {
            this.controller.next();
        }.bind(this));
        this.buttons.end.addEventListener("click", function() {
            this.controller.end();
        }.bind(this));
    },

    render: function() {
        return this.el;
    },

    update: function() {
        this.buttons.start.disabled = !this.controller.isStart();
        this.buttons.prev.disabled = !this.controller.isPrev();
        this.buttons.next.disabled = !this.controller.isNext();
        this.buttons.end.disabled = !this.controller.isEnd();
    }

};