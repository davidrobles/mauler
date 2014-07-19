// Model

var game = new mauler.Tic.Model();

var match = new mauler.Match({
    game: game
});

// View

var canvasView = new mauler.Tic.CanvasView({
    model: game,
    width: 400,
    height: 400,
    canvas: document.getElementById("tic-canvas")
});

var infoView = new mauler.Tic.InfoView({
    model: game,
    el: document.getElementById("info-view")
});

var restartView = new mauler.RestartView({
    match: match,
    el: document.getElementById("restart-button")
});

// Observers

match.on("all", canvasView.update, canvasView);
match.on("all", infoView.update, infoView);
match.on("all", restartView.update, restartView);

// Players

var canvasPlayer = new mauler.players.CanvasPlayer({
    match: match,
    canvasView: canvasView
});

var players = [canvasPlayer, canvasPlayer];
match.players = players;