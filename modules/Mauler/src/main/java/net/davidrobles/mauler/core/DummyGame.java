package net.davidrobles.mauler.core;

import java.util.List;
import java.util.Optional;

/**
 * This is a fake game used for testing.
 */
public class DummyGame implements Game<DummyGame>
{
    @Override
    public DummyGame copy()
    {
        return null;
    }

    @Override
    public int getCurPlayer()
    {
        return 0;
    }

    @Override
    public List<String> getMoves()
    {
        return List.of();
    }

    @Override
    public String getName()
    {
        return null;
    }

    @Override
    public int getNumPlayers()
    {
        return 0;
    }

    @Override
    public Optional<GameResult[]> getOutcome()
    {
        return Optional.empty();
    }

    @Override
    public void makeMove(int move)
    {

    }

    @Override
    public void reset()
    {

    }
}
