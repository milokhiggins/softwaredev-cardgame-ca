package cardgame;

/**
 * CardReceiver can receive cards
 * <p>
 * CardGame deals cards to implementers of this interface
 *
 * @version 1.1
 */
public interface CardReceiver {
    /**
     * Add a card to the receiver's internal store
     * @param card card to add
     */
    void appendCard(Card card);
}
