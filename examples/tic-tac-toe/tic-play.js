// Game

var game = new Tic.Model();

// View

var canvasView = new Tic.CanvasView({
    model: game,
    width: 400,
    height: 400,
    canvas: document.getElementById("canvas")
});

var infoView = new Tic.InfoView({
    model: game,
    el: document.getElementById("infoView")
});

// Controller

var controller = new mauler.Match({
    game: game
});
//controller.registerObserver(canvasView);
//controller.registerObserver(infoView);

// Players

var mcts = new mauler.players.MCTS({
    treePolicy: new mauler.players.UCB1({ c: 0.5 }),
    defaultPolicy: new mauler.players.Random(),
    numSims: 50000
});

var rand = new mauler.players.Random();

var canvasPlayer = new mauler.players.CanvasPlayer({
    controller: controller,
    canvasView: canvasView
});

var alphaBeta = new mauler.players.AlphaBeta();

var players = [canvasPlayer, canvasPlayer];
controller.players = players;


var controlsView = new mauler.ControlsView({
    controller: controller
});
document.body.appendChild(controlsView.render());

controller.registerObserver(controlsView);

//console.time('robles');
//var stats = mauler.Util.playNGames(game, players, 200);
//console.timeEnd('robles');
//console.log(stats);