(function() {

    var tic = new mauler.games.TicTacToe();

    var match = new mauler.Match({
        game: tic
    });

    var canvasView = new mauler.views.TicTacToeCanvas({
        model: tic,
        width: 200,
        height: 200,
        canvas: document.getElementById("tic-canvas")
    });

    //var svgView = new mauler.views.TicTacToeSVGView({
    //    model: tic,
    //    width: 200,
    //    height: 200,
    //    svg: document.getElementById("tic-svg")
    //});

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
    //match.on("all", svgView.update, svgView);
    match.on("all", infoView.update, infoView);
    match.on("all", restartView.update, restartView);

    // Players

    var alphaBeta = mauler.players.monteCarlo();

    var canvasPlayer = new mauler.views.CanvasPlayer({
        match: match,
        canvasView: canvasView
    });

    match.players = [canvasPlayer, alphaBeta];

}());