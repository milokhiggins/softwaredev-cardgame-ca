package cardgame;

public class MockCardDeck implements CardDeckInterface {

    private int num;

    @Override
    public CardGame.Card takeCard() {
        return new CardGame.Card(num);
    }

    /**
     * Set the value of the Card returned by takeCard
     * @param num number to set
     */
    public void setCardNumber(int num) {
        this.num = num;
    }

    @Override
    public synchronized void addCard(CardGame.Card card) {

    }

    @Override
    public void createOutputFile() {

    }

    @Override
    public boolean isNotEmpty() {
        return false;
    }

    @Override
    public void addConsumerPlayer(Player player) {

    }

    @Override
    public void addProducerPlayer(Player player) {

    }
}