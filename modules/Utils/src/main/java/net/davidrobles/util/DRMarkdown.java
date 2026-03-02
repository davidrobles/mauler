package net.davidrobles.util;

public class DRMarkdown
{
    public static void printHeader(String str, String header)
    {
        for (int i = 0; i < str.length(); i++)
            System.out.print(header);

        System.out.println("\n" + str);

        for (int i = 0; i < str.length(); i++)
            System.out.print(header);

        System.out.println("\n");
    }

    public static void printH1(String str)
    {
        printHeader(str, "=");
    }

    public static void printH2(String str)
    {
        printHeader(str, "-");
    }

    public static void printH3(String str)
    {
        System.out.println("### " + str);
    }
}
