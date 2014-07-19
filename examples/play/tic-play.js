// Model

var game = new mauler.tic.Model();

var match = new mauler.Match({
    game: game
});

// View

var canvasView = new mauler.tic.CanvasView({
    model: game,
    width: 400,
    height: 400,
    canvas: document.getElementById("tic-canvas")
});

var infoView = new mauler.views.InfoView({
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

var canvasPlayer = new mauler.tic.CanvasPlayer({
    match: match,
    canvasView: canvasView
});

var players = [canvasPlayer, canvasPlayer];
match.players = players;