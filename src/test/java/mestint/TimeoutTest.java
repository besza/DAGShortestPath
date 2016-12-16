package mestint;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class TimeoutTest {

    private static final String FOLDER = "src\\test\\resources";

    private static final String TXT_EXT = ".txt";

    private static final String INPUT_FILE_PREFIX = "be";

    private static final String OUTPUT_FILE_PREFIX = "ki";

    private static final int TIMEOUT = 6000;

    @Parameterized.Parameter
    public Integer which;

    private List<String> readFile(String folder, String fileName) {
        Path file = Paths.get(folder, fileName);
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test//(timeout = TIMEOUT)
    public void testInputFile() {
        String partialFileName = which + TXT_EXT;

        List<String> correctResults = readFile(FOLDER, OUTPUT_FILE_PREFIX + partialFileName);

        Game game = Importer.importGame(Paths.get(FOLDER, INPUT_FILE_PREFIX + partialFileName));

        long start = System.nanoTime();

        Solver solver = new Solver(game);

        long elapsed = System.nanoTime() - start;

        System.out.println("method call execution in seconds = " + elapsed * 10e-9);

        assertThat(solver.getOptimumTitanium(), is(Integer.parseInt(correctResults.get(0))));
    }

    @Parameterized.Parameters(name = "be{index}.txt")
    public static Collection<Integer> data() {
        return Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    }

}
