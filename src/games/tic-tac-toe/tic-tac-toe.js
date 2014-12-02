(function() {

    var TicTacToe = function(options) {
        this.size = 3;
        this.crosses = 0;
        this.noughts = 0;
        options || (options = {});
        if (options.board) {
            this.setBoard(options.board);
        }
    };

    TicTacToe.PATTERNS = [7, 56, 448, 73, 146, 292, 273, 84];

    TicTacToe.prototype = {

        constructor: TicTacToe,

        ////////////////////////////
        // Mauler Game Interface  //
        ////////////////////////////

        copy: function() {
            var tic = new TicTacToe();
            tic.crosses = this.crosses;
            tic.noughts = this.noughts;
            return tic;
        },

        currentPlayer: function() {
            return (this.emptyCells() + 1) % 2;
        },

        isGameOver: function() {
            return this.numMoves() === 0;
        },

        move: function(move) {
            if (this.isGameOver()) {
                throw new Error("Can't make more moves, the game is over!");
            }
            // Make random move if no move given
            if (arguments.length === 0) {
                move = Math.floor(Math.random() * this.numMoves());
            } else if (typeof move === 'string') {
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
                throw new RangeError('Illegal move');
            }
            this.setCurBitboard(this.getCurBitboard() | (1 << this.legalMoves()[move]));
            return this;
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
            if (!this.isGameOver()) {
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
            var builder = '';
            if (!this.isGameOver()) {
                builder += 'Player: ' + this.currentPlayer() + '\n';
                builder += 'Moves: ' + this.moves() + '\n';
            } else {
                builder += 'Game Over!\n';
            }
            builder += '\n';
            for (var i = 0; i < 9; i++) {
                if ((this.crosses & (1 << i)) !== 0) {
                    builder += ' X ';
                } else if ((this.noughts & (1 << i)) !== 0) {
                    builder += ' O ';
                } else {
                    builder += ' - ';
                }
                if (i % 3 === 2) {
                    builder += '\n';
                }
            }
            return builder;
        },

        //////////////////////////
        // Tic Tac Toe specific //
        //////////////////////////

        equals: function(other) {
            return this.crosses === other.crosses && this.noughts === other.noughts;
        },

        cellIndex: function(cellIndex) {
            if ((this.crosses & (1 << cellIndex)) !== 0) {
                return 'CROSS';
            }
            if ((this.noughts & (1 << cellIndex)) !== 0) {
                return 'NOUGHT';
            }
            return 'EMPTY';
        },

        cell: function(row, col) {
            return this.cellIndex(this.size * row + col);
        },

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
            return this.currentPlayer() === 0 ? this.crosses : this.noughts;
        },

        setCurBitboard: function(bitboard) {
            if (this.currentPlayer() === 0) {
                this.crosses = bitboard;
            } else {
                this.noughts = bitboard;
            }
        },

        setBoard: function(board) {
            for (var row = 0; row < board.length; row++) {
                for (var col = 0; col < board[row].length; col++) {
                    var value = board[row][col];
                    if (value === 'X') {
                        this.crosses |= (1 << ((row * this.size) + col));
                    } else if (value === 'O') {
                        this.noughts |= (1 << ((row * this.size) + col));
                    }
                }
            }
        }

    };

    mauler.games.tic = mauler.games.tic || {};
    mauler.games.tic.letters = ['A', 'B', 'C'];
    mauler.games.tic.TicTacToe = TicTacToe;

}());