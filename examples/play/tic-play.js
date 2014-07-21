(function() {

    var tic = new mauler.games.tic.TicTacToe();

    var match = new mauler.Match({
        game: tic
    });

    var canvasView = new mauler.games.tic.CanvasView({
        model: tic,
        width: 400,
        height: 400,
        canvas: document.getElementById("tic-canvas")
    });

    var infoView = new mauler.views.InfoView({
        model: tic,
        el: document.getElementById("info-view")
    });

    var restartView = new mauler.views.RestartView({
        match: match,
        el: document.getElementById("restart-button")
    });

    // Events

    match.on("all", canvasView.update, canvasView);
    match.on("all", infoView.update, infoView);
    match.on("all", restartView.update, restartView);

    // Players

    var canvasPlayer = new mauler.games.tic.CanvasPlayer({
        match: match,
        canvasView: canvasView
    });

    match.players = [canvasPlayer, canvasPlayer];

}());