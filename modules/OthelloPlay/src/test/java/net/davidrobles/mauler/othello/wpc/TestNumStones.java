package net.davidrobles.mauler.othello.wpc;

import net.davidrobles.mauler.othello.ef.wpc.WPCStages;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestNumStones
{
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testValidNumStones1()
    {
        WPCStages.validateNumStones(4);
    }

    @Test
    public void testValidNumStones2()
    {
        WPCStages.validateNumStones(40);
    }

    @Test
    public void testValidNumStones3()
    {
        WPCStages.validateNumStones(64);
    }

    @Test
    public void testValidNumStones4()
    {
        exception.expect(IllegalArgumentException.class);
        WPCStages.validateNumStones(0);
    }

    @Test
    public void testValidNumStones5()
    {
        exception.expect(IllegalArgumentException.class);
        WPCStages.validateNumStones(3);
    }

    @Test
    public void testValidNumStones6()
    {
        exception.expect(IllegalArgumentException.class);
        WPCStages.validateNumStones(65);
    }
}
