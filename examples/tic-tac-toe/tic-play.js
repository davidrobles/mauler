// Game

var game = new Tic.Model();

// View

var canvasView = new Tic.CanvasView({
    model: game,
    width: 400,
    height: 400,
    canvas: document.getElementById("canvas")
});

// Controller

var controller = new Mauler.Controller({
    game: game
});
controller.registerObserver(canvasView);

// Players

var mcts = new Mauler.Players.MCTS({
    treePolicy: new Mauler.Players.UCB1({ c: 0.5 }),
    defaultPolicy: new Mauler.Players.Random(),
    numSims: 50000
});

var rand = new Mauler.Players.Random();

var canvasPlayer = new Mauler.Players.CanvasPlayer({
    controller: controller,
    canvasView: canvasView
});

var alphaBeta = new Mauler.Players.AlphaBeta();

var players = [canvasPlayer, canvasPlayer];
controller.players = players;


var controlsView = new Mauler.ControlsView({
    controller: controller
});
document.body.appendChild(controlsView.render());

controller.registerObserver(controlsView);

//console.time('robles');
//var stats = Mauler.Util.playNGames(game, players, 200);
//console.timeEnd('robles');
//console.log(stats);