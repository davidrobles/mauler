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
        bg: "rgb(255, 219, 122)",
        border: "rgb(229, 197, 110)",
        cross: "rgb(255, 73, 77)",
        nought: "rgb(85, 119, 254)"
    };
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
        var borderSize = Math.round(this.canvas.width * 0.02); // TODO refactor this
        this.canvas.style.border = borderSize + "px solid " + this.colors.border;
    },

    renderLines: function() {
        var ctx = this.ctx;
        ctx.lineWidth = Math.round(this.canvas.width * 0.02)
        var ss = this.canvas.width / this.model.size;
        for (var row = 1; row < this.model.size; row++) {
            // vertical
            ctx.strokeStyle = this.colors.border;
            ctx.beginPath();
            ctx.moveTo(row * ss, 0);
            ctx.lineTo(row * ss, this.canvas.height);
            this.ctx.stroke();
            // horizontal
            ctx.beginPath();
            ctx.moveTo(0, row * ss);
            ctx.lineTo(this.canvas.width, row * ss);
            this.ctx.stroke();
        }
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