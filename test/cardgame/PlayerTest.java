package cardgame;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;

import java.util.ArrayList;
import static cardgame.Util.invokeMethod;

public class PlayerTest {

    private Player player;

    @Before
    public void setUp() {
       MockCardDeck mockLeftDeck = new MockCardDeck();
       MockCardDeck mockRightDeck = new MockCardDeck();
       player = new Player(1, mockLeftDeck, mockRightDeck);
    }

    @Test
    public void testRun() throws Exception {
        player.run();
    }

    private void genericAppendCardTest(int num, String fieldName) throws Exception {
        player.appendCard(new CardGame.Card(num));

        //get private field
        Field handField = player.getClass().getDeclaredField(fieldName);
        //hand is private; set accessible
        handField.setAccessible(true);
        ArrayList<CardGame.Card> hand = (ArrayList<CardGame.Card>) handField.get(player);
        assertEquals(hand.size(), 1);
        assertEquals(hand.get(0), new CardGame.Card(num));
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
        ArrayList<CardGame.Card> hand = new ArrayList<CardGame.Card>();
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
        ArrayList<CardGame.Card> hand = new ArrayList<CardGame.Card>();
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
        ArrayList<CardGame.Card> hand = new ArrayList<CardGame.Card>();
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
        ArrayList<CardGame.Card> hand = new ArrayList<CardGame.Card>();
        for (int i = 0; i < 3; i++) {
            hand.add(new CardGame.Card (3));
        }
        hand.add(new CardGame.Card (7));
        unfavouredHand.set(player, hand);
        Boolean ifWon = (Boolean) invokeMethod(player, "checkIfWon");
        assertFalse(ifWon);
    }
}
