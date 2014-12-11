(function() {

    var CanvasView = function(options) {
        this.model = options.model;
        this.canvas = options.canvas || document.createElement("canvas");
        this.canvas.width = options.width || 100;
        this.canvas.height = options.height || 100;
        this.ctx = this.canvas.getContext("2d");
        this.squareSize = this.canvas.width / this.model.size;
        this.cellPer = 0.7;
        this.colors = {
            bg: "rgb(255, 219, 122)",
            border: "rgb(229, 197, 110)",
            cross: "rgba(231, 76, 60, 1.0)",
            crossLight: "rgba(231, 76, 60, 0.5)",
            nought: "rgba(41, 128, 185,1.0)",
            noughtLight: "rgba(41, 128, 185, 0.5)"
        };
        this.highlightedMoves = [];
        this.borderSize = 0.02; // percentage
        this.linesWidth = Math.round(this.canvas.width * this.borderSize);
        this.render();
    };

    CanvasView.squareToMove = function(row, col) {
        return mauler.games.tic.letters[row] + (col + 1);
    };

    CanvasView.prototype = {

        constructor: CanvasView,

        render: function() {
            this.drawBackground();
            this.drawLines();
            this.drawBorder();
            this.drawSquares();
            return this.canvas;
        },

        getCurPlayerColor: function() {
            return this.model.currentPlayer() === 0 ? this.colors.crossLight : this.colors.noughtLight;
        },

        drawBackground: function() {
            this.ctx.fillStyle = this.colors.bg;
            this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);
        },

        drawBorder: function() {
            this.ctx.beginPath();
            this.ctx.strokeStyle = this.colors.border;
            this.ctx.lineWidth = this.linesWidth;
            this.ctx.strokeRect(this.linesWidth / 2,
                    this.linesWidth / 2,
                    this.canvas.width - this.linesWidth,
                    this.canvas.height - this.linesWidth);
        },

        drawLines: function() {
            this.ctx.lineWidth = Math.round(this.canvas.width * this.borderSize);
            for (var i = 1; i < this.model.size; i++) {
                this.drawVerticalLine(i);
                this.drawHorizontalLine(i);
            }
        },

        drawSquares: function() {
            for (var row = 0; row < this.model.size; row++) {
                for (var col = 0; col < this.model.size; col++) {
                    var cellType = this.model.cell(row, col);
                    var hello = CanvasView.squareToMove(row, col);
                    if (cellType === 'CROSS') {
                        this.drawCross(row, col, this.colors.cross);
                    } else if (cellType === 'NOUGHT') {
                        this.drawNought(row, col, this.colors.nought);
                    } else if (!this.model.frozen && !this.model.isGameOver() && _.contains(this.highlightedMoves, hello)) {
                        var color = this.getCurPlayerColor();
                        if (this.model.currentPlayer() === 0) {
                            this.drawCross(row, col, color);
                        } else if (this.model.currentPlayer() === 1) {
                            this.drawNought(row, col, color);
                        }
                    }
                }
            }
        },

        drawHorizontalLine: function (row) {
            this.ctx.beginPath();
            this.ctx.moveTo(0, row * this.squareSize);
            this.ctx.lineTo(this.canvas.width, row * this.squareSize);
            this.ctx.stroke();
        },

        drawVerticalLine: function (col) {
            this.ctx.strokeStyle = this.colors.border;
            this.ctx.beginPath();
            this.ctx.moveTo(col * this.squareSize, 0);
            this.ctx.lineTo(col * this.squareSize, this.canvas.height);
            this.ctx.stroke();
        },

        drawCross: function (row, col, color) {
            var space = this.squareSize * ((1 - this.cellPer)),
                x = col * this.squareSize,
                y = row * this.squareSize;

            this.ctx.lineWidth = this.linesWidth * 2; // TODO make it relative to size
            this.ctx.strokeStyle = color;
            this.ctx.lineCap = 'round';

            this.ctx.beginPath();

            // Top Left to Bottom Right
            this.ctx.moveTo(x + space, y + space);
            this.ctx.lineTo(x + this.squareSize - space, y + this.squareSize - space);

            // Bottom Left to Top Right
            this.ctx.moveTo(x + space, y + this.squareSize - space);
            this.ctx.lineTo(x + this.squareSize - space, y + space);

            this.ctx.stroke();
        },

        drawNought: function (row, col, color) {
            this.ctx.beginPath();
            var centerX = col * this.squareSize + (this.squareSize / 2),
                centerY = row * this.squareSize + (this.squareSize / 2),
                radius = this.squareSize / 2 * this.cellPer,
                startAngle = 0,
                endAngle = 2 * Math.PI,
                counterClockwise = false;
            this.ctx.arc(centerX, centerY, radius, startAngle, endAngle, counterClockwise);
            this.ctx.fillStyle = color;
            this.ctx.fill();
        },

        // Callbacks

        update: function(event, model) {
            this.model = model;
            this.render();
        },

        // Clickable

        coordToSquare: function(x, y) {
            return {
                row: Math.floor(y / this.squareSize),
                col: Math.floor(x / this.squareSize)
            };
        },

        canvasLocationToMove: function(loc) {
            var square = this.coordToSquare(loc.x, loc.y);
            return CanvasView.squareToMove(square.row, square.col);
        }

    };

    mauler.games.tic = mauler.games.tic || {};
    mauler.games.tic.CanvasView = CanvasView;

}());