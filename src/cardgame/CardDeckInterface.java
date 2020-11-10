package cardgame;

/**
 *
 * Implemented in order to use mock object CardDecks to test code.
 */
public interface CardDeckInterface {

    /**
     * Card is popped off top of card deck and returned to caller.
     * @return
     */
    CardGame.Card takeCard();

    /**
     *Card is added to the bottom of the deck by caller.
     * @param card
     */
    void addCard(CardGame.Card card);

    /**
     * Creates an output file which contains a line describing the decks contents.
     */
    void createOutputFile();

    /**
     * Checks if the deck is empty.
     * @return
     */
    boolean isNotEmpty();

    /**
     * Gets the decks number.
     * @return
     */
    int getDeckNumber();
}
