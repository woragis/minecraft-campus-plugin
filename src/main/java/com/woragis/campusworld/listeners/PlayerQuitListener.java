package com.woragis.campusworld.listeners;

import com.woragis.campusworld.CampusWorldPlugin;
import com.woragis.campusworld.api.ApiException;
import com.woragis.campusworld.api.CampusWorldApiClient;
import com.woragis.campusworld.config.PluginConfig;
import com.woragis.campusworld.session.PlayerSessionCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final CampusWorldPlugin plugin;
    private final CampusWorldApiClient api;
    private final PluginConfig config;

    public PlayerQuitListener(CampusWorldPlugin plugin, CampusWorldApiClient api, PluginConfig config) {
        this.plugin = plugin;
        this.api = api;
        this.config = config;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        if (!config.presenceEnabled()) {
            return;
        }
        Player player = event.getPlayer();
        String campusPlayerId = PlayerSessionCache.get().campusPlayerId(player.getUniqueId());
        PlayerSessionCache.get().remove(player.getUniqueId());
        if (campusPlayerId == null) {
            return;
        }
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                api.presenceOffline(campusPlayerId, config.serverSlug());
            } catch (ApiException e) {
                plugin.getLogger().fine("Presence offline falhou para " + player.getName() + ": " + e.getMessage());
            }
        });
    }
}
