package data_management;

import com.data_management.DataStorage;
import com.data_management.FileDataReader;
import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;


/**
 * This strategy class verifies the functionality of FileDataReader.
 * It checks whether data is correctly read from files under various conditions,
 * including valid data, empty files, missing directories, and weird content.
 *
 * Assumptions:
 * - Files must follow a specific format (e.g., id,value,type,timestamp).
 * - Empty files and invalid paths should trigger IOExceptions.
 */
class FileDataReaderTest {

    private Path myPath;
    private DataStorage dataStorage;

    @BeforeEach
    void setUp() throws IOException {
        myPath = Files.createTempDirectory("test_data");
        dataStorage = DataStorage.getInstance(); // Using the singleton call.
        dataStorage.clear(); // Clear each record after testing.
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walk(myPath)
                .map(Path::toFile)
                .forEach(File::delete);
    }

    /**
     * Tests that a file with correct format and data is properly read into DataStorage.
     */
    @Test
    void testReadDataFromValidFile() throws IOException {
        String content = "1,98.6,BodyTemp,1714376789050";
        Path testFile = myPath.resolve("sample.csv");
        Files.write(testFile, content.getBytes());

        FileDataReader reader = new FileDataReader(myPath.toString());
        reader.readData(dataStorage);

        assertEquals(1, dataStorage.getRecords(1, 1714370000000L, 1714380000000L).size());
    }

    /**
     * Tests that reading from an invalid directory path throws an IOException.
     */
    @Test
    void testThrowsExceptionForInvalidDirectory() {
        FileDataReader reader = new FileDataReader("invalid/path");
        assertThrows(IOException.class, () -> reader.readData(dataStorage));
    }

    /**
     * Tests that reading from a directory with no files throws an IOException.
     */
    @Test
    void testThrowsExceptionWhenNoFilesPresent() {
        FileDataReader reader = new FileDataReader(myPath.toString());
        assertThrows(IOException.class, () -> reader.readData(dataStorage));
    }


    /**
     * Tests that reading from an empty file results in an IOException.
     * Assumes that empty files are considered invalid input.
     */
    @Test
    void testThrowsExceptionEmptyFile() throws IOException {
        Path testFile = myPath.resolve("empty.csv");
        Files.createFile(testFile);

        FileDataReader reader = new FileDataReader(myPath.toString());

        assertThrows(IOException.class, () -> reader.readData(dataStorage));
    }


    /**
     * Tests that a file with malformed content does not crash the program
     * and does not store invalid data.
     *
     * Assumes: The system skips or throws an error on malformed lines.
     */
    @Test
    void testForWrongFormat() throws IOException {
        String content = "CristianoRonald,SUIIII,777,yes";
        Path testFile = myPath.resolve("invalid_format.csv");

        FileDataReader reader = new FileDataReader(myPath.toString());
        assertThrows(IOException.class, () -> reader.readData(dataStorage));
    }
}