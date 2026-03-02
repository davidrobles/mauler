package net.davidrobles.mauler.players.mcts.parallel;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.players.Player;
import net.davidrobles.mauler.players.mcts.MCTS;
import net.davidrobles.mauler.players.mcts.MCTSNode;
import net.davidrobles.mauler.players.mcts.UCT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * MCTS with root parallelisation.
 */
public class MCTSRootP<GAME extends Game<GAME>> implements Player<GAME>
{
    private MCTS<GAME> mcts;

    public MCTSRootP(MCTS<GAME> mcts)
    {
        this.mcts = mcts;
    }

    @Override
    public boolean equals(Object obj)
    {
        return super.equals(obj);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * Creates an MCTS node and attaches all the information
     * from the list of nodes.
     */
    public MCTSNode<GAME> joinMCTSNodes(List<MCTSNode<GAME>> rootNodes)
    {
        MCTSNode<GAME> all = new MCTSNode<GAME>(rootNodes.get(0).getGame().copy());
        all.init();

        for (MCTSNode<GAME> rootNode : rootNodes)
        {
            for (int move = 0; move < rootNode.getChildren().size(); move++)
            {
                int oldVisits = all.getChild(move).getCount();
                int newVisits = oldVisits + rootNode.getChild(move).getCount();
                all.getChild(move).setCount(newVisits);
            }
        }

        return all;
    }

    // TODO: what about replacing this with a tree policy???
    /**
     * Returns the best move using the visits count of the actions.
     */
    public int selectByVisitsCount(MCTSNode<GAME> mctsNode)
    {
        int bestMove = -1;
        int mostVisits = Integer.MIN_VALUE;
        int childrenSize = mctsNode.getChildren().size();

        for (int move = 0; move < childrenSize; move++)
        {
            int childVisits = mctsNode.getChild(move).getCount();

            if (childVisits > mostVisits)
            {
                mostVisits = childVisits;
                bestMove = move;
            }
        }

        return bestMove;
    }

    class MCTSTask implements Callable<MCTSNode<GAME>>
    {
        private GAME game;
        private int timeout; // TODO: be careful about 2 timeouts

        public MCTSTask(GAME game, int timeout)
        {
            this.game = game;
            this.timeout = timeout;
        }

        @Override
        public MCTSNode<GAME> call() throws Exception
        {
            long timeDue = System.currentTimeMillis() + timeout;
            int curPlayer = game.getCurPlayer();
//            MCTS<GAME> simMcts = mcts.copy();
            MCTS<GAME> simMcts = new UCT<GAME>(0.5);
            MCTSNode<GAME> mctsNode = new MCTSNode<GAME>(game.copy());
            int count = 0;

            while (System.currentTimeMillis() < timeDue) {
                simMcts.simulate(mctsNode, curPlayer);
//                if (count++ % 50 == 0)
//                    System.out.println("count: " + count);
            }

            return mctsNode;
        }
    }

    ////////////
    // Player //
    ////////////

    @Override
    public boolean isDeterministic()
    {
        return false;
    }

    @Override
    public int move(GAME game)
    {
        return 0;
    }

    @Override
    public int move(GAME game, int timeout)
    {
        int nProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(nProcessors);
        Collection<MCTSTask> mctsTasks = new ArrayList<MCTSTask>();

        for (int i = 0; i < nProcessors; i++)
            mctsTasks.add(new MCTSTask(game.copy(), timeout));

        List<MCTSNode<GAME>> mctsNodes = new ArrayList<MCTSNode<GAME>>();

        try
        {
            List<Future<MCTSNode<GAME>>> futures = executorService.invokeAll(mctsTasks);
            executorService.shutdown();

            for (Future<MCTSNode<GAME>> future : futures)
                mctsNodes.add(future.get());
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return selectByVisitsCount(joinMCTSNodes(mctsNodes));
    }
}
