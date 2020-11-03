package cardgame;

import java.util.ArrayDeque;

public class MockCardDeck implements CardDeckInterface {

    public int deckNumber;
    public ArrayDeque<CardGame.Card> contents = new ArrayDeque<>();

    public MockCardDeck(int num) {
        this.deckNumber = num;
    }

    @Override
    public CardGame.Card takeCard() {
        return contents.remove();
    }


    @Override
    public synchronized void addCard(CardGame.Card card) {
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
