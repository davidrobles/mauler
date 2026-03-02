package net.davidrobles.thesis.othello.ch4.ga;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class GANTuplesTest
{
    @Rule
    public ExpectedException exception = ExpectedException.none();
    private List<ScoredNTS> scoredPop = new ArrayList<ScoredNTS>();
    private double[] fitnesses;
    @Before
    public void init()
    {
        scoredPop.add(new ScoredNTS(null, 3.3));
        scoredPop.add(new ScoredNTS(null, 4));
        scoredPop.add(new ScoredNTS(null, 7.2));
        scoredPop.add(new ScoredNTS(null, 2.8));
        fitnesses = new double[scoredPop.size()];
        fitnesses = FitProp.createFitnessesArray(scoredPop);
    }

    @Test
    public void testRandFit()
    {
        for (int i = 0; i < 100; i++)
        {
            double value = FitProp.genRandFit(fitnesses);
            assertTrue(value >= 0 && value <= fitnesses[fitnesses.length - 1]);
        }
    }

    @Test
    public void testFitnessesArray()
    {
        double[] array = FitProp.createFitnessesArray(scoredPop);
        assertEquals(3.3, array[0], 0.01);
        assertEquals(7.3, array[1], 0.01);
        assertEquals(14.5, array[2], 0.01);
        assertEquals(17.3, array[3], 0.01);
    }

    @Test
    public void testFitnessProportionateSelection()
    {
//        assertEquals(0, FitProp.fitnessProportionateSelection(0, fitnesses), 0.01);
//        assertEquals(0, FitProp.fitnessProportionateSelection(1.5, fitnesses), 0.01);
//        assertEquals(0, FitProp.fitnessProportionateSelection(3.2, fitnesses), 0.01);
//        assertEquals(0, FitProp.fitnessProportionateSelection(3.3, fitnesses), 0.01);
//        assertEquals(1, FitProp.fitnessProportionateSelection(3.4, fitnesses), 0.01);
//        assertEquals(1, FitProp.fitnessProportionateSelection(7.3, fitnesses), 0.01);
//        assertEquals(2, FitProp.fitnessProportionateSelection(7.4, fitnesses), 0.01);
//        assertEquals(2, FitProp.fitnessProportionateSelection(14.5, fitnesses), 0.01);
//        assertEquals(3, FitProp.fitnessProportionateSelection(14.6, fitnesses), 0.01);
//        assertEquals(3, FitProp.fitnessProportionateSelection(17.3, fitnesses), 0.01);
//
//        exception.expect(IllegalArgumentException.class);
//        assertEquals(4, FitProp.fitnessProportionateSelection(17.4, fitnesses), 0.01);
    }
}
