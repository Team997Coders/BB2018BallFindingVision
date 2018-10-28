import static org.junit.Assert.*;
import java.io.*;

import org.junit.*;

/**
 * Unit tests for runtime settings class
 * This class factors out use of jcommander from main class.
 */
public class RuntimeSettingsTest 
{
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Before
    public void setUpStreams()
    {
        // Redirect sysout and syserr to vars that we can interrogate
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams()
    {
        // Be a good citizen and restore console settings
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void itShouldErrorWithMissingTeamParameter()
    {
        // Assemble
        String[] noSettings = {};
        RuntimeSettings settings = new RuntimeSettings(noSettings);

        // Act
        boolean parseResult = settings.parse();

        // Assert
        assertFalse(parseResult);
        assertEquals("The following option is required: [--team | -t]", settings.getParseErrorMessage());
    }

    @Test
    public void itShouldParse()
    {
        // Assemble
        String[] argv = {"-t", "997"};
        RuntimeSettings settings = new RuntimeSettings(argv);

        // Act
        boolean parseResult = settings.parse();

        // Assert
        assertTrue(parseResult);
        assertEquals(997, settings.getTeam());
    }

    @Test
    public void itShouldParseNTHost()
    {
        // Assemble
        String[] argv = {"-t", "997", "--nthost", "localhost"};
        RuntimeSettings settings = new RuntimeSettings(argv);

        // Act
        boolean parseResult = settings.parse();

        // Assert
        assertTrue(parseResult);
        assertEquals("localhost", settings.getNTHost());
    }

    @Test
    public void itShouldSpewUsageToSysout()
    {
        // Assemble
        String[] argv = {};
        RuntimeSettings settings = new RuntimeSettings(argv);

        // Act
        settings.parse();
        settings.printUsage();

        // Assert
        assertTrue(outContent.size() > 0);
    }
}
