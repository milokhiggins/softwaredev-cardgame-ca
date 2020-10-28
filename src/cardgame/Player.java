package cardgame;

import java.util.ArrayList;

/**
 * Player class (something something something)
 *
 * @version 1.1
 * @author SN690024245, SN69********
 */
public class Player implements Runnable, CardReceiver {

    private ArrayList<CardGame.Card> favouredHand = new ArrayList<>();
    private ArrayList<CardGame.Card> unfavouredHand = new ArrayList<>();
    private int playerNumber;
    private CardDeckInterface leftDeck;
    private CardDeckInterface rightDeck;

    /**
     * Constructs a player with the given number.
     * @param number    player number
     * @param leftDeck  deck to the left of the player
     * @param rightDeck deck to the right of the player
     */
    public Player(int number, CardDeckInterface leftDeck, CardDeckInterface rightDeck) {
        this.leftDeck = leftDeck;
        this.rightDeck = rightDeck;
        this.playerNumber = number;
    }

    /**
     * Run the player thread.
     */
    public void run() {

    }

    /**
     * Receive a card.
     * @param card card to add
     */
    public void appendCard(CardGame.Card card) {
        int cardValue = card.getNumber();
        if (cardValue == playerNumber) {
            favouredHand.add(card);
        } else {
            unfavouredHand.add(card);
        }
    }

    /**
     * Check if the player has won.
     * @return true if the player has a winning hand; false otherwise
     */
    private boolean checkIfWon() {
        return true;
    }
}
