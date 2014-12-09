(function() {

    // Base size used for drawing is 1000 pixels
    var TicTacToeSVGView = function(options) {
        this.model = options.model;
        this.sideLength = options.sideLength;
        this.svg = options.svg || document.createElement("svg");
        this.svg = d3.select(this.svg).append("g");
        this.svg.attr("transform", "scale(2.0)");
        this.colors = {
            bg: "rgb(255, 219, 122)",
            border: "rgb(229, 197, 110)",
            cross: "rgba(231, 76, 60, 1.0)",
            crossLight: "rgba(231, 76, 60, 0.5)",
            nought: "rgba(41, 128, 185,1.0)",
            noughtLight: "rgba(41, 128, 185, 0.5)"
        };
        this.lineWidth = this.sideLength * 0.02;
        this.borderWidth = this.sideLength * 0.04;
        this.render();
    };

    TicTacToeSVGView.squareToMove = function(row, col) {
        return mauler.games.tic.letters[row] + (col + 1);
    };

    TicTacToeSVGView.prototype.render = function() {
        this.drawBackground();
        this.drawLines();
        this.drawBorder();
        this.drawSquares();
        return this;
    };

    TicTacToeSVGView.prototype.drawBackground = function() {
        this.svg.append("rect")
            .attr({
                "class": "bg",
                "x": 0,
                "y": 0,
                "width": this.sideLength,
                "height": this.sideLength,
                "fill": this.colors.bg,
                "stroke": "none"
            });
    };

    TicTacToeSVGView.prototype.drawLines = function() {
        for (var i = 1; i < this.model.size; i++) {
            this.drawVerticalLine(i);
            this.drawHorizontalLine(i);
        }
    };

    TicTacToeSVGView.prototype.drawBorder = function() {
        this.svg.append("rect")
            .attr({
                "class": "border",
                "x": 0,
                "y": 0,
                "width": this.sideLength,
                "height": this.sideLength,
                "fill": "none",
                "stroke": this.colors.border,
                "stroke-width": this.borderWidth
            });
    };

    TicTacToeSVGView.prototype.drawHorizontalLine = function (row) {
        this.svg.append("line")
            .attr("x1", 0)
            .attr("y1", (this.sideLength / 3) * row)
            .attr("x2", this.sideLength)
            .attr("y2", (this.sideLength / 3) * row)
            .attr("stroke", this.colors.border)
            .attr("stroke-width", this.lineWidth);
    };

    TicTacToeSVGView.prototype.drawVerticalLine = function (col) {
        this.svg.append("line")
            .attr("x1", (this.sideLength / 3) * col)
            .attr("y1", 0)
            .attr("x2", (this.sideLength / 3) * col)
            .attr("y2", this.sideLength)
            .attr("stroke", this.colors.border)
            .attr("stroke-width", this.lineWidth);
    };

    TicTacToeSVGView.prototype.drawSquares = function() {
        for (var row = 0; row < this.model.size; row++) {
            for (var col = 0; col < this.model.size; col++) {
                var cellType = this.model.cell(row, col);
                if (cellType === 'CROSS') {
                    this.drawCross(row, col, this.colors.cross);
                } else if (cellType === 'NOUGHT') {
                    this.drawCircle(row, col, this.colors.nought);
                }
            }
        }
    };

    TicTacToeSVGView.prototype.drawCross = function (row, col, color) {
        var scale = d3.scale.ordinal().domain([0, 1, 2]).rangeRoundBands([0, this.sideLength], 1, 0.5),
            cellSize = this.sideLength / 11;

        this.svg.append("line")
            .attr("x1", function() {
                return scale(col) - cellSize;
            })
            .attr("y1", function() {
                return scale(row) - cellSize;
            })
            .attr("x2", function() {
                return scale(col) + cellSize;
            })
            .attr("y2", function() {
                return scale(row) + cellSize;
            })
            .attr("stroke", color)
            .attr("stroke-width", this.sideLength / 30);

        this.svg.append("line")
            .attr("x1", function() {
                return scale(col) - cellSize;
            })
            .attr("y1", function() {
                return scale(row) + cellSize;
            })
            .attr("x2", function() {
                return scale(col) + cellSize;
            })
            .attr("y2", function() {
                return scale(row) - cellSize;
            })
            .attr("stroke", color)
            .attr("stroke-width", this.sideLength / 30);
    };

    // Fix this
    TicTacToeSVGView.prototype.drawCircle = function (row, col, color) {

        var scale = d3.scale.ordinal().domain([0, 1, 2]).rangeRoundBands([0, this.sideLength], 1, 0.5);

        this.svg
            .append("circle")
            .attr("cx", function() {
                return scale(col);
            })
            .attr("cy", function() {
                return scale(row);
            })
            .attr("r", this.sideLength * 0.1)
            .attr("fill", color);
    };

    TicTacToeSVGView.prototype.update = function(event, model) {
        this.model = model;
        this.render();
    };

    mauler.games.tic = mauler.games.tic || {};
    mauler.games.tic.TicTacToeSVGView = TicTacToeSVGView;

})();