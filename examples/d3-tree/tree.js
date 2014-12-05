
/////////////
// Minimax //
/////////////


var minimax = function(node, player, curDepth) {
    if (node.game.isGameOver() || curDepth === this.maxDepth) {
        return node.score = mauler.utils.utilFunc(node.game, player);
    }
    var bestScore = node.game.currentPlayer() === player ? -Number.MAX_VALUE : Number.MAX_VALUE,
        numMoves = node.children ? node.children.length : 0;
    for (var move = 0; move < numMoves; move++) {
        var curScore = minimax(node.children[move], player, curDepth + 1);
        if (node.game.currentPlayer() === player) {
            if (curScore > bestScore) {
                bestScore = curScore;
            }
        } else if (curScore < bestScore) {
            bestScore = curScore;
        }
    }
    return node.score = bestScore;
};

///////////////////////
// Tic Tac Toe stuff //
///////////////////////

var tic = new mauler.games.tic.TicTacToe().move(4).move(0).move(6).move(2);

var nodeSize = 50;

var svgView = new mauler.games.tic.TicTacToeSVGView({
    model: tic,
    sideLength: nodeSize
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
minimax(root, root.game.currentPlayer(), 0);

///////////////////////////
// Non Tic Tac Toe stuff //
///////////////////////////

var margin = { top: 50, right: 50, bottom: 100, left: 50 },
    width = 3000 - margin.left - margin.right,
    height = 600 - margin.top - margin.bottom;

var diagonal = d3.svg.diagonal()
    .projection(function(d) { return [d.x, d.y]; });

var svg = d3.select("body")
    .append("svg")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom)
    .attr("style", "background-color: wheat")
    .append("g")
    .attr("transform", "translate(" + margin.left + ", " + margin.top + ")");

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

    svg.selectAll("g.node-group")
        .data(nodes)
        .enter()
        .append("g")
        .attr("class", "node-group")
        .attr("transform", function(d) {
            return "translate(" + (d.x - 12) + ", " + d.y + ")"
        });

    // Draw nodes
    svg.selectAll(".node-group")
        .each(function(node) {
            svgView.model = node.game;
            svgView.svg = d3.select(this);
            svgView.render();
        });

    svg.selectAll(".node-group")
        .selectAll("rect.border")
        .filter(function(d) {
            return d.game.isGameOver();
        })
        .attr("stroke", "#ff0000")
        .attr("stroke-width", 2);

    svg.selectAll(".node-group")
        .append("circle")
        .attr("cx", function() {
            return nodeSize / 2;
        })
        .attr("cy", function() {
            return nodeSize + 40;
        })
        .attr("r", "20")
        .attr("font-family", "Helvetica")
        .attr("font-size", "30px")
        .attr("text-anchor", "middle")
        .attr("fill", "white")
        .attr("stroke", "black")
        .attr("stroke-width", "2px")
        .text(function(d) {
            return d.score;
        });

    svg.selectAll(".node-group")
        .append("text")
        .attr("x", function() {
            return nodeSize / 2;
        })
        .attr("y", function() {
            return nodeSize + 48;
        })
        .attr("font-family", "Helvetica")
        .attr("font-size", "26px")
        .attr("text-anchor", "middle")
        .attr("fill", "red")
        .text(function(d) {
            return d.score;
        });

    svg.selectAll(".node-group")
        .attr("transform", function() {
            return this.getAttribute("transform") + " scale(0.6)";
        });


};

drawNodes();