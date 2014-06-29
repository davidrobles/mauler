var Minotauro = Minotauro || {};

Minotauro.ControlsView = function(options) {
    this.controller = options.controller;
    this.div = document.createElement("div");
    this.initButtons();
};

Minotauro.ControlsView.prototype = {

    constructor: Minotauro.ControlsView,

    initButtons: function() {
        this.buttons = {
            start: document.createElement("button"),
            prev: document.createElement("button"),
            next: document.createElement("button"),
            end: document.createElement("button")
        };

        this.buttons.start.innerHTML = "|&#60;";
        this.buttons.prev.innerHTML = "&#60;";
        this.buttons.next.innerHTML = "&#62;";
        this.buttons.end.innerHTML = "&#62;|";

        this.div.appendChild(this.buttons.start);
        this.div.appendChild(this.buttons.prev);
        this.div.appendChild(this.buttons.next);
        this.div.appendChild(this.buttons.end);
    },

    render: function() {
        return this.div;
    }

};

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