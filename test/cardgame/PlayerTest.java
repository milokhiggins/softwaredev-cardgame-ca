package cardgame;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class PlayerTest {

    private Player player;
    private MockGameRunner game;
    private MockCardDeck mockLeftDeck;
    private MockCardDeck mockRightDeck;

    private void givePlayerCards(int... values) {
        for (int i : values) {
            player.appendCard(new Card(i));
        }
    }
    @Before
    public void setUp() {
        mockLeftDeck = new MockCardDeck(1);
        mockRightDeck = new MockCardDeck(2);
        game = new MockGameRunner();
        //prevent for each loop on game.decks throwing NullPointerException
        game.decks = new CardDeck[]{new CardDeck(1)};
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
            player.appendCard(new Card(3));
        }
        player.run();
        //assert that the player has set the winner attribute to their own value
        assertEquals(1, game.winner.get());

    }

    @Test
    public void testRunStartHandWinFavoured() throws AssertionError {
        for (int i = 0; i < 4; i++) {
            player.appendCard(new Card(1));
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
        mockLeftDeck.addCard(new Card(1));

        Thread.sleep(100);

        ArrayList<Card> favouredHand
                = (ArrayList<Card>) Util.getFieldByName(player, "favouredHand");
        Card[] expectedHand = new Card[] {
                new Card(1),
                new Card(1)
        };
        //convert hand to array, compare each element
        assertArrayEquals(expectedHand, favouredHand.toArray());
    }

    @Test
    public void testRunDiscardFavoured() throws AssertionError, InterruptedException {
        givePlayerCards(1, 3, 3, 3);

        Thread playerThread = new Thread(player);
        playerThread.start();
        mockLeftDeck.addCard(new Card(1));

        Thread.sleep(100);

        assertEquals(1,mockRightDeck.contents.size());
        assertEquals(3,mockRightDeck.contents.peek().getNumber());
    }

    @Test
    public void testRunUnfavouredCard() throws AssertionError, InterruptedException {
        givePlayerCards(1, 3, 3, 3);

        Thread playerThread = new Thread(player);
        playerThread.start();
        mockLeftDeck.addCard(new Card(4));

        Thread.sleep(100);

        assertEquals(1,mockRightDeck.contents.size());
        int discardCardValue = mockRightDeck.contents.peek().getNumber();

        //player should discard either 3 or 4
        assertTrue(discardCardValue == 3 || discardCardValue == 4 );
    }

    @Test
    public void testRunDiscardUnfavoured() throws AssertionError, InterruptedException {
        givePlayerCards(1, 3, 3, 3);
        Thread playerThread = new Thread(player);
        playerThread.start();
        mockLeftDeck.addCard(new Card(3));

        Thread.sleep(100);

        assertEquals(1,mockRightDeck.contents.size());
        assertEquals(3,mockRightDeck.contents.peek().getNumber());

    }

    @Test
    public void testRunFavouredWin() throws Exception {
        givePlayerCards(1, 1, 5, 7);
        Thread playerThread = new Thread(player);
        mockLeftDeck.addCard(new Card(1));
        mockLeftDeck.addCard(new Card(1));
        playerThread.start();
        Thread.sleep(100);

        assertEquals(1, game.winner.get());

        Thread.State state = playerThread.getState();
        assertSame(Thread.State.TERMINATED, state);

    }

    @Test
    public void testRunUnfavouredWin() throws Exception {
        givePlayerCards(7, 9, 9, 9);
        mockLeftDeck.addCard(new Card(9));
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

    @Test
    public void testAppendCardFavoured() throws Exception {
        player.appendCard(new Card(1));
        ArrayList<Card> hand = (ArrayList<Card>) Util.getFieldByName(player, "favouredHand");
        assertEquals(1, hand.size());
        assertEquals(new Card(1), hand.get(0));
    }

    @Test
    public void testAppendCardUnfavoured() throws Exception {
        player.appendCard(new Card(3));
        ArrayDeque<Card> hand = (ArrayDeque<Card>) Util.getFieldByName(player, "unfavouredHand");
        assertEquals(1, hand.size());
        assertEquals(new Card(3), hand.peekFirst());
    }

    @Test
    public void testCheckIfWonFavouredWin() throws  Exception {
        ArrayList<Card> hand = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            hand.add(new Card (1));
        }
        Util.setField(player, "favouredHand", hand);
        assertTrue((Boolean) Util.invokeMethod(player, "checkIfWon"));
    }

    @Test
    public void testCheckIfWonUnfavouredWin() throws  Exception {
        ArrayDeque<Card> hand = new ArrayDeque<>();
        for (int i = 0; i < 4; i++) {
            hand.add(new Card (3));
        }
        Util.setField(player, "unfavouredHand", hand);
        assertTrue((Boolean) Util.invokeMethod(player, "checkIfWon"));
    }

    @Test
    public void testCheckIfWonFavouredLoss() throws  Exception {
        ArrayList<Card> hand = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            hand.add(new Card (1));
        }
        Util.setField(player, "favouredHand", hand);
        Boolean ifWon = (Boolean) Util.invokeMethod(player, "checkIfWon");
        assertFalse(ifWon);
    }

    @Test
    public void testCheckIfWonUnfavouredLoss() throws  Exception {
        ArrayDeque<Card> hand = new ArrayDeque<>();
        for (int i = 0; i < 3; i++) {
            hand.add(new Card (3));
        }
        hand.add(new Card (7));

        Util.setField(player, "unfavouredHand", hand);
        Boolean ifWon = (Boolean) Util.invokeMethod(player, "checkIfWon");
        assertFalse(ifWon);
    }

    @Test
    public void testMarkAction() throws Exception {
        Card drawn = new Card(3);
        Card discard = new Card(4);
        Util.invokeMethod(player, "markAction", drawn, discard);

        ArrayList<String> actualResult = (ArrayList<String>) Util.getFieldByName(player, "log");

        String[] expectedResult = {
                "Player 1 draws a 3 from deck 1",
                "Player 1 discards a 4 to deck 2",
                "Player 1 current hand: "
        };

        assertArrayEquals(expectedResult, actualResult.toArray());
    }

    @Test
    public void testHandToString() throws Exception {
        ArrayDeque<Card> unfavouredHand = new ArrayDeque<>(Arrays.asList(new Card(3),
                new Card(8)));
        Util.setField(player, "unfavouredHand", unfavouredHand);

        ArrayList<Card> favouredHand = new ArrayList<>(Arrays.asList(new Card(1),
                new Card(1)));
        Util.setField(player, "favouredHand", favouredHand);

        String actualResult = (String) Util.invokeMethod(player, "handToString");

        assertEquals("1 1 3 8 ", actualResult);

    }

    @Test
    public void testWinAndExit() throws Exception {
        Util.invokeMethod(player, "winAndExit");

        boolean gameOver = (boolean) Util.getFieldByName(player, "gameOver");
        ArrayList<String> log = (ArrayList<String>) Util.getFieldByName(player, "log");

        assertTrue(gameOver);
        String entry = log.get(0);
        assertEquals("Player 1 wins \nPlayer 1 Exits\nPlayer 1 final hand ", entry);

    }

    @Test
    public void testLoseAndExit() throws Exception {
        Util.invokeMethod(player, "loseAndExit");

        boolean gameOver = (boolean) Util.getFieldByName(player, "gameOver");
        ArrayList<String> log = (ArrayList<String>) Util.getFieldByName(player, "log");

        assertTrue(gameOver);
        String entry1 = log.get(0);
        String entry2 = log.get(1);
        assertEquals("Player 0 has informed player 1 that player 0 has won", entry1);
        assertEquals("Player 1 Exits\nPlayer 1 final hand ", entry2);

    }

    @Test
    public void testCreateLog() throws Exception {
        ArrayList<String> testLog = new ArrayList<>(Arrays.asList("line 1", "line 2", "line 3"));
        Util.setField(player, "log", testLog);

        Util.invokeMethod(player, "createLog");

        //check the the output file exists
        File logFile = new File("player1_output.txt");
        assertTrue(logFile.isFile());

        //read contents of file
        BufferedReader reader = new BufferedReader(new FileReader("player1_output.txt"));
        String line1 = reader.readLine();
        String line2 = reader.readLine();
        String line3 = reader.readLine();
        reader.close();
        assertEquals("line 1", line1);
        assertEquals("line 2", line2);
        assertEquals("line 3", line3);
    }
}
