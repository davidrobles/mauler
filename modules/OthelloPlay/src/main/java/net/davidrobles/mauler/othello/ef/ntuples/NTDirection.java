package net.davidrobles.mauler.othello.ef.ntuples;

import net.davidrobles.mauler.othello.Othello;

public enum NTDirection
{
    UP
            {
                @Override
                public long shift(long bitboard)
                {
                    return bitboard >> 8 & Othello.DOWN_MASK;
                }
            },
    UP_RIGHT
            {
                @Override
                public long shift(long bitboard)
                {
                    return bitboard >> 7 & Othello.LEFT_MASK & Othello.DOWN_MASK;
                }
            },
    RIGHT
            {
                @Override
                public long shift(long bitboard)
                {
                    return bitboard << 1 & Othello.LEFT_MASK;
                }
            },
    DOWN_RIGHT
            {
                @Override
                public long shift(long bitboard)
                {
                    return bitboard << 9 & Othello.LEFT_MASK & Othello.UP_MASK;
                }
            },
    DOWN
            {
                @Override
                public long shift(long bitboard)
                {
                    return bitboard << 8 & Othello.UP_MASK;
                }
            },
    DOWN_LEFT
            {
                @Override
                public long shift(long bitboard)
                {
                    return bitboard << 7 & Othello.RIGHT_MASK & Othello.UP_MASK;
                }
            },
    LEFT
            {
                @Override
                public long shift(long bitboard)
                {
                    return bitboard >> 1 & Othello.RIGHT_MASK;
                }
            },
    UP_LEFT
            {
                @Override
                public long shift(long bitboard)
                {
                    return bitboard >> 9 & Othello.RIGHT_MASK & Othello.DOWN_MASK;
                }
            }
//        ,
//        ALL
//        {
//            @Override
//            public long shift(long bitboard)
//            {
//                long tempBoard = 0L;
//
//                for (Direction dir : Direction.values())
//                    if (dir != this)
//                        tempBoard |= dir.shift(bitboard);
//
//                return tempBoard;
//            }
//        }
    ;

    public abstract long shift(long bitboard);
}
