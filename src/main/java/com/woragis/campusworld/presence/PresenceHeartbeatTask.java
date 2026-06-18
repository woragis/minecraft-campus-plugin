package com.woragis.campusworld.presence;

import com.woragis.campusworld.CampusWorldPlugin;
import com.woragis.campusworld.api.ApiException;
import com.woragis.campusworld.api.CampusWorldApiClient;
import com.woragis.campusworld.config.PluginConfig;
import com.woragis.campusworld.session.PlayerSessionCache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PresenceHeartbeatTask extends BukkitRunnable {

    private final CampusWorldPlugin plugin;
    private final CampusWorldApiClient api;
    private final PluginConfig config;

    public PresenceHeartbeatTask(CampusWorldPlugin plugin, CampusWorldApiClient api, PluginConfig config) {
        this.plugin = plugin;
        this.api = api;
        this.config = config;
    }

    public void start() {
        long interval = Math.max(20L, config.presenceHeartbeatIntervalTicks());
        runTaskTimerAsynchronously(plugin, interval, interval);
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            String campusPlayerId = PlayerSessionCache.get().campusPlayerId(player.getUniqueId());
            if (campusPlayerId == null) {
                continue;
            }
            try {
                api.presenceHeartbeat(campusPlayerId, config.serverSlug());
            } catch (ApiException e) {
                plugin.getLogger().fine("Presence heartbeat falhou para " + player.getName());
            }
        }
    }
}
