package mestint;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Theories.class)
public class TimeoutTest {

    private static final String FOLDER = "src\\test\\resources";

    private static final String TXT_EXT = ".txt";

    private static final int TIMEOUT = 6000;

    private List<String> readFile(String folder, String fileName) {
        Path file = Paths.get(folder, fileName);
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Theory
    @Test(timeout = TIMEOUT)
    public void testInputFile(int id) {
        String fileName = "ki" + id + TXT_EXT;
        List<String> correctResults = readFile(FOLDER, fileName);

        Game game = Importer.importGame(Paths.get(FOLDER, "be" + id + TXT_EXT));

        Solver solver = new Solver(game);

        assertThat(solver.getMaxReachableTitanium(), is(Integer.parseInt(correctResults.get(0))));
    }

    public static final @DataPoints int[] ids = IntStream.rangeClosed(0, 9).toArray();
}
