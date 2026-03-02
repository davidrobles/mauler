package net.davidrobles.mauler.planetwars;

import javax.swing.*;
import java.awt.*;

public class PWViewGraph extends JPanel implements PWGameObserver {

    private PWGame game;
    private int maxShip = 5000;
    private int height = 400;
    private int width = 400;
//    private int maxTurns = 1000;

    private Color[] colors = new Color[] {Color.BLUE, Color.RED};

    public PWViewGraph(PWGame game) {
        this.game = game;
        game.registerView(this);
        setPreferredSize(new Dimension(width, height));
        setLocation(500, 500);
        setBackground(Color.WHITE);
    }

    int x = 0;
    int y = 0;

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        for (PWGameState gameState : game.getHistory())
        {
            for (int i = 0; i < gameState.getnPlayers(); i++) {
                int numShips = gameState.NumShips(i + 1);
                g.setColor(colors[i]);

                y = numShips;
                g.drawLine(x * 2, height - ((y * 400) / maxShip), (x * 2) + 2, height - ((numShips * 400) / maxShip));
            }
            x += 2;
        }
        
        x = 0;
        y = 0;
    }

    @Override
    public void update() {
        repaint();
    }

}
