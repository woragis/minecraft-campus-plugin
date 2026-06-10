package com.woragis.campusworld.listeners;

import com.woragis.campusworld.CampusWorldPlugin;
import com.woragis.campusworld.api.ApiException;
import com.woragis.campusworld.api.CampusWorldApiClient;
import com.woragis.campusworld.api.dto.ClaimPermissionResponse;
import com.woragis.campusworld.config.PluginConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ClaimProtectionListener implements Listener {

    private final CampusWorldPlugin plugin;
    private final CampusWorldApiClient api;
    private final PluginConfig config;
    private final Map<String, Boolean> permissionCache = new ConcurrentHashMap<>();

    public ClaimProtectionListener(CampusWorldPlugin plugin, CampusWorldApiClient api, PluginConfig config) {
        this.plugin = plugin;
        this.api = api;
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!checkAllowed(event.getPlayer(), event.getBlock().getX(), event.getBlock().getZ(), event.getBlock().getWorld().getName())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(config.claimDenied());
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!checkAllowed(event.getPlayer(), event.getBlock().getX(), event.getBlock().getZ(), event.getBlock().getWorld().getName())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(config.claimDenied());
        }
    }

    private boolean checkAllowed(Player player, int x, int z, String world) {
        String cacheKey = cacheKey(player.getUniqueId(), world, x, z);
        Boolean cached = permissionCache.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        try {
            ClaimPermissionResponse response = api.checkClaimPermission(player.getUniqueId(), world, x, z);
            boolean allowed = response.isAllowed();
            permissionCache.put(cacheKey, allowed);
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> permissionCache.remove(cacheKey), 40L);
            return allowed;
        } catch (ApiException e) {
            plugin.getLogger().warning("Claim permission check failed: " + e.getMessage());
            return true;
        }
    }

    private static String cacheKey(UUID playerId, String world, int x, int z) {
        return playerId + ":" + world + ":" + x + ":" + z;
    }
}
