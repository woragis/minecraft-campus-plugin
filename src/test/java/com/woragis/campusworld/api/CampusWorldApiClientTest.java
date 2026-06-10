package com.woragis.campusworld.api;

import com.sun.net.httpserver.HttpServer;
import com.woragis.campusworld.api.dto.InviteResponse;
import com.woragis.campusworld.api.dto.WhitelistResponse;
import com.woragis.campusworld.config.PluginConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CampusWorldApiClientTest {

    private HttpServer server;
    private String baseUrl;
    private CampusWorldApiClient client;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        int port = server.getAddress().getPort();
        baseUrl = "http://127.0.0.1:" + port;
        server.start();

        client = new CampusWorldApiClient(PluginConfig.forTest(baseUrl, "test-key"));
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
        client.close();
    }

    @Test
    void checkWhitelistAllowed() throws Exception {
        server.createContext("/v1/internal/whitelist/" + uuid(), exchange -> {
            assertEquals("test-key", exchange.getRequestHeaders().getFirst("X-Plugin-Key"));
            writeJson(exchange, 200, "{\"allowed\":true,\"reason\":\"active\"}");
        });

        WhitelistResponse response = client.checkWhitelist(uuid(), "Steve");
        assertTrue(response.isAllowed());
        assertEquals("active", response.getReason());
    }

    @Test
    void checkWhitelistDenied() throws Exception {
        server.createContext("/v1/internal/whitelist/" + uuid(), exchange ->
                writeJson(exchange, 200, "{\"allowed\":false,\"reason\":\"not_invited\"}")
        );

        WhitelistResponse response = client.checkWhitelist(uuid(), "Alex");
        assertFalse(response.isAllowed());
    }

    @Test
    void createInvite() throws Exception {
        server.createContext("/v1/internal/invites", exchange -> {
            writeJson(exchange, 201, "{\"id\":\"abc\",\"code\":\"CW-TEST01\",\"targetUsername\":\"Alex\",\"status\":\"pending\"}");
        });

        InviteResponse response = client.createInvite(uuid(), "Alex");
        assertEquals("CW-TEST01", response.getCode());
    }

    private static UUID uuid() {
        return UUID.fromString("11111111-1111-1111-1111-111111111111");
    }

    private static void writeJson(com.sun.net.httpserver.HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
