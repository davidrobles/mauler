package net.davidrobles.mauler.breakthrough;

import net.davidrobles.mauler.core.GameTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BreakthroughTest extends GameTest<Breakthrough>
{
    @Before
    public void init() {
        this.game = new Breakthrough();
    }

    @Test
    public void testNumberOfPlayers() throws Exception {
        assertEquals(2, game.getNumPlayers());
    }

    @Test
    public void testInitialBoard() {

    }
}
