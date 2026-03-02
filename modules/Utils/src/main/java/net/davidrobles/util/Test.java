package net.davidrobles.util;

import java.util.EnumMap;

public class Test
{
    enum Ghost { BLINKY, INKY, SUE, PINKY }
    enum Move { UP, RIGHT, DOWN, LEFT }

    static EnumMap<Ghost, Move>[] calcAllCombinations(EnumMap<Ghost, Move[]> map)
    {
        int count = 0;

        for (Move[] m : map.values())
        {
            if (count == 0)
                count = m.length;
            else
                count *= m.length;
        }

        // ghosts list

        Ghost[] ghosts = new Ghost[map.size()];

        int r = 0;

        for (Ghost ghost : map.keySet())
            ghosts[r++] = ghost;

        // truth table

        int[] sizes = new int[ghosts.length];

        for (int col = 0; col < ghosts.length; col++)
        {
            sizes[col] = map.get(ghosts[col]).length;

            if (col > 0)
                sizes[col] *= sizes[col - 1];
        }

        @SuppressWarnings("unchecked")
        EnumMap<Ghost, Move>[] perms = new EnumMap[count];

        for (int row = 0; row < count; row++)
        {
            perms[row] = new EnumMap<Ghost, Move>(Ghost.class);

            for (int col = 0; col < ghosts.length; col++)
            {
                Move[] ms = map.get(ghosts[col]);
                perms[row].put(ghosts[col], ms[(row / (count / sizes[col])) % ms.length]);
            }
        }

        return perms;
    }

    public static void main (String [] args)
    {
        EnumMap<Ghost, Move[]> map = new EnumMap<Ghost, Move[]>(Ghost.class);
        map.put(Ghost.BLINKY, new Move[] { Move.UP, Move.DOWN, Move.RIGHT });
        map.put(Ghost.INKY, new Move[] { Move.LEFT, Move.RIGHT });
        map.put(Ghost.SUE, new Move[] { Move.LEFT, Move.RIGHT, Move.UP });
        map.put(Ghost.PINKY, new Move[] { Move.DOWN, Move.UP });

        for (EnumMap<Ghost, Move> perm : calcAllCombinations(map))
            System.out.println(perm);
    }
}



