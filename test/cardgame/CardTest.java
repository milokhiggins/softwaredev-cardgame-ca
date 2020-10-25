package cardgame;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;



public class CardTest {
    @Test
    public void getTest() throws Exception {
        CardGame.Card testCard = new CardGame.Card(5);
        assertEquals( 5, testCard.getNumber() );
    }

}

