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

var controller = new mauler.Controller({
    game: game
});

controller.registerObserver(canvasView);
controller.registerObserver(infoView);

// Players

var canvasPlayer = new mauler.players.CanvasPlayer({
    controller: controller,
    canvasView: canvasView
});

var players = [canvasPlayer, canvasPlayer];
controller.players = players;