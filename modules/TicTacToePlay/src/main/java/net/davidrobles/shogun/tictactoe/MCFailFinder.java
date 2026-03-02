//package dr.shogun.tictactoe;
//
//import Game;
//import Outcome;
//import dr.mauler.core.players.RandPlayer;
//import dr.mauler.othello.Othello;
//import UtilFunc;
//import MonteCarlo;
//import MCTS;
//import UCT;
//import UCB1;
//import AlphaBeta;
//import TicTacToe;
//
//import java.util.*;
//
//public class MCFailFinder {
//
//    private static boolean run() {
//
//        TicTacToe game = new TicTacToe();
////        Game game = new Othello();
//
//        int initMoves = 6;
////        int initMoves = 56;
//        Random rnd = new Random();
//
//        for (int i = 0; i < initMoves && !game.isOver(); i++) {
//            game.makeMove(rnd.nextInt(game.getNumMoves()));
//        }
//
////        System.out.println(game);
//
//        if (game.getNumMoves() != 3)
//            return false;
//
////        Set<String> set = new HashSet<String>();
//        List<String> set = new ArrayList<String>();
//        set.add("WL");
//        set.add("DD");
//        set.add("LW");
//
//        // left
//        Game gameLeft = game.copy();
//        gameLeft.makeMove(0);
//        Outcome[] left = hello(gameLeft);
//        String result = getValue(left);
//        set.remove(result);
//
//        // center
//        Game gameCenter = game.copy();
//        gameCenter.makeMove(1);
//        Outcome[] center = hello(gameCenter);
//        result = getValue(center);
//        set.remove(result);
//
//        // right
//        Game gameRight = game.copy();
//        gameRight.makeMove(2);
//        Outcome[] right = hello(game);
//        result = getValue(right);
//        set.remove(result);
//
////        if (set.size() == 1) {
////            for (String s : set) {
////                System.out.println(s);
////            }
////            System.out.println("-----------");
////        }
//
//        return false;
//    }
//
//    private static String getValue(Outcome[] outcomes) {
//        if (outcomes == null) {
//            return "";
//        }
//        if (outcomes[0] == Outcome.WIN && outcomes[1] == Outcome.LOSS) {
//            return "WL";
//        }
//        if (outcomes[0] == Outcome.DRAW && outcomes[1] == Outcome.DRAW) {
//            return "DD";
//        }
//        if (outcomes[0] == Outcome.LOSS && outcomes[1] == Outcome.WIN) {
//            return "LW";
//        }
//        return "";
//    }
//
//    static void printGraphviz()
//    {
////        Othello othello = new Othello();
////        advance(othello, 30);
////        System.out.println(othello);
////        double c = 0.2;
////        int nSims = 40000;
////        MCTS<Othello> mcts = new MCTS<Othello>(new UCB1<Othello>(c, new Random()), new RandPlayer<Othello>(), nSims);
////        mcts.setUtilFunc(new UtilFunc<Othello>(1.0, 0.0, 0.5));
//////        mcts.setUtilFunc(new UtilFunc<Othello>(1.0, -1.0, 0.0));
////        mcts.move(othello);
//
//
//
//
//    }
//
//    static void moveToBoard() {
//        Othello othello = new Othello();
//        long blackBB = -9223195493621641220L;
//        long whiteBB = 5728402182782136323L;
//        othello.setBoard(blackBB, whiteBB);
//    }
//
//    public static void main(String[] args) {
//
//        String str = "testing";
//        String str2 = "hola";
//        System.out.println(str.hashCode());
//        System.out.println(str2.hashCode());
//
////        Othello othello = new Othello();
////        long blackBB = -9223195493621641220L;
////        long whiteBB = 5728402182782136323L;
////        othello.setBoard(blackBB, whiteBB);
////        othello.makeMove("H7");
////        othello.makeMove("PASS");
////        othello.makeMove("F8");
////        othello.makeMove("PASS");
////        othello.makeMove("E8");
//
////        printGraphviz();
//
////        double[] outcomes = new double[othello.getNumMoves()];
////        Random rand = new Random();
////        UtilFunc<Othello> utilFunc = new UtilFunc<Othello>();
//
//
////        for (int i = 0; i < 10000; i++)
////        {
////            Othello newGame = othello.copy();
////            int move = i % othello.getNumMoves();
////            newGame.makeMove(move);
////
////            while (!newGame.isOver())
////                newGame.makeMove(rand.nextInt(newGame.getNumMoves()));
////
////            outcomes[move] += utilFunc.eval(newGame, 0);
////        }
////
////        for (int i = 0; i < outcomes.length; i++) {
////            System.out.println(othello.getMoves()[i] + ": " + outcomes[i]);
////        }
//
////        return DRUtil.argMax(outcomes);
//
////        System.out.println(othello);
//
////        int initMoves = 58;
////        int nGames = 1;
////        for (int i = 0; i < nGames; i++) {
//////            Othello game = new Othello();
////            Othello game = new Othello();
////            long blackBB = -9223195493621641220L;
////            long whiteBB = 5728402182782136323L;
////            game.setBoard(blackBB, whiteBB);
//////            if (!advance(game, initMoves)) {
//////                continue;
//////            }
////            Othello copy = game.copy();
////            boolean result = isWinForAlphaBeta(copy);
////            if (result) {
////                boolean mcResult = isWinForMC(copy);
//////                System.out.println("here");
////                if (!mcResult) {
////                    System.out.println(copy);
////                    System.out.println(copy.getCurPlayer());
////                    System.out.println(copy.getBlackBB());
////                    System.out.println(copy.getWhiteBB());
////                    System.out.println("-----");
////                }
////            }
////        }
//    }
//
//    static <GAME extends Game<GAME>> boolean isWinForAlphaBeta(GAME game) {
//        AlphaBeta<GAME> ab = new AlphaBeta<GAME>(new UtilFunc<GAME>());
//        game = game.copy();
//        while (!game.isOver()) {
//            game.makeMove(ab.move(game));
//        }
//        return game.getOutcome()[0] == Outcome.WIN;
//    }
//
//    static <GAME extends Game<GAME>> boolean isWinForMC(GAME game) {
//        MonteCarlo<GAME> mc = new MonteCarlo<GAME>(10000);
//        AlphaBeta<GAME> ab = new AlphaBeta<GAME>(new UtilFunc<GAME>());
//        game = game.copy();
//        while (!game.isOver()) {
//            if (game.getCurPlayer() == 0)
//                game.makeMove(mc.move(game));
//            else
//                game.makeMove(ab.move(game));
//        }
//        return game.getOutcome()[0] == Outcome.WIN;
//    }
//
//    private static Outcome[] hello(Game game) {
//        Outcome[] outcomes = new Outcome[2];
//
//        // Left
//        Game leftBoard = game.copy();
//        if (leftBoard.getNumMoves() != 2)
//            return null;
//        leftBoard.makeMove(0);
//        if (leftBoard.getNumMoves() != 1)
//            return null;
//        leftBoard.makeMove(0);
//        outcomes[0] = leftBoard.getOutcome()[0];
//
//        // Right
//
//        Game rightBoard = game.copy();
//        if (rightBoard.getNumMoves() != 2)
//            return null;
//        rightBoard.makeMove(1);
//        if (rightBoard.getNumMoves() != 1)
//            return null;
//        rightBoard.makeMove(0);
//        outcomes[1] = rightBoard.getOutcome()[0];
//
//        return outcomes;
//    }
//}
