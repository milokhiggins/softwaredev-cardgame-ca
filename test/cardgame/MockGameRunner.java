package cardgame;

import java.util.concurrent.atomic.AtomicInteger;

public class MockGameRunner implements GameRunnerInterface {

    public AtomicInteger winner;
    public CardDeck[] decks;
    public Player[] players;

    public MockGameRunner() {
        winner = new AtomicInteger(0);
    }

    public int getWinner() {
        return winner.get();
    }

    public boolean compareAndSetWinner(int expected, int value) {
        return winner.compareAndSet(expected, value);
    }

    @Override
    public void notifyAllPlayers() {
        //do nothing
    }
}
