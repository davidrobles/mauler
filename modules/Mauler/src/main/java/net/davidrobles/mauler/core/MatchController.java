package net.davidrobles.mauler.core;

import net.davidrobles.mauler.players.Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MatchController<GAME extends Game<GAME> & MoveObservable>
{
    private GAME game;
    private List<Player<GAME>> players;
    private long timeout;
    private List<MatchControllerObserver<GAME>> observers = new ArrayList<MatchControllerObserver<GAME>>();
    private LinkedList<GAME> gameHistory = new LinkedList<GAME>();
    private LinkedList<String> moveHistory = new LinkedList<String>();
    private int currentBoardIndex = 0;

    public MatchController(GAME game, List<Player<GAME>> players, long timeout)
    {
        if (players.size() != game.getNumPlayers())
            throw new IllegalArgumentException("Different size of players.");

        this.game = game;
        this.players = players;
        this.timeout = timeout;
        reset();
    }

    public synchronized void reset()
    {
        currentBoardIndex = 0;
        moveHistory.clear();
        gameHistory.clear();
        gameHistory.add(game.newInstance());
        notifyObservers();
    }

    public synchronized void playToEnd()
    {
        while (isNext())
        {
            next();
            notifyObservers();
        }
    }

    public synchronized void setChange(int index)
    {
        this.currentBoardIndex = index;
        notifyObservers();
    }

    public synchronized int getSize()
    {
        return gameHistory.size();
    }

    public int getCurrentIndex()
    {
        return currentBoardIndex;
    }

    public synchronized GAME getGame()
    {
        return gameHistory.get(currentBoardIndex);
    }

    public synchronized GAME getGame(int ply)
    {
        return gameHistory.get(ply);
    }

    public synchronized String getMove(int gameIndex)
    {
        return moveHistory.get(gameIndex);
    }

    // Validation

    public synchronized boolean isStart()
    {
        return currentBoardIndex > 0;
    }

    public synchronized boolean isEnd()
    {
        return currentBoardIndex < gameHistory.size() - 1;
    }

    public synchronized boolean isOver()
    {
        return gameHistory.getLast().isOver();
    }

    public synchronized boolean isNext()
    {
        return currentBoardIndex != gameHistory.size() - 1 || !gameHistory.getLast().isOver();
    }

    public synchronized boolean isPrev()
    {
        return currentBoardIndex > 0;
    }

    // Move boards

    public synchronized void start()
    {
        if (isStart())
        {
            currentBoardIndex = 0;
            notifyObservers();
        }
    }

    public synchronized void prev()
    {
        if (isPrev())
        {
            currentBoardIndex--;
            notifyObservers();
        }
    }

    public synchronized void next()
    {
        // game over
        if (!isNext())
            return;

        GAME gameCopy = gameHistory.getLast().copy();

        // current boards == last boards
        if (currentBoardIndex == gameHistory.size() - 1)
        {
            // TODO: Fix timeDue
            int moveIndex = players.get(gameCopy.getCurPlayer()).move(gameCopy);
            String moveString = gameCopy.getMoves()[moveIndex];
            gameCopy.makeMove(moveIndex);

            if (!gameHistory.getLast().equals(gameCopy))
            {
                gameHistory.add(gameCopy);
                moveHistory.add(moveString);
                currentBoardIndex++;
                notifyObservers();
            }
        }
        else // moving in the positions history
        {
            currentBoardIndex++;
            notifyObservers();
        }
    }

    public synchronized void end()
    {
        if (isEnd())
        {
            currentBoardIndex = gameHistory.size() - 1;
            notifyObservers();
        }
    }

    ///////////////
    // Observers //
    ///////////////

    public void registerObserver(MatchControllerObserver<GAME> observer)
    {
        this.observers.add(observer);
    }

    public void removeObserver(MatchControllerObserver<GAME> observer)
    {
        this.observers.remove(observer);
    }

    public void notifyObservers()
    {
        for (MatchControllerObserver<GAME> observer : observers)
            observer.update(gameHistory.get(currentBoardIndex));
    }
}
