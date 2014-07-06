var Mauler = Mauler || {};
Mauler.Players = Mauler.Players || {};

Mauler.Players.CanvasPlayer = function(options) {
    this.desiredMove = 0;
    this.controller = options.controller;
    this.canvasView = options.canvasView;
    this.canvas = options.canvasView.canvas;
    this.addListeners();
};

Mauler.Players.CanvasPlayer.prototype = {

    constructor: Mauler.Players.Random,

    move: function() {
        if (!this.desiredMove) {
            throw new Error("No move chosen!");
        }
        var chosenMove = this.desiredMove;
        this.desiredMove = null;
        return chosenMove;
    },

    // Listeners

    addListeners: function() {
        this.canvas.addEventListener("click", function(event) {
            var loc = Mauler.Util.windowToCanvas(this.canvas, event.clientX, event.clientY);
            var square = this.coordToSquare(loc.x, loc.y);
            var str = Tic.letters[square.row] + (square.col + 1);
            this.controller.next();
            // write square to
        }.bind(this));

        this.canvas.addEventListener("mousemove", function(event) {
            var loc = Mauler.Util.windowToCanvas(this.canvas, event.clientX, event.clientY);
            var square = this.canvasView.coordToSquare(loc.x, loc.y);
            this.canvasView.mouse.over.row = square.row;
            this.canvasView.mouse.over.col = square.col;
        }.bind(this));

        this.canvas.addEventListener("mouseout", function() {
            this.canvasView.mouse.over.row = null;
            this.canvasView.mouse.over.col = null;
        }.bind(this));
    }

};