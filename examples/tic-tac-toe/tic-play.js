var playRandomGame = function(game) {
    console.log(game.toString());
    while (!game.isOver()) {
        game.move();
        console.log(game.toString());
    }
};

var drawTicTacToe = function(ctx, tic) {
    var width = ctx.canvas.width,
        height = ctx.canvas.height,
        squareSize = canvas.width / 3,
        cellPer = 0.8;

    // background
    ctx.fillStyle = 'rgb(255, 203, 5)';
    ctx.fillRect(0, 0, width, height);

    // border
    ctx.strokeStyle = 'rgb(0, 0, 0)';
    ctx.strokeRect(0, 0, width, height);

    for (var row = 0; row < 3; row++) {
        for (var col = 0; col < 3; col++) {
            if (tic.cell(row, col) === 'CROSS') {
                var x = col * squareSize + (squareSize * ((1 - cellPer) / 2)),
                    y = row * squareSize + (squareSize * ((1 - cellPer) / 2)),
                    pieceWidth = squareSize * cellPer,
                    pieceHeight = squareSize * cellPer;
                ctx.fillStyle = 'rgb(0, 0, 255';
                ctx.fillRect(x, y, pieceWidth, pieceHeight);
            } else if (tic.cell(row, col) === 'NOUGHT') {
                ctx.beginPath();
                var centerX = col * squareSize + (squareSize / 2),
                    centerY = row * squareSize + (squareSize / 2),
                    radius = squareSize / 2 * cellPer, // 80% of the square size
                    startAngle = 0,
                    endAngle = 2 * Math.PI,
                    counterClockwise = false;
                ctx.arc(centerX, centerY, radius, startAngle, endAngle, counterClockwise);
                ctx.StrokeStyle = 'green';
                ctx.stroke();
            }
        }
    }
};



var randPlayer = function(game) {
    return Math.floor(Math.random() * game.numMoves());
};

var players = [randPlayer, randPlayer];

game = new TicTacToe();
match = new MatchController(game, players);

// playRandomGame(game);
var ctx = document.getElementById("canvas").getContext("2d");

var startButton = document.getElementById('startButton'),
    prevButton = document.getElementById('prevButton'),
    playButton = document.getElementById('playButton'),
    nextButton = document.getElementById('nextButton'),
    endButton = document.getElementById('endButton'),
    resetButton = document.getElementById('resetButton'),
    curPlayerDiv = document.getElementById('curPlayer'),
    index = document.getElementById('index');

startButton.addEventListener('click', match.start);
prevButton.addEventListener('click', match.prev);
playButton.addEventListener('click', match.play);
nextButton.addEventListener('click', match.next);
endButton.addEventListener('click', match.end);
curPlayerDiv.addEventListener('click', match.curPlayerDiv);
resetButton.addEventListener('click', match.reset);

var robles = function(matchController) {
    startButton.disabled = !matchController.isStart();
    prevButton.disabled = !matchController.isPrev();
    nextButton.disabled = !matchController.isNext();
    endButton.disabled = !matchController.isEnd();
    index.innerHTML = (matchController.getCurrentIndex() + 1) + ' / ' + matchController.getSize();
    curPlayerDiv.innerHTML = matchController.getGame().curPlayer() + 1;
    drawTicTacToe(ctx, matchController.getGame());
};

match.registerObserver(robles);
match.notifyObservers();
match.playToEnd();