package cardgame;

/**
 * COMMENT ME
 */
public interface CardDeckInterface {
    /**
     *
     * @return
     */
    CardGame.Card takeCard();

    /**
     *
     * @param card
     */
    void addCard(CardGame.Card card);

    /**
     *
     */
    void createOutputFile();

    /**
     *
     * @return
     */
    boolean isNotEmpty();

    /**
     *
     * @param player
     */
    void addConsumerPlayer(Player player);

    /**
     *
     * @param player
     */
    void addProducerPlayer(Player player);
}
