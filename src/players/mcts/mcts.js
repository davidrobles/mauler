var Mauler = Mauler || {};
Mauler.Players = Mauler.Players || {};

Mauler.Players.MCTS = function(options) {
    options = options || {};
    this.treePolicy = options.treePolicy;
    this.defaultPolicy = options.defaultPolicy;
    this.numSims = options.numSims;
    this.utilFunc = options.utilFunc || new Mauler.Util.UtilFunc();
};

Mauler.Players.MCTS.prototype = {

    constructor: Mauler.Players.MCTS,

    copy: function() {
        return new Mauler.MCTS(this.treePolicy, this.defaultPolicy, this.numSims);
    },

    simulate: function(curPos, player) {
        var visitedNodes = this.simTree(curPos, player),
            lastNode = visitedNodes[visitedNodes.length - 1],
            outcome = this.simDefault(lastNode, player);
        this.backup(visitedNodes, outcome)
    },

    simTree: function(curPos, player) {
        var nodes = [],
            curNode = curPos;
        while (!curNode.game.isOver()) {
            nodes.push(curNode);
            var lastNode = nodes[nodes.length - 1];
            if (lastNode.count === 0) {
                this.newNode(lastNode, player);
                return nodes;
            }
            var move = this.treePolicy.move(nodes[nodes.length - 1], player); // TODO refactor
            curNode = curNode.children[move];
        }
        nodes.push(curNode);
        return nodes;
    },

    simDefault: function(node, player) {
        var copy = node.game.copy();
        while (!copy.isOver()) {
            copy.move(this.defaultPolicy.move(copy));
        }
        return this.utilFunc.eval(copy, player);
    },

    backup: function (visitedNodes, outcome) {
        visitedNodes.forEach(function(node) {
            node.update(outcome);
        });
    },

    newNode: function(node, player) { // todo remove this?
        node.init();
    },

    // Player Interface

    move: function(game) {
        game = game.copy();
        var root = new Mauler.Players.MCTSNode(game);
        var curPlayer = game.curPlayer();
        for (var i = 0; i < this.numSims; i++) {
            this.simulate(root, curPlayer);
        }
        return this.treePolicy.move(root, curPlayer);
    }

};

Mauler.Players.MCTSNode = function(game) {
    this.game = game;
    this.count = 0;
    this.value = 0.0;
    this.children = [];
};

Mauler.Players.MCTSNode.prototype = {

    constructor: Mauler.Players.MCTSNode,

    init: function() {
        for (var move = 0; move < this.game.numMoves(); move++) {
            var newGame = this.game.copy();
            newGame.move(move);
            this.children.push(new Mauler.Players.MCTSNode(newGame));
        }
    },

    update: function(outcome) {
        this.count++;
        this.value += (outcome - this.value) / this.count;
    },

    actionCount: function(move) {
        return this.children[move].count; // TODO refactor
    },

    actionValue: function(move) {
        return this.children[move].value; // TODO refactor
    }

};