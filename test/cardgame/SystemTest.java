package cardgame;

import org.junit.Test;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;


public class SystemTest {

    @Test
    public void repeatTest() throws Exception {
        int normalWins = 0;
        String winner;
        for (int i = 0; i < 100; i++) {
            winner = testEverything();
            if (winner.equals("1")) {
                normalWins++;
            }
        }
        if (normalWins < 90) {
            //this is *very* unlikely; more likely is that something is wrong with the code
            assert false;
        }
    }


    public String testEverything() throws Exception {

        InputStream mockIn = MockSystemIO.makeMockInputStream(new String[]{"2\n","test\\cardgame\\testPack1.txt\n"});
        ByteArrayOutputStream mockOut = new ByteArrayOutputStream();
        PrintStream mockPrintOut = new PrintStream(mockOut);

        InputStream systemIn = System.in;
        PrintStream systemOut = System.out;

        System.setIn(mockIn);
        System.setOut(mockPrintOut);
        try {
            //play the game
            CardGame.main(new String[0]);
        } finally {
            //restore system in and out
            System.setIn(systemIn);
            System.setOut(systemOut);
        }
        String consoleOutput = mockOut.toString();

        return consoleOutput.substring(86,87);
    }
}
