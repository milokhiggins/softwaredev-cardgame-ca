package cardgame;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;

import java.util.ArrayList;
import static cardgame.Util.invokeMethod;
import static org.junit.Assert.*;

public class PlayerTest {

    private Player player;
    private MockCardGame game;
    private MockCardDeck mockLeftDeck;
    private MockCardDeck mockRightDeck;

    private void givePlayerCards(int... values) {
        for (int i : values) {
            player.appendCard(new CardGame.Card(i));
        }
    }
    @Before
    public void setUp() {
       mockLeftDeck = new MockCardDeck(1);
       mockRightDeck = new MockCardDeck(2);
       game = new MockCardGame();
       player = new Player(1, game, mockLeftDeck, mockRightDeck);
    }

    @After
    public void tearDown() {
        //set the winner variable to a bogus value so that the player exits
        game.winner.value = 44;
        synchronized (mockLeftDeck) {
            //notify the player if they are waiting on the deck
            mockLeftDeck.notify();
        }
    }

    @Test
    public void testRunStartHandWinUnfavoured() throws AssertionError {
        for (int i = 0; i < 4; i++) {
            player.appendCard(new CardGame.Card(3));
        }
        player.run();
        //assert that the player has set the winner attribute to their own value
        assertEquals(1, game.winner.value);

    }

    @Test
    public void testRunStartHandWinFavoured() throws AssertionError {
        for (int i = 0; i < 4; i++) {
            player.appendCard(new CardGame.Card(1));
        }
        player.run();
        //assert that the player has set the winner attribute to their own value
        assertEquals(1, game.winner.value);

    }

    @Test
    public void testRunWait() throws InterruptedException, AssertionError {

        Thread playerThread = new Thread(player);
        playerThread.start();
        Thread.sleep(100);
        Thread.State state = playerThread.getState();

        assertSame(Thread.State.WAITING, state);
    }

    @Test
    public void testRunFavouredCard() throws InterruptedException, AssertionError, NoSuchFieldException,
            IllegalAccessException {
        givePlayerCards(1,3,3,3);
        Thread playerThread = new Thread(player);
        playerThread.start();
        mockLeftDeck.addCard(new CardGame.Card(1));

        Thread.sleep(100);

        Field favouredHandField = player.getClass().getDeclaredField("favouredHand");
        favouredHandField.setAccessible(true);
        ArrayList<CardGame.Card> favouredHand = (ArrayList<CardGame.Card>) favouredHandField.get(player);
        CardGame.Card[] expectedHand = new CardGame.Card[] {
                new CardGame.Card(1),
                new CardGame.Card(1)
        };
        //convert hand to array, compare each element
        assertArrayEquals(expectedHand, favouredHand.toArray());
    }

    @Test
    public void testRunDiscardFavoured() throws AssertionError, InterruptedException {
        givePlayerCards(1, 3, 3, 3);

        Thread playerThread = new Thread(player);
        playerThread.start();
        mockLeftDeck.addCard(new CardGame.Card(1));

        Thread.sleep(100);

        assertEquals(1,mockRightDeck.contents.size());
        assertEquals(3,mockRightDeck.contents.peek().getNumber());
    }

    @Test
    public void testRunUnfavouredCard() throws AssertionError, InterruptedException, NoSuchFieldException,
            IllegalAccessException {
        givePlayerCards(1, 3, 3, 3);

        Thread playerThread = new Thread(player);
        playerThread.start();
        mockLeftDeck.addCard(new CardGame.Card(4));

        Thread.sleep(100);

        Field unfavouredHandField = player.getClass().getDeclaredField("unfavouredHand");
        unfavouredHandField.setAccessible(true);
        ArrayList<CardGame.Card> unfavouredHand = (ArrayList<CardGame.Card>) unfavouredHandField.get(player);
        CardGame.Card[] expectedHand = new CardGame.Card[] {
                new CardGame.Card(3),
                new CardGame.Card(3),
                new CardGame.Card(3),
                new CardGame.Card(4)
        };
        //convert hand to array, compare each element
        assertArrayEquals(expectedHand, unfavouredHand.toArray());
    }

    @Test
    public void testRunDiscardUnfavoured() throws AssertionError, InterruptedException {
        givePlayerCards(1, 3, 3, 3);
        Thread playerThread = new Thread(player);
        playerThread.start();
        mockLeftDeck.addCard(new CardGame.Card(3));

        Thread.sleep(100);

        assertEquals(1,mockRightDeck.contents.size());
        assertEquals(3,mockRightDeck.contents.peek().getNumber());

    }

    @Test
    public void testRunFavouredWin() throws Exception {

        player.run();
    }

    @Test
    public void testRunUnfavouredWin() throws Exception {

        player.run();
    }

    @Test
    public void testRunNoWin() throws Exception {

        player.run();
    }

    private void genericAppendCardTest(int num, String fieldName) throws Exception {
        player.appendCard(new CardGame.Card(num));

        //get private field
        Field handField = player.getClass().getDeclaredField(fieldName);
        //hand is private; set accessible
        handField.setAccessible(true);
        ArrayList<CardGame.Card> hand = (ArrayList<CardGame.Card>) handField.get(player);
        assertEquals(1, hand.size());
        assertEquals(new CardGame.Card(num), hand.get(0));
    }

    @Test
    public void testAppendCardFavoured() throws Exception {
        genericAppendCardTest(1,"favouredHand");
    }

    @Test
    public void testAppendCardUnfavoured() throws Exception {
        genericAppendCardTest(3,"unfavouredHand");
    }

    @Test
    public void testCheckIfWonFavouredWin() throws  Exception {
        //get private field
        Field favouredHand = player.getClass().getDeclaredField("favouredHand");
        //hand is private; set accessible
        favouredHand.setAccessible(true);
        ArrayList<CardGame.Card> hand = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            hand.add(new CardGame.Card (1));
        }
        favouredHand.set(player, hand);
        assertTrue((Boolean) invokeMethod(player, "checkIfWon"));
    }

    @Test
    public void testCheckIfWonUnfavouredWin() throws  Exception {
        //get private field
        Field unfavouredHand = player.getClass().getDeclaredField("unfavouredHand");
        //hand is private; set accessible
        unfavouredHand.setAccessible(true);
        ArrayList<CardGame.Card> hand = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            hand.add(new CardGame.Card (3));
        }
        unfavouredHand.set(player, hand);
        assertTrue((Boolean) invokeMethod(player, "checkIfWon"));
    }

    @Test
    public void testCheckIfWonFavouredLoss() throws  Exception {
        //get private field
        Field favouredHand = player.getClass().getDeclaredField("favouredHand");
        //hand is private; set accessible
        favouredHand.setAccessible(true);
        ArrayList<CardGame.Card> hand = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            hand.add(new CardGame.Card (1));
        }
        favouredHand.set(player, hand);
        Boolean ifWon = (Boolean) invokeMethod(player, "checkIfWon");
        assertFalse(ifWon);
    }

    @Test
    public void testCheckIfWonUnfavouredLoss() throws  Exception {
        //get private field
        Field unfavouredHand = player.getClass().getDeclaredField("unfavouredHand");
        //hand is private; set accessible
        unfavouredHand.setAccessible(true);
        ArrayList<CardGame.Card> hand = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            hand.add(new CardGame.Card (3));
        }
        hand.add(new CardGame.Card (7));
        unfavouredHand.set(player, hand);
        Boolean ifWon = (Boolean) invokeMethod(player, "checkIfWon");
        assertFalse(ifWon);
    }
}
