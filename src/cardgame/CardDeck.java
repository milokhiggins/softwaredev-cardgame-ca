package cardgame;

import java.util.ArrayDeque;

/**
 * Card deck holds a stack of cards
 */
public class CardDeck implements CardDeckInterface, CardReceiver {

    private ArrayDeque<CardGame.Card> contents = new ArrayDeque<>();
    private int deckNumber;

    /**
     *
     * @param deckNumber
     */
    public CardDeck (int deckNumber){
        this.deckNumber = deckNumber;
    }

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
        if ( contents.size() == 0 ){
            return false;
        }else{
            return true;
        }
    }

    /**
     *
     * @param card card to add
     */
    public void appendCard(CardGame.Card card) {
        contents.add(card);
    }

    /**
     *
     * @return Deck's Number
     */
    public int getDeckNumber(){
        return this.deckNumber;
    }
}
