package net.davidrobles.mauler.othello.ef.wpc;

import java.io.*;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/**
 * Utility methods used by weighted piece counters.
 * @author David Robles
 */
public class WPCUtil
{
    /**
     * @param type the type of the wpc (symmetric or asymmetric)
     * @return an array of zeros of size of the given WPC type.
     */
    public static double[] getZeroWeights(WPCType type)
    {
        double[] weights = new double[type.getSize()];
        Arrays.fill(weights, 0);
        return weights;
    }

    /**
     * Returns an array of random numbers. The size of the array is given by the WPC type.
     * @param rng the random number generator
     * @param type the type of the wpc (asymmetric or asymmetric)
     * @return an array of random numbers
     */
    public static double[] getRandomWeights(Random rng, WPCType type)
    {
        double[] weights = new double[type.getSize()];

        for (int i = 0; i < weights.length; i++)
            weights[i] = ((2 * rng.nextDouble()) - 1) / 8;

        return weights;
    }

    /**
     * Loads a WPC.
     * @param filename the filename of the WPC file
     * @return a WPC
     */
    public static double[] load(String filename)
    {
        return load(new File("OthelloSB/resources/wpc/" + filename + ".wpc"));
    }

    /**
     * Loads a WPC
     * @param file the file to be loaded as a WPC
     * @return a WPC
     */
    public static double[] load(File file)
    {
        double[] weights = null;

        try
        {
            Scanner scanner = new Scanner(file);

            if (scanner.hasNextLine())
            {
                String[] weightStrings = scanner.nextLine().split(",");

                if (weightStrings.length == WPCType.SYM.getSize())
                    weights = new double[WPCType.SYM.getSize()];
                else if (weightStrings.length == WPCType.ASYM.getSize())
                    weights = new double[WPCType.ASYM.getSize()];
                else
                    throw new IllegalArgumentException("Sym or Asym only!" + weightStrings.length);

                for (int i = 0; i < weightStrings.length; i++)
                    weights[i] = Double.valueOf(weightStrings[i]);
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return weights;
    }

    /**
     * Saves the weights of a WPC
     * @param filename the name of the file where the weights will be saved
     * @param weights the weights of the WPC
     */
    public static void save(String filename, double[] weights)
    {
        save(new File("OthelloSB/resources/wpc/" + filename + ".wpc"), weights);
    }

    /**
     * Saves the weights of a WPC
     * @param file the file where the weights will be saved
     * @param weights the weights of the WPC
     */
    public static void save(File file, double[] weights)
    {
        try
        {
            StringBuilder builder = new StringBuilder();

            for (double weight : weights)
                builder.append((weight + ","));

            PrintWriter writer = new PrintWriter(file);
            writer.print(builder.deleteCharAt(builder.length() - 1));
            writer.flush();
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
