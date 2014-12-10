var depthFirstIteration = function(node) {
    if (node === undefined) {
        return undefined;
    }
    // go to parent node
    if (node.game.numMoves() === 0 ||
        (node.children !== undefined && node.children.length === node.game.numMoves())) {
        return depthFirstIteration(node.parent);
    }
    else {
        if (node.children === undefined) {
            node.children = [];
        }
        var child = {
            game: node.game.copy().move(node.children.length),
            parent: node,
            id: nodes.length
        };
        node.children.push(child);
        return child;
    }
};

var counter = 1;

var breadthFirstIteration = function() {
    var node = undefined;
    if (queue.length > 0) {
        node = queue.shift();
        if (node.parent !== undefined && node.id !== 0) {
            if (node.parent.children === undefined) {
                node.parent.children = [];
            }
            node.parent.children.push(node);
        }
        var numMoves = node.game.numMoves();
        for (var i = 0; i < numMoves; i++) {
            var child = {
                game: node.game.copy().move(i),
                parent: node,
                id: counter++
            };
            queue.push(child);
        }
    }
    return node;
};

var tic = new mauler.games.tic.TicTacToe().move(4).move(0).move(6).move(2).move(0);

var nodeSize = 80;

var svgView = new mauler.games.tic.TicTacToeSVGView({
    model: tic,
    sideLength: nodeSize
});

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

var curNode = root;
var queue = [root];

var update = function() {
    if (!curNode) {
        return clearInterval(timer);
    }
    //curNode = breadthFirstIteration();
    nodes.push(curNode);

    // Enter nodes
    svg.selectAll(".node-group")
        .data(tree.nodes(root), function(d) {
            return d.id;
        })
        .enter()
        .append("g")
        .attr("class", "node-group")
        .attr("transform", function(d) {
            svgView.model = d.game;
            svgView.svg = d3.select(this);
            svgView.render();
            return "translate(" + (d.parent.px - (nodeSize / 2)) + ", " + (d.parent.py - (nodeSize / 2)) + ")";
        });

    // Enter links
    svg.selectAll(".link")
        .data(tree.links(nodes), function(d) {
            return d.source.id + "-" + d.target.id;
        })
        .enter()
        .insert("path", ".node-group")
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

    var t = svg.transition().duration(200);

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

    //curNode = depthFirstIteration(curNode);
    curNode = breadthFirstIteration();
};

breadthFirstIteration();
curNode = root;

var duration = 200,
    timer = setInterval(update, duration);