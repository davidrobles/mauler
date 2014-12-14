(function() {

    var tic = new ma.games.TicTacToe();

    var match = new ma.Match({
        game: tic
    });

    var canvasView = new ma.views.TicTacToeCanvas({
        model: tic,
        width: 200,
        height: 200,
        canvas: document.getElementById("tic-canvas")
    });

    //var svgView = new ma.views.TicTacToeSVGView({
    //    model: tic,
    //    width: 200,
    //    height: 200,
    //    svg: document.getElementById("tic-svg")
    //});

    var infoView = new ma.views.InfoView({
        model: tic,
        el: document.getElementById("info-view")
    });

    var restartView = new ma.views.RestartView({
        match: match,
        el: document.getElementById("restart-button")
    });

    // Events

    match.on("all", canvasView.update, canvasView);
    //match.on("all", svgView.update, svgView);
    match.on("all", infoView.update, infoView);
    match.on("all", restartView.update, restartView);

    // Players

    var alphaBeta = ma.players.monteCarlo();

    var canvasPlayer = new ma.views.CanvasPlayer({
        match: match,
        canvasView: canvasView
    });

    match.players = [canvasPlayer, alphaBeta];

}());