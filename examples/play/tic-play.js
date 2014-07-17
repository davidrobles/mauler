// Model

var game = new Tic.Model();

// Controller

var controller = new mauler.Controller({
    game: game
});

// View

var canvasView = new Tic.CanvasView({
    model: game,
    width: 400,
    height: 400,
    canvas: document.getElementById("tic-canvas")
});

var infoView = new Tic.InfoView({
    model: game,
    el: document.getElementById("info-view")
});

var restartView = new mauler.RestartView({
    controller: controller,
    el: document.getElementById("restart-button")
});

// Observers

controller.registerObserver(canvasView);
controller.registerObserver(infoView);
controller.registerObserver(restartView);

// Players

var canvasPlayer = new mauler.players.CanvasPlayer({
    controller: controller,
    canvasView: canvasView
});

var players = [canvasPlayer, canvasPlayer];
controller.players = players;