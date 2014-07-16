var mauler = mauler || {};
mauler.players = mauler.players || {};

mauler.players.MCTS = function(options) {
    options = options || {};
    this.treePolicy = options.treePolicy;
    this.defaultPolicy = options.defaultPolicy;
    this.numSims = options.numSims;
    this.utilFunc = options.utilFunc || new mauler.utils.UtilFunc();
};

mauler.players.MCTS.prototype = {

    constructor: mauler.players.MCTS,

    copy: function() {
        return new mauler.MCTS(this.treePolicy, this.defaultPolicy, this.numSims);
    },

    simulate: function(curPos, player) {
        var visitedNodes = this.simTree(curPos, player),
            lastNode = visitedNodes[visitedNodes.length - 1],
            outcome = this.simDefault(lastNode, player);
        this.backup(visitedNodes, outcome);
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
        var root = new mauler.players.MCTSNode(game);
        var curPlayer = game.curPlayer();
        for (var i = 0; i < this.numSims; i++) {
            this.simulate(root, curPlayer);
        }
        return this.treePolicy.move(root, curPlayer);
    }

};

mauler.players.MCTSNode = function(game) {
    this.game = game;
    this.count = 0;
    this.value = 0.0;
    this.children = [];
};

mauler.players.MCTSNode.prototype = {

    constructor: mauler.players.MCTSNode,

    init: function() {
        for (var move = 0; move < this.game.numMoves(); move++) {
            var newGame = this.game.copy();
            newGame.move(move);
            this.children.push(new mauler.players.MCTSNode(newGame));
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

///////////////////
// Tree policies //
///////////////////

mauler.players.UCB1 = function(options) {
    options = options || {};
    this.c = options.c; // TODO add random number generator
};

mauler.players.UCB1.prototype = {

    constructor: mauler.players.UCB1,

    move: function(node, player) {
        var bestMove = -1,
            max = node.game.curPlayer() === player,
            bestValue = max ? -Number.MAX_VALUE : Number.MAX_VALUE,
            nb = 0;
        for (var move = 0; move < node.game.numMoves(); move++) {
            nb += node.actionCount(move);
        }
        for (move = 0; move < node.game.numMoves(); move++) {
            var value = 0;

            // ensures that each arm is selected once before further exploration
            if (node.actionCount(move) === 0)
            {
                var bias = (Math.random() * 1000) + 10;
                value = max ? (100000000 - bias) : (-100000000 + bias); // TODO: refactor
            }
            else
            {
                var exploitation = node.actionValue(move);
                var exploration = this.c * Math.sqrt(Math.log(nb) / node.actionCount(move));
                value += exploitation;
                value += max ? exploration : -exploration;
            }

            if (max)
            {
                if (value > bestValue) {
                    bestMove = move;
                    bestValue = value;
                }
            }
            else if (value < bestValue) { // min
                bestMove = move;
                bestValue = value;
            }
        }
        return bestMove;
    }

};