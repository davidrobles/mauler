var Tic = Tic || {};

Tic.CanvasView = function(options) {
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
        cross: "rgb(255, 73, 77)",
        nought: "rgb(85, 119, 254)"
    };
    this.borderSize = 0.02; // percentage
};

Tic.CanvasView.prototype = {

    constructor: Tic.CanvasView,

    render: function() {
        this.renderBackground();
        this.renderBorder();
        this.renderLines();
        this.renderSquares();
        return this.canvas;
    },

    renderBackground: function() {
        this.ctx.fillStyle = this.colors.bg;
        this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);
    },

    renderBorder: function() {
        var borderSize = Math.round(this.canvas.width * this.borderSize); // TODO refactor this
        this.canvas.style.border = borderSize + "px solid " + this.colors.border;
    },

    renderLines: function() {
        this.ctx.lineWidth = Math.round(this.canvas.width * this.borderSize);
        for (var i = 1; i < this.model.size; i++) {
            this.drawVerticalLine(i);
            this.drawHorizontalLine(i);
        }
    },

    renderSquares: function() {
        for (var row = 0; row < this.model.size; row++) {
            for (var col = 0; col < this.model.size; col++) {
                var cellType = this.model.cell(row, col);
                if (cellType === 'CROSS') {
                    this.drawCross(row, col);
                } else if (cellType === 'NOUGHT') {
                    this.drawNought(row, col);
                }
            }
        }
    },

    drawHorizontalLine: function (row) {
        this.ctx.beginPath();
        this.ctx.moveTo(0, row * this.squareSize);
        this.ctx.lineTo(this.canvas.width, row * this.squareSize);
        this.ctx.lineTo(this.canvas.width, row * this.squareSize);
        this.ctx.stroke();
    },

    drawVerticalLine: function (row) {
        this.ctx.strokeStyle = this.colors.border;
        this.ctx.beginPath();
        this.ctx.moveTo(row * this.squareSize, 0);
        this.ctx.lineTo(row * this.squareSize, this.canvas.height);
        this.ctx.stroke();
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