package cardgame;

/**
 * Implemented in order to use mock object CardDecks to test code.
 */
public interface CardDeckInterface {

    /**
     * Card is popped off top of card deck and returned to caller.
     * @return card from top of deck
     */
    Card takeCard();

    /**
     *Card is added to the bottom of the deck by caller.
     * @param card card to add
     */
    void addCard(Card card);

    /**
     * Creates an output file which contains a line describing the decks contents.
     */
    void createOutputFile();

    /**
     * Checks if the deck is empty.
     * @return true if not empty; false otherwise
     */
    boolean isNotEmpty();

    /**
     * Gets the deck's number.
     * @return deck's number
     */
    int getDeckNumber();
}
