package server_testing;

import com.cardio_generator.outputs.WebSocketOutputStrategy;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.*;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the error handling capabilities of WebSocketOutputStrategy
 * when network or connection issues occur.
 */
public class WebSocketOutputStrategyErrorTest {

    private static WebSocketOutputStrategy outputStrategy;
    private static final int PORT = 9091;
    private static final String WS_URI = "ws://localhost:" + PORT;

    @BeforeAll
    public static void setUp() {
        outputStrategy = new WebSocketOutputStrategy(PORT);
    }

    @AfterAll
    public static void tearDown() throws Exception {
        // Let server shut down naturally when program exits
    }

    /**
     * Tests sending output when no clients are connected.
     * Ensures no exceptions are thrown.
     */
    @Test
    public void testSendWithNoClientConnected() {
        assertDoesNotThrow(() ->
                        outputStrategy.output(1, System.currentTimeMillis(), "HeartRate", "75.0"),
                "Should not throw error when sending without clients");
    }

    /**
     * Tests if server handles a client disconnecting unexpectedly during communication.
     */
    @Test
    public void testClientDisconnectDuringSend() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        WebSocketClient client = new WebSocketClient(new URI(WS_URI)) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                latch.countDown();
                close(); // Disconnect immediately after connecting
            }

            @Override
            public void onMessage(String message) {}

            @Override
            public void onClose(int code, String reason, boolean remote) {}

            @Override
            public void onError(Exception ex) {}
        };

        client.connect();
        assertTrue(latch.await(3, TimeUnit.SECONDS), "Client should connect");

        // Try to send after client disconnects
        assertDoesNotThrow(() ->
                        outputStrategy.output(2, System.currentTimeMillis(), "ECG", "0.98"),
                "Server should handle client disconnect gracefully");
    }

    /**
     * Tests that malformed data does not crash the server.
     */
    @Test
    public void testMalformedDataHandling() {
        assertDoesNotThrow(() ->
                        outputStrategy.output(3, System.currentTimeMillis(), "InvalidType", "??"),
                "Malformed data should not crash the server");
    }
}
