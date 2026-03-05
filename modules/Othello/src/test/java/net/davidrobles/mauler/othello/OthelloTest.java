package net.davidrobles.mauler.othello;

import static org.junit.Assert.*;

import java.util.List;
import net.davidrobles.mauler.core.GameResult;
import net.davidrobles.mauler.core.GameTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Full test suite for {@link Othello}.
 *
 * <p>Inherited from {@link GameTest}: testGameFinishes, testCopy, testHashCode,
 * testNumLegalMovesEqualsListMoves.
 *
 * <p>Board layout (row-major, 0-indexed):
 *
 * <pre>
 *    a  b  c  d  e  f  g  h
 * 1  0  1  2  3  4  5  6  7
 * 2  8  9 10 11 12 13 14 15
 * 3 16 17 18 19 20 21 22 23
 * 4 24 25 26 27 28 29 30 31
 * 5 32 33 34 35 36 37 38 39
 * 6 40 41 42 43 44 45 46 47
 * ...
 * </pre>
 *
 * <p>Standard starting position: White at d4(27) and e5(36), Black at e4(28) and d5(35). Black
 * (player 0) moves first. Initial legal moves for Black: d3(19), c4(26), f5(37), e6(44).
 */
public class OthelloTest extends GameTest<Othello> {
    @Before
    public void init() {
        game = new Othello();
    }

    // -------------------------------------------------------------------------
    // Initial state
    // -------------------------------------------------------------------------

    @Test
    public void initialPlayerIsBlack() {
        assertEquals(0, game.getCurPlayer());
    }

    @Test
    public void initialDiscsPerPlayer() {
        assertEquals(2, game.getNumDiscs(0)); // black
        assertEquals(2, game.getNumDiscs(1)); // white
    }

    @Test
    public void initialTotalDiscIsFour() {
        assertEquals(Othello.NUM_DISCS_START, game.getNumDiscs());
    }

    @Test
    public void initialBoardHasStandardOthelloPosition() {
        // d4=27:White, e4=28:Black, d5=35:Black, e5=36:White; all others empty
        for (int i = 0; i < Othello.NUM_SQUARES; i++) {
            if (i == 27 || i == 36)
                assertEquals(
                        "expected White at cell " + i, Othello.Square.WHITE, game.getSquare(i));
            else if (i == 28 || i == 35)
                assertEquals(
                        "expected Black at cell " + i, Othello.Square.BLACK, game.getSquare(i));
            else
                assertEquals(
                        "expected Empty at cell " + i, Othello.Square.EMPTY, game.getSquare(i));
        }
    }

    @Test
    public void initialNumMovesIsFour() {
        assertEquals(4, game.getNumMoves());
    }

    @Test
    public void initialMovesAreStandardOpeningMoves() {
        // Black's four opening moves in standard Othello
        List<String> moves = game.getMoves();
        assertTrue(moves.contains("d3")); // cell 19
        assertTrue(moves.contains("c4")); // cell 26
        assertTrue(moves.contains("f5")); // cell 37
        assertTrue(moves.contains("e6")); // cell 44
        assertEquals(4, moves.size());
    }

    @Test
    public void initialGameIsNotOver() {
        assertFalse(game.isOver());
    }

    @Test
    public void initialOutcomeIsAbsent() {
        assertFalse(game.getOutcome().isPresent());
    }

    @Test
    public void getNumPlayersIsTwo() {
        assertEquals(2, game.getNumPlayers());
    }

    @Test
    public void getNameIsOthello() {
        assertEquals("Othello", game.getName());
    }

    // -------------------------------------------------------------------------
    // Move mechanics — flip and disc counts
    // -------------------------------------------------------------------------

    @Test
    public void playerSwitchesAfterNormalMove() {
        assertEquals(0, game.getCurPlayer());
        game.makeMove(0); // Black plays
        assertEquals(1, game.getCurPlayer());
    }

    @Test
    public void playingD3FlipsWhiteDisc() {
        // Black plays d3 (cell 19). White at d4 (cell 27) is sandwiched
        // between the new Black disc at d3 and the existing Black disc at d5 (cell 35).
        game.makeMove(0); // d3 = first move in ascending cell order
        assertEquals(Othello.Square.BLACK, game.getSquare(19)); // placed
        assertEquals(Othello.Square.BLACK, game.getSquare(27)); // flipped White → Black
        assertEquals(Othello.Square.BLACK, game.getSquare(28)); // unchanged
        assertEquals(Othello.Square.BLACK, game.getSquare(35)); // unchanged
        assertEquals(Othello.Square.WHITE, game.getSquare(36)); // unchanged
    }

    @Test
    public void discCountsUpdateAfterFlip() {
        // Black plays d3: places 1 disc, flips 1 White → Black
        game.makeMove(0); // d3
        assertEquals(4, game.getNumDiscs(0)); // Black: 2 + 1 placed + 1 flipped
        assertEquals(1, game.getNumDiscs(1)); // White: 2 - 1 flipped
    }

