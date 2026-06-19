package com.woragis.campusworld.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

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
        sessions.put(minecraftUuid, new SessionEntry(campusPlayerId, status.toLowerCase(), System.currentTimeMillis(), new AtomicInteger(0)));
    }

    public SessionEntry take(UUID minecraftUuid) {
        return sessions.remove(minecraftUuid);
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

    public void incrementMobKills(UUID minecraftUuid) {
        SessionEntry entry = sessions.get(minecraftUuid);
        if (entry != null) {
            entry.mobKills().incrementAndGet();
        }
    }

    public void remove(UUID minecraftUuid) {
        sessions.remove(minecraftUuid);
    }

    public long sessionSeconds(SessionEntry entry) {
        if (entry == null) {
            return 0;
        }
        long elapsed = System.currentTimeMillis() - entry.joinedAtMillis();
        if (elapsed <= 0) {
            return 0;
        }
        return elapsed / 1000L;
    }

    public record SessionEntry(String campusPlayerId, String status, long joinedAtMillis, AtomicInteger mobKills) {
    }
}
