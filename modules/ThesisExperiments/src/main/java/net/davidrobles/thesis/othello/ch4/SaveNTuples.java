package net.davidrobles.thesis.othello.ch4;

import net.davidrobles.mauler.othello.ef.ntuples.NTUtil;
import net.davidrobles.mauler.othello.ef.ntuples.NTuple;
import net.davidrobles.mauler.othello.ef.ntuples.NTupleSystem;
import net.davidrobles.thesis.othello.ch4.ga.GANTuples;

public class SaveNTuples
{
    static void save()
    {
        NTupleSystem a = NTUtil.generateRandomNTupleSystem(7, 6);
        NTupleSystem b = NTUtil.generateRandomNTupleSystem(7, 6);
        NTupleSystem[] offspring = GANTuples.reproduce(a, b);
        NTUtil.save(a, "a");
        NTUtil.save(b, "b");
        NTUtil.save(offspring[0], "offspring-a");
        NTUtil.save(offspring[1], "offspring-b");

        print(a);
        System.out.println("----------");
        print(b);
        System.out.println("----------");
        print(offspring[0]);
        System.out.println("----------");
        print(offspring[1]);
        System.out.println("----------");
    }

    static void mutate()
    {
        NTupleSystem b = NTUtil.load("b");
        b.setNTuple(3, NTUtil.generateNTuple(6));
        NTUtil.save(b, "b-mut");
    }

    static void print(NTupleSystem nts)
    {
        for (NTuple nTuple : nts.getNTuples())
            NTUtil.printWalk(nTuple.getTuples()[0]);
    }

    public static void main(String[] args)
    {
        mutate();
    }
}
