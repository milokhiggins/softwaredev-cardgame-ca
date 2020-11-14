package cardgame;

/**
 * Mock implementation of CardReceiver
 * @version 1.1
 */
public class MockCardReceiver implements CardReceiver {

    public Card[] cardList = new Card[4];
    private int index = 0;

    /**
     * Add a card
     * @param card card to add
     */
    public void appendCard(Card card) {
        cardList[index] = card;
        index++;
    }
}
