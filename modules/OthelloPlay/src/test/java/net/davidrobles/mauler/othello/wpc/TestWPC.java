package net.davidrobles.mauler.othello.wpc;

import net.davidrobles.mauler.othello.ef.wpc.WPC;
import net.davidrobles.mauler.othello.ef.wpc.WPCType;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class TestWPC
{
    @Test
    public void testSymWPCTypeSize()
    {
        Assert.assertEquals(10, WPCType.SYM.getSize());
    }

    @Test
    public void testAsymWPCTypeSize()
    {
        assertEquals(64, WPCType.ASYM.getSize());
    }

    @Test
    public void testSymWPCType()
    {
        double[] weights = new double[WPCType.SYM.getSize()];
        WPC wpc = new WPC(weights);
        assertEquals(WPCType.SYM, wpc.getType());
    }

    @Test
    public void testAsymWPCType()
    {
        double[] weights = new double[WPCType.ASYM.getSize()];
        WPC wpc = new WPC(weights);
        assertEquals(WPCType.ASYM, wpc.getType());
    }

    @Test
    public void testSetSymWeightsCopy()
    {
        double[] weights = new double[WPCType.SYM.getSize()];

        for (int i = 0; i < WPCType.SYM.getSize(); i++)
            weights[i] = i;

        WPC wpc = new WPC(weights);
        wpc.setWeights(weights);
        assertNotSame(weights, wpc.getWeights());
    }

    @Test
    public void testSetAsymWeightsCopy()
    {
        double[] weights = new double[WPCType.ASYM.getSize()];

        for (int i = 0; i < WPCType.ASYM.getSize(); i++)
            weights[i] = i;

        WPC wpc = new WPC(weights);
        wpc.setWeights(weights);
        assertNotSame(weights, wpc.getWeights());
    }

    @Test
    public void testSetSymWeightsEquals()
    {
        double[] weights = new double[WPCType.SYM.getSize()];

        for (int i = 0; i < WPCType.SYM.getSize(); i++)
            weights[i] = i;

        WPC wpc = new WPC(weights);
        wpc.setWeights(weights);

        assertArrayEquals(weights, wpc.getWeights(), 0.1);
    }

    @Test
    public void testSetAsymWeightsEquals()
    {
        double[] weights = new double[WPCType.ASYM.getSize()];

        for (int i = 0; i < WPCType.ASYM.getSize(); i++)
            weights[i] = i;

        WPC wpc = new WPC(weights);
        wpc.setWeights(weights);

        assertArrayEquals(weights, wpc.getWeights(), 0.1);
    }
}
