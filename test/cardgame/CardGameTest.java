package cardgame;

import org.junit.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.util.Stack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import static cardgame.Util.getMethodByName;
import static cardgame.Util.invokeMethod;

public class CardGameTest {

    @Test
    public void testMain() throws Exception {
        CardGame.main(new String[]{"args"});
    }

    @Test
    public void testRoundRobinDealDecks() throws Exception {

        MockCardReceiver mockReceiverA = new MockCardReceiver();
        MockCardReceiver mockReceiverB = new MockCardReceiver();
        MockCardReceiver[] receivers = new MockCardReceiver[]{mockReceiverA, mockReceiverB};
        Stack<CardGame.Card> pack = new Stack<>();
        for (int i = 1; i < 9; i++) {
            pack.push(new CardGame.Card(i));
        }

        invokeMethod(new CardGame(), "roundRobinDeal", receivers, pack);

        Object[] receiverACards = new Object[4];
        Object[] receiverBCards = new Object[4];

        int[] valuesA = new int[]{8, 6, 4, 2};
        int[] valuesB = new int[]{7, 5, 3, 1};

        for(int i = 0; i < 4; i++) {
            receiverACards[i] = new CardGame.Card(valuesA[i]);
            receiverBCards[i] = new CardGame.Card(valuesB[i]);
        }

        assertArrayEquals(receiverACards, mockReceiverA.cardList);
        assertArrayEquals(receiverBCards, mockReceiverB.cardList);
    }

    @Test
    public void testRoundRobinDealPack() throws Exception {

        MockCardReceiver mockReceiverA = new MockCardReceiver();
        MockCardReceiver mockReceiverB = new MockCardReceiver();
        MockCardReceiver[] receivers = new MockCardReceiver[] {mockReceiverA, mockReceiverB};
        Stack<CardGame.Card> pack = new Stack<>();

        for (int i = 1; i < 17; i++) {
            pack.push(new CardGame.Card(i));
        }

        invokeMethod(new CardGame(), "roundRobinDeal", receivers, pack);

        CardGame.Card[] packLeftover = new CardGame.Card[8];
        for (int i = 0; i < 8; i++) {
            packLeftover[i] = new CardGame.Card(i+1);
        }
        assertArrayEquals(packLeftover, pack.toArray());
    }
}

