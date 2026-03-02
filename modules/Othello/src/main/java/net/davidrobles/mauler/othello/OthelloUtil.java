package net.davidrobles.mauler.othello;

import java.util.ArrayList;
import java.util.List;

public class OthelloUtil
{
    public static int[] SYMMETRY_MAP = new int[] {
        0, 1, 2, 3, 3, 2, 1, 0,
        1, 4, 5, 6, 6, 5, 4, 1,
        2, 5, 7, 8, 8, 7, 5, 2,
        3, 6, 8, 9, 9, 8, 6, 3,
        3, 6, 8, 9, 9, 8, 6, 3,
        2, 5, 7, 8, 8, 7, 5, 2,
        1, 4, 5, 6, 6, 5, 4, 1,
        0, 1, 2, 3, 3, 2, 1, 0,
    };

    public static long[] symBitboards = new long[] {
            -9151314442816847743L, 4792111478498951490L, 2594215222373842980L, 1729382813125312536L, 18577348462920192L,
            10205666933351424L, 6755684016199680L, 39582420959232L, 26543503441920L, 103481868288L };

    /**
     * This is a helper class used to create new bitboard based on given moves.
     */
    public enum Direction
    {
        UP {
            @Override
            public long shift(long bitboard) {
                return bitboard >> 8 & Othello.DOWN_MASK;
            }
        },
        UP_RIGHT {
            @Override
            public long shift(long bitboard) {
                return bitboard >> 7 & Othello.LEFT_MASK & Othello.DOWN_MASK;
            }
        },
        RIGHT
                {
                    @Override
                    public long shift(long bitboard) {
                        return bitboard << 1 & Othello.LEFT_MASK;
                    }
                },
        DOWN_RIGHT {
            @Override
            public long shift(long bitboard) {
                return bitboard << 9 & Othello.LEFT_MASK & Othello.UP_MASK;
            }
        },
        DOWN {
            @Override
            public long shift(long bitboard) {
                return bitboard << 8 & Othello.UP_MASK;
            }
        },
        DOWN_LEFT {
            @Override
            public long shift(long bitboard) {
                return bitboard << 7 & Othello.RIGHT_MASK & Othello.UP_MASK;
            }
        },
        LEFT {
            @Override
            public long shift(long bitboard) {
                return bitboard >> 1 & Othello.RIGHT_MASK;
            }
        },
        UP_LEFT {
            @Override
            public long shift(long bitboard) {
                return bitboard >> 9 & Othello.RIGHT_MASK & Othello.DOWN_MASK;
            }
        };

        public abstract long shift(long bitboard);
    }

    public static void printBitBoard(long bitboard) {
        for (long i = 0; i < Othello.NUM_SQUARES; i++) {
            System.out.print(((bitboard & (1L << i)) != 0) ? " X " : " - ");
            if (i % Othello.SIZE == Othello.SIZE - 1) {
                System.out.println();
            }
        }
        System.out.println("\n");
    }

    public static Direction[] legals(long cellBitboard) {
        List<Direction> dirs = new ArrayList<Direction>();
        for (Direction dir : Direction.values()) {
            if (dir.shift(cellBitboard) != 0L) {
                dirs.add(dir);
            }
        }
        return dirs.toArray(new Direction[dirs.size()]);
    }

    public static char colToChar(int col) {
        return (char) ('a' + col);
    }

    public static int cellToRowNum(int cellIndex) {
        return cellIndex / Othello.SIZE + 1;
    }

    public static String cellToStr(int cellIndex) {
        return "" + colToChar(cellCol(cellIndex)) + cellToRowNum(cellIndex);
    }

    public static int cellRow(int cellIndex) {
        return cellIndex / Othello.SIZE;
    }

    public static int cellCol(int cellIndex) {
        return cellIndex % Othello.SIZE;
    }
}
