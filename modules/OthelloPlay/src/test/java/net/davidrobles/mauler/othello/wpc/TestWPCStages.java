package net.davidrobles.mauler.othello.wpc;

import net.davidrobles.mauler.othello.ef.wpc.WPCStages;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestWPCStages
{
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testValidateNumStones()
    {
        WPCStages.validateNumStones(4);
        WPCStages.validateNumStones(40);
        WPCStages.validateNumStones(64);
        exception.expect(IllegalArgumentException.class);
        WPCStages.validateNumStones(0);
        exception.expect(IllegalArgumentException.class);
        WPCStages.validateNumStones(3);
        exception.expect(IllegalArgumentException.class);
        WPCStages.validateNumStones(65);
    }
}
