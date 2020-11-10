package cardgame;

import java.util.ArrayDeque;

public class MockCardDeck implements CardDeckInterface {

    public int deckNumber;
    public ArrayDeque<Card> contents = new ArrayDeque<>();

    public MockCardDeck(int num) {
        this.deckNumber = num;
    }

    @Override
    public Card takeCard() {
        return contents.remove();
    }


    @Override
    public synchronized void addCard(Card card) {
        contents.add(card);
        notify();
    }

    @Override
    public void createOutputFile() {

    }

    @Override
    public boolean isNotEmpty() {
        return contents.size() != 0;
    }

    public int getDeckNumber(){
        return this.deckNumber;
    }
}
