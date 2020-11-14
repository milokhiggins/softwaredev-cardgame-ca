package cardgame;

import org.junit.Test;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;

import java.util.Collections;
import java.util.Arrays;
import java.util.List;

public class IntegratedTest {

    final Integer[] PACK_VALUES = {1, 1, 1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};

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


    private String testEverything() throws Exception {

        String filePath = makePackFile();
        InputStream mockIn = MockSystemIO.makeMockInputStream(new String[]{"2\n",filePath+"\n"});
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

    private String makePackFile() {
        //shuffle the pack values
        List<Integer> packValues = Arrays.asList(PACK_VALUES);
        Collections.shuffle(packValues);
        
        //make pack file
        String filePath = "";
        try {
            File packFile = File.createTempFile("testpack", ".txt");
            packFile.deleteOnExit();
            //get path and escape backslashes
            filePath = packFile.getPath().replace("\\", "\\\\");
        } catch (IOException e) { }

        //write values
        String newline = System.lineSeparator();
        try (FileWriter writer = new FileWriter(filePath)) {
            for (Integer num : packValues) {
                String line = num.toString() + newline;
                writer.write(line);
            }
        } catch (IOException e) { }

        return filePath;
    }
}
