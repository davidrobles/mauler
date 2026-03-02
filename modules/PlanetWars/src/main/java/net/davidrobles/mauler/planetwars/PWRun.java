package net.davidrobles.mauler.planetwars;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PWRun {

    private static final Random rand = new Random();

    private static void runGameModel(int games)
    {
        List<PWBot> bots = new ArrayList<PWBot>();
        bots.add(new PWBotMC(1));
        bots.add(new PWBotTest(2));

        int[] wins = new int[3];

        for (int i = 0; i < games; i++) {
            int mapIndex = rand.nextInt(100) + 1;
            String gameStateString = PWAdapter.loadMapToString("PlanetWars/maps/map" + mapIndex + ".txt");
            PWGame game = new PWGame(gameStateString, bots);

            // Game loop
            int winner = -1;

            while ((winner = game.getGameState().Winner()) < 0) {
                game.next();
            }

            wins[winner] += 1; 

//            System.out.println("Winner: Player " + winner);
        }

        for (int i = 0; i < 3; i++) {
            System.out.printf("Wins for player " + i + ": " + wins[i] + "\n");
        }
    }

    private static void runGameWithView(PWBot bot1, PWBot bot2)
    {
        int sleepTime = 100;

        // Set the bots
        List<PWBot> bots = new ArrayList<PWBot>();
        bots.add(bot1);
        bots.add(bot2);

        // Loads a game string from a random map
        int mapIndex = rand.nextInt(100) + 1;
        String gameStateString = PWAdapter.loadMapToString("PlanetWars/resources/maps/map" + mapIndex + ".txt");

        // Create a new game
        PWGame game = new PWGame(gameStateString, bots);
        PWView view = new PWView(game);
        PWViewGraph graph = new PWViewGraph(game);
        PWFrame viewFrame = new PWFrame(view);
        viewFrame.setLocation(100, 100);
        PWFrame graphFrame = new PWFrame(graph);
        graphFrame.setLocation(900, 300);

        // Game loop
        int winner = -1;

        while ((winner = game.getGameState().Winner()) < 0) {
            game.next();
            sleep(sleepTime);
        }

        // Result
        System.out.println("Winner: Player " + winner);
    }

    private static void sleep(int sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        runGameWithView(new PWBotHC(1), new PWBotWanstein(2));
    }

}
