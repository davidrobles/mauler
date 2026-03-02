package net.davidrobles.mauler.planetwars;

import javax.swing.*;
import java.awt.*;

public class PWView extends JPanel implements PWGameObserver {

    private PWGame game;
    private static final int SCALE = 23;
    private static final int LEFT_OFFSET = 20;
    private static final int TOP_OFFSET = 20;

    // Colours
    private static Color P1_COLOR = new Color(0x99ccff); // BLUE
    private static Color P2_COLOR = new Color(0xcc3333); // RED
    private static Color NEUTRAL_COLOR = Color.GRAY;
    private static Color NUM_SHIPS_COLOR = Color.WHITE;

    public PWView(PWGame game) {
        this.game = game;
        game.registerView(this);
        setPreferredSize(new Dimension(700, 700));
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        // Retrieve the graphics context; this object is used to paint shapes
        Graphics2D g2d = (Graphics2D)g;

        // Enable antialiasing for shapes
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw graph edges
//        g.setColor(Color.YELLOW);
//        for (PWPlanet planet : game.getGameState().getAllPlanets()) {
//            int xPosPlanet = (int) planet.getX() * SCALE;
//            int yPosPlanet = (int) planet.getY() * SCALE;
//            int planetSize = (planet.getGrowthRate() * 50 / 5) + 20;
//            for (PWPlanet neighbor : planet.getNeighbors()) {
//                int xPosNeighbor = (int) neighbor.getX() * SCALE;
//                int yPosNeighbor = (int) neighbor.getY() * SCALE;
//                int neighborSize = (neighbor.getGrowthRate() * 50 / 5) + 20;
//                g.drawLine(xPosPlanet + LEFT_OFFSET + (planetSize / 2),
//                        yPosPlanet + TOP_OFFSET + (planetSize / 2),
//                        xPosNeighbor + LEFT_OFFSET + (neighborSize / 2),
//                        yPosNeighbor + TOP_OFFSET + (neighborSize / 2));
//            }
//        }

        for (PWPlanet planet : game.getGameState().getAllPlanets())
        {
            // Draw planets
            if (planet.getOwner() == 1)
                g.setColor(P1_COLOR);
            else if (planet.getOwner() == 2)
                g.setColor(P2_COLOR);
            else
                g.setColor(NEUTRAL_COLOR);

            int planetSize = (planet.getGrowthRate() * 50 / 5) + 20;
            int xPos = (int) planet.getX() * SCALE;
            int yPos = (int) planet.getY() * SCALE; 
            g.fillOval(xPos + LEFT_OFFSET, yPos + TOP_OFFSET, planetSize, planetSize);

            // Draw Num Ships
            g.setColor(NUM_SHIPS_COLOR);
            g.drawString(String.valueOf(planet.getNumShips()) + "(" + planet.getGrowthRate() + ")",
                    xPos + LEFT_OFFSET +  (planetSize / 2), yPos + TOP_OFFSET + (planetSize / 2));
        }



        for (PWFleet fleet : game.getGameState().getAllFleets())
        {
            if (fleet.getOwner() == 1)
                g.setColor(P1_COLOR);
            else if (fleet.getOwner() == 2)
                g.setColor(P2_COLOR);

            PWPlanet sourcePlanet = game.getGameState().getPlanet(fleet.getSourcePlanetID());
            PWPlanet destinationPlanet = game.getGameState().getPlanet(fleet.getDestinationPlanetID());
            double xStep = (destinationPlanet.getX() - sourcePlanet.getX()) / fleet.getTotalTripLength();
            double yStep = (destinationPlanet.getY() - sourcePlanet.getY()) / fleet.getTotalTripLength();
            int xPos = (int) ((destinationPlanet.getX() - (xStep * fleet.getTurnsRemaining())) * SCALE);
            int yPos = (int) ((destinationPlanet.getY() - (yStep * fleet.getTurnsRemaining())) * SCALE);
            g.drawString(String.valueOf(fleet.getNumShips()), xPos + LEFT_OFFSET, yPos + TOP_OFFSET);
        }
    }

    @Override
    public void update() {
        repaint();
    }
    
}
