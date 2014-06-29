var players = [Minotauro.Players.random, Minotauro.Players.random];

game = new Tic.Model();
match = new MatchController(game, players);

// playRandomGame(game);

//var startButton = document.getElementById('startButton'),
//    prevButton = document.getElementById('prevButton'),
//    playButton = document.getElementById('playButton'),
//    nextButton = document.getElementById('nextButton'),
//    endButton = document.getElementById('endButton'),
//    resetButton = document.getElementById('resetButton'),
//    curPlayerDiv = document.getElementById('curPlayer'),
//    index = document.getElementById('index');

//startButton.addEventListener('click', match.start);
//prevButton.addEventListener('click', match.prev);
//playButton.addEventListener('click', match.play);
//nextButton.addEventListener('click', match.next);
//endButton.addEventListener('click', match.end);
//curPlayerDiv.addEventListener('click', match.curPlayerDiv);
//resetButton.addEventListener('click', match.reset);

//var robles = function(matchController) {
//    startButton.disabled = !matchController.isStart();
//    prevButton.disabled = !matchController.isPrev();
//    nextButton.disabled = !matchController.isNext();
//    endButton.disabled = !matchController.isEnd();
//    index.innerHTML = (matchController.getCurrentIndex() + 1) + ' / ' + matchController.getSize();
//    curPlayerDiv.innerHTML = matchController.getGame().curPlayer() + 1;
//    drawTicTacToe(ctx, matchController.getGame());
//};

//match.registerObserver(robles);
//match.notifyObservers();
//match.playToEnd();

Minotauro.Util.playRandomGame(game);

console.log(game.toString());

var canvasView = new Tic.CanvasView({
    model: game,
    width: 400,
    height: 400,
    canvas: document.getElementById("canvas")
});

canvasView.render();