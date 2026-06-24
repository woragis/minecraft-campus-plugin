package com.woragis.campusworld.hud;

import com.woragis.campusworld.api.dto.HudResponse;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class HudCache {

    private static final HudCache INSTANCE = new HudCache();

    private final Map<UUID, HudResponse> snapshots = new ConcurrentHashMap<>();

    public static HudCache get() {
        return INSTANCE;
    }

    public void put(UUID minecraftUuid, HudResponse hud) {
        if (minecraftUuid != null && hud != null) {
            snapshots.put(minecraftUuid, hud);
        }
    }

    public HudResponse get(UUID minecraftUuid) {
        return minecraftUuid == null ? null : snapshots.get(minecraftUuid);
    }

    public void remove(UUID minecraftUuid) {
        if (minecraftUuid != null) {
            snapshots.remove(minecraftUuid);
        }
    }
}
