package com.woragis.campusworld.session;

import org.bukkit.Location;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ClaimCornerCache {

    private static final ClaimCornerCache INSTANCE = new ClaimCornerCache();

    private final Map<UUID, Location> corner1 = new ConcurrentHashMap<>();
    private final Map<UUID, Location> corner2 = new ConcurrentHashMap<>();

    private ClaimCornerCache() {
    }

    public static ClaimCornerCache get() {
        return INSTANCE;
    }

    public void setCorner1(UUID playerId, Location location) {
        corner1.put(playerId, location.clone());
        corner2.remove(playerId);
    }

    public void setCorner2(UUID playerId, Location location) {
        corner2.put(playerId, location.clone());
    }

    public Location getCorner1(UUID playerId) {
        Location loc = corner1.get(playerId);
        return loc == null ? null : loc.clone();
    }

    public Location getCorner2(UUID playerId) {
        Location loc = corner2.get(playerId);
        return loc == null ? null : loc.clone();
    }

    public void clear(UUID playerId) {
        corner1.remove(playerId);
        corner2.remove(playerId);
    }
}
