package net.davidrobles.mauler.planetwars;

import java.util.ArrayList;
import java.util.List;

public class PWGame {

    private PWGameState gameState;
    private List<PWBot> bots = new ArrayList<PWBot>();
    private List<PWGameObserver> observers = new ArrayList<PWGameObserver>();
    private List<PWGameState> history = new ArrayList<PWGameState>();
    
    public PWGame(String gameStateString, List<PWBot> bots) {
        gameState = new PWGameState(gameStateString, bots);
        this.bots = bots;
    }

    public PWGameState getGameState() {
        return gameState;
    }

    public void next()
    {
        for (PWBot bot : bots) {
            List<PWOrder> orders = bot.getOrders(gameState);
            gameState.makeMoves(orders);
        }

        gameState.doTimeStep();
        history.add(new PWGameState(gameState));
        notifyViews();
    }

    private void notifyViews() {
        for (PWGameObserver view : observers) {
            view.update();
        }
    }

    public List<PWGameState> getHistory() {
        return history;
    }

    public void registerView(PWGameObserver observer) {
        observers.add(observer);
    }
    
}
