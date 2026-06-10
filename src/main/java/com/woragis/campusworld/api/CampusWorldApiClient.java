package com.woragis.campusworld.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.woragis.campusworld.api.dto.ApiErrorResponse;
import com.woragis.campusworld.api.dto.AuditEventPayload;
import com.woragis.campusworld.api.dto.CityResponse;
import com.woragis.campusworld.api.dto.ClaimPermissionResponse;
import com.woragis.campusworld.api.dto.ClaimResponse;
import com.woragis.campusworld.api.dto.GuildResponse;
import com.woragis.campusworld.api.dto.InviteResponse;
import com.woragis.campusworld.api.dto.PlayerResponse;
import com.woragis.campusworld.api.dto.RollbackItemsListResponse;
import com.woragis.campusworld.api.dto.RollbackResponse;
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
import java.util.List;
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

    public GuildResponse createGuild(UUID leaderUuid, String name) throws ApiException {
        Map<String, String> body = Map.of(
                "leaderUuid", leaderUuid.toString(),
                "name", name
        );
        return post("/v1/internal/guilds", body, GuildResponse.class);
    }

    public void joinGuild(String guildId, UUID playerUuid) throws ApiException {
        Map<String, String> body = Map.of("playerUuid", playerUuid.toString());
        post("/v1/internal/guilds/" + guildId + "/join", body, Void.class);
    }

    public void leaveGuild(String guildId, UUID playerUuid) throws ApiException {
        Map<String, String> body = Map.of("playerUuid", playerUuid.toString());
        post("/v1/internal/guilds/" + guildId + "/leave", body, Void.class);
    }

    public InviteResponse createInvite(UUID sponsorUuid, String targetUsername) throws ApiException {
        Map<String, String> body = Map.of(
                "sponsorUuid", sponsorUuid.toString(),
                "targetUsername", targetUsername
        );
        return post("/v1/internal/invites", body, InviteResponse.class);
    }

    public CityResponse createCity(UUID founderUuid, String name, String world, int centerX, int centerZ) throws ApiException {
        Map<String, Object> body = Map.of(
                "founderUuid", founderUuid.toString(),
                "name", name,
                "serverSlug", config.serverSlug(),
                "world", world,
                "centerX", centerX,
                "centerZ", centerZ
        );
        return post("/v1/internal/cities", body, CityResponse.class);
    }

    public ClaimResponse createClaim(UUID ownerUuid, String world, int minX, int maxX, int minZ, int maxZ, String zoneType) throws ApiException {
        Map<String, Object> body = Map.of(
                "ownerUuid", ownerUuid.toString(),
                "serverSlug", config.serverSlug(),
                "world", world,
                "minX", minX,
                "maxX", maxX,
                "minZ", minZ,
                "maxZ", maxZ,
                "zoneType", zoneType
        );
        return post("/v1/internal/claims", body, ClaimResponse.class);
    }

    public void deleteClaim(String claimId, UUID ownerUuid) throws ApiException {
        Map<String, String> body = Map.of("ownerUuid", ownerUuid.toString());
        delete("/v1/internal/claims/" + claimId, body, Void.class);
    }

    public void ingestAuditEvents(List<AuditEventPayload> events) throws ApiException {
        post("/v1/internal/audit/events", Map.of("events", events), Void.class);
    }

    public RollbackResponse createRollback(UUID targetUuid, UUID actorUuid, String world, int windowMinutes) throws ApiException {
        Map<String, Object> body = Map.of(
                "targetUuid", targetUuid.toString(),
                "actorUuid", actorUuid.toString(),
                "serverSlug", config.serverSlug(),
                "world", world,
                "windowMinutes", windowMinutes
        );
        return post("/v1/internal/rollbacks", body, RollbackResponse.class);
    }

    public List<com.woragis.campusworld.api.dto.RollbackItemResponse> listRollbackItems(String rollbackId) throws ApiException {
        RollbackItemsListResponse response = get("/v1/internal/rollbacks/" + rollbackId + "/items", RollbackItemsListResponse.class);
        return response == null || response.getItems() == null ? List.of() : response.getItems();
    }

    public void completeRollback(String rollbackId, int appliedCount) throws ApiException {
        post("/v1/internal/rollbacks/" + rollbackId + "/complete", Map.of("appliedCount", appliedCount), Void.class);
    }

    public ClaimPermissionResponse checkClaimPermission(UUID minecraftUuid, String world, int x, int z) throws ApiException {
        String encodedWorld = URLEncoder.encode(world, StandardCharsets.UTF_8);
        String path = "/v1/internal/claims/permission?minecraftUuid=" + minecraftUuid
                + "&serverSlug=" + URLEncoder.encode(config.serverSlug(), StandardCharsets.UTF_8)
                + "&world=" + encodedWorld
                + "&x=" + x
                + "&z=" + z;
        return get(path, ClaimPermissionResponse.class);
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

    private <T> T delete(String path, Object body, Class<T> type) throws ApiException {
        String json = gson.toJson(body);
        HttpRequest request = baseRequest(path)
                .header("Content-Type", "application/json")
                .method("DELETE", HttpRequest.BodyPublishers.ofString(json))
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
                if (type == Void.class || body.isBlank()) {
                    return null;
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
