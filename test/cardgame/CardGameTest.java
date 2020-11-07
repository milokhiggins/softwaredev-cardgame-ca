package cardgame;

import java.io.*;


import org.junit.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.util.Random;
import java.util.Stack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        String consoleOutput = genericIOTest(inputs, "inputFromUserNumberOfPlayers", null);

        //make sure test is platform independent ;)
        String newline = System.lineSeparator();
        String expectedString = "Please enter the number of players: Number of players must be an integer." + newline +
                                "Please enter the number of players: ";
        assertEquals(expectedString,consoleOutput);

    }

    @Test
    public void testInputFromUserNegativeNumberNumberOfPlayers() throws Exception {

        String[] inputs = new String[]{"-7\n", "3\n"};
        String consoleOutput = genericIOTest(inputs, "inputFromUserNumberOfPlayers", null);

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
        String consoleOutput = genericIOTest(inputs, "inputFromUserNumberOfPlayers", null);

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
    private String genericIOTest(String[] inputs, String method, CardGame game) throws Exception {
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

            testMethod.invoke(game);
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
        CardGame game = new CardGame();
        String consoleOutput = genericIOTest(inputs, "inputFromUserPackPath", game);

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

    private CardGame makeGameAndSetNumPlayers(int num) throws IllegalAccessException, NoSuchFieldException {
        //set n using reflection
        CardGame game = new CardGame();
        Field numberOfPlayers = game.getClass().getDeclaredField("numberOfPlayers");
        numberOfPlayers.setAccessible(true);
        numberOfPlayers.set(game, num);
        return game;
    }
    @Test
    public void testValidPackFile_ValidPack() throws Exception {//TODO: this test fails???
        CardGame game = makeGameAndSetNumPlayers(3);

        String[] packContents = new String[24];
        Random random = new Random();
        for (int i=0; i<24; i++){
            packContents[i] = Integer.toString(random.nextInt(99)+1);
        }
        String cardPackPath = createTempPackFile(packContents);
        CardGame.Card[] expectedPack = new CardGame.Card[24];
        for (int i=0; i<24; i++){
            expectedPack[i] = new CardGame.Card(Integer.parseInt(packContents[i]));
        }

        Method getPack = getMethodByName(CardGame.class,"validPackFile");
        getPack.setAccessible(true);
        boolean result = (boolean) getPack.invoke(game, cardPackPath);
        assertTrue(result);
        //check that the output pack
        Field pack = game.getClass().getDeclaredField("pack");
        pack.setAccessible(true);
        CardGame.Card[] actualPack = (CardGame.Card[]) pack.get(game);
        assertArrayEquals(expectedPack, actualPack);

    }

    @Test
    public void testValidPackFile_TooShort() throws Exception {
        CardGame game = makeGameAndSetNumPlayers(3);

        String[] packContents = new String[15];
        Random random = new Random();
        for (int i = 0; i < 15; i++) {
            packContents[i] = Integer.toString(random.nextInt(100));
        }
        String cardPackPath = createTempPackFile(packContents);

        Method getPack = getMethodByName(CardGame.class,"validPackFile");
        getPack.setAccessible(true);
        boolean result = (boolean) getPack.invoke(game, cardPackPath);

        //not valid pack. should return false
        assertFalse(result);
    }

    @Test
    public void testValidPackFile_NotInt() throws Exception {
        CardGame game = makeGameAndSetNumPlayers(3);

        String[] packContents = new String[24];
        for (int i = 0; i < 24; i++) {
            packContents[i] = "abc";
        }
        String cardPackPath = createTempPackFile(packContents);

        Method getPack = getMethodByName(CardGame.class,"validPackFile");
        getPack.setAccessible(true);
        boolean result = (boolean) getPack.invoke(game, cardPackPath);

        //not valid pack. should return false
        assertFalse(result);
    }

    @Test
    public void testValidPackFile_BadIntValues() throws Exception {
        CardGame game = makeGameAndSetNumPlayers(3);

        String[] packContents = new String[24];
        for (int i = 0; i < 24; i++) {
            packContents[i] = "-2";
        }
        String cardPackPath = createTempPackFile(packContents);

        Method getPack = getMethodByName(CardGame.class,"validPackFile");
        getPack.setAccessible(true);
        boolean result = (boolean) getPack.invoke(game, cardPackPath);

        //not valid pack. should return false
        assertFalse(result);
    }

    private String createTempPackFile(String[] packContents) throws Exception {
        String cardDeckFile = createTempFile();
        FileWriter myWriter = new FileWriter(cardDeckFile);
        for (String line : packContents) {
            myWriter.write(line);
        }
        myWriter.close();
        return cardDeckFile;
    }

}

