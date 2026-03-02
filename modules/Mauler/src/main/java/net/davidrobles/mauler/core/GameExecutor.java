package net.davidrobles.mauler.core;//
//package shogun.core;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.*;
//
//public class GameExecutor<GAME extends Game<GAME>>
//{
//    private GAME game;
//    private List<PlayerTask<GAME>> playerTasks;
//    private long timeout;
//    private TimeUnit unit;
//    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
////    private ScheduledExecutorService executor = Executors.new;
//
//    public GameExecutor(GAME game, List<Player<GAME>> players, long timeout, TimeUnit unit)
//    {
//        this.game = game;
//        this.timeout = timeout;
//        this.unit = unit;
//        this.playerTasks = new ArrayList<PlayerTask<GAME>>(players.size());
//
//        for (Player<GAME> player : players)
//            playerTasks.add(new PlayerTask<GAME>(player));
//    }
//
//    public void play()
//    {
//        System.out.println(game);
//
//        while (!game.isOver())
//        {
//            PlayerTask<GAME> playerTask = playerTasks.get(game.getCurPlayer());
//            playerTask.setTimeout(timeout, unit);
//            playerTask.setGame(game);
//
//            final Future<Integer> handler = executor.submit(playerTask);
//            int action = -1;
//
//
//
//            try
//            {
//                action = handler.get(timeout, unit);
//            }
//            catch (TimeoutException e)
//            {
//                System.out.println("Fucked");
//                e.printStackTrace();
//                System.exit(1);
//            }
//            catch (InterruptedException e)
//            {
//                e.printStackTrace();
//            }
//            catch (ExecutionException e)
//            {
//                e.printStackTrace();
//            }
//
//            game.makeMove(action);
//            System.out.println(game.gameToString());
//        }
//
//        executor.shutdown();
//    }
//}
