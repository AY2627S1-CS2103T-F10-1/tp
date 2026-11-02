package seedu.address.commons.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class LogsCenterTest {
    @TempDir
    public Path testFolder;

    @Test
    public void getLogger_unavailableLogFile_keepsConsoleLogging() throws Exception {
        Files.createDirectory(testFolder.resolve("addressbook.log.0"));
        Path output = testFolder.resolve("output.txt");
        String classPath = Path.of(LogsCenterTest.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                + File.pathSeparator
                + Path.of(LogsCenter.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        String java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        Process process = new ProcessBuilder(java, "-cp", classPath, LoggerProbe.class.getName())
                .directory(testFolder.toFile()).redirectErrorStream(true).redirectOutput(output.toFile()).start();
        try {
            assertTrue(process.waitFor(30, TimeUnit.SECONDS), "Logger process timed out");
            String messages = Files.readString(output);
            assertEquals(0, process.exitValue(), messages);
            assertTrue(messages.contains("Error adding file handler for logger."), messages);
            assertTrue(messages.contains("Console logging remains available"), messages);
        } finally {
            process.destroyForcibly();
        }
    }

    /** Runs in a fresh JVM so logger initialization has not already succeeded in another test. */
    public static class LoggerProbe {
        public static void main(String[] args) {
            LogsCenter.getLogger(LoggerProbe.class).info("Console logging remains available");
        }
    }
}
