var mcts = new Mauler.Players.MCTS({
    treePolicy: new Mauler.Players.UCB1({ c: 0.5 }),
    defaultPolicy: new Mauler.Players.Random(),
    numSims: 50000
});

var rand = new Mauler.Players.Random();

var alphaBeta = new Mauler.Players.AlphaBeta();

var players = [mcts, mcts];

var game = new Tic.Model();

var canvasView = new Tic.CanvasView({
    model: game,
    width: 400,
    height: 400,
    canvas: document.getElementById("canvas")
});

var canvasEl = canvasView.render();

canvasEl.addEventListener("click", function(event) {
    var loc = Mauler.Util.windowToCanvas(this, event.clientX, event.clientY);
});

canvasEl.addEventListener("mousemove", function(event) {
    var loc = Mauler.Util.windowToCanvas(this, event.clientX, event.clientY);
    var square = canvasView.coordToSquare(loc.x, loc.y);
    canvasView.mouse.over.row = square.row;
    canvasView.mouse.over.col = square.col;
    canvasView.render();
});

canvasEl.addEventListener("mouseout", function(event) {
    canvasView.mouse.over.row = null;
    canvasView.mouse.over.col = null;
    canvasView.render()
});

var match = new Mauler.Controller(game, players);
match.registerObserver(canvasView);

var controlsView = new Mauler.ControlsView({
    controller: match
});
document.body.appendChild(controlsView.render());


match.registerObserver(controlsView);

console.time('robles');
var stats = Mauler.Util.playNGames(game, players, 200);
console.timeEnd('robles');
console.log(stats);