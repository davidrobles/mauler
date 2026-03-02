package net.davidrobles.thesis.othello.ch4;//package dr.thesis.othello;
//
//import Outcome;
//import dr.mauler.core.util.Series;
//import dr.mauler.othello.ef.wpc.*;
//import GreedyPlayer;
//import dr.mauler.core.players.Player;
//import Othello;
//import EpsilonGreedy;
//import dr.mauler.core.players.RandomPlayer;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Random;
//
//import static DRMarkdown.*;
//
//public class Experiments
//{
//    static Random RNG = new Random();
//
//    ///////////////
//    // Chapter 4 //
//    ///////////////
//
//    static void learnSymWPCMultiple()
//    {
//        int runs = 30;
//        double[] test = { 0.0, 0.0, 0.0 };
//
//        for (int i = 0; i < runs; i++)
//        {
//            System.out.print(i + " ");
//            WPCTDLearn wpc = new WPCTDLearn.Builder(WPCType.SYM, 500, 0.01, 1.0).build();
//            wpc.learn();
//            List<Player<Othello>> players = new ArrayList<Player<Othello>>();
//            players.add(new GreedyPlayer<Othello>(wpc));
//            players.add(new RandomPlayer<Othello>(RNG));
//            double[] here = null; // GamesUtil.playNGames(new Othello(), players, 10000);
//
//            for (int j = 0; j < test.length; j++)
//                test[j] += here[j];
//        }
//
//        System.out.println();
//
//        for (int j = 0; j < test.length; j++)
//            test[j] /= runs;
//
//        System.out.println(Arrays.toString(test));
//    }
//
//    static void learnWPCWithTD0()
//    {
//        printH1("Learning a WPC using TD(0)");;
//        WPCTDLearn wpc = new WPCTDLearn.Builder(WPCType.ASYM, 10000, 0.01, 1.0).build();
//        wpc.learn();
//
//        printH2("Learnt weights");
//        System.out.println(wpc.getFormattedWeights());
//
//        printH2("Playing against random");
//        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
////        players.add(new EpsilonGreedy<Othello>(wpc, 0.05));
////        players.add(new EpsilonGreedy<Othello>(wpc, 0.05));
//        players.add(new GreedyPlayer<Othello>(wpc));
//        players.add(new RandomPlayer<Othello>(RNG));
//        Series<Othello> series = new Series.Builder<Othello>(new Othello(), 50000, players).build();
//        series.playSeries();
//        series.printResults();
//        System.out.println();
//
//        printH2("Latex table");
//        System.out.println(wpc.toLatexTable());
//    }
//
//    static void learnWPCvsSym()
//    {
//        int episodes = 150000, nGames = 10000, interval = episodes / 100;
//        WPCType type = WPCType.SYM;
//
//        // Player 1
//        WPCTDLearn wpc = new WPCTDLearn.Builder(type, episodes, 0.001, 1.0)
//                .lowerBound(0)
//                .upperBound(22)
//                .build();
//        wpc.setWeights(WPCUtil.getZeroWeights(type));
//
//        // Player 2
//        WPC symWPC = new WPC(WPCUtil.load("sml-weights-sym"));
//
//        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
//        players.add(new EpsilonGreedy<Othello>(wpc, 0.05, RNG));
//        players.add(new EpsilonGreedy<Othello>(symWPC, 0.05, RNG));
//
//        for (int episode = 0; episode <= episodes; episode++)
//        {
//            wpc.iteration();
//
//            if (episode % interval == 0)
//            {
//                System.out.print(episode + ", ");
//                Series<Othello> series = new Series.Builder<Othello>(new Othello(), 10000, players).build();
//                series.playSeries();
//
//                double[] outcomes = series.getData();
//                System.out.println("Fuck: " + outcomes[0]);
//
//                if (outcomes[0] > 0.50)
//                {
//                    System.out.println(outcomes[0]);
//                    WPCUtil.save("dr-sym-" + outcomes[0], wpc.getWeights());
//                    System.out.println("-------------");
//                }
//            }
//        }
//    }
//
//    static void learnWPCStages()
//    {
//        int episodes = 150000;
//        int nGames = 10000;
//        int interval = episodes / 100;
//
//        int numStages = 3;
//        int stonesPerStage = Othello.NUM_SQUARES / numStages;
//
//        // wcps for each state
//        List<WPCTDLearn> allWPCs = new ArrayList<WPCTDLearn>();
//
//        for (int stage = 0; stage < numStages; stage++)
//        {
//            WPCTDLearn wpc = new WPCTDLearn.Builder(WPCType.SYM, 150000, 0.001, 1.0)
//                    .lowerBound(stage * stonesPerStage)
//                    .upperBound(stage * stonesPerStage + stonesPerStage)
//                    .build();
//            allWPCs.add(wpc);
//        }
//
//        // Player 2
//        WPC symWPC = new WPC(WPCUtil.load("sml-weights-sym"));
//
//        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
//        // TODO: temp fix
//        players.add(new EpsilonGreedy<Othello>(new WPCStages(allWPCs, new int[] { 1, 2, 3 }), 0.05, RNG));
//        players.add(new EpsilonGreedy<Othello>(symWPC, 0.05, RNG));
//
//        for (int episode = 0; episode <= episodes; episode++)
//        {
//            for (WPCTDLearn wpc : allWPCs)
//                wpc.iteration();
//
//            if (episode % interval == 0)
//            {
//                System.out.print(episode + ", ");
//                Series<Othello> series = new Series.Builder<Othello>(new Othello(), nGames, players).build();
//                series.playSeries();
//                double[] outcomes = series.getData();
//                System.out.println("es: " + outcomes[0]);
//                if (outcomes[0] > 0.60)
//                {
//                    System.out.println(outcomes[0]);
////                    WPCUtil.save("dr-sym-" + outcomes[0], wpc.getWeights());
//                    System.out.println("-------------");
//                }
//            }
//        }
//    }
//
//    static void wpcEpsilon()
//    {
//        String colsStr = "Training mauler";
//        String rowsStr = "Winning Percentage";
//
//        int repetitions = 5, episodes = 5000, nGames = 10000, interval = episodes / 5;
//
//        System.out.println("Running experiments");
//        System.out.println("Repetitions: " + repetitions);
//
//        WPCType type = WPCType.SYM;
//
//        WPCTDLearn[] wpcs = {
//                new WPCTDLearn.Builder(type, episodes, 0.001, 1.0).build(),
//                new WPCTDLearn.Builder(type, episodes, 0.01, 1.0).build(),
//                new WPCTDLearn.Builder(type, episodes, 0.1, 1.0).build()
//        };
//
//        String[] labels = { "\\alpha = 0.001",
//                            "\\alpha = 0.01",
//                            "\\alpha = 0.1" };
//
//        List<List<Player<Othello>>> players = new ArrayList<List<Player<Othello>>>();
//        double[] weights = WPCUtil.getRandomWeights(RNG, type);
//        WPC symWPC = new WPC(WPCUtil.load(new File("OthelloSB/resources/wpc/sml-weights-sym.wpc")));
//
//        for (int i = 0; i < wpcs.length; i++)
//        {
//            wpcs[i].setWeights(weights);
//            List<Player<Othello>> ps = new ArrayList<Player<Othello>>();
//            ps.add(new EpsilonGreedy<Othello>(wpcs[i], 0.05, RNG));
//            ps.add(new EpsilonGreedy<Othello>(symWPC, 0.05, RNG));
////            ps.add(new RandomPlayer<Othello>(RNG));
//            players.add(ps);
//        }
//
//        StringBuilder header = new StringBuilder();
//        double[][] data = new double[wpcs.length][episodes / interval + 1];
//
//        for (int rep = 0; rep < repetitions; rep++)
//        {
//            System.out.println("Run: " + rep + "\n");
//            int[] inter = new int[players.size()];
//
//            for (int i = 0; i < wpcs.length; i++)
//                wpcs[i].setWeights(WPCUtil.getRandomWeights(RNG, type));
//
//            for (int episode = 0; episode <= episodes; episode++)
//            {
//                for (WPCTDLearn wpc : wpcs)
//                    wpc.iteration();
//
//                if (episode % interval == 0)
//                {
//                    System.out.print(episode + ", ");
//
//                    if (rep == 0)
//                        header.append(episode + ",");
//
//                    for (int player = 0; player < players.size(); player++)
//                    {
//                        int wins = 0;
//
//                        for (int game = 0; game < nGames; game++)
//                        {
//                            Othello othello = new Othello();
//
//                            while (!othello.isOver())
//                                othello.makeMove(players.get(player).get(othello.getCurPlayer()).move(othello));
//
//                            if (othello.getOutcome()[0] == Outcome.WIN)
//                                wins++;
//                        }
//
//                        data[player][inter[player]++] += wins / (double) nGames;
//                    }
//                }
//            }
//
//            System.out.println();
//        }
//
//        System.out.println(rowsStr + "\\" + colsStr + "," +header.substring(0, header.length() - 1));
//
//        for (int player = 0; player < data.length; player++)
//        {
//            System.out.print(labels[player] + ",");
//
//            for (int episode = 0; episode < data[0].length; episode++)
//                System.out.print((data[player][episode] / repetitions) + (episode == (data[0].length -1) ? "" : ","));
//
//            System.out.println();
//        }
//    }
//
//    static void bestWPCVsStdWPC()
//    {
//        printH1("Learning the weights using TD(0)");
//        WPCTDLearn wpc = new WPCTDLearn.Builder(WPCType.SYM, 150000, 0.001, 1.0).build();
//        wpc.learn();
//        WPCUtil.save("buenero", wpc.getWeights());
//
//        printH2("Learnt weights");
//        System.out.println(wpc.getFormattedWeights());
//        printH2("Playing against random");
//        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
//        players.add(new GreedyPlayer<Othello>(wpc));
//        players.add(new RandomPlayer<Othello>(RNG));
////        GamesUtil.playNGames(new Othello(), players, 50000);
//        System.out.println("");
//        printH2("Latex table");
//        System.out.println(wpc.toLatexTable());
//
//        WPC symWPC = new WPC(WPCUtil.load("sml-weights-sym"));
//
//        List<Player<Othello>> players2 = new ArrayList<Player<Othello>>();
//        players2.add(new EpsilonGreedy<Othello>(symWPC, 0.05, RNG));
//        players2.add(new EpsilonGreedy<Othello>(symWPC, 0.05, RNG));
////        players2.add(new RandomPlayer<Othello>(RNG));
////        GamesUtil.playNGames(new Othello(), players2, 50000);
//
//        Series<Othello> series = new Series.Builder<Othello>(new Othello(), 50000, players).build();
//        series.playSeries();
//        series.printResults();
//    }
//
//    static void testBests()
//    {
////        WPC drSymWPC = new WPC(WPCUtil.load("dr-sym-6462"));
//        WPC drAsymWPC = new WPC(WPCUtil.load("dr-asym-6125"));
//        WPC smlSymWPC = new WPC(WPCUtil.load("sml-weights-sym"));
//
//        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
//        players.add(new EpsilonGreedy<Othello>(drAsymWPC, 0.05, RNG));
//        players.add(new EpsilonGreedy<Othello>(smlSymWPC, 0.05, RNG));
////        players.add(new RandomPlayer<Othello>(RNG));
////        players.add(new RandomPlayer<Othello>(RNG));
////        GamesUtil.playNGames(new Othello(), players, 50000);
//
////        Othello othello = new Othello();
////        int[] results = new int[3];
////
////        int nGames = 50000;
////
////        for (int i = 0; i < nGames; i++)
////        {
////            othello.reset();
////
////            while (!othello.isOver())
////                othello.makeMove(players.get(othello.getCurPlayer()).move(othello.copy()));
////
////            Outcome[] outcomes = othello.getOutcome();
////
////            if (outcomes[0] == Outcome.WIN)
////                results[0]++;
////            else if (outcomes[0] == Outcome.DRAW)
////                results[1]++;
////            else if (outcomes[0] == Outcome.LOSS)
////                results[2]++;
////        }
////
////        double[] resultPer = { results[0] / (double) nGames * 100,
////                results[2] / (double) nGames * 100,
////                results[1] / (double) nGames * 100 };
////
////        System.out.format("%8s: %4.2f%%\n", "Player 1", resultPer[0]);
////        System.out.format("%8s: %4.2f%%\n", "Player 2", resultPer[1]);
////        System.out.format("%8s: %4.2f%%\n", "Draws", resultPer[2]);
//    }
//
//    static void hello()
//    {
//        printH1("Learning the weights using TD(0)");
//        WPCTDLearn wpc = new WPCTDLearn.Builder(WPCType.SYM, 1000, 0.001, 1.0).build();
//        WPC smlSymWPC = new WPC(WPCUtil.load("sml-weights-sym"));
//
//        for (int i = 0; wpc.getCurEpisode() < wpc.getNumEpisodes(); i++)
//        {
//            if (i % 50 == 0)
//            {
//                printH2("Game " + i);
//
//                List<Player<Othello>> players = new ArrayList<Player<Othello>>();
//                players.add(new GreedyPlayer<Othello>(wpc));
////                players.add(new EpsilonGreedy<Othello>(wpc, 0.05));
////                players.add(new EpsilonGreedy<Othello>(smlSymWPC, 0.05));
//                players.add(new RandomPlayer<Othello>(RNG));
//                Series<Othello> series = new Series.Builder<Othello>(new Othello(), 10000, players).build();
//
//
//                series.playSeries();
//                double[] results = series.printResults();
//                System.out.println("result: " + results[0]);
//                series.reset();
//            }
//
//            wpc.iteration();
//        }
//    }
//
//    static void learnWPCWithStages()
//    {
//        // TD(0) parameters
//
//        WPCType type = WPCType.SYM;
//        int nEpisodes = 150000;
//        double learningRate = 0.001;
//        double discountFactor = 1.0;
//
//        // WPC stages and ranges
//
//        int whichToLearn = 1;
//
//        int[] ranges = { 22, 44 };
//        List<WPCTDLearn> wpcs = new ArrayList<WPCTDLearn>();
//
//        for (int stage = 0; stage < ranges.length + 1; stage++)
//        {
//            int lower = (stage == 0 ? 0 : ranges[stage - 1]);
//            int upper = (stage == ranges.length ? Othello.NUM_SQUARES : ranges[stage]);
//            System.out.format("[%d, %d)\n", lower, upper);
//
//            WPCTDLearn wpc = new WPCTDLearn.Builder(type, nEpisodes, learningRate, discountFactor)
//                                 .lowerBound(lower).upperBound(upper).build();
//            wpcs.add(wpc);
//        }
//
//        for (int i = 0; i < wpcs.size(); i++)
//            if (i != whichToLearn)
//                wpcs.set(i, null);
//
//        // Players list to test the current win rate
//        System.out.println("length: " + wpcs.size());
//        WPC stdWPC = new WPC(WPCUtil.load("sml-weights-sym"));
//        WPCStages wpcStages = new WPCStages(wpcs, ranges);
//
//        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
//        players.add(new EpsilonGreedy<Othello>(wpcStages, 0.05, RNG));
//        players.add(new EpsilonGreedy<Othello>(stdWPC, 0.05, RNG));
////        players.add(new RandomPlayer<Othello>());
//
//        // Learn the weights
//
//        int interval = 1000;
//        int nGames = 10000;
//
//        for (int episode = 0; episode <= nEpisodes; episode++)
//        {
//            for (WPCTDLearn wpc : wpcs)
//                if (wpc != null)
//                    wpc.iteration();
//
//            if (episode % interval == 0)
//            {
//                System.out.print(episode + ", ");
//                Series<Othello> series = new Series.Builder<Othello>(new Othello(), nGames, players).build();
//                series.playSeries();
//                double[] outcomes = series.getData();
//                System.out.println("es: " + outcomes[0]);
//
//                if (outcomes[0] > 0.20)
//                {
//                    WPCUtil.save("dr-sym-std-" + whichToLearn + "-" + wpcs.size() + "-" + outcomes[0], wpcs.get(whichToLearn).getWeights()); // TODO: change this
//                    System.out.println("-------------");
//                }
//            }
//        }
//    }
//
//    static void yetAnotherOne()
//    {
//        // TD(0) parameters
//
//        WPCType type = WPCType.SYM;
//        int nEpisodes = 150000;
//        double learningRate = 0.001;
//        double discountFactor = 0.99;
//        double epsilon = 0.01;
//
//        // WPC stages and ranges
//
//        int[] ranges = { 20, 40 };
//        WPC[] wpcs = new WPC[ranges.length + 1];
//
//        for (int stage = 0; stage < wpcs.length; stage++)
//            wpcs[stage] = new WPC(type);
//
//        // Players list to test the current win rate
//        WPC stdWPC = new WPC(WPCUtil.load("sml-weights-sym"));
//        WPCTDLearnMulti wpcStages = new WPCTDLearnMulti.Builder(wpcs, ranges, nEpisodes, learningRate, discountFactor)
//                .setEpsilon(epsilon).build();
//
//        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
//        players.add(new EpsilonGreedy<Othello>(wpcStages, 0.05, RNG));
//        players.add(new EpsilonGreedy<Othello>(stdWPC, 0.05, RNG));
////        players.add(new RandomPlayer<Othello>());
//
//        // Learn the weights
//
//        int interval = 1000;
//        int nGames = 10000;
//
//        for (int episode = 0; episode <= nEpisodes; episode++)
//        {
//            wpcStages.iteration();
//
//            if (episode % interval == 0)
//            {
//                System.out.print(episode + ", ");
//                Series<Othello> series = new Series.Builder<Othello>(new Othello(), nGames, players).build();
//                series.playSeries();
//                double[] outcomes = series.getData();
//                System.out.println("es: " + outcomes[0]);
//
////                if (outcomes[0] > 0.20)
////                {
////                    WPCUtil.save("dr-sym-std-" + whichToLearn + "-" + wpcs.size() + "-" + outcomes[0], wpcs.get(whichToLearn).getWeights()); // TODO: change this
////                    System.out.println("-------------");
////                }
//            }
//        }
//    }
//
//    public static void doIt()
//    {
//        List<WPC> wpcs = new ArrayList<WPC>();
//
////        double[] weights = WPCUtil.load("dr-sym-0-4-0.6667");
////        System.out.println(Arrays.toString(weights));
//
////        wpcs.add(new WPC(WPCUtil.load("dr-sym-0-4-0.6542")));
////        wpcs.add(new WPC(WPCUtil.load("dr-sym-1-4-0.7624")));
////        wpcs.add(new WPC(WPCUtil.load("dr-sym-2-4-0.685")));
////        wpcs.add(new WPC(WPCUtil.load("dr-sym-3-4-0.6406")));
//
//        wpcs.add(new WPC(WPCUtil.load("dr-sym-6462")));
//        wpcs.add(new WPC(WPCUtil.load("dr-sym-6462")));
//        wpcs.add(new WPC(WPCUtil.load("dr-sym-6462")));
//
////        wpcs.add(new WPC(WPCUtil.load("osaki-1")));
////        wpcs.add(new WPC(WPCUtil.load("osaki-2")));
////        wpcs.add(new WPC(WPCUtil.load("osaki-3")));
//
//        WPCStages wpcStages = new WPCStages(wpcs, new int[] { 22, 44 });
//        WPC smlSymWPC = new WPC(WPCUtil.load("sml-weights-sym"));
//
//        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
//        players.add(new EpsilonGreedy<Othello>(wpcStages, 0.05, RNG));
//        players.add(new EpsilonGreedy<Othello>(smlSymWPC, 0.05, RNG));
////        players.add(new RandomPlayer<Othello>());
//
//        Series<Othello> series = new Series.Builder<Othello>(new Othello(), 50000, players).build();
//        series.playSeries();
//        series.printResults();
//    }
//
//    public static void main(String[] args)
//    {
////        learnWPCWithStages();
//        yetAnotherOne();
////        WPC wpc = new WPC(WPCUtil.load("osaki-3"));
////        System.out.println(wpc.getFormattedWeights());
//    }
//}
