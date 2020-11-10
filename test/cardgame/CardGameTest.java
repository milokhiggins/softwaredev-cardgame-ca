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
import static org.junit.Assert.assertSame;

import static cardgame.Util.getMethodByName;
import static cardgame.Util.invokeMethod;

public class CardGameTest {

    @Test
    public void testRoundRobinDealDecks() throws Exception {

        MockCardReceiver mockReceiverA = new MockCardReceiver();
        MockCardReceiver mockReceiverB = new MockCardReceiver();
        MockCardReceiver[] receivers = {mockReceiverA, mockReceiverB};
        Card[] pack = new Card[8];
        for (int i = 0; i < 8; i++) {
            pack[i] = new Card(i+1);
        }

        invokeMethod(new CardGame(), "roundRobinDeal", receivers, pack);

        Object[] receiverACards = new Object[4];
        Object[] receiverBCards = new Object[4];

        int[] valuesB = {2, 4, 6, 8};
        int[] valuesA = {1, 3, 5, 7};

        for(int i = 0; i < 4; i++) {
            receiverACards[i] = new Card(valuesA[i]);
            receiverBCards[i] = new Card(valuesB[i]);
        }

        assertArrayEquals(receiverACards, mockReceiverA.cardList);
        assertArrayEquals(receiverBCards, mockReceiverB.cardList);
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
    public void testInputFromUserPackInvalidPath() throws Exception {
        String input = "is?fg$5&%/:\n";
        genericPackPathTest(input);
    }

    @Test
    public void testInputFromUserPackDoesNotExistPath() throws Exception {
        String input = "hdsfkajsbfgyhdg32342s.txt\n";
        genericPackPathTest(input);
    }

    @Test
    public void testInputFromUserPackInvalidPackFile() throws Exception {
        String[] linesInvalid = {"this", "is", "not", "valid"};
        String filePathInvalid = createTempPackFile(linesInvalid);
        String[] linesValid = new String[16];
        for (int i = 0; i < 16; i++) {
            linesValid[i] = Integer.toString(i+1);
        }
        String filePathValid = createTempPackFile(linesValid);
        String[] inputs = {filePathInvalid+"\n", filePathValid+"\n"};
        CardGame game = makeGameAndSetNumPlayers(2);
        String consoleOutput = genericIOTest(inputs, "inputFromUserPackPath", game);

        String newline = System.lineSeparator();
        String expectedOutput = "Please enter the location of pack to load: The provided file is not valid; either it" +
                                " doesn't have enough values, or one of the lines is not a positive integer." + newline+
                                "Please enter the location of pack to load: ";
        assertEquals(expectedOutput, consoleOutput);
    }

    /**
     *
     * @param input input to test
     * @throws Exception any error occurred, including assertion fail
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
     * Make a temporary .txt file
     * @return path of the temp file
     * @throws IOException error making file
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
    public void testValidPackFile_ValidPack() throws Exception {
        CardGame game = makeGameAndSetNumPlayers(3);

        String[] packContents = new String[24];
        Random random = new Random();
        for (int i=0; i<24; i++){
            packContents[i] = Integer.toString(random.nextInt(99)+1);
        }
        String cardPackPath = createTempPackFile(packContents);
        Card[] expectedPack = new Card[24];
        for (int i=0; i<24; i++){
            expectedPack[i] = new Card(Integer.parseInt(packContents[i]));
        }

        Method getPack = getMethodByName(CardGame.class,"validPackFile");
        getPack.setAccessible(true);
        boolean result = (boolean) getPack.invoke(game, cardPackPath);
        assertTrue(result);
        //check that the output pack
        Field pack = game.getClass().getDeclaredField("pack");
        pack.setAccessible(true);
        Card[] actualPack = (Card[]) pack.get(game);
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
        String newline = System.lineSeparator();
        for (String line : packContents) {
            myWriter.write(line + newline);
        }
        myWriter.close();
        return cardDeckFile;
    }

    @Test
    public void testNotifyAllPlayers() throws Exception {
        CardGame game = new CardGame();
        CardDeck mockLeftDeck = new CardDeck(1);
        CardDeck mockRightDeck = new CardDeck(2);
        Player player1 = new Player(1,game, mockLeftDeck, mockRightDeck);
        Player player2 = new Player(2,game, mockRightDeck, mockLeftDeck);
        Util.setField(game,"players",new Player[]{player1, player2});
        Util.setField(game,"decks", new CardDeck[]{mockLeftDeck, mockRightDeck});
        Thread player1Thread = new Thread(player1);
        Thread player2Thread = new Thread(player2);
        //start player threads; should immediately wait on the deck, because no cards have been dealt yet
        player1Thread.start();
        player2Thread.start();
        //yield and wait for a little while to ensure player threads have reached wait point
        Thread.sleep(100);
        //set the winner so the player threads exit
        game.winner.set(3);
        Thread.sleep(100);
        game.notifyAllPlayers();
        //sleep again just to be certain all the player threads were notified
        Thread.sleep(100);

        Thread.State player1State = player1Thread.getState();
        Thread.State player2State = player2Thread.getState();

        assertSame(Thread.State.TERMINATED, player1State);
        assertSame(Thread.State.TERMINATED, player2State);

    }

}

