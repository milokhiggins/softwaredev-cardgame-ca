package cardgame;

import java.util.ArrayList;
import java.io.ByteArrayInputStream;
import java.io.SequenceInputStream;
import java.io.InputStream;

import java.util.Collections;


public class MockSystemIO {

    public static InputStream makeMockInputStream(String[] inputs) {
        ArrayList<InputStream> streams = new ArrayList<>();
        for (String s : inputs) {
            streams.add(new ByteArrayInputStream(s.getBytes()));
        }
        InputStream fullStream = new SequenceInputStream(Collections.enumeration(streams));
        return fullStream;
    }
}
