package cardgame;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;

import java.util.ArrayList;

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
}
