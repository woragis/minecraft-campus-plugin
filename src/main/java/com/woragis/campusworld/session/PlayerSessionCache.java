package com.woragis.campusworld.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerSessionCache {

    private static final PlayerSessionCache INSTANCE = new PlayerSessionCache();

    private final Map<UUID, String> statusByPlayer = new ConcurrentHashMap<>();

    public static PlayerSessionCache get() {
        return INSTANCE;
    }

    public void put(UUID playerId, String status) {
        if (playerId != null && status != null) {
            statusByPlayer.put(playerId, status.toLowerCase());
        }
    }

    public String status(UUID playerId) {
        return statusByPlayer.get(playerId);
    }

    public boolean isProbation(UUID playerId) {
        return "probation".equals(status(playerId));
    }

    public void remove(UUID playerId) {
        statusByPlayer.remove(playerId);
    }
}
