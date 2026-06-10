package com.woragis.campusworld.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.woragis.campusworld.api.dto.ApiErrorResponse;
import com.woragis.campusworld.api.dto.InviteResponse;
import com.woragis.campusworld.api.dto.PlayerResponse;
import com.woragis.campusworld.api.dto.WhitelistResponse;
import com.woragis.campusworld.config.PluginConfig;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

public class CampusWorldApiClient {

    private static final String HEADER_PLUGIN_KEY = "X-Plugin-Key";

    private final PluginConfig config;
    private final HttpClient httpClient;
    private final Gson gson;

    public CampusWorldApiClient(PluginConfig config) {
        this.config = config;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(config.timeoutMs()))
                .build();
        this.gson = new GsonBuilder().create();
    }

    public WhitelistResponse checkWhitelist(UUID minecraftUuid, String username) throws ApiException {
        String encodedName = URLEncoder.encode(username, StandardCharsets.UTF_8);
        String path = "/v1/internal/whitelist/" + minecraftUuid + "?username=" + encodedName;
        return get(path, WhitelistResponse.class);
    }

    public PlayerResponse upsertPlayer(UUID minecraftUuid, String username) throws ApiException {
        Map<String, String> body = Map.of(
                "minecraftUuid", minecraftUuid.toString(),
                "username", username,
                "serverSlug", config.serverSlug()
        );
        return post("/v1/internal/players/upsert", body, PlayerResponse.class);
    }

    public boolean isApiHealthy() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.apiBaseUrl() + "/health"))
                    .timeout(Duration.ofMillis(config.timeoutMs()))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return false;
        }
    }

    public InviteResponse createInvite(UUID sponsorUuid, String targetUsername) throws ApiException {
        Map<String, String> body = Map.of(
                "sponsorUuid", sponsorUuid.toString(),
                "targetUsername", targetUsername
        );
        return post("/v1/internal/invites", body, InviteResponse.class);
    }

    public void close() {
        // HttpClient não precisa de close explícito nesta versão.
    }

    private <T> T get(String path, Class<T> type) throws ApiException {
        HttpRequest request = baseRequest(path).GET().build();
        return send(request, type);
    }

    private <T> T post(String path, Object body, Class<T> type) throws ApiException {
        String json = gson.toJson(body);
        HttpRequest request = baseRequest(path)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return send(request, type);
    }

    private HttpRequest.Builder baseRequest(String path) {
        return HttpRequest.newBuilder()
                .uri(URI.create(config.apiBaseUrl() + path))
                .timeout(Duration.ofMillis(config.timeoutMs()))
                .header(HEADER_PLUGIN_KEY, config.pluginKey());
    }

    private <T> T send(HttpRequest request, Class<T> type) throws ApiException {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            String body = response.body() == null ? "" : response.body();

            if (status >= 200 && status < 300) {
                if (body.isBlank()) {
                    throw new ApiException("API returned empty body with status " + status);
                }
                return gson.fromJson(body, type);
            }

            ApiErrorResponse err = body.isBlank() ? null : gson.fromJson(body, ApiErrorResponse.class);
            String code = err != null ? err.getCode() : null;
            String message = err != null && err.getMessage() != null ? err.getMessage() : ("HTTP " + status);
            throw new ApiException(status, code, message);
        } catch (ApiException e) {
            throw e;
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new ApiException("Failed to reach CampusWorld API", e);
        }
    }
}
