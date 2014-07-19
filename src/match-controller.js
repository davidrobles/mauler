mauler.Controller = function(options) {
    this.game = options.game;
    this.players = options.players;
    this.currentBoardIndex = 0;
    _.extend(this, Backbone.Events);
    this.reset();
};

mauler.Controller.prototype = {

    constructor: mauler.Controller,

    playToEnd: function() {
        while (this.isNext()) {
            this.next();
            this.trigger("fix", this.curGame());
        }
    },

    curGame: function() {
        return this.gameHistory[this.currentBoardIndex];
    },

    setChange: function(index) {
        this.currentBoardIndex = index;
        this.trigger("change", this.curGame());
    },

    getSize: function() {
        return this.gameHistory.length;
    },

    getCurrentIndex: function() {
        return this.currentBoardIndex;
    },

    getGame: function(ply) {
        if (!ply) {
            return this.gameHistory[this.currentBoardIndex];
        }
        return this.gameHistory[ply];
    },

    getMove: function(gameIndex) {
        return this.moveHistory[gameIndex];
    },

    isStart: function() {
        return this.currentBoardIndex > 0;
    },

    isEnd: function() {
        return this.currentBoardIndex < this.gameHistory.length - 1;
    },

    isOver: function() {
        return this.gameHistory[this.gameHistory.length - 1].isOver();
    },

    isNext: function() {
        return (this.currentBoardIndex !== this.gameHistory.length - 1) ||
               (!this.gameHistory[this.gameHistory.length - 1].isOver());
    },

    isPrev: function() {
        return this.currentBoardIndex > 0;
    },

    start: function() {
        if (this.isStart()) {
            this.currentBoardIndex = 0;
            this.trigger("start", this.curGame());
        }
    },

    prev: function() {
        if (this.isPrev()) {
            this.currentBoardIndex--;
            this.trigger("previous", this.curGame());
        }
    },

    copy: function() {

    },

    next: function() {
        if (!this.isNext()) {
            return;
        }
        var gameCopy = this.gameHistory[this.gameHistory.length - 1].copy();
        if (this.currentBoardIndex === this.gameHistory.length - 1) {
            var moveIndex = this.players[gameCopy.curPlayer()].move(gameCopy);
            var moveString = gameCopy.moves()[moveIndex];
            gameCopy.move(moveIndex);
            if (!this.gameHistory[this.gameHistory.length - 1].equals(gameCopy)) {
                this.gameHistory.push(gameCopy);
                this.moveHistory.push(moveString);
                this.currentBoardIndex++;
                this.trigger("fix", this.curGame());
            }
        } else {
            this.currentBoardIndex++;
            this.trigger("fix", this.curGame());
        }
    },

    end: function() {
        if (this.isEnd()) {
            this.currentBoardIndex = this.gameHistory.length - 1;
            this.trigger("end", this.curGame());
        }
    },

    reset: function() {
        this.currentBoardIndex = 0;
        this.moveHistory = [];
        this.gameHistory = [this.game.newGame()];
        this.trigger("reset", this.curGame());
    }

};