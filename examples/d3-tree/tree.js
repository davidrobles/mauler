///////////////////////
// Tic Tac Toe stuff //
///////////////////////

var tic = new mauler.games.tic.TicTacToe();

tic.move(0);
tic.move(2);
tic.move(3);
tic.move(1);
tic.move(1);

console.log()

var svgView = new mauler.games.tic.TicTacToeSVGView({
    model: tic,
    svg: document.getElementById("tic-svg")
});

var generator = function(node) {
    for (var i = 0; i < node.game.numMoves(); i++) {
        var newTic = node.game.copy();
        newTic.move(i);
        var newGameNode = { children: [], game: newTic };
        node.children.push(newGameNode);
        generator(newGameNode);
    }
};

var gameNode = { name: "root", children: [], game: tic };

generator(gameNode);

///////////////////////////
// Non Tic Tac Toe stuff //
///////////////////////////

document.getElementById("button").addEventListener("click", function() {
    opti.children = [{"name": "David", "size": 5000}];
    drawNodes();
});

var opti = {
    "name": "AspectRatioBanker",
    "size": 7074
};

var root = {
    "name": "flare",
    "children": [
        {
            "name": "analytics",
            "children": [
                {
                    "name": "cluster",
                    "children": [
                        {"name": "AgglomerativeCluster", "size": 3938},
                        {"name": "CommunityStructure", "size": 3812},
                        {"name": "HierarchicalCluster", "size": 6714},
                        {"name": "MergeEdge", "size": 743}
                    ]
                },
                {
                    "name": "graph",
                    "children": [
                        {"name": "BetweennessCentrality", "size": 3534},
                        {"name": "LinkDistance", "size": 5731},
                        {"name": "MaxFlowMinCut", "size": 7840},
                        {"name": "ShortestPaths", "size": 5914},
                        {"name": "SpanningTree", "size": 3416}
                    ]
                },
                {
                    "name": "optimization",
                    "children": [
                        opti
                    ]
                }
            ]
        }]
};

var width = 1800,
    height = 900;

var diagonal = d3.svg.diagonal()
    .projection(function(d) { return [d.x, d.y]; });


var svg = d3.select("body")
    .append("svg")
    .attr("width", this.width)
    .attr("height", this.height)
    .attr("style", "background-color: wheat");

var tree = d3.layout.tree().size([width, height]);
var nodes = tree(gameNode);

var drawNodes = function() {
    svg.selectAll("path")
        .data(tree.links(nodes))
        .enter().append("path")
        .attr("d", diagonal)
        .attr("fill", "none")
        .attr("stroke", "blue");

    svg.selectAll("g")
        .data(nodes)
        .enter()
        .append("g")
        .attr("transform", function(d) {
            return "translate(" + (d.x - 15) + ", " + (d.y - 30) + ")"
        })
        .attr("class", "here");

    svg.selectAll(".here")
        .each(function(d) {
            var tic = new mauler.games.tic.TicTacToe();
            tic.move(Math.floor(Math.random() * 4));
            tic.move(Math.floor(Math.random() * 4));
            tic.move(Math.floor(Math.random() * 4));
            svgView.model = d.game;
            svgView.svg = d3.select(this);
            svgView.render();
        });
};

drawNodes();