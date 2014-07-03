//var players = [Minotauro.Players.random, Minotauro.Players.monteCarlo];

//var players = [Minotauro.Players.monteCarlo, Minotauro.Players.random];

//var players = [Minotauro.Players.random, Minotauro.Players.monteCarlo];
//var players = [Minotauro.Players.random, Minotauro.Players.random];

//var players = [Minotauro.Players.alphaBeta, Minotauro.Players.random];

var players = [Minotauro.Players.minimax, Minotauro.Players.minimax];

var game = new Tic.Model();
//game.move(3);

var canvasView = new Tic.CanvasView({
    model: game,
    width: 400,
    height: 400,
    canvas: document.getElementById("canvas")
});

canvasView.render();

var match = new Minotauro.Controller(game, players);
match.registerObserver(canvasView);

var controlsView = new Minotauro.ControlsView({
    controller: match
});
document.body.appendChild(controlsView.render());


match.registerObserver(controlsView);

console.time('robles');
var stats = Minotauro.Util.playNGames(game, players, 100);
console.timeEnd('robles');
console.log(stats);