(function() {

    for (var i = 0; i < 200; i++) {
        var tic = new ma.games.tic.TicTacToe();
        tic.move();
        tic.move();
        tic.move();
        var view = new ma.games.tic.CanvasView({
            model: tic,
            width: 140,
            height: 140
        });
        document.body.appendChild(view.render());
    }

}());