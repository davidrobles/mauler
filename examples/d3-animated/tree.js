
///////////////////////
// Tic Tac Toe stuff //
///////////////////////

var tic = new mauler.games.tic.TicTacToe().move(4).move(0).move(6).move(2).move(0);

var nodeSize = 40;

var svgView = new mauler.games.tic.TicTacToeSVGView({
    model: tic,
    sideLength: nodeSize
});

var oneIter = function(node) {
    if (node === undefined) {
        return undefined;
    }
    // up
    if (node.game.numMoves() === 0 ||
        (node.children !== undefined && node.children.length === node.game.numMoves())) {
        return oneIter(node.parent);
    }
    if (node.children === undefined) {
        var child = { game: node.game.copy().move(0), parent: node };
        node.children = [child];
        return child;
    }
    if (node.children.length !== node.game.numMoves()) {
        var move = node.children.length;
        var child = { game: node.game.copy().move(move), parent: node };
        node.children.push(child);
        return child;
    }
};

var root = {
    game: tic
};

///////////////////////////
// Non Tic Tac Toe stuff //
///////////////////////////

var margin = { top: 50, right: 50, bottom: 100, left: 50 },
    width = 1200 - margin.left - margin.right,
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

window.nodes = tree(root);

var drawNodes = function() {

    // Enter links

    svg.selectAll(".link")
        .data(window.links)
        .enter()
        .append("path")
        .attr("class", "link")
        .attr("d", diagonal)
        .attr("fill", "none")
        .attr("stroke", "#666666")
        .attr("stroke-width", 2);

    // Update links
    svg.selectAll(".link")
        .attr("d", diagonal);

    // Enter nodes
    svg.selectAll("g.node-group")
        .data(window.nodes)
        .enter()
        .append("g")
        .attr("class", "node-group")
        .attr("transform", function(d) {
            return "translate(" + (d.x - 20) + ", " + d.y + ")"
        });

    // Update nodes
    svg.selectAll("g.node-group")
        .data(window.nodes)
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

};

var getLeafNodes = function() {
    var leafNodes = [];
    for (var i = 0; i < window.nodes.length; i++) {
        if (window.nodes[i].children === undefined || window.nodes[i].children.length === 0) {
            leafNodes.push(window.nodes[i]);
        }
    }
    return leafNodes;
};

var curNode = root;

var update = function() {
    if (window.curNode === undefined) {
        return clearInterval(timer);
    }
    window.curNode = oneIter(curNode);
    window.nodes = tree.nodes(root);
    window.links = tree.links(window.nodes);
    drawNodes();
};

var duration = 100,
    timer = setInterval(update, duration);
