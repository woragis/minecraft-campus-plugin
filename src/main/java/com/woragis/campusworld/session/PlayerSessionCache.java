package com.woragis.campusworld.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerSessionCache {

    private static final PlayerSessionCache INSTANCE = new PlayerSessionCache();

    private final Map<UUID, SessionEntry> sessions = new ConcurrentHashMap<>();

    public static PlayerSessionCache get() {
        return INSTANCE;
    }

    public void put(UUID minecraftUuid, String campusPlayerId, String status) {
        if (minecraftUuid == null || campusPlayerId == null || status == null) {
            return;
        }
        sessions.put(minecraftUuid, new SessionEntry(campusPlayerId, status.toLowerCase()));
    }

    public String campusPlayerId(UUID minecraftUuid) {
        SessionEntry entry = sessions.get(minecraftUuid);
        return entry == null ? null : entry.campusPlayerId();
    }

    public String status(UUID minecraftUuid) {
        SessionEntry entry = sessions.get(minecraftUuid);
        return entry == null ? null : entry.status();
    }

    public boolean isProbation(UUID minecraftUuid) {
        return "probation".equals(status(minecraftUuid));
    }

    public void remove(UUID minecraftUuid) {
        sessions.remove(minecraftUuid);
    }

    public record SessionEntry(String campusPlayerId, String status) {
    }
}
