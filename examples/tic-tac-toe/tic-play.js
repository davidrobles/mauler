(function() {
    var tic = new ma.games.tic.TicTacToe();
    var rand = new ma.players.Random();
    var alphaBeta = new ma.players.AlphaBeta();
    var players = [alphaBeta, alphaBeta];
    console.time('robles');
    var stats = ma.utils.playNGames(tic, players, 100);
    console.timeEnd('robles');
    console.log(stats);
}());

//(function() {
//
//    var tic = new ma.games.tic.TicTacToe();
//
//    var match = new ma.Match({
//        game: tic
//    });
//
//    var canvasView = new ma.games.tic.CanvasView({
//        model: tic,
//        width: 400,
//        height: 400,
//        canvas: document.getElementById('tic-canvas')
//    });
//
//    var infoView = new ma.games.tic.InfoView({
//        model: tic,
//        el: document.getElementById('info-view')
//    });
//
//    var match = new ma.Match({
//        game: tic
//    });
//
//    match.on("all", canvasView.update, canvasView);
//    match.on("all", infoView.update, infoView);
//
//    // Players
//
//    var mcts = new ma.players.MCTS({
//        treePolicy: new ma.players.UCB1({ c: 0.5 }),
//        defaultPolicy: new ma.players.Random(),
//        numSims: 50000
//    });
//
//    var rand = new ma.players.Random();
//
//    var canvasPlayer = new ma.players.CanvasPlayer({
//        controller: controller,
//        canvasView: canvasView
//    });
//
//    var alphaBeta = new ma.players.AlphaBeta();
//
//    var players = [canvasPlayer, canvasPlayer];
//    match.players = players;
//
//
//    var controlsView = new ma.ControlsView({
//        match: match
//    });
//    document.body.appendChild(controlsView.render());
//
//    match.on("all", controlsView.update, controlsView);
//
//    console.time('robles');
//    var stats = ma.Util.playNGames(tic, players, 200);
//    console.timeEnd('robles');
//    console.log(stats);
//}());