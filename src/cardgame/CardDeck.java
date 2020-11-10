package cardgame;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;

/**
 * Card deck holds a stack of cards
 */
public class CardDeck implements CardDeckInterface, CardReceiver {

    private ArrayDeque<CardGame.Card> contents = new ArrayDeque<>();
    private int deckNumber;

    /**
     *
     * @param deckNumber number of the deck
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
        FileWriter myWriter = null;
        try {
            myWriter = new FileWriter("deck" + deckNumber + "_output.txt");
            String newline = System.lineSeparator();
            String line = "deck" + deckNumber + " content";
            for (CardGame.Card card : contents) {
                line += " " + card.getNumber();
            }
            myWriter.write(line);
        }catch (IOException e){
            System.out.print("IOException while trying to make output file.");
            return;
        } finally {
            try {
                myWriter.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     *
     * @return true if the deck isn't empty, false otherwise
     */
    public boolean isNotEmpty() {
        return contents.size() != 0;
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
