package cardgame;

import java.io.*;

import java.net.URISyntaxException;
import java.util.ArrayList;

import org.junit.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.util.Random;
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
    public void testInputFromUserNotNumberNumberOfPlayers() throws Exception {

        String[] inputs = new String[]{"not a number\n", "3\n"};
        String consoleOutput = genericIOTest(inputs, "inputFromUserNumberOfPlayers");

        //make sure test is platform independent ;)
        String newline = System.lineSeparator();
        String expectedString = "Please enter the number of players: Number of players must be an integer." + newline +
                                "Please enter the number of players: ";
        assertEquals(expectedString,consoleOutput);

    }

    @Test
    public void testInputFromUserNegativeNumberNumberOfPlayers() throws Exception {

        String[] inputs = new String[]{"-7\n", "3\n"};
        String consoleOutput = genericIOTest(inputs, "inputFromUserNumberOfPlayers");

        //make sure test is platform independent ;)
        String newline = System.lineSeparator();
        String expectedString =
                "Please enter the number of players: Number of players must be greater than 1." + newline +
                "Please enter the number of players: ";
        assertEquals(expectedString,consoleOutput);

    }

    @Test
    public void testInputFromUserWrittenNumberNumberOfPlayers() throws Exception {

        String[] inputs = new String[]{"three\n", "3\n"};
        String consoleOutput = genericIOTest(inputs, "inputFromUserNumberOfPlayers");

        //make sure test is platform independent ;)
        String newline = System.lineSeparator();
        String expectedString =
                "Please enter the number of players: Number of players must be an integer." + newline +
                "Please enter the number of players: ";
        assertEquals(expectedString,consoleOutput);

    }

    /**
     * Take mock user input and return program console Output.
     *
     * @param inputs
     * @return contentOutput
     * @throws Exception
     */
    private String genericIOTest (String[] inputs, String method ) throws Exception {
        //store original System.in and System.out for later
        InputStream systemInBackup = System.in;
        PrintStream systemOutBackup = System.out;

        String consoleOutput;

        try {
            InputStream mockIn = MockSystemIO.makeMockInputStream(inputs);
            System.setIn(mockIn);

            OutputStream mockOut = new ByteArrayOutputStream();
            PrintStream mockPrintOut = new PrintStream(mockOut);
            System.setOut(mockPrintOut);

            Method testMethod = getMethodByName(CardGame.class, method);
            testMethod.setAccessible(true);

            testMethod.invoke(null);
            consoleOutput = mockOut.toString();

        } finally {
            //*always* restore System.in and System.out to their default (original) values
            System.setIn(systemInBackup);
            System.setOut(systemOutBackup);
        }

        return consoleOutput;
    }

    @Test
    public void testInputFromUserPackInvalidPath() throws AssertionError, Exception {
        String input = "is?fg$5&%/:\n";
        genericPackPathTest(input);
    }

    @Test
    public void testInputFromUserPackDoesNotExistPath() throws AssertionError, Exception {
        String input = "hdsfkajsbfgyhdg32342s.txt\n";
        genericPackPathTest(input);
    }

    /**
     *
     * @param input
     * @throws Exception
     */
    private void genericPackPathTest(String input) throws Exception {
        String filename = createTempFile();
        String[] inputs = {input, filename};
        String consoleOutput = genericIOTest(inputs, "inputFromUserPackPath");

        //make sure test is platform independent ;)
        String newline = System.lineSeparator();
        String expectedString =
                "Please enter the location of pack to load: Invalid filename." + newline +
                        "Please enter the location of pack to load: ";
        assertEquals(expectedString,consoleOutput);
    }

    /**
     *
     * @return
     * @throws IOException
     */
    private String createTempFile() throws IOException {
        File tempFile = File.createTempFile("PackTest-", ".txt");
        String filename = tempFile.getPath().replace("\\", "\\\\");
        tempFile.deleteOnExit();
        return filename;
    }

    @Test
    public void testGetPackValidPack() throws Exception {
        //set n using reflection
        CardGame game = new CardGame();
        Field numberOfPlayers = game.getClass().getDeclaredField("numberOfPlayers");
        numberOfPlayers.setAccessible(true);
        numberOfPlayers.set(game, 3);

        String[] packContents = new String[24];
        Random random = new Random();
        for (int i=0; i<24; i++){
            packContents[i] = Integer.toString(random.nextInt(100));
        }
        String cardPackPath = createTempPackFile(packContents);
        CardGame.Card[] expectedPack = new CardGame.Card[24];
        for (int i=0; i<24; i++){
            expectedPack[i] = new CardGame.Card(Integer.parseInt(packContents[i]));
        }

        Method getPack = getMethodByName(CardGame.class,"getPack");
        getPack.setAccessible(true);
        getPack.invoke(game, cardPackPath);
        //check that the output pack

        Field pack = game.getClass().getDeclaredField("pack");
        pack.setAccessible(true);
        CardGame.Card[] actualPack = (CardGame.Card[]) pack.get(game);
        assertArrayEquals(expectedPack, actualPack);

    }

    private String createTempPackFile(String[] packContents) throws Exception {
        String cardDeckFile = createTempFile();
        FileWriter myWriter = new FileWriter(cardDeckFile);
        for (String line :packContents) {
            myWriter.write(line);
        }
        myWriter.close();
        return cardDeckFile;
    }

}

