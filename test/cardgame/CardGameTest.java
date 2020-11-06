package cardgame;

import java.io.*;

import java.util.ArrayList;

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

    @Test
    public void testInputFromUserNumberOfPlayers() throws Exception {
        //store original System.in and System.out for later
        InputStream systemInBackup = System.in;
        PrintStream systemOutBackup = System.out;

        int result;
        String consoleOutput;

        try {
            String[] inputs = new String[]{"not a number\n", "-7\n", "three\n", "3\n"};

            InputStream mockIn = MockSystemIO.makeMockInputStream(inputs);
            System.setIn(mockIn);

            OutputStream mockOut = new ByteArrayOutputStream();
            PrintStream mockPrintOut = new PrintStream(mockOut);
            System.setOut(mockPrintOut);

            Method testMethod = getMethodByName(CardGame.class, "inputFromUserNumberOfPlayers");
            testMethod.setAccessible(true);

            result = (int) testMethod.invoke(null);
            consoleOutput = mockOut.toString();

        } finally {
            //*always* restore System.in and System.out to their default (original) values
            System.setIn(systemInBackup);
            System.setOut(systemOutBackup);
        }

        assertEquals(3, result);
        //make sure test is platform independent ;)
        String newline = System.lineSeparator();
        String expectedString = "Please enter the number of players: Number of players must be an integer." + newline +
                                "Please enter the number of players: Number of players must be greater than 1." + newline +
                                "Please enter the number of players: Number of players must be an integer." + newline +
                                "Please enter the number of players: ";
        assertEquals(expectedString,consoleOutput);

    }

    @Test
    public void testInputFromUserPackFilename() throws AssertionError {

    }

    @Test
    public void testGetPack() throws AssertionError {

    }
}

