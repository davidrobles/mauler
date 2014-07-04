var players = [Mauler.Players.alphaBeta, Mauler.Players.random];

var game = new Tic.Model();
//game.move(3);

var canvasView = new Tic.CanvasView({
    model: game,
    width: 400,
    height: 400,
    canvas: document.getElementById("canvas")
});

canvasView.render();

var match = new Mauler.Controller(game, players);
match.registerObserver(canvasView);

var controlsView = new Mauler.ControlsView({
    controller: match
});
document.body.appendChild(controlsView.render());


match.registerObserver(controlsView);

//console.time('robles');
//var stats = Mauler.Util.playNGames(game, players, 100);
//console.timeEnd('robles');
//console.log(stats);