package net.davidrobles.mauler.othello.wpc;

import net.davidrobles.mauler.othello.ef.wpc.WPCStages;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class TestStonesToStages
{
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testFindStage1()
    {
        assertEquals(-1, WPCStages.findStage(0, new int[] { 30 }));
    }

    @Test
    public void testFindStage2()
    {
        assertEquals(-1, WPCStages.findStage(3, new int[] { 30 }));
    }

    @Test
    public void testFindStage3()
    {
        assertEquals(0, WPCStages.findStage(4, new int[] { 30 }));
    }

    @Test
    public void testFindStage4()
    {
        assertEquals(0, WPCStages.findStage(32, new int[] { 30 }));
    }

    @Test
    public void testFindStage5()
    {
        assertEquals(0, WPCStages.findStage(33, new int[] { 30 }));
    }

    @Test
    public void testFindStage6()
    {
        assertEquals(1, WPCStages.findStage(34, new int[] { 30 }));
    }

    @Test
    public void testFindStage7()
    {
        assertEquals(1, WPCStages.findStage(35, new int[] { 30 }));
    }

    @Test
    public void testFindStage8()
    {
        assertEquals(1, WPCStages.findStage(64, new int[] { 30 }));
    }

    @Test
    public void testFindStage9()
    {
        assertEquals(-1, WPCStages.findStage(65, new int[] { 30 }));
    }

    @Test
    public void testFindStage10()
    {
        assertEquals(1, WPCStages.findStage(43, new int[] { 20, 40 }));
    }

    @Test
    public void testFindStage11()
    {
        assertEquals(2, WPCStages.findStage(44, new int[] { 20, 40 }));
    }

    @Test
    public void testFindStage12()
    {
        assertEquals(0, WPCStages.findStage(23, new int[] { 20, 40 }));
    }

    @Test
    public void testFindStage13()
    {
        assertEquals(1, WPCStages.findStage(24, new int[] { 20, 40 }));
    }
}
