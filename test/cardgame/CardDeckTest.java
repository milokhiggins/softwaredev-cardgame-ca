package cardgame;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayDeque;

import org.junit.Test;
import org.junit.Before;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;


public class CardDeckTest {

    private CardDeck cardDeck;

    @Before
    public void setUp() {
        cardDeck = new CardDeck(3);
    }

    @Test
    public void testTakeCard() throws Exception {

        Card card1 = new Card(1);
        Card card2 = new Card(2);
        Card card3 = new Card(3);
        ArrayDeque<Card> contents = new ArrayDeque<>(Arrays.asList(card1, card2, card3));

        Util.setField(cardDeck, "contents", contents);

        Card returnCard1 = cardDeck.takeCard();
        assertSame(card1, returnCard1);

        Card returnCard2 = cardDeck.takeCard();
        assertSame(card2, returnCard2);

        Card returnCard3 = cardDeck.takeCard();
        assertSame(card3, returnCard3);
    }


    @Test(expected = NoSuchElementException.class)
    public void testTakeCardThrowsException() {
        cardDeck.takeCard();
    }

    @Test
    public void testAddCard() throws Exception {
        Card expectedCard1 = new Card(7);
        cardDeck.addCard(expectedCard1);
        Card expectedCard2 = new Card(8);
        cardDeck.addCard(expectedCard2);
        Card expectedCard3 = new Card(9);
        cardDeck.addCard(expectedCard3);

        ArrayDeque<Card> deckContents = (ArrayDeque<Card>) Util.getFieldByName(cardDeck, "contents");

        int length = deckContents.size();
        assertEquals(3, length);

        Card actualCard1 = deckContents.remove();
        Card actualCard2 = deckContents.remove();
        Card actualCard3 = deckContents.remove();

        //check that the actual card matches the expected card; should be the *exact* same object
        assertSame(expectedCard1, actualCard1);
        assertSame(expectedCard2, actualCard2);
        assertSame(expectedCard3, actualCard3);
    }

    @Test
    public void testAddCardNotifies() throws Exception {
        CardDeck personalDeck = new CardDeck(3);
        //start a thread to be notified by (waits on) the deck
        Thread waiterThread = new Thread(() -> {
            synchronized (personalDeck) {
                try {
                    personalDeck.wait();
                } catch (InterruptedException e) {
                    System.out.println("dummy thread was interrupted");
                }
            }
        });

        Thread adderThread = new Thread(() -> personalDeck.addCard(new Card(5000)));

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
        Card card1 = new Card(1);
        Card card2 = new Card(2);
        Card card3 = new Card(3);
        Card card4 = new Card(4);

        ArrayDeque<Card> testContents =  new ArrayDeque<Card> (Arrays.asList(card1, card2, card3, card4));
        Util.setField(cardDeck, "contents", testContents);

        Util.invokeMethod(cardDeck, "createOutputFile");

        //check the the output file exists
        File deckOutputFile = new File("deck3_output.txt");
        assertTrue(deckOutputFile.isFile());

        //read contents of file
        BufferedReader reader = new BufferedReader(new FileReader("deck3_output.txt"));
        String line1 = reader.readLine();
        reader.close();
        assertEquals("deck3 content 1 2 3 4", line1);
    }

    @Test
    public void testIsNotEmptyTrue() throws Exception {

        ArrayDeque<Card> contents = new ArrayDeque<>();
        contents.add(new Card(1));

        Util.setField(cardDeck, "contents", contents);

        assertTrue(cardDeck.isNotEmpty());
    }

    @Test
    public void testIsNotEmptyMultipleCardsTrue() throws Exception {

        ArrayDeque<Card> contents = new ArrayDeque<>();
        for (int i = 0; i < 12; i++ ){
            contents.add(new Card(i));
        }
        Util.setField(cardDeck, "contents", contents);
        assertTrue(cardDeck.isNotEmpty());
    }

    @Test
    public void testIsNotEmptyFalse() throws Exception {
        assertFalse(cardDeck.isNotEmpty());
    }

    @Test
    public void testAppendCard() throws Exception {
        Card expectedCard1 = new Card(7);
        cardDeck.appendCard(expectedCard1);
        Card expectedCard2 = new Card(8);
        cardDeck.appendCard(expectedCard2);
        Card expectedCard3 = new Card(9);
        cardDeck.appendCard(expectedCard3);

        ArrayDeque<Card> deckContents = (ArrayDeque<Card>) Util.getFieldByName(cardDeck, "contents");

        int length = deckContents.size();
        assertEquals(3, length);

        Card actualCard1 = deckContents.remove();
        Card actualCard2 = deckContents.remove();
        Card actualCard3 = deckContents.remove();

        //check that the actual card matches the expected card; should be the *exact* same object
        assertSame(expectedCard1, actualCard1);
        assertSame(expectedCard2, actualCard2);
        assertSame(expectedCard3, actualCard3);
    }

    @Test
    public void testGetDeckNumber() throws AssertionError {
        assertEquals(3, cardDeck.getDeckNumber());
    }

    @Test
    public void testGetDeckNegativeNumber() throws AssertionError {
        CardDeck cardDeck2 = new CardDeck(-100);
        assertEquals(-100, cardDeck2.getDeckNumber());
    }
}
