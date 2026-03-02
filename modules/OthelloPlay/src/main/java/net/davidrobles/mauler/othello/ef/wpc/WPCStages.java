package net.davidrobles.mauler.othello.ef.wpc;

import net.davidrobles.mauler.othello.Othello;
import net.davidrobles.mauler.players.EvalFunc;

import java.util.List;
import java.util.Random;

public class WPCStages implements EvalFunc<Othello>
{
    private List<? extends WPC> wpcs;
    private int[] ranges;
    private Random rng;
    private static final boolean DEBUG = false;

    public WPCStages(List<? extends WPC> wpcs, int[] ranges, Random rng)
    {
        if (wpcs.size() - 1 != ranges.length)
            throw new IllegalArgumentException();

        this.wpcs = wpcs;
        this.ranges = ranges;
        this.rng = rng;
    }

    public WPCStages(List<? extends WPC> wpcs, int[] ranges)
    {
        this(wpcs, ranges, new Random());
    }

    @Override
    public double eval(Othello othello, int player)
    {
        int stage = findStage(othello.getNumDiscs(), ranges);
        WPC wpc = wpcs.get(stage);

        if (wpc == null)
            return rng.nextDouble() * 2 - 1;

        return wpc.eval(othello, player);
    }

    public static void validateNumStones(int stones)
    {
        if (stones < 4 || stones > 64)
            throw new IllegalArgumentException();
    }

    public static void validateRanges(int[] ranges)
    {
        if (ranges.length == 0)
            throw new IllegalArgumentException();

        for (int i = 0; i < ranges.length; i++)
        {
            if (ranges[i] <= 0 || ranges[i] > 60)
                throw new IllegalArgumentException();

            if (i > 0 && ranges[i - 1] >= ranges[i])
                throw new IllegalArgumentException();
        }
    }

    // returns -1 if no stage found
    public static int findStage(int stones, int[] ranges)
    {
        if (DEBUG)
        {
            validateRanges(ranges);
            validateNumStones(stones);
        }

        for (int i = 0; i < ranges.length + 1; i++)
        {
            if (i == 0)
            {
                if (stones >= Othello.NUM_DISCS_START && stones < ranges[i] + Othello.NUM_DISCS_START)
                    return i;
            }
            else if (i == ranges.length)
            {
                if (stones >= ranges[i - 1] + Othello.NUM_DISCS_START && stones <= Othello.NUM_SQUARES)
                    return i;
            }
            else
            {
                if (stones >= ranges[i - 1] + Othello.NUM_DISCS_START && stones < ranges[i] + Othello.NUM_DISCS_START)
                    return i;
            }
        }

        return -1;
    }

    @Override
    public String toString()
    {
        return "WPCStages";
    }
}