    @Test
    public void totalDiscsIncreaseByOneAfterNormalMove() {
        // Placing always adds exactly one disc; flips only convert, not add
        int before = game.getNumDiscs();
        game.makeMove(0);
        assertEquals(before + 1, game.getNumDiscs());
    }

    @Test
    public void makeMoveByStringEquivalentToIndex() {
        // makeMove("d3") should produce the same state as makeMove(0)
        Othello byIndex = new Othello();
        byIndex.makeMove(0);

        game.makeMove("d3");

        assertEquals(byIndex, game);
    }

    // -------------------------------------------------------------------------
    // Game over and outcomes
    // -------------------------------------------------------------------------

    @Test
    public void outcomeAbsentDuringPlay() {
        for (int i = 0; i < 10; i++) {
            assertFalse(game.getOutcome().isPresent());
            game.makeMove(0);
        }
    }

    @Test
    public void noMovesWhenGameOver() {
        while (!game.isOver()) game.makeMove(0);
        assertEquals(0, game.getNumMoves());
        assertEquals(0, game.getMoves().size());
    }

    @Test
    public void outcomeConsistentWithDiscCount() {
        while (!game.isOver()) game.makeMove(0);

        GameResult[] outcome = game.getOutcome().orElseThrow();
        int black = game.getNumDiscs(0);
        int white = game.getNumDiscs(1);

        if (black > white) {
            assertEquals(GameResult.WIN, outcome[0]);
            assertEquals(GameResult.LOSS, outcome[1]);
        } else if (white > black) {
            assertEquals(GameResult.LOSS, outcome[0]);
            assertEquals(GameResult.WIN, outcome[1]);
        } else {
            assertEquals(GameResult.DRAW, outcome[0]);
            assertEquals(GameResult.DRAW, outcome[1]);
        }
    }

    // -------------------------------------------------------------------------
    // reset()
    // -------------------------------------------------------------------------

    @Test
    public void resetRestoresInitialState() {
        game.makeMove(0);
        game.makeMove(0);
        game.reset();

        assertEquals(0, game.getCurPlayer());
        assertFalse(game.isOver());
        assertEquals(4, game.getNumMoves());
        assertEquals(2, game.getNumDiscs(0));
        assertEquals(2, game.getNumDiscs(1));
        assertFalse(game.getOutcome().isPresent());
    }

    // -------------------------------------------------------------------------
    // copy() independence
    // -------------------------------------------------------------------------

    @Test
    public void copyIsIndependentFromOriginal() {
        game.makeMove(0); // Black plays d3
        Othello copy = game.copy();
        copy.makeMove(0); // White plays on copy only

        // Original: still White's turn, White has 1 disc
        assertEquals(1, game.getCurPlayer());
        assertEquals(1, game.getNumDiscs(1));

        // Copy: Black's turn again, White has more discs
        assertEquals(0, copy.getCurPlayer());
        assertTrue(copy.getNumDiscs(1) > 1);
    }

    @Test
    public void originalIsIndependentFromCopy() {
        Othello copy = game.copy();
        copy.makeMove(0);

        assertEquals(0, game.getCurPlayer());
        assertEquals(4, game.getNumMoves());
        assertEquals(2, game.getNumDiscs(0));
        assertEquals(2, game.getNumDiscs(1));
    }

    // -------------------------------------------------------------------------
    // equals() / hashCode()
    // -------------------------------------------------------------------------

    @Test
    public void equalGamesHaveSameHashCode() {
        game.makeMove(0);
        Othello other = game.copy();
        assertEquals(game, other);
        assertEquals(game.hashCode(), other.hashCode());
    }

    @Test
    public void differentGamesAreNotEqual() {
        Othello other = new Othello();
        game.makeMove(0);
        assertNotEquals(game, other);
    }

    // -------------------------------------------------------------------------
    // getSquare / getBoard
    // -------------------------------------------------------------------------

    @Test
    public void getSquareRowColMatchesIndex() {
        game.makeMove(0); // change state so it is not trivially all-empty
        for (int row = 0; row < Othello.SIZE; row++)
            for (int col = 0; col < Othello.SIZE; col++)
                assertEquals(game.getSquare(Othello.SIZE * row + col), game.getSquare(row, col));
    }

    @Test
    public void getBoardMatchesGetSquare() {
        game.makeMove(0);
        Othello.Square[] board = game.getBoard();
        for (int i = 0; i < Othello.NUM_SQUARES; i++) assertEquals(game.getSquare(i), board[i]);
    }

    // -------------------------------------------------------------------------
    // makeMove() validation
    // -------------------------------------------------------------------------

    @Test(expected = IllegalArgumentException.class)
    public void makeMoveNegativeIndexThrows() {
        game.makeMove(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void makeMoveOutOfRangeThrows() {
        game.makeMove(game.getNumMoves());
    }

    @Test(expected = IllegalArgumentException.class)
    public void makeMoveWhenGameOverThrows() {
        while (!game.isOver()) game.makeMove(0);
        game.makeMove(0);
    }
}
