package net.davidrobles.mauler.othello.xof;

import net.davidrobles.mauler.othello.Othello;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Utility methods to read XOF files. XOF is the XML Othello game file
 * format used into OthBase as the primary supported file format for mauler databases.
 */
public class XOFReader
{
    static String[] loadGames(String filename) throws FileNotFoundException
    {
        List<String> games = new ArrayList<String>();
        Scanner scanner = new Scanner(new File(filename));

        while (scanner.hasNextLine())
            games.add(scanner.nextLine());

        return games.toArray(new String[games.size()]);
    }

    static boolean checkGame(Othello othello, String[] moves, boolean debug)
    {
        int ply = 0;

        while (!othello.isOver())
        {
            String myMove = moves[ply++];

            if (!checkMove(othello, myMove))
                return false;

            othello.makeMove(Arrays.asList(othello.getMoves()).indexOf(myMove));

            if (debug)
                System.out.println(othello);

            if (othello.getMoves().length == 1 && othello.getMoves()[0].equals("PASS"))
            {
                othello.makeMove(0);

                if (debug)
                    System.out.println(othello);
            }
        }

        System.out.println("done");

        return true;
    }

    static String[] breakMoves(String movesStr)
    {
        String[] moves = new String[movesStr.length() / 2];

        for (int i = 0; i < moves.length; i++)
            moves[i] = String.valueOf(movesStr.charAt(i * 2)) + String.valueOf(movesStr.charAt(i * 2 + 1));

        return moves;
    }

    static boolean checkMove(Othello othello, String moveStr)
    {
        return Arrays.asList(othello.getMoves()).contains(moveStr);
    }

    static void checkGames(String[] games, boolean debug)
    {
        Othello othello = new Othello();

        for (String gameStr : games)
        {
            othello.reset();

            if (!checkGame(othello, breakMoves(gameStr), debug))
                System.out.println(gameStr);
        }
    }

    static void checkGameString(String gameStr, boolean debug)
    {
        System.out.println("Checking game string: " + gameStr);
        System.out.println("Is legal? " + checkGame(new Othello(), breakMoves(gameStr), debug));
    }

    public static void main(String[] args) throws FileNotFoundException
    {
//        checkGames(loadGames("OthelloSB/resources/mauler/woc2006.txt"));
//        checkGames(loadGames("/Users/drobles/Desktop/sap23.txt"));
//        checkGameString("f3d6c3d3c4f4f6g5e6f7d7c5g3e3d2c7b5b6c6d8e7f3a6h3g4e8h6b4a5a3a4a7g6c2b2b3f2h5a2a1e2h4h2b1c1f1e1b7h7d1g2h1g1h8b8g7a8c8f8g8");
        checkGame(new Othello(), breakMoves("f5d6c3d3c4f4f6g5e6f7d7c5g3e3d2c7b5b6c6d8e7f3a6h3g4e8h6b4a5a3a4a7g6c2b2b3f2h5a2a1e2h4h2b1c1f1e1b7h7d1g2h1g1h8b8g7a8c8f8g8"), true);
    }
}
