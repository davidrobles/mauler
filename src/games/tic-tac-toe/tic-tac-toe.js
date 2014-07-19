(function() {

    var TicTacToe = function() {
        this.size = 3;
        this.crosses = 0;
        this.noughts = 0;
    };

    TicTacToe.PATTERNS = [7, 56, 448, 73, 146, 292, 273, 84];

    TicTacToe.prototype = {

        constructor: TicTacToe,

        equals: function(other) {
            return this.crosses === other.crosses && this.noughts === other.noughts;
        },

        cellIndex: function(cellIndex) {
            if ((this.crosses & (1 << cellIndex)) !== 0) {
                return "CROSS";
            }
            if ((this.noughts & (1 << cellIndex)) !== 0) {
                return "NOUGHT";
            }
            return "EMPTY";
        },

        cell: function(row, col) {
            return this.cellIndex(this.size * row + col);
        },

        // Turn-Based Game API methods

        copy: function() {
            var tic = new TicTacToe(); // TODO refactor
            tic.crosses = this.crosses;
            tic.noughts = this.noughts;
            return tic;
        },

        curPlayer: function() {
            return (this.emptyCells() + 1) % 2;
        },

        isOver: function() {
            return this.numMoves() === 0;
        },

        move: function(move) {
            // tic is over
            if (this.isOver()) {
                throw new RangeError("Can't make more moves, tic is over.");
            }
            // make random move if no move given
            if (arguments.length === 0) {
                move = Math.floor(Math.random() * this.numMoves());
            }
            if (typeof move === "string") {
                var theMoves = this.moves();
                var nMoves = theMoves.length;
                for (var i = 0; i < nMoves; i++) {
                    if (move === theMoves[i]) {
                        move = i;
                        break;
                    }
                }
            }
            if (move < 0 || move >= this.numMoves()) { // TODO refactor, use move length
                throw new RangeError("Illegal move");
            }
            this.setCurBitboard(this.getCurBitboard() | (1 << this.legalMoves()[move]));
        },

        moves: function() {
            var mvs = [],
                legal = this.legalMoves();
            for (var i = 0; i < legal.length; i++) {
                var row = Math.floor(legal[i] / 3);
                var col = (legal[i] % 3) + 1;
                mvs.push(mauler.games.tic.letters[row] + col.toString());
            }
            return mvs
        },

        newGame: function() {
            return new TicTacToe();
        },

        // TODO no need for having this method... moves().length should be enough
        numMoves: function() {
            return this.isWin() ? 0 : this.emptyCells();
        },

        numPlayers: function() {
            return 2;
        },

        outcomes: function() {
            if (!this.isOver()) {
                return ['NA', 'NA'];
            }
            if (this.checkWin(this.crosses)) {
                return ['WIN', 'LOSS'];
            }
            if (this.checkWin(this.noughts)) {
                return ['LOSS', 'WIN'];
            }
            return ['DRAW', 'DRAW'];
        },

        reset: function() {
            this.crosses = 0;
            this.noughts = 0;
        },

        toString: function() {
            var builder = "";
            if (!this.isOver()) {
                builder += "Player: " + this.curPlayer() + "\n";
                builder += "Moves: " + this.moves() + "\n";
            } else {
                builder += "Game Over!\n";
            }
            builder += "\n";
            for (var i = 0; i < 9; i++) {
                if ((this.crosses & (1 << i)) !== 0) {
                    builder += " X ";
                } else if ((this.noughts & (1 << i)) !== 0) {
                    builder += " O ";
                } else {
                    builder += " - ";
                }
                if (i % 3 === 2) {
                    builder += "\n";
                }
            }
            return builder;
        },

        // Tic-Tac-Toe methods
        isWin: function() {
            return this.checkWin(this.crosses) || this.checkWin(this.noughts);
        },

        emptyCells: function() {
            return 9 - this.bitCount(this.crosses | this.noughts);
        },

        bitCount: function(num) {
            var count = 0;
            for (var i = 0; i < 9; i++) {
                if ((num & (1 << i)) > 0) {
                    count++;
                }
            }
            return count;
        },

        legalMoves: function() {
            var moves = [];
            if (this.numMoves() > 0) {
                var legal = ~(this.crosses | this.noughts);
                for (var i = 0; i < 9; i++) {
                    if ((legal & (1 << i)) !== 0) {
                        moves.push(i);
                    }
                }
            }
            return moves;
        },

        checkWin: function(board) {
            for (var i = 0; i < TicTacToe.PATTERNS.length; i++) {
                if ((board & TicTacToe.PATTERNS[i]) === TicTacToe.PATTERNS[i]) {
                    return true;
                }
            }
            return false;
        },

        getCurBitboard: function() {
            return this.curPlayer() === 0 ? this.crosses : this.noughts;
        },

        setCurBitboard: function(bitboard) {
            if (this.curPlayer() === 0) {
                this.crosses = bitboard;
            } else {
                this.noughts = bitboard;
            }
        }

    };

    mauler.games.tic = mauler.games.tic || {};
    mauler.games.tic.letters = ['A', 'B', 'C']; // TODO is this needed? move out?
    mauler.games.tic.TicTacToe = TicTacToe;

}());