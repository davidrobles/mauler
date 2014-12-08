
///////////////////////
// Tic Tac Toe stuff //
///////////////////////

var tic = new mauler.games.tic.TicTacToe().move(4).move(0).move(6).move(2).move(0);

var nodeSize = 60;

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
        var child = { game: node.game.copy().move(0), parent: node, id: nodes.length };
        node.children = [child];
        return child;
    }
    if (node.children.length !== node.game.numMoves()) {
        var move = node.children.length;
        var child = { game: node.game.copy().move(move), parent: node, id: nodes.length };
        node.children.push(child);
        return child;
    }
};

///////////////////////////
// Non Tic Tac Toe stuff //
///////////////////////////

var margin = { top: 50, right: 50, bottom: 100, left: 50 },
    width = 1800 - margin.left - margin.right,
    height = 900 - margin.top - margin.bottom;

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

var root = { game: tic, id: 0 },
    nodes = tree(root);

root.parent = root;
root.px = root.x;
root.py = root.y;

var drawNodes = function() {

    // Enter links
    svg.selectAll(".link")
        .data(tree.links(nodes), function(d) { return d.source.id + "-" + d.target.id; })
        .enter()
        .insert("path")
        .attr("class", "link")
        .attr("d", function(d) {
            var o = {
                x: d.source.px,
                y: d.source.py
            };
            return diagonal({
                source: o,
                target: o
            });
        })
        .attr("fill", "none")
        .attr("stroke", "#666666")
        .attr("stroke-width", 2);

    // Enter nodes
    svg.selectAll(".node-group")
        .data(tree.nodes(root), function(d) { return d.id; })
        .enter()
        .append("g")
        .attr("class", "node-group")
        .attr("transform", function(d) {
            svgView.model = d.game;
            svgView.svg = d3.select(this);
            svgView.render();
            return "translate(" + (d.parent.px - (nodeSize / 2)) + ", " + (d.parent.py - (nodeSize / 2)) + ")";
        });

    var t = svg.transition()
        .duration(200);

    // Update links
    t.selectAll(".link")
        .attr("d", diagonal);

    // Update nodes
    t.selectAll(".node-group")
        .attr("transform", function(d) {
            d.px = d.x;
            d.py = d.y;
            return "translate(" + (d.x - (nodeSize / 2)) + ", " + (d.y - (nodeSize / 2)) + ")"
        });

};

var curNode = root;

var update = function() {
    if (window.curNode === undefined) {
        return clearInterval(timer);
    }
    drawNodes();
    window.curNode = oneIter(curNode);
    nodes.push(window.curNode);
};

var duration = 200,
    timer = setInterval(update, duration);