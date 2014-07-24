(function() {

    var CanvasPlayer = function(options) {
        this.moveChosen = null;
        this.match = options.match;
        this.canvasView = options.canvasView;
        this.canvas = options.canvasView.canvas;
        this.addListeners();
    };

    CanvasPlayer.prototype = {

        constructor: CanvasPlayer,

        move: function() {
            if (!this.moveChosen) {
                throw new Error("No move chosen!");
            }
            var selMove = this.moveChosen;
            this.moveChosen = null;
            return selMove;
        },

        // Listeners

        addListeners: function() {
            this.addClickListener();
            this.addMouseMoveListener();
            this.addMouseOutListener();
        },

        addClickListener: function () {
            this.canvas.addEventListener("click", function(event) {
                if (this.match.curGame().currentPlayer() === 0) {
                    var canvasLoc = mauler.utils.windowToCanvas(this.canvas, event.clientX, event.clientY);
                    var move = this.canvasView.canvasLocationToMove(canvasLoc);
                    var moves = this.match.curGame().moves();
                    if (_.contains(moves, move)) {
                        this.moveChosen = move;
                        this.match.next();
                        this.canvasView.render(); // TODO Move somewhere else?
                        this.match.next();
                        // TODO add trigger()?
                    }
                }
            }.bind(this));
        },

        addMouseMoveListener: function () {
            this.canvas.addEventListener("mousemove", function(event) {
                if (this.match.curGame().currentPlayer() === 0) {
                    var canvasLoc = mauler.utils.windowToCanvas(this.canvas, event.clientX, event.clientY);
                    var move = this.canvasView.canvasLocationToMove(canvasLoc);
                    var moves = this.match.curGame().moves();
                    if (_.contains(moves, move)) {
                        this.canvasView.highlightedMoves = [move];
                        this.canvasView.render();
                    }
                }
            }.bind(this));
        },

        addMouseOutListener: function () {
            this.canvas.addEventListener("mouseout", function() {
                this.canvasView.highlightedMoves = [];
                this.canvasView.render();
            }.bind(this));
        }

    };

    mauler.games.tic = mauler.games.tic || {};
    mauler.games.tic.CanvasPlayer = CanvasPlayer;

}());