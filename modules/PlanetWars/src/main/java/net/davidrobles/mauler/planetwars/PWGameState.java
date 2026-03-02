package net.davidrobles.mauler.planetwars;

import java.util.*;

public class PWGameState {

    private int turns = 0;
    private int maxGameLength = 200;
    private List<PWPlanet> planets = planets = new ArrayList<PWPlanet>();
    private List<PWFleet> fleets = new ArrayList<PWFleet>();
    private int nPlayers = 2;

    public int getnPlayers() {
        return nPlayers;
    }

    public PWGameState(String gameState, List<PWBot> bots) {
        planets = new ArrayList<PWPlanet>();
        fleets = new ArrayList<PWFleet>();
        parseGameState(gameState);
        calcNeighbors();
    }

    private PWGameState() {
    }

    public PWGameState(PWGameState gameState) {
        // Copy planets
        for (PWPlanet planet : gameState.getAllPlanets())
            planets.add(new PWPlanet(planet));
        // Copy fleets
        for (PWFleet fleet : gameState.getAllFleets())
            fleets.add(new PWFleet(fleet));
        // Copy rest stuff
        turns = gameState.getTurns();
        nPlayers = gameState.nPlayers;
        // again, calculate the neighbors
        calcNeighbors();
    }

    private void calcNeighbors() {
        for (PWPlanet planet : planets) {
            for (PWPlanet p : planets) {
                if (planet != p && distance(planet.getPlanetID(), p.getPlanetID()) < 12) {
                    planet.addNeighbor(p);
                }
            }
        }
    }

    public int getTurns() {
        return turns;
    }

    public List<PWFleet> getAllFleets() {
        return fleets;
    }

    public List<PWFleet> myFleets(int playerIndex) {
        List<PWFleet> r = new ArrayList<PWFleet>();
        for (PWFleet f : fleets) {
            if (f.getOwner() == playerIndex) {
                r.add(f);
            }
        }
        return r;
    }

    public List<PWPlanet> getAllPlanets() {
        LinkedList<PWPlanet> r = new LinkedList<PWPlanet>();
        for (PWPlanet p : planets)
            r.add(p);
        return r;
    }

    public PWPlanet getPlanet(int planetID) {
        return planets.get(planetID);
    }

    public LinkedList<PWPlanet> growth5Planets() {
        LinkedList<PWPlanet> r = new LinkedList<PWPlanet>();
        for (PWPlanet p : planets)
            if (p.getGrowthRate() == 5)
                r.add(p);
        return r;
    }

    public LinkedList<PWPlanet> myPlanets(int playerIndex) {
        LinkedList<PWPlanet> r = new LinkedList<PWPlanet>();
        for (PWPlanet p : planets)
            if (p.getOwner() == playerIndex)
                r.add(p);
        return r;
    }

    public List<PWPlanet> notMyPlanets(int playerIndex) {
        List<PWPlanet> notMyPlanets = new ArrayList<PWPlanet>();
        for (PWPlanet p : planets)
            if (p.getOwner() != playerIndex)
                notMyPlanets.add(p);
        return notMyPlanets;
    }

    public LinkedList<PWPlanet> oppPlanets(int playerIndex) {
        LinkedList<PWPlanet> r = new LinkedList<PWPlanet>();
        for (PWPlanet p : planets)
            if (p.getOwner() != playerIndex && p.getOwner() != 0)
                r.add(p);
        return r;
    }

    public LinkedList<PWPlanet> neutralPlanets() {
        LinkedList<PWPlanet> r = new LinkedList<PWPlanet>();
        for (PWPlanet p : planets)
            if (p.getOwner() == 0)
                r.add(p);
        return r;
    }

    private int parseGameState(String s)
    {
        planets.clear();
        fleets.clear();
        int planetID = 0;
        String[] lines = s.split("\n");

        for (String line : lines)
        {
            int commentBegin = line.indexOf('#');

            if (commentBegin >= 0)
                line = line.substring(0, commentBegin);

            if (line.trim().length() == 0)
                continue;

            String[] tokens = line.split(" ");
            if (tokens.length == 0) {
                continue;
            }

            // Planets
            if (tokens[0].equals("P"))
            {
                if (tokens.length != 6)
                    return 0;

                double x = Double.parseDouble(tokens[1]);
                double y = Double.parseDouble(tokens[2]);
                int owner = Integer.parseInt(tokens[3]);
                int numShips = Integer.parseInt(tokens[4]);
                int growthRate = Integer.parseInt(tokens[5]);

                PWPlanet p = new PWPlanet(planetID++, owner, numShips, growthRate, x, y);
                planets.add(p);
            }
            // Fleets
            else if (tokens[0].equals("F"))
            {
                if (tokens.length != 7)
                    return 0;

                int owner = Integer.parseInt(tokens[1]);
                int numShips = Integer.parseInt(tokens[2]);
                int source = Integer.parseInt(tokens[3]);
                int destination = Integer.parseInt(tokens[4]);
                int totalTripLength = Integer.parseInt(tokens[5]);
                int turnsRemaining = Integer.parseInt(tokens[6]);

                PWFleet f = new PWFleet(owner, numShips, source, destination, totalTripLength, turnsRemaining);
                fleets.add(f);
            } else {
                return 0;
            }
        }
        return 1;
    }

    // Returns the number of ships that the current player has, either located
    // on planets or in flight.
    public int NumShips(int playerID)
    {
        int numShips = 0;

        for (PWPlanet p : planets)
            if (p.getOwner() == playerID)
                numShips += p.getNumShips();

        for (PWFleet f : fleets)
            if (f.getOwner() == playerID)
                numShips += f.getNumShips();

        return numShips;
    }

    public int Winner()
    {
        Set<Integer> remainingPlayers = new TreeSet<Integer>();

        for (PWPlanet p : planets)
            remainingPlayers.add(p.getOwner());

        for (PWFleet f : fleets)
            remainingPlayers.add(f.getOwner());

        remainingPlayers.remove(0);

        if (turns > maxGameLength) {
            int leadingPlayer = -1;
            int mostShips = -1;
            for (int playerID : remainingPlayers) {
                int numShips = NumShips(playerID);
                if (numShips == mostShips) {
                    leadingPlayer = 0;
                } else if (numShips > mostShips) {
                    leadingPlayer = playerID;
                    mostShips = numShips;
                }
            }
            return leadingPlayer;
        }
        switch (remainingPlayers.size()) {
            case 0:
                return 0;
            case 1:
                return ((Integer) remainingPlayers.toArray()[0]).intValue();
            default:
                return -1;
        }
    }

    public boolean isGameOver() {
        return false;
    }

    // Executes one time step.
    //   * PWPlanet bonuses are added to non-neutral planets.
    //   * Fleets are advanced towards their destinations.
    //   * Fleets that arrive at their destination are dealt with.
    public void doTimeStep()
    {
        // Add ships to each non-neutral planet according to its growth rate.
        for (PWPlanet p : planets)
            if (p.getOwner() > 0)
                p.addShips(p.getGrowthRate());

        // Advance all fleets by one time step.
        for (PWFleet f : fleets)
            f.move();

        // Determine the result of any battles
        for (PWPlanet p : planets)
            FightBattle(p);

        turns++;
    }

    //Resolves the battle at planet p, if there is one.
    //* Removes all fleets involved in the battle
    //* Sets the number of ships and owner of the planet according the outcome
    private void FightBattle(PWPlanet p)
    {
        Map<Integer, Integer> participants = new TreeMap<Integer, Integer>();
        participants.put(p.getOwner(), p.getNumShips());

        Iterator<PWFleet> it = fleets.iterator();

        while (it.hasNext()) {
            PWFleet f = it.next();
            if (f.getTurnsRemaining() <= 0 && getPlanet(f.getDestinationPlanetID()) == p) {
                if (!participants.containsKey(f.getOwner())) {
                    participants.put(f.getOwner(), f.getNumShips());
                } else {
                    participants.put(f.getOwner(), f.getNumShips() + participants.get(f.getOwner()));
                }
                it.remove();
            }
        }

        PWFleet winner = new PWFleet(0, 0);
        PWFleet second = new PWFleet(0, 0);
        for (Map.Entry<Integer, Integer> f : participants.entrySet()) {
            if (f.getValue() > second.getNumShips()) {
                if (f.getValue() > winner.getNumShips()) {
                    second = winner;
                    winner = new PWFleet(f.getKey(), f.getValue());
                } else {
                    second = new PWFleet(f.getKey(), f.getValue());
                }
            }
        }

        if (winner.getNumShips() > second.getNumShips()) {
            p.setNumShips(winner.getNumShips() - second.getNumShips());
            p.setOwner(winner.getOwner());
        } else {
            p.setNumShips(0);
        }
    }

    // TODO: not here, in PWgame
    public void makeMoves(List<PWOrder> orders) {
        for (PWOrder order : orders) {
            IssueOrder(order.getPlayerID(), order.getSourcePlanet(), order.getDestinationPlanet(),
                    order.getNumShips());
        }
    }



    // Issue an order. This function takes num_ships off the source_planet,
    // puts them into a newly-created fleet, calculates the distance to the
    // destination_planet, and sets the fleet's total trip time to that
    // distance. Checks that the given player_id is allowed to give the given
    // order. If not, the offending player is kicked from the game. If the
    // order was carried out without any issue, and everything is peachy, then
    // 0 is returned. Otherwise, -1 is returned.
    public int IssueOrder(int playerID, int sourcePlanet, int destinationPlanet, int numShips)
    {
        PWPlanet source = planets.get(sourcePlanet);

        // checks for a legal order
        if (source.getOwner() != playerID || numShips > source.getNumShips() || numShips < 0) {
            dropPlayer(playerID);
            return -1;
        }

        source.removeShips(numShips);
        int distance = distance(sourcePlanet, destinationPlanet);
        PWFleet f = new PWFleet(source.getOwner(), numShips, sourcePlanet, destinationPlanet, distance, distance);
        fleets.add(f);

        return 0;
    }

    public void dropPlayer(int playerID) {
        for (PWPlanet p : planets) {
            if (p.getOwner() == playerID) {
                p.setOwner(0);
            }
        }
        for (PWFleet f : fleets) {
            if (f.getOwner() == playerID) {
                f.kill();
            }
        }
    }

    public int distance(int sourcePlanet, int destinationPlanet){
        PWPlanet source = planets.get(sourcePlanet);
        PWPlanet destination = planets.get(destinationPlanet);
        double dx = source.getX() - destination.getX();
        double dy = source.getY() - destination.getY();
        return (int) Math.ceil(Math.sqrt(dx * dx + dy * dy));
    }

}
