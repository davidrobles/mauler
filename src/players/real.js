mauler.players.CanvasPlayer = function(options) {
    this.desiredMove = 0;
    this.controller = options.controller;
    this.canvasView = options.canvasView;
    this.canvas = options.canvasView.canvas;
    this.addListeners();
};

mauler.players.CanvasPlayer.prototype = {

    constructor: mauler.players.Random,

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
            var loc = mauler.utils.windowToCanvas(this.canvas, event.clientX, event.clientY);
            var square = this.canvasView.coordToSquare(loc.x, loc.y);
            var str = Tic.letters[square.row] + (square.col + 1);
            this.desiredMove = str;
            this.controller.next();
            // write square to
            this.canvasView.render(); // TODO Move somewhere else?
        }.bind(this));

        this.canvas.addEventListener("mousemove", function(event) {
            var loc = mauler.utils.windowToCanvas(this.canvas, event.clientX, event.clientY);
            var square = this.canvasView.coordToSquare(loc.x, loc.y);
            this.canvasView.mouse.over.row = square.row;
            this.canvasView.mouse.over.col = square.col;
            this.canvasView.render();
        }.bind(this));

        this.canvas.addEventListener("mouseout", function() {
            this.canvasView.mouse.over.row = null;
            this.canvasView.mouse.over.col = null;
            this.canvasView.render();
        }.bind(this));
    }

};