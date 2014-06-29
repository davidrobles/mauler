var players = [Minotauro.Players.random, Minotauro.Players.random];
var game = new Tic.Model();
//Minotauro.Util.playRandomGame(game);

var canvasView = new Tic.CanvasView({
    model: game,
    width: 400,
    height: 400,
    canvas: document.getElementById("canvas")
});

canvasView.render();

var match = new MatchController(game, players);
match.registerObserver(canvasView);

var controlsView = new Minotauro.ControlsView({
    controller: match
});
document.body.appendChild(controlsView.render());

//match.registerObserver(robles);
//match.notifyObservers();
//match.playToEnd();

match.registerObserver(controlsView);

//match.playToEnd();