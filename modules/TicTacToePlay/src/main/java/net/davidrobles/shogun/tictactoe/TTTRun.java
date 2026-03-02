//package dr.shogun.tictactoe;
//
//import Player;
//import RandPlayer;
//import GamesUtil;
//import UtilFunc;
//import dr.mauler.players.mc.MCTime;
//import dr.mauler.players.mcts.MCTSSims;
//import dr.mauler.players.mcts.MCTSTime;
//import TreePolicy;
//import dr.mauler.players.mcts.tree.GreedyPolicy;
//import dr.mauler.players.mcts.tree.UCTPolicy;
//import AlphaBeta;
//import dr.mauler.players.minimax.IterativeDeepening;
//import Minimax;
//import Negamax;
//import TicTacToe;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//public class TTTRun
//{
//    // Parameters
//    static int N_SIMS = 1000;
//    static double C = 0.3;
//
//    static Random RNG = new Random();
//    static TicTacToe tic = new TicTacToe();
//    static UtilFunc<TicTacToe> utilFunc = new UtilFunc<TicTacToe>();
//
//    // MCTS
//    static TreePolicy<TicTacToe> greedyTP = new GreedyPolicy<TicTacToe>(RNG);
//    static TreePolicy<TicTacToe> uct = new UCTPolicy<TicTacToe>(C, RNG);
//
//    // MixedPlayer
//    static RandPlayer<TicTacToe> randomPlayer = new RandPlayer<TicTacToe>(RNG);
//
//    // Players
//    static Minimax<TicTacToe> minimax = new Minimax<TicTacToe>(utilFunc, 25);
//    static Negamax<TicTacToe> negamax = new Negamax<TicTacToe>(utilFunc, 25);
//    static AlphaBeta<TicTacToe> alphaBeta = new AlphaBeta<TicTacToe>(utilFunc, 25);
////    static MCSims<TicTacToe> fixedMC = new MCSims<TicTacToe>(utilFunc, N_SIMS, RNG);
//    static MCTSSims<TicTacToe> fixedStandardMCTS = new MCTSSims<TicTacToe>(uct, randomPlayer, utilFunc, N_SIMS);
//    static MCTSSims<TicTacToe> fixedGreedyMCTS = new MCTSSims<TicTacToe>(greedyTP, randomPlayer, utilFunc, N_SIMS);
//
//    // TimedPlayer
//    static IterativeDeepening<TicTacToe> minimaxID = new IterativeDeepening<TicTacToe>(minimax, 20);
//    static IterativeDeepening<TicTacToe> negamaxID = new IterativeDeepening<TicTacToe>(negamax, 20);
//    static IterativeDeepening<TicTacToe> alphaBetaID = new IterativeDeepening<TicTacToe>(alphaBeta, 20);
//    static MCTime<TicTacToe> timedMC = new MCTime<TicTacToe>(utilFunc, RNG);
//    static MCTSTime<TicTacToe> timedStandardMCTS = new MCTSTime<TicTacToe>(uct, randomPlayer, utilFunc);
//    static MCTSTime<TicTacToe> timedGreedyMCTS = new MCTSTime<TicTacToe>(uct, randomPlayer, utilFunc);
//
//    private static void runFixed()
//    {
//        List<Player<TicTacToe>> players = new ArrayList<Player<TicTacToe>>();
//
//        players.add(randomPlayer);
//        players.add(alphaBeta);
//
////        players.add(minimax);
////        players.add(fixedMC);
////        players.add(negamax);
////        players.add(alphaBeta);
////        players.add(fixedMC);
////        players.add(fixedStandardMCTS);
////        players.add(fixedGreedyMCTS);
//
////        GamesUtil.playNGames(tic, players, 100); // TODO change this
////        ParallelRoundRobin<TicTacToe> roundRobin = new ParallelRoundRobin<TicTacToe>(tic, players, 50);
////        roundRobin.run();
////        System.out.println(roundRobin.toFormattedTable());
//    }
//
//    private static void runTimed()
//    {
//        List<Player<TicTacToe>> players = new ArrayList<Player<TicTacToe>>();
//
//        players.add(randomPlayer);
////        players.add(minimaxID);
//        players.add(negamaxID);
////        players.add(alphaBetaID);
////        players.add(timedMC);
////        players.add(timedStandardMCTS);
////        players.add(timedGreedyMCTS);
//
////        GamesUtil.playNGamesTimed(tic, players, 100, 100, true);
////        RoundRobinTime<TicTacToe> roundRobin = new RoundRobinTime<TicTacToe>(tic, players, 20, 200);
////        roundRobin.run();
////        System.out.println(roundRobin.toFormattedTable());
//    }
//
//    public static void main(String[] args)
//    {
////        runFixed();
////        runTimed();
//        GamesUtil.playRandomGame(new TicTacToe());
//    }
//}
