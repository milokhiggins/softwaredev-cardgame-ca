package cardgame;

public interface CardDeckInterface {
    CardGame.Card takeCard();
    void addCard(CardGame.Card card);
    void createOutputFile();
    boolean enoughCards();
    void addConsumerPlayer(Player player);
    void addProducerPlayer(Player player);
}
