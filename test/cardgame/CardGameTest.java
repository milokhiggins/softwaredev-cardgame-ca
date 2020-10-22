package cardgame;

import org.junit.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;

import static org.junit.Assert.assertArrayEquals;

public class CardGameTest {

    @Test
    public void testMain() throws Exception {
        CardGame.main(new String[]{"args"});
    }

    @Test
    public void testRoundRobinDeal() throws Exception {

        MockCardReceiver mockReceiverA = new MockCardReceiver();
        MockCardReceiver mockReceiverB = new MockCardReceiver();
        MockCardReceiver[] receivers = new MockCardReceiver[] {mockReceiverA, mockReceiverB};
        CardGame.Card[] pack = {
            new CardGame.Card(1),
            new CardGame.Card(2),
            new CardGame.Card(3),
            new CardGame.Card(4),
            new CardGame.Card(5),
            new CardGame.Card(6),
            new CardGame.Card(7),
            new CardGame.Card(8),
        };

        Method roundRobin = null;
        Method[] methods = CardGame.class.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals("roundRobinDeal")) {
                roundRobin = method;
            }
        }
        //roundRobinDeal is private; set it too be accessible
        roundRobin.setAccessible(true);
        roundRobin.invoke(null, receivers, pack);

        Object[] receiverACards = new Object[]{
                new CardGame.Card(1),
                new CardGame.Card(3),
                new CardGame.Card(5),
                new CardGame.Card(7)
        };
        Object[] receiverBCards = new Object[]{
                new CardGame.Card(2),
                new CardGame.Card(4),
                new CardGame.Card(6),
                new CardGame.Card(8)
        };
        assertArrayEquals(receiverACards, mockReceiverA.cardList);
        assertArrayEquals(receiverBCards, mockReceiverB.cardList);
    }
}

