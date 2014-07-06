var mcts = new Mauler.Players.MCTS({
    treePolicy: new Mauler.Players.UCB1({ c: 0.5 }),
    defaultPolicy: new Mauler.Players.Random(),
    numSims: 50000
});

var rand = new Mauler.Players.Random();

var alphaBeta = new Mauler.Players.AlphaBeta();

var players = [rand, rand];

var game = new Tic.Model();

var canvasView = new Tic.CanvasView({
    model: game,
    width: 400,
    height: 400,
    canvas: document.getElementById("canvas")
});

var match = new Mauler.Controller(game, players);
match.registerObserver(canvasView);

var controlsView = new Mauler.ControlsView({
    controller: match
});
document.body.appendChild(controlsView.render());


match.registerObserver(controlsView);

//console.time('robles');
//var stats = Mauler.Util.playNGames(game, players, 200);
//console.timeEnd('robles');
//console.log(stats);