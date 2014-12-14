
/////////////
// Minimax //
/////////////

var minimax = function(node) {
    var player = node.game.currentPlayer(),
        curDepth = 0,
        maxDepth = 6,
        evalFunc = mauler.utils.utilFunc;
    return (function minimax(node, curDepth) {
        if (node.game.isGameOver() || curDepth === maxDepth) {
            return node.score = evalFunc(node.game, player);
        }
        var bestScore = node.game.currentPlayer() === 0 ? -Number.MAX_VALUE : Number.MAX_VALUE,
            bestFunc = node.game.currentPlayer() === 0 ? Math.max : Math.min,
            childrenSize = node.children ? node.children.length : 0;
        for (var child = 0; child < childrenSize; child++) {
            var curScore = minimax(node.children[child], curDepth + 1);
            bestScore = bestFunc(bestScore, curScore);
        }
        return node.score = bestScore;
    })(node, curDepth);
};

///////////////////////
// Tic Tac Toe stuff //
///////////////////////

var tic = new mauler.games.TicTacToe().move(4).move(0).move(6).move(2);

var nodeSize = 70;

var svgView = new mauler.games.tic.TicTacToeSVGView({
    model: tic,
    sideLength: nodeSize
});

var depthFirstTreeGenerator = function(node) {
    var moves = node.game.moves();
    if (moves.length > 0) {
        node.children = [];
        for (var i = 0; i < moves.length; i++) {
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
    width = 4000 - margin.left - margin.right,
    height = 700 - margin.top - margin.bottom;

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
            return "translate(" + (d.x - 20) + ", " + d.y + ")"
        });

    // Draw nodes
    svg.selectAll(".node-group")
        .each(function(node) {
            svgView.model = node.game;
            svgView.svg = d3.select(this);
            svgView.render();
        });


    // Leaf nodes

    //svg.selectAll(".node-group")
    //    .selectAll("rect.border")
    //    .filter(function(d) {
    //        return d.game.isGameOver();
    //    })
    //    .attr("stroke", "#ff0000")
    //    .attr("stroke-width", 2);

    // green values
    svg.selectAll(".node-group")
        .selectAll("rect.border")
        .filter(function(d) {
            return d.score === 1;
        })
        .attr("stroke", "#008000")
        .attr("stroke-width", 2);

    // red values
    svg.selectAll(".node-group")
        .selectAll("rect.border")
        .filter(function(d) {
            return d.score === -1;
        })
        .attr("stroke", "#ff0000")
        .attr("stroke-width", 2);

    // gray values
    svg.selectAll(".node-group")
        .selectAll("rect.border")
        .filter(function(d) {
            return d.score === 0;
        })
        .attr("stroke", "#686868")
        .attr("stroke-width", 2);

    svg.selectAll(".node-group")
        .append("circle")
        .attr("cx", function() {
            return nodeSize / 2;
        })
        .attr("cy", function() {
            return nodeSize + 30;
        })
        .attr("r", "20")
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
            return nodeSize + 38;
        })
        .attr("font-family", "Helvetica")
        .attr("font-size", "24px")
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