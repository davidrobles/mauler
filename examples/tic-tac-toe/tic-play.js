(function() {
    var tic = new mauler.games.tic.TicTacToe();
    var rand = new mauler.players.Random();
    var alphaBeta = new mauler.players.AlphaBeta();
    var players = [alphaBeta, alphaBeta];
    console.time('robles');
    var stats = mauler.utils.playNGames(tic, players, 100);
    console.timeEnd('robles');
    console.log(stats);
}());

//(function() {
//
//    var tic = new mauler.games.tic.TicTacToe();
//
//    var match = new mauler.Match({
//        game: tic
//    });
//
//    var canvasView = new mauler.games.tic.CanvasView({
//        model: tic,
//        width: 400,
//        height: 400,
//        canvas: document.getElementById('tic-canvas')
//    });
//
//    var infoView = new mauler.games.tic.InfoView({
//        model: tic,
//        el: document.getElementById('info-view')
//    });
//
//    var match = new mauler.Match({
//        game: tic
//    });
//
//    match.on("all", canvasView.update, canvasView);
//    match.on("all", infoView.update, infoView);
//
//    // Players
//
//    var mcts = new mauler.players.MCTS({
//        treePolicy: new mauler.players.UCB1({ c: 0.5 }),
//        defaultPolicy: new mauler.players.Random(),
//        numSims: 50000
//    });
//
//    var rand = new mauler.players.Random();
//
//    var canvasPlayer = new mauler.players.CanvasPlayer({
//        controller: controller,
//        canvasView: canvasView
//    });
//
//    var alphaBeta = new mauler.players.AlphaBeta();
//
//    var players = [canvasPlayer, canvasPlayer];
//    match.players = players;
//
//
//    var controlsView = new mauler.ControlsView({
//        match: match
//    });
//    document.body.appendChild(controlsView.render());
//
//    match.on("all", controlsView.update, controlsView);
//
//    console.time('robles');
//    var stats = mauler.Util.playNGames(tic, players, 200);
//    console.timeEnd('robles');
//    console.log(stats);
//}());