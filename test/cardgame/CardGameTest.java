package cardgame;

import org.junit.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.util.Stack;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class CardGameTest {

    @Test
    public void testMain() throws Exception {
        CardGame.main(new String[]{"args"});
    }

    @Test
    public void testRoundRobinDealDecks() throws Exception {

        MockCardReceiver mockReceiverA = new MockCardReceiver();
        MockCardReceiver mockReceiverB = new MockCardReceiver();
        MockCardReceiver[] receivers = new MockCardReceiver[] {mockReceiverA, mockReceiverB};
        Stack<CardGame.Card> pack = new Stack<CardGame.Card>();
            pack.push(new CardGame.Card(1));
            pack.push(new CardGame.Card(2));
            pack.push(new CardGame.Card(3));
            pack.push(new CardGame.Card(4));
            pack.push(new CardGame.Card(5));
            pack.push(new CardGame.Card(6));
            pack.push(new CardGame.Card(7));
            pack.push(new CardGame.Card(8));

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

    @Test
    public void testRoundRobinDealPack() throws Exception {

        MockCardReceiver mockReceiverA = new MockCardReceiver();
        MockCardReceiver mockReceiverB = new MockCardReceiver();
        MockCardReceiver[] receivers = new MockCardReceiver[] {mockReceiverA, mockReceiverB};
        Stack<CardGame.Card> pack = new Stack<CardGame.Card>();
        pack.push(new CardGame.Card(1));
        pack.push(new CardGame.Card(2));
        pack.push(new CardGame.Card(3));
        pack.push(new CardGame.Card(4));
        pack.push(new CardGame.Card(5));
        pack.push(new CardGame.Card(6));
        pack.push(new CardGame.Card(7));
        pack.push(new CardGame.Card(8));
        pack.push(new CardGame.Card(9));
        pack.push(new CardGame.Card(10));
        pack.push(new CardGame.Card(11));
        pack.push(new CardGame.Card(12));
        pack.push(new CardGame.Card(13));
        pack.push(new CardGame.Card(14));
        pack.push(new CardGame.Card(15));
        pack.push(new CardGame.Card(16));

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

        Stack<CardGame.Card> packLeftovers = new Stack<CardGame.Card>();
        packLeftovers.push(new CardGame.Card(1));
        packLeftovers.push(new CardGame.Card(2));
        packLeftovers.push(new CardGame.Card(3));
        packLeftovers.push(new CardGame.Card(4));
        packLeftovers.push(new CardGame.Card(5));
        packLeftovers.push(new CardGame.Card(6));
        packLeftovers.push(new CardGame.Card(7));
        packLeftovers.push(new CardGame.Card(8));

        assertEquals((Object)packLeftovers, (Object)pack);
    }
}

