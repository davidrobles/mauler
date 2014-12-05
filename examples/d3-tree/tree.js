///////////////////////
// Tic Tac Toe stuff //
///////////////////////

var tic = new mauler.games.tic.TicTacToe();

tic.move(0).move(2).move(3).move(1).move(1).move(0);

var svgView = new mauler.games.tic.TicTacToeSVGView({
    model: tic
    //svg: document.getElementById("tic-svg")
});

var depthFirstTreeGenerator = function(node) {
    var numMoves = node.game.numMoves();
    if (numMoves > 0) {
        node.children = [];
        for (var i = 0; i < numMoves; i++) {
            var newTic = node.game.copy();
            newTic.move(i);
            var newGameNode = { game: newTic };
            node.children.push(newGameNode);
            depthFirstTreeGenerator(newGameNode);
        }
    }
};

var root = {
    name: "root",
    game: tic
};

depthFirstTreeGenerator(root);

///////////////////////////
// Non Tic Tac Toe stuff //
///////////////////////////

var width = 500,
    height = 500;

var diagonal = d3.svg.diagonal()
    .projection(function(d) { return [d.x, d.y]; });


var svg = d3.select("body")
    .append("svg")
    .attr("width", this.width + 80)
    .attr("height", this.height + 80)
    .attr("style", "background-color: wheat");

var tree = d3.layout.tree().size([width, height]);

tree.separation(function(a, b) {
    return a.parent == b.parent ? 1.5 : 2;
});

var nodes = tree(root);

var drawNodes = function() {
    // Draw edges
    svg.selectAll("path")
        .data(tree.links(nodes))
        .enter().append("path")
        .attr("d", diagonal)
        .attr("fill", "none")
        .attr("stroke", "#666666")
        .attr("stroke-width", 2);

    svg.selectAll("g")
        .data(nodes)
        .enter()
        .append("g")
        .attr("transform", function(d) {
            return "translate(" + (d.x - 30) + ", " + d.y + ")"
        })
        .attr("class", "here");

    // Draw nodes
    svg.selectAll(".here")
        .each(function(node) {
            svgView.model = node.game;
            svgView.svg = d3.select(this);
            svgView.render();
        });

    svg.selectAll(".here")
        .selectAll("rect.border")
        .filter(function(d) {
            return d.game.isGameOver();
        })
        .attr("stroke", "#ff0000")
        .attr("stroke-width", 2);

    svg.selectAll(".here")
        .filter(function(d) {
            return d.game.isGameOver();
        })
        .append("text")
        .attr("x", function(d) {
            return 30;
        })
        .attr("y", function(d) {
            return 75;
        })
        .attr("text-anchor", "middle")
        .attr("fill", "red")
        .text(function(d) {
            return mauler.utils.utilFunc(d.game, 0);
        });
};

drawNodes();