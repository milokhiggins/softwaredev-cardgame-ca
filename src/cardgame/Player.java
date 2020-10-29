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
     *
     * If a player has four cards of the same denomination the have a winning
     * hand.
     * @return true if player has won the game, false otherwise.
     */
    private Boolean checkIfWon() {
        if (favouredHand.size() == 4){
            // Only cards of the same value are in the favoured hand.
            // Having four cards in the favoured list must be a winning hand.
            return true;
            // Checks if all the cards in the unfavoured hand are the same.
        } else if(unfavouredHand.size()==4) {
            int value = unfavouredHand.get(0).getNumber();
            for (CardGame.Card i : unfavouredHand) {
                if (i.getNumber() != value) {
                    return false;
                }
            }
            return true;
        // If both hands have less than four cards it is not possible to win.
        } else {return false;}
    }


}
