(function() {

    var Match = function(options) {
        this.game = options.game;
        this.players = options.players;
        this.currentGameIndex = 0;
        this.reset();
    };

    Match.prototype = {

        constructor: Match,

        playToEnd: function() {
            while (this.isNext()) {
                this.next();
                this.trigger("fix", this.curGame());
            }
        },

        curGame: function() {
            return this.gameHistory[this.currentGameIndex];
        },

        setChange: function(index) {
            this.currentGameIndex = index;
            this.trigger("change", this.curGame());
        },

        getSize: function() {
            return this.gameHistory.length;
        },

        getCurrentIndex: function() {
            return this.currentGameIndex;
        },

        getGame: function(ply) {
            if (!ply) {
                return this.gameHistory[this.currentGameIndex];
            }
            return this.gameHistory[ply];
        },

        getMove: function(gameIndex) {
            return this.moveHistory[gameIndex];
        },

        isStart: function() {
            return this.currentGameIndex > 0;
        },

        isEnd: function() {
            return this.currentGameIndex < this.gameHistory.length - 1;
        },

        isOver: function() {
            return this.gameHistory[this.gameHistory.length - 1].isOver();
        },

        isNext: function() {
            return (this.currentGameIndex !== this.gameHistory.length - 1) ||
                (!this.gameHistory[this.gameHistory.length - 1].isOver());
        },

        isPrev: function() {
            return this.currentGameIndex > 0;
        },

        start: function() {
            if (this.isStart()) {
                this.currentGameIndex = 0;
                this.trigger("start", this.curGame());
            }
        },

        prev: function() {
            if (this.isPrev()) {
                this.currentGameIndex--;
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
            if (this.currentGameIndex === this.gameHistory.length - 1) {
                var moveIndex = this.players[gameCopy.currentPlayer()].move(gameCopy);
                var moveString = gameCopy.moves()[moveIndex];
                gameCopy.move(moveIndex);
                if (!this.gameHistory[this.gameHistory.length - 1].equals(gameCopy)) {
                    this.gameHistory.push(gameCopy);
                    this.moveHistory.push(moveString);
                    this.currentGameIndex++;
                    this.trigger("fix", this.curGame());
                }
            } else {
                this.currentGameIndex++;
                this.trigger("fix", this.curGame());
            }
        },

        end: function() {
            if (this.isEnd()) {
                this.currentGameIndex = this.gameHistory.length - 1;
                this.trigger("end", this.curGame());
            }
        },

        reset: function() {
            this.currentGameIndex = 0;
            this.moveHistory = [];
            this.gameHistory = [this.game.newGame()];
            this.trigger("reset", this.curGame());
        }

    };

    _.extend(Match.prototype, Backbone.Events);
    mauler.Match = Match;

}());