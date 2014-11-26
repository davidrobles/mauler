(function() {

    var TicTacToeSVGView = function(options) {
        this.model = options.model;
        this.svg = options.svg || document.createElement("svg");
        this.squareSize = this.svg.width / this.model.size;
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
        this.linesWidth = Math.round(this.svg.width * this.borderSize);

        this.width = parseInt(this.svg.getAttribute("width"));
        this.height = parseInt(this.svg.getAttribute("height"));

        this.render();
    };

    TicTacToeSVGView.squareToMove = function(row, col) {
        return mauler.games.tic.letters[row] + (col + 1);
    };

    TicTacToeSVGView.prototype.hack = function() {

    };

    TicTacToeSVGView.prototype.render = function() {

        d3.select(this.svg)
            .selectAll(".test")
            .remove();

        this.drawBackground();
        this.drawBorder();
        this.drawLines();
        this.drawSquares();
        return this;
    };



    TicTacToeSVGView.prototype.drawBorder = function() {
        d3.select(this.svg)
            .append("rect")
            .attr({
                "x": 0,
                "y": 0,
                "width": this.width,
                "height": this.height,
                "fill": "none",
                "stroke": this.colors.border,
                "stroke-width": 4
            });
    };

    TicTacToeSVGView.prototype.drawBackground = function() {
        d3.select(this.svg)
            .style("background-color", this.colors.bg);
    };

    TicTacToeSVGView.prototype.drawSquares = function() {
        for (var row = 0; row < this.model.size; row++) {
            for (var col = 0; col < this.model.size; col++) {
                var cellType = this.model.cell(row, col);
                if (cellType === 'CROSS') {
                    this.drawCross(row, col, this.colors.cross);
                } else if (cellType === 'NOUGHT') {
                    this.drawCross(row, col, this.colors.nought);
                }
            }
        }
    };

    // Fix this
    TicTacToeSVGView.prototype.drawCross = function (row, col, color) {

        var scale = d3.scale.ordinal().domain([0, 1, 2]).rangeRoundBands([0, this.width], 1, 0.5);

        d3.select(this.svg)
            .append("circle")
            .attr("class", "test")
            .attr("cx", function() {
                return scale(col);
            })
            .attr("cy", function() {
                return scale(row);
            })
            .attr("r", 20)
            .attr("fill", color);
    };

    TicTacToeSVGView.prototype.drawLines = function() {
        for (var i = 1; i < this.model.size; i++) {
            this.drawVerticalLine(i);
            this.drawHorizontalLine(i);
        }
    };

    TicTacToeSVGView.prototype.drawHorizontalLine = function (row) {
        d3.select(this.svg)
            .append("line")
            .attr("x1", function() {
                return 0;
            })
            .attr("y1", function() {
                return (100 / 3) * row;
            })
            .attr("x2", function() {
                return 100;
            })
            .attr("y2", function() {
                return (100 / 3) * row;
            })
            .attr("stroke", this.colors.border)
            .attr("stroke-width", 2)
            .attr("transform", "scale(2, 2)");
    };

    TicTacToeSVGView.prototype.drawVerticalLine = function (col) {
        d3.select(this.svg)
            .append("line")
            .attr("x1", function() {
                return (100 / 3) * col;
            })
            .attr("y1", function() {
                return 0;
            })
            .attr("x2", function() {
                return (100 / 3) * col;
            })
            .attr("y2", function() {
                return 100;
            })
            .attr("stroke", this.colors.border)
            .attr("stroke-width", 2)
            .attr("transform", "scale(2, 2)");
    };

    TicTacToeSVGView.prototype.update = function(event, model) {
        this.model = model;
        this.render();
    };

    mauler.games.tic = mauler.games.tic || {};
    mauler.games.tic.TicTacToeSVGView = TicTacToeSVGView;

})();