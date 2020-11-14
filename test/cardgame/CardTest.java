package cardgame;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;



public class CardTest {
    @Test
    public void getTest() throws Exception {
        Card testCard = new Card(5);
        assertEquals( 5, testCard.getNumber() );
    }

    @Test
    public void testEqualsSame() throws Exception {
        Card testCard = new Card(6);
        Card testOtherCard = new Card(6);
        assertTrue(testCard.equals(testOtherCard));
    }

    @Test
    public void testEqualsNotSame() throws Exception {
        Card testCard = new Card(5);
        Card testOtherCard = new Card(7);
        assertFalse(testCard.equals(testOtherCard));
    }

    @Test
    public void testEqualsDifferentClass() throws Exception {
        Card card = new Card(5);
        String notCard = "5";
        assertFalse(card.equals(notCard));
    }
}

