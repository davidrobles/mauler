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
    this.linesWidth = Math.round(this.canvas.width * this.borderSize);
};

Tic.CanvasView.prototype = {

    constructor: Tic.CanvasView,

    render: function() {
        this.drawBackground();
        this.drawLines();
        this.drawBorder();
        this.drawSquares();
        return this.canvas;
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
        this.ctx.stroke();
    },

    drawVerticalLine: function (col) {
        this.ctx.strokeStyle = this.colors.border;
        this.ctx.beginPath();
        this.ctx.moveTo(col * this.squareSize, 0);
        this.ctx.lineTo(col * this.squareSize, this.canvas.height);
        this.ctx.stroke();
    },

    drawCross: function (row, col) {

        var space = this.squareSize * ((1 - this.cellPer)),
            x = col * this.squareSize,
            y = row * this.squareSize;

        this.ctx.lineWidth = 14; // TODO make it relative to size
        this.ctx.strokeStyle = this.colors.cross;
        this.ctx.lineCap = 'round';
        this.ctx.beginPath();

        // Top Left to Bottom Right
        this.ctx.moveTo(x + space, y + space);
        this.ctx.lineTo(x + this.squareSize - space, y + this.squareSize - space);
        this.ctx.stroke();

        // Bottom Left to Top Right
        this.ctx.moveTo(x + space, y + this.squareSize - space);
        this.ctx.lineTo(x + this.squareSize - space, y + space);
        this.ctx.stroke();
    },

    drawNought: function (row, col) {
        this.ctx.beginPath();
        var centerX = col * this.squareSize + (this.squareSize / 2),
            centerY = row * this.squareSize + (this.squareSize / 2),
            radius = this.squareSize / 2 * this.cellPer,
            startAngle = 0,
            endAngle = 2 * Math.PI,
            counterClockwise = false;
        this.ctx.arc(centerX, centerY, radius, startAngle, endAngle, counterClockwise);
        this.ctx.fillStyle = this.colors.nought;
        this.ctx.fill();
    },

    // Callbacks

    update: function(model) {
        this.model = model;
        this.render();
    }

};