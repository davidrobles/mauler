package net.davidrobles.mauler.othello;//package dr.mauler.othello;
//
//public class OthelloRun
//{
//    // MixedPlayer
//    static RandPlayer<Othello> randomPlayer = new RandPlayer<Othello>(rand);
//
//    // Players
//    static Minimax<Othello> minimax = new Minimax<Othello>(wpc, 4);
//    static Negamax<Othello> negamax = new Negamax<Othello>(wpc, 4);
//    static AlphaBeta<Othello> alphaBeta = new AlphaBeta<Othello>(wpc, 4);
////    static MCSims<Othello> fixedMC = new MCSims<Othello>(uf, nSims, rand);
//    static MCTSSims<Othello> fixedStandardMCTS = new MCTSSims<Othello>(uct, randomPlayer, uf, nSims);
//    static MCTSSims<Othello> fixedGreedyMCTS = new MCTSSims<Othello>(greedyTP, randomPlayer, uf, nSims);
//
//    // TimedPlayer
//    static IterativeDeepening<Othello> minimaxID = new IterativeDeepening<Othello>(minimax, 2);
//    static IterativeDeepening<Othello> negamaxID = new IterativeDeepening<Othello>(negamax, 4);
//    static IterativeDeepening<Othello> alphaBetaID = new IterativeDeepening<Othello>(alphaBeta, 4);
//    static MCTime<Othello> timedMC = new MCTime<Othello>(uf, rand);
//    static MCTSTime<Othello> timedStandardMCTS = new MCTSTime<Othello>(uct, randomPlayer, uf);
//    static MCTSTime<Othello> timedGreedyMCTS = new MCTSTime<Othello>(uct, randomPlayer, uf);
//    static MCTSTime<Othello> noRollout = new MCTSNoRollout<Othello>(uct, uf, wpc);
//
//    static RandomEF<Othello> randomEF = new RandomEF<Othello>();
//
//    private static void runFixed()
//    {
//        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
////        players.add(randomPlayer);
//
//        players.add(new AlphaBeta<Othello>(randomEF, uf, 1));
//        players.add(new AlphaBeta<Othello>(numStonesEF, uf, 1));
//
////        players.add(new AlphaBeta<Othello>(wpc, utilFunc, 1));
////        players.add(new AlphaBeta<Othello>(numStonesEF, utilFunc, 1));
//
////        GamesUtil.playNGames(othello, players, 100);
////        ParallelRoundRobin<Othello> roundRobin = new ParallelRoundRobin<Othello>(othello, players, 50);
////        roundRobin.run();
////        System.out.println(roundRobin.toFormattedTable());
//    }
//
//    private static void runTimed()
//    {
//        List<Player<Othello>> players = new ArrayList<Player<Othello>>();
////        players.add(randomPlayer);
//        players.add(randomPlayer);
////        players.add(minimaxID);
////        players.add(negamaxID);
////        players.add(alphaBetaID);
////        players.add(timedMC);
////        players.add(timedStandardMCTS);
////        players.add(timedGreedyMCTS);
//        players.add(noRollout);
//
////        GamesUtil.playNGamesTimed(othello, players, 10, 100, true);
////        TimedParallelRoundRobin<Othello> roundRobin = new TimedParallelRoundRobin<Othello>(othello, players, 5, 100);
////        roundRobin.run();
////        System.out.println(roundRobin.toFormattedTable());
////        GamesUtil.playGameTimed(othello, players, 500);
////        GamesUtil.playNGamesTimed(othello, players, 50, 50, true);
//    }
//
//    public static void main(String[] args)
//    {
////        runFixed();
//        runTimed();
//    }
//}
