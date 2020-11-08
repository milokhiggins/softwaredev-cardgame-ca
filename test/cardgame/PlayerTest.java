package cardgame;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import static cardgame.Util.invokeMethod;
import static org.junit.Assert.*;

public class PlayerTest {

    private Player player;
    private CardGame game;
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
        game = new CardGame();
        player = new Player(1, game, mockLeftDeck, mockRightDeck);
    }

    @After
    public void tearDown() {
        //set the winner variable to a bogus value so that the player exits
        game.winner.set(44);
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
        assertEquals(1, game.winner.get());

    }

    @Test
    public void testRunStartHandWinFavoured() throws AssertionError {
        for (int i = 0; i < 4; i++) {
            player.appendCard(new CardGame.Card(1));
        }
        player.run();
        //assert that the player has set the winner attribute to their own value
        assertEquals(1, game.winner.get());

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

        assertEquals(1,mockRightDeck.contents.size());
        int discardCardValue = mockRightDeck.contents.peek().getNumber();

        //convert hand to array, compare each element
        assertTrue(discardCardValue== 3 || discardCardValue== 4 );
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
        givePlayerCards(1, 1, 5, 7);
        Thread playerThread = new Thread(player);
        mockLeftDeck.addCard(new CardGame.Card(1));
        mockLeftDeck.addCard(new CardGame.Card(1));
        playerThread.start();
        Thread.sleep(100);

        assertEquals(1, game.winner.get());

        Thread.State state = playerThread.getState();
        assertSame(Thread.State.TERMINATED, state);

    }

    @Test
    public void testRunUnfavouredWin() throws Exception {
        Field randomField = player.getClass().getDeclaredField("rand");
        randomField.setAccessible(true);
        Random random = new Random(23);
        randomField.set(player, random);
        givePlayerCards(9, 9, 7, 9);
        mockLeftDeck.addCard(new CardGame.Card(9));
        Thread playerThread = new Thread(player);
        playerThread.start();
        Thread.sleep(100);

        assertEquals(1, game.winner.get());

        Thread.State state = playerThread.getState();
        assertSame(Thread.State.TERMINATED, state);
    }

    @Test
    public void testExitOnCompetitorWin() throws Exception {
        game.winner.set(3);
        Thread playerThread = new Thread(player);
        playerThread.start();

        Thread.sleep(100);

        Thread.State state = playerThread.getState();
        assertSame(Thread.State.TERMINATED, state);

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

    @Test
    public void testMarkAction() throws Exception {
        CardGame.Card drawn = new CardGame.Card(3);
        CardGame.Card discard = new CardGame.Card(4);
        invokeMethod(player, "markAction", drawn, discard);

        Field log = player.getClass().getDeclaredField("log");
        log.setAccessible(true);
        ArrayList<String> actualResult = (ArrayList<String>) log.get(player);

        String[] expectedResult = { "Player 1 draws a 3 from deck 1",
                     "Player 1 discards a 4 to deck 2",
                     "Player 1 current hand: "
        };

        assertArrayEquals(expectedResult, actualResult.toArray());
    }

    @Test
    public void testHandToString() throws Exception {
        Field unfavouredHandField = player.getClass().getDeclaredField("unfavouredHand");
        unfavouredHandField.setAccessible(true);
        ArrayList<CardGame.Card> unfavouredHand = new ArrayList<CardGame.Card>(Arrays.asList(new CardGame.Card(3),
                new CardGame.Card(8)));
        unfavouredHandField.set(player, unfavouredHand);
        Field favouredHandField = player.getClass().getDeclaredField("favouredHand");
        favouredHandField.setAccessible(true);
        ArrayList<CardGame.Card> favouredHand = new ArrayList<CardGame.Card>(Arrays.asList(new CardGame.Card(1),
                new CardGame.Card(1)));
        favouredHandField.set(player, favouredHand);

        String actualResult = (String)invokeMethod(player, "handToString");

        assertEquals("1 1 3 8 ", actualResult);

    }

}
