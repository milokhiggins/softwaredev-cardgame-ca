package cardgame;

import java.util.ArrayDeque;

/**
 * Card deck holds a stack of cards
 */
public class CardDeck implements CardDeckInterface, CardReceiver {

    private ArrayDeque<CardGame.Card> contents = new ArrayDeque<>();

    /**
     * Take a card from the top of the pile
     * @return the top card
     */
    public CardGame.Card takeCard() {
        return contents.remove();
    }

    /**
     * Put a card at the bottom of the pile
     * @param card card to place at the bottom
     */
    public synchronized void addCard(CardGame.Card card) {
        contents.add(card);
        notify();
    }

    /**
     * Make the deck's output/log file
     */
    public void createOutputFile() {

    }

    /**
     *
     * @return
     */
    public boolean isNotEmpty() {
        return false;
    }

    /**
     *
     * @param player
     */
    public void addConsumerPlayer(Player player) {

    }

    /**
     *
     * @param player
     */
    public void addProducerPlayer(Player player) {

    }

    /**
     *
     * @param card card to add
     */
    public void appendCard(CardGame.Card card) {

    }
}
