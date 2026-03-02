package net.davidrobles.mauler.othello;

import net.davidrobles.mauler.core.AbstractGame;
import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.core.Outcome;
import net.davidrobles.mauler.core.util.SpeedTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Othello extends AbstractGame implements Game<Othello> {

    /** The size of the sides. */
    public static final int SIZE = 8;

    /** The total number of squares on the board. */
    public static final int NUM_SQUARES = 64;

    public static final int NUM_DISCS_START = 4;

    /** Unicode character for a black disc. */
    private static final char BLACK_STONE = '\u25C9';

    /** Unicode character for a white disc. */
    private static final char WHITE_STONE = '\u25CE';

    /** The index of the player in turn. 0 is player 1 and 1 is player 2. */
    private int current;

    /** True if the game is over. */
    private boolean gameOver;

    /** Bitboard for the black discs. */
    private long blackBB;

    /** Bitboard of the white discs. */
    private long whiteBB;

    /** Bitboard for the black stones. */
    private long legalBB;

    /** Bitboard of the black stones at the start of the game. */
    private static final long INIT_BLACK_BB = 34628173824L;

    /** Bitboard of the white stones at the start of the game. */
    private static final long INIT_WHITE_BB = 68853694464L;

    /** Bitboard of the legal moves at the start of the game. (For the black player) */
    private static final Long INIT_LEGAL_BB = 17729692631040L;

    /////////////
    // Caching //
    /////////////

    // TODO: should this be created at the beginning?

    private boolean movesArrayUpdated = false;
    private long[] movesArray = null;
    private long[] allCellsArray = new long[NUM_SQUARES];
    private long[] tmpCellsArray = new long[NUM_SQUARES];
    private int allCellsCount = 0;
    private int tmpCellsCount = 0;

    /**
     * Creates a new Othello game with the initial board.
     */
    public Othello() {
        reset();
    }

    public void setBoard(long blackBB, long whiteBB) {
        this.blackBB = blackBB;
        this.whiteBB = whiteBB;
        calculateMoves();
    }

    public long getBlackBB() {
        return blackBB;
    }

    public long getWhiteBB() {
        return whiteBB;
    }

    /**
     * Returns the square on the board represented by the
     * given <code>squareIndex</code>.
     * @param squareIndex the index of the square
     * @return the square
     */
    public Square getSquare(int squareIndex) {
        // blackBB stone in the cell
        if ((blackBB & (1L << squareIndex)) != 0L) {
            return Square.BLACK;
        }
        // whiteBB stone in the cell
        if ((whiteBB & (1L << squareIndex)) != 0L) {
            return Square.WHITE;
        }
        // no stones in the cell
        return Square.EMPTY;
    }

    /**
     * Returns the square on the board located in the
     * given row and column
     * @param row the row of the square
     * @param col the column of the square
     * @return the square
     */
    public Square getSquare(int row, int col) {
        return getSquare(Othello.SIZE * row + col);
    }

    /**
     * Returns the number of discs on the board for the given player.
     * @param player the player whose number of discs are counted
     * @return the number of discs
     */
    public int getNumDiscs(int player) {
        return player == 0 ? Long.bitCount(blackBB) : Long.bitCount(whiteBB);
    }

    /**
     * The number of discs on the board (both black and white).
     * @return number of discs on the board
     */
    public int getNumDiscs() {
        return Long.bitCount(blackBB) + Long.bitCount(whiteBB);
    }

    /**
     * Returns the current board of the game as an array
     * of squares. The order of the squares in the array
     * starts from row by row.
     * @return the squares of the board
     */
    public Square[] getBoard() {
        Square[] boardArray = new Square[NUM_SQUARES];
        for (int i = 0; i < NUM_SQUARES; i++) {
            boardArray[i] = getSquare(i);
        }
        return boardArray;
    }

    /**
     * Calculates the legal moves and saves them
     * in the bitboard of legal moves <code>legalBB</code>.
     */
    private void calculateMoves()
    {
        legalBB = 0L;
        long potentialMoves;
        long curBoard = currentBoard();
        long oppBoard = opponentBoard();
        long emptyBoard = emptyBoard();

        // UP
        potentialMoves = (curBoard >> SIZE) & DOWN_MASK & oppBoard;

        while (potentialMoves != 0L)
        {
            long tmp = (potentialMoves >> SIZE) & DOWN_MASK;
            legalBB |= tmp & emptyBoard;
            potentialMoves = tmp & oppBoard;
        }

        // DOWN
        potentialMoves = (curBoard << SIZE) & UP_MASK & oppBoard;

        while (potentialMoves != 0L)
        {
            long tmp = (potentialMoves << SIZE) & UP_MASK;
            legalBB |= tmp & emptyBoard;
            potentialMoves = tmp & oppBoard;
        }

        // LEFT
        potentialMoves = (curBoard >> 1L) & RIGHT_MASK & oppBoard;

        while (potentialMoves != 0L)
        {
            long tmp = (potentialMoves >> 1L) & RIGHT_MASK;
            legalBB |= tmp & emptyBoard;
            potentialMoves = tmp & oppBoard;
        }

        // RIGHT
        potentialMoves = (curBoard << 1L) & LEFT_MASK & oppBoard;

        while (potentialMoves != 0L)
        {
            long tmp = (potentialMoves << 1L) & LEFT_MASK;
            legalBB |= tmp & emptyBoard;
            potentialMoves = tmp & oppBoard;
        }

        // UP LEFT
        potentialMoves = (curBoard >> (SIZE + 1L)) & RIGHT_MASK & DOWN_MASK & oppBoard;

        while (potentialMoves != 0L)
        {
            long tmp = (potentialMoves >> (SIZE + 1L)) & RIGHT_MASK & DOWN_MASK;
            legalBB |= tmp & emptyBoard;
            potentialMoves = tmp & oppBoard;
        }

        // UP RIGHT
        potentialMoves = (curBoard >> (SIZE - 1L)) & LEFT_MASK & DOWN_MASK & oppBoard;

        while (potentialMoves != 0L)
        {
            long tmp = (potentialMoves >> (SIZE - 1L)) & LEFT_MASK & DOWN_MASK;
            legalBB |= tmp & emptyBoard;
            potentialMoves = tmp & oppBoard;
        }

        // DOWN LEFT
        potentialMoves = (curBoard << (SIZE - 1L)) & RIGHT_MASK & UP_MASK & oppBoard;

        while (potentialMoves != 0L)
        {
            long tmp = (potentialMoves << (SIZE - 1L)) & RIGHT_MASK & UP_MASK;
            legalBB |= tmp & emptyBoard;
            potentialMoves = tmp & oppBoard;
        }

        // DOWN RIGHT
        potentialMoves = (curBoard << (SIZE + 1L)) & LEFT_MASK & UP_MASK & oppBoard;

        while (potentialMoves != 0L)
        {
            long tmp = (potentialMoves << (SIZE + 1L)) & LEFT_MASK & UP_MASK;
            legalBB |= tmp & emptyBoard;
            potentialMoves = tmp & oppBoard;
        }

        movesArrayUpdated = false;
    }

    /**
     * Returns an array of bitboard of the legal moves,
     * where each bitboard has only one bit on, which is
     * the index of the square on the board.
     * @return an array of bitboards of legal moves
     */
    private long[] getBitMovesArray() {
        if (movesArray == null) {
            movesArray = new long[NUM_SQUARES];
        }
        if (!movesArrayUpdated) {
            if (legalBB == PASS) {
                movesArray[0] = PASS;
            }
            else {
                for (int i = 0, count = 0; i < NUM_SQUARES; i++) {
                    if ((legalBB & (1L << i)) != 0L) {
                        movesArray[count++] = 1L << i;
                    }
                }
            }
            movesArrayUpdated = true;
        }
        return movesArray;
    }

    /**
     * Returns a bitboard of the empty squares.
     * @return a bitboard of the empty squares
     */
    private long emptyBoard() {
        return ~(blackBB | whiteBB);
    }

    /**
     * Returns a bitboard of the discs for the player in turn.
     * @return a bitboard of discs
     */
    private long currentBoard() {
        return current == 0 ? blackBB : whiteBB;
    }

    /**
     * Returns a bitboard of the discs for the player not in turn.
     * @return a bitboard of discs
     */
    private long opponentBoard() {
        return current == 0 ? whiteBB : blackBB;
    }

    /**
     * Sets the bitboard of discs for the player in turn.
     * @param bitboard the new bitboard of discs
     */
    private void setCurrentBoard(long bitboard) {
        if (current == 0) {
            blackBB = bitboard;
        } else {
            whiteBB = bitboard;
        }
    }

    /**
     * Sets the bitboard of discs for the player not in turn.
     * @param bitboard the new bitboard of discs
     */
    private void setOpponentBoard(long bitboard) {
        if (current == 0) {
            whiteBB = bitboard;
        } else {
            blackBB = bitboard;
        }
    }

    /**
     * Sets a new value for the given square.
     * @param squareIndex the index of the square
     * @param square the new value of the square
     */
    public void setSquare(int squareIndex, Square square) {
        removeDisc(squareIndex);
        if (square == Square.BLACK) {
            blackBB |= (1L << squareIndex);
        } else if (square == Square.WHITE) {
            whiteBB |= (1L << squareIndex);
        }
    }

    /**
     * Removes any disc from the given square.
     * @param squareIndex the index of the square
     */
    private void removeDisc(int squareIndex) {
        blackBB &= ~(1L << squareIndex);
        whiteBB &= ~(1L << squareIndex);
    }

    public void initWithString(String board, int current) {
        if (board.length() != NUM_SQUARES) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < NUM_SQUARES; i++) {
            switch (board.charAt(i)) {
                case '-':
                    removeDisc(i);
                    break;
                case 'X':
                    setSquare(i, Square.BLACK);
                    break;
                case 'O':
                    setSquare(i, Square.WHITE);
                    break;
            }
        }
        this.current = current;
        calculateMoves();
    }

    private String getGameInfo() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Turn: %s\n", curPlayerStr()));
        builder.append(String.format("Black count: %d\n", Long.bitCount(blackBB)));
        builder.append(String.format("White count: %d\n", Long.bitCount(whiteBB)));
        builder.append(("Legal moves: " + Arrays.toString(getMoves()) + "\n\n"));
        return builder.toString();
    }

    private String getColumnHeaders() {
        StringBuilder builder = new StringBuilder("   ");
        for (int col = 0; col < SIZE; col++) {
            builder.append((" " + OthelloUtil.colToChar(col)  + " "));
        }
        builder.append("\n");
        return builder.toString();
    }

    private char cellToChar(int cellIndex) {
        if ((blackBB & (1L << cellIndex)) != 0) {
            return BLACK_STONE;
        }
        else if ((whiteBB & (1L << cellIndex)) != 0) {
            return WHITE_STONE;
        }
        else if ((legalBB & (1L << cellIndex)) != 0) {
            return 'x';
        }
        return '-';
    }

    private String getBoardStr() {
        StringBuilder builder = new StringBuilder();
        for (int cellIndex = 0; cellIndex < NUM_SQUARES; cellIndex++) {
            if (cellIndex % SIZE == 0) {
                builder.append((" " + OthelloUtil.cellToRowNum(cellIndex) + " "));
            }
            builder.append((" " + cellToChar(cellIndex) + " "));
            if (cellIndex % SIZE == SIZE - 1) {
                builder.append((" " + OthelloUtil.cellToRowNum(cellIndex) + " \n"));
            }
        }
        return builder.toString();
    }

    private String curPlayerStr() {
        return current == 0 ? "Black" : "White";
    }

    //////////
    // Game //
    //////////

    @Override
    public Othello copy() {
        Othello newOthello = new Othello();
        newOthello.current = current;
        newOthello.gameOver = gameOver;
        newOthello.blackBB = blackBB;
        newOthello.whiteBB = whiteBB;
        newOthello.legalBB = legalBB;
        return newOthello;
    }

    @Override
    public int getCurPlayer() {
        return current;
    }

    @Override
    public int getNumPlayers() {
        return 2;
    }

    @Override
    public int getNumMoves() {
        return legalBB == PASS ? 1 : Long.bitCount(legalBB);
    }

    @Override
    public String[] getMoves() {
        List<String> othelloMoves = new ArrayList<String>();
        long[] bitMovesArray = getBitMovesArray();
        int nMoves = getNumMoves();
        if (nMoves == 1 && bitMovesArray[0] == PASS) {
            othelloMoves.add("PASS");
        }
        else {
            for (int i = 0; i < nMoves; i++) {
                int cellIndex = Long.numberOfTrailingZeros(bitMovesArray[i]);
                othelloMoves.add(OthelloUtil.cellToStr(cellIndex));
            }
        }
        return othelloMoves.toArray(new String[othelloMoves.size()]);
    }

    @Override
    public String getName() {
        return "Othello";
    }

    @Override
    public Outcome[] getOutcome() {
        if (!isOver()) {
            return new Outcome[] {Outcome.NA, Outcome.NA};
        }
        int blackStonesCount = Long.bitCount(blackBB);
        int whiteStonesCount = Long.bitCount(whiteBB);
        if (blackStonesCount > whiteStonesCount) {
            return new Outcome[] {Outcome.WIN, Outcome.LOSS};
        }
        if (whiteStonesCount > blackStonesCount) {
            return new Outcome[] {Outcome.LOSS, Outcome.WIN};
        }
        return new Outcome[] {Outcome.DRAW, Outcome.DRAW};
    }

    @Override
    public boolean isOver() {
        return gameOver;
    }

    public void makeMove(String move) {
        String[] moves = getMoves();
        for (int i = 0; i < moves.length; i++) {
            if (moves[i].equalsIgnoreCase(move)) {
                makeMove(i);
                return;
            }
        }
    }

    @Override
    public void makeMove(int move)
    {
        int nMoves = getNumMoves();
        long[] movesArray = getBitMovesArray();

        if (move < 0 || move >= nMoves) {
            throw new IllegalArgumentException("Wrong move: " + move);
        }

        long theMove = movesArray[move];

        if (theMove != PASS)
        {
            long next; // potential moves
            long lastCell;
            long oppBoard = opponentBoard();
            long curBoard = currentBoard();
            setCurrentBoard(currentBoard() | theMove); // place the new stone on the board
            allCellsCount = 0;

            // UP
            lastCell = 0L;
            tmpCellsCount = 0;
            next = (theMove >> SIZE) & DOWN_MASK & oppBoard;

            while (next != 0L) {
                tmpCellsArray[tmpCellsCount++] = next;
                long tmp = (next >> SIZE) & DOWN_MASK;
                lastCell = tmp & curBoard;
                next = tmp & oppBoard;
            }

            if (lastCell != 0L) {
                for (int i = 0; i < tmpCellsCount; i++) {
                    allCellsArray[allCellsCount++] = tmpCellsArray[i];
                }
            }

            // DOWN
            lastCell = 0L;
            tmpCellsCount = 0;
            next = (theMove << SIZE) & UP_MASK & oppBoard;

            while (next != 0L) {
                tmpCellsArray[tmpCellsCount++] = next;
                long tmp = (next << SIZE) & UP_MASK;
                lastCell = tmp & curBoard;
                next = tmp & oppBoard;
            }

            if (lastCell != 0L) {
                for (int i = 0; i < tmpCellsCount; i++) {
                    allCellsArray[allCellsCount++] = tmpCellsArray[i];
                }
            }

            // LEFT
            lastCell = 0L;
            tmpCellsCount = 0;
            next = (theMove >> 1L) & RIGHT_MASK & oppBoard;

            while (next != 0L) {
                tmpCellsArray[tmpCellsCount++] = next;
                long tmp = (next >> 1L) & RIGHT_MASK;
                lastCell = tmp & curBoard;
                next = tmp & oppBoard;
            }

            if (lastCell != 0L) {
                for (int i = 0; i < tmpCellsCount; i++) {
                    allCellsArray[allCellsCount++] = tmpCellsArray[i];
                }
            }

            // RIGHT
            lastCell = 0L;
            tmpCellsCount = 0;
            next = (theMove << 1L) & LEFT_MASK & oppBoard;

            while (next != 0L) {
                tmpCellsArray[tmpCellsCount++] = next;
                long tmp = (next << 1L) & LEFT_MASK;
                lastCell = tmp & curBoard;
                next = tmp & oppBoard;
            }

            if (lastCell != 0L) {
                for (int i = 0; i < tmpCellsCount; i++) {
                    allCellsArray[allCellsCount++] = tmpCellsArray[i];
                }
            }

            // TOP LEFT
            lastCell = 0L;
            tmpCellsCount = 0;
            next = (theMove >> (SIZE + 1L)) & RIGHT_MASK & DOWN_MASK & oppBoard;

            while (next != 0L) {
                tmpCellsArray[tmpCellsCount++] = next;
                long tmp = (next >> (SIZE + 1L)) & RIGHT_MASK & DOWN_MASK;
                lastCell = tmp & curBoard;
                next = tmp & oppBoard;
            }

            if (lastCell != 0L) {
                for (int i = 0; i < tmpCellsCount; i++) {
                    allCellsArray[allCellsCount++] = tmpCellsArray[i];
                }
            }

            // TOP RIGHT
            lastCell = 0L;
            tmpCellsCount = 0;
            next = (theMove >> (SIZE - 1L)) & LEFT_MASK & DOWN_MASK & oppBoard;

            while (next != 0L) {
                tmpCellsArray[tmpCellsCount++] = next;
                long tmp = (next >> (SIZE - 1L)) & LEFT_MASK & DOWN_MASK;
                lastCell = tmp & curBoard;
                next = tmp & oppBoard;
            }

            if (lastCell != 0L) {
                for (int i = 0; i < tmpCellsCount; i++) {
                    allCellsArray[allCellsCount++] = tmpCellsArray[i];
                }
            }

            // DOWN LEFT
            lastCell = 0L;
            tmpCellsCount = 0;
            next = (theMove << (SIZE - 1L)) & RIGHT_MASK & UP_MASK & oppBoard;

            while (next != 0L) {
                tmpCellsArray[tmpCellsCount++] = next;
                long tmp = (next << (SIZE - 1L)) & RIGHT_MASK & UP_MASK;
                lastCell = tmp & curBoard;
                next = tmp & oppBoard;
            }

            if (lastCell != 0L) {
                for (int i = 0; i < tmpCellsCount; i++) {
                    allCellsArray[allCellsCount++] = tmpCellsArray[i];
                }
            }

            // DOWN RIGHT
            lastCell = 0L;
            tmpCellsCount = 0;
            next = (theMove << (SIZE + 1L)) & LEFT_MASK & UP_MASK & oppBoard;

            while (next != 0L) {
                tmpCellsArray[tmpCellsCount++] = next;
                long tmp = (next << (SIZE + 1L)) & LEFT_MASK & UP_MASK;
                lastCell = tmp & curBoard;
                next = tmp & oppBoard;
            }

            if (lastCell != 0L) {
                for (int i = 0; i < tmpCellsCount; i++) {
                    allCellsArray[allCellsCount++] = tmpCellsArray[i];
                }
            }

            // flip the stones
            for (int i = 0; i < allCellsCount; i++) {
                setCurrentBoard(currentBoard() | allCellsArray[i]);
                setOpponentBoard(opponentBoard() & ~allCellsArray[i]);
            }
        }

        current = (current + 1) % 2;
        calculateMoves();

        if (Long.bitCount(legalBB) == 0)
        {
            current = (current + 1) % 2;
            calculateMoves();

            if (Long.bitCount(legalBB) == 0)
                gameOver = true;
            else
                legalBB = PASS;

            current = (current + 1) % 2;
        }

        notifyMoveObservers();
    }

    @Override
    public Othello newInstance() {
        return new Othello();
    }

    @Override
    public void reset() {
        current = 0;
        gameOver = false;
        blackBB = INIT_BLACK_BB;
        whiteBB = INIT_WHITE_BB;
        legalBB = INIT_LEGAL_BB;
        movesArrayUpdated = false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getGameInfo());
        builder.append(getColumnHeaders());
        builder.append(getBoardStr());
        builder.append(getColumnHeaders());
        return builder.toString();
    }

    ////////////
    // Object //
    ////////////

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Othello othello = (Othello) o;
        return  blackBB == othello.blackBB &&
                current == othello.current &&
               gameOver == othello.gameOver &&
                legalBB == othello.legalBB &&
                whiteBB == othello.whiteBB;
    }

    @Override
    public int hashCode() {
        int result = current;
        result = 31 * result + (gameOver ? 1 : 0);
        result = 31 * result + (int) (blackBB ^ (blackBB >>> 32));
        result = 31 * result + (int) (whiteBB ^ (whiteBB >>> 32));
        result = 31 * result + (int) (legalBB ^ (legalBB >>> 32));
        return result;
    }


    /**
     * The three states in which a square of the Othello can be: black disc, white disc or empty.
     */
    public enum Square {

        BLACK(0), WHITE(1), EMPTY(-1);

        /** The owner of the disc, -1 if the square is empty. */
        private int player;

        /**
         * Creates a new square.
         * @param player the player that owns the square
         */
        private Square(int player) {
            this.player = player;
        }

        /**
         * Returns the player that owns the square. Returns -1 if empty.
         * @return the index of the player that owns the square
         */
        public int getPlayer() {
            return player;
        }
    }

    /////////////////////
    // DEBUGGING STUFF //
    /////////////////////

    // X  X  X  X  X  X  X  -
    // X  X  X  X  X  X  X  -
    // X  X  X  X  X  X  X  -
    // X  X  X  X  X  X  X  -
    // X  X  X  X  X  X  X  -
    // X  X  X  X  X  X  X  -
    // X  X  X  X  X  X  X  -
    // X  X  X  X  X  X  X  -
    public static final long RIGHT_MASK = 9187201950435737471L;

    // -  X  X  X  X  X  X  X
    // -  X  X  X  X  X  X  X
    // -  X  X  X  X  X  X  X
    // -  X  X  X  X  X  X  X
    // -  X  X  X  X  X  X  X
    // -  X  X  X  X  X  X  X
    // -  X  X  X  X  X  X  X
    // -  X  X  X  X  X  X  X
    public static final long LEFT_MASK = -72340172838076674L;

    // -  -  -  -  -  -  -  -
    // X  X  X  X  X  X  X  X
    // X  X  X  X  X  X  X  X
    // X  X  X  X  X  X  X  X
    // X  X  X  X  X  X  X  X
    // X  X  X  X  X  X  X  X
    // X  X  X  X  X  X  X  X
    // X  X  X  X  X  X  X  X
    public static final long UP_MASK = -256L;

    // X  X  X  X  X  X  X  X
    // X  X  X  X  X  X  X  X
    // X  X  X  X  X  X  X  X
    // X  X  X  X  X  X  X  X
    // X  X  X  X  X  X  X  X
    // X  X  X  X  X  X  X  X
    // X  X  X  X  X  X  X  X
    // -  -  -  -  -  -  -  -
    public static final long DOWN_MASK = 72057594037927935L;

    // X  X  X  X  X  X  X  X
    // X  X  X  X  X  X  X  X
    // X  X  X  X  X  X  X  X
    // X  X  X  X  X  X  X  X
    // X  X  X  X  X  X  X  X
    // X  X  X  X  X  X  X  X
    // X  X  X  X  X  X  X  X
    // X  X  X  X  X  X  X  X
    private static final long PASS = -1L;

    public static void main(String[] args) {
        Othello tic = new Othello();
        System.out.println(SpeedTest.gameSpeed(tic, 10));
    }
}
