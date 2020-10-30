package cardgame;

import java.lang.reflect.Array;
import java.util.ArrayDeque;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import org.junit.Test;
import org.junit.Before;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;


public class CardDeckTest {
    CardDeck cardDeck;

    @Before
    public void setUp() {
        cardDeck = new CardDeck();
    }

    @Test
    public void testTakeCard() throws Exception {
        Field deckContentsField = CardDeck.class.getDeclaredField("contents");
        deckContentsField.setAccessible(true);

        ArrayDeque<CardGame.Card> contents = new ArrayDeque<>();
        CardGame.Card card1 = new CardGame.Card(1);
        CardGame.Card card2 = new CardGame.Card(2);
        CardGame.Card card3 = new CardGame.Card(3);
        contents.add(card1);
        contents.add(card2);
        contents.add(card3);

        deckContentsField.set(cardDeck, contents);

        CardGame.Card returnCard1 = cardDeck.takeCard();
        assertSame(returnCard1, card1);

        CardGame.Card returnCard2 = cardDeck.takeCard();
        assertSame(returnCard2, card2);

        CardGame.Card returnCard3 = cardDeck.takeCard();
        assertSame(returnCard3, card3);
    }


    @Test(expected = NoSuchElementException.class)
    public void testTakeCardThrowsException() {
        cardDeck.takeCard();
    }

    @Test
    public void testAddCard() throws Exception {
        CardGame.Card expectedCard1 = new CardGame.Card(7);
        cardDeck.addCard(expectedCard1);
        CardGame.Card expectedCard2 = new CardGame.Card(8);
        cardDeck.addCard(expectedCard2);
        CardGame.Card expectedCard3 = new CardGame.Card(9);
        cardDeck.addCard(expectedCard3);

        Field deckContentsField = CardDeck.class.getDeclaredField("contents");
        deckContentsField.setAccessible(true);

        ArrayDeque<CardGame.Card> deckContents = (ArrayDeque<CardGame.Card>) deckContentsField.get(cardDeck);

        int length = deckContents.size();
        assertEquals(length, 3);

        CardGame.Card actualCard1 = deckContents.remove();
        CardGame.Card actualCard2 = deckContents.remove();
        CardGame.Card actualCard3 = deckContents.remove();

        //check that the actual card matches the expected card; should be the *exact* same object
        assertSame(expectedCard1, actualCard1);
        assertSame(expectedCard2, actualCard2);
        assertSame(expectedCard3, actualCard3);
    }

    @Test
    public void testAddCardNotifies() throws Exception {
        CardDeck personalDeck = new CardDeck();
        //start a thread to be notified by (waits on) the deck
        Thread waiterThread = new Thread(() -> {
            synchronized (personalDeck) {
                try {
                    System.out.println("thread about to wait");
                    personalDeck.wait();
                    System.out.println("thread finished waiting");
                } catch (InterruptedException e) {
                    System.out.println("dummy thread was interrupted");
                }
            }
        });

        Thread adderThread = new Thread(() -> {
            System.out.println("about to add card");
            personalDeck.addCard(new CardGame.Card(5000));
            System.out.println("added card");
        });

        waiterThread.setPriority(Thread.MAX_PRIORITY);
        waiterThread.start();
        Thread.sleep(100);
        adderThread.start();

        Thread.sleep(1000);
        //if adderThread has notified waiterThread, then waiterThread should finish and exit -> will not be alive
        assertFalse(waiterThread.isAlive());
    }

    @Test
    public void testCreateOutputFile() throws Exception {
        cardDeck.createOutputFile();
    }

    @Test
    public void testIsNotEmptyTrue() throws Exception {
        Field deckContentsField = CardDeck.class.getDeclaredField("contents");
        deckContentsField.setAccessible(true);

        ArrayDeque<CardGame.Card> contents = new ArrayDeque<>();
        //contents.add(new CardGame.Card(1));

        deckContentsField.set(cardDeck, contents);

        assertTrue(cardDeck.isNotEmpty());
    }

    @Test
    public void testIsNotEmptyFalse() throws Exception {
        assertFalse(cardDeck.isNotEmpty());
    }

    @Test
    public void testAddConsumerPlayer() throws Exception {
        cardDeck.addConsumerPlayer(new Player(0, new CardDeck(), new CardDeck()));
    }

    @Test
    public void testAddProducerPlayer() throws Exception {
        cardDeck.addProducerPlayer(new Player(0, new CardDeck(), new CardDeck()));
    }

    @Test
    public void testAppendCard() throws Exception {
        cardDeck.appendCard(new CardGame.Card(0));
    }
}
