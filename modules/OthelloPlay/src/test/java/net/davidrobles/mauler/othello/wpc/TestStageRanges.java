package net.davidrobles.mauler.othello.wpc;

import net.davidrobles.mauler.othello.ef.wpc.WPCStages;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestStageRanges
{
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testValidateRanges1()
    {
        exception.expect(IllegalArgumentException.class);
        WPCStages.validateRanges(new int[] { -1 });
    }

    @Test
    public void testValidateRange2()
    {
        exception.expect(IllegalArgumentException.class);
        WPCStages.validateRanges(new int[] { 0 });
    }

    @Test
    public void testValidateRanges3()
    {
        WPCStages.validateRanges(new int[] { 1 });
    }

    @Test
    public void testValidateRanges5()
    {
        WPCStages.validateRanges(new int[] { 59 });
    }

    @Test
    public void testValidateRanges6()
    {
        WPCStages.validateRanges(new int[] { 60 });
    }

    @Test
    public void testValidateRanges7()
    {
        exception.expect(IllegalArgumentException.class);
        WPCStages.validateRanges(new int[] { 61 });
    }

    @Test
    public void testValidateRanges8()
    {
        WPCStages.validateRanges(new int[] { 2, 5 });
    }

    @Test
    public void testValidateRanges9()
    {
        exception.expect(IllegalArgumentException.class);
        WPCStages.validateRanges(new int[] { 5, 2 });
    }

    @Test
    public void testValidateRanges10()
    {
        exception.expect(IllegalArgumentException.class);
        WPCStages.validateRanges(new int[] { });
    }
}
