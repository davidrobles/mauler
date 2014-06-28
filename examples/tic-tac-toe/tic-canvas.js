var Tic = Tic || {};

Tic.CanvasView = function(options) {
    this.model = options.model;
    this.canvas = options.canvas || document.createElement("canvas");
    this.canvas.width = options.width || 100;
    this.canvas.height = options.height || 100;
    this.ctx = this.canvas.getContext("2d");
    this.squareSize = this.canvas.width / 3;
    this.cellPer = 0.8;
    this.colors = {
        bg: "#EECD74",
        border: "#CEB26A",
        cross: "rgb(80, 94, 147)",
        nought: "rgb(182, 40, 65)"
    };
};

Tic.CanvasView.prototype = {

    constructor: Tic.CanvasView,

    render: function() {
        this.renderBackground();
        this.renderBorder();
        this.renderSquares();
        return this.canvas;
    },

    renderBackground: function() {
        this.ctx.fillStyle = this.colors.bg;
        this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);
    },

    renderBorder: function() {
        var borderSize = Math.round(this.canvas.width * 0.02);
        this.canvas.style.border = borderSize + "px solid " + this.colors.border;
    },

    renderSquares: function() {
        for (var row = 0; row < 3; row++) {
            for (var col = 0; col < 3; col++) {
                var cellType = this.model.cell(row, col);
                if (cellType === 'CROSS') {
                    this.drawCross(row, col);
                } else if (cellType === 'NOUGHT') {
                    this.drawNought(row, col);
                }
            }
        }
    },

    drawCross: function (row, col) {
        var x = col * this.squareSize + (this.squareSize * ((1 - this.cellPer) / 2)),
            y = row * this.squareSize + (this.squareSize * ((1 - this.cellPer) / 2)),
            pieceWidth = this.squareSize * this.cellPer,
            pieceHeight = this.squareSize * this.cellPer;
        this.ctx.fillStyle = this.colors.cross;
        this.ctx.fillRect(x, y, pieceWidth, pieceHeight);
    },

    drawNought: function (row, col) {
        this.ctx.beginPath();
        var centerX = col * this.squareSize + (this.squareSize / 2),
            centerY = row * this.squareSize + (this.squareSize / 2),
            radius = this.squareSize / 2 * this.cellPer, // 80% of the square size
            startAngle = 0,
            endAngle = 2 * Math.PI,
            counterClockwise = false;
        this.ctx.arc(centerX, centerY, radius, startAngle, endAngle, counterClockwise);
        this.ctx.fillStyle = this.colors.nought;
        this.ctx.fill();
    }

};