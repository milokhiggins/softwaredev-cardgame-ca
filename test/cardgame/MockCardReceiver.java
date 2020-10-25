package cardgame;

/**
 * Mock implementation of CardReceiver
 * @author SN690024245 & SN680046138
 * @version 1.1
 */
public class MockCardReceiver implements CardReceiver {

    public CardGame.Card[] cardList = new CardGame.Card[4];
    private int index = 0;

    /**
     * Add a card
     * @param card card to add
     */
    public void appendCard(CardGame.Card card) {
        cardList[index] = card;
        index++;
    }
}
