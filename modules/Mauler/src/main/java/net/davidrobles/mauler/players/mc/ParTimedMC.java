package net.davidrobles.mauler.players.mc;//package dr.dr.mauler.players.mc;
//
//import dr.Game;
//import dr.Player;
//import dr.dr.mauler.players.UtilityFunction;
//import DRUtil;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.Random;
//import java.util.concurrent.*;
//
///**
// * A parallel implementation of a simple Monte-Carlo algorithm.
// */
//public class ParTimedMC<GAME extends Game<GAME>> extends AbstractMC<GAME> implements Player<GAME>
//{
//    public ParTimedMC(UtilityFunction<GAME> utilFunc)
//    {
//        this(utilFunc, new Random());
//    }
//
//    public ParTimedMC(UtilityFunction<GAME> utilFunc, Random rand)
//    {
//        super(utilFunc, rand);
//    }
//
//    ////////////
//    // Player //
//    ////////////
//
//    @Override
//    public boolean isDeterministic()
//    {
//        return false;
//    }
//
//    @Override
//    public int move(GAME game)
//    {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public int move(GAME game, long time)
//    {
//        int numMoves = game.getNumMoves();
//
//        if (numMoves == 1)
//            return 0;
//
//        ExecutorService executor = Executors.newFixedThreadPool(2);
//        Collection<Round> tasks = new ArrayList<Round>();
//        tasks.add(new Round(game.copy(), time));
//        tasks.add(new Round(game.copy(), time));
//        List<Future<double[]>> futures = null;
//
//        try {
//            futures = executor.invokeAll(tasks);
//            executor.shutdown();
//        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        double[] outcomes = new double[numMoves];
//        assert futures != null;
//
//        for (Future<double[]> future : futures) {
//            try {
//                double[] doubles = future.get();
//
//                for (int i = 0; i < doubles.length; i++)
//                    outcomes[i] += doubles[i];
//            }
//            catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            catch (ExecutionException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return DRUtil.argMax(outcomes);
//    }
//
//
//
//    class Round implements Callable<double[]>
//    {
//        private GAME game;
//        private long time;
//
//        Round(GAME game, long time)
//        {
//            this.game = game;
//            this.time = time;
//        }
//
//        @Override
//        public double[] call()
//        {
//            long currentTime = System.currentTimeMillis();
//            int numMoves = game.getNumMoves();
//            double[] outcomes = new double[numMoves];
//            int simsCount = 0;
//            long timeDue = currentTime + time;
//
//            while (System.currentTimeMillis() < timeDue)
//            {
//                for (int move = 0; move < numMoves; move++)
//                {
//                    GAME newGame = game.copy();
//                    newGame.makeMove(move);
//
//                    while (!newGame.isOver())
//                        newGame.makeMove(rand.nextInt(newGame.getNumMoves()));
//
//                    simsCount++;
//                    outcomes[move] += utilFunc.eval(newGame, game.getCurPlayer());
//                }
//            }
//
//            return outcomes;
//        }
//    }
//}
