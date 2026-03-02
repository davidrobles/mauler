package net.davidrobles.shogun.tictactoe;

import java.util.concurrent.*;

public class ExecutorTest
{
    public static void main(String[] args)
    {
        int bestMove = -1;
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<Integer> future = es.submit(new Callable<Integer>()
        {
            @Override
            public Integer call() throws Exception
            {
                Thread.sleep(501);
                return 3;
            }
        });
        es.shutdown();
        try
        {
            bestMove = future.get(500, TimeUnit.MILLISECONDS);
        }
        catch (TimeoutException e)
        {
            future.cancel(true);
            bestMove = 5;
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }
        System.out.println("Best move: " + bestMove);
    }
}
