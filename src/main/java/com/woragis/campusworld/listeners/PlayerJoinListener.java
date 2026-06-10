package com.woragis.campusworld.listeners;

import com.woragis.campusworld.CampusWorldPlugin;
import com.woragis.campusworld.api.ApiException;
import com.woragis.campusworld.api.CampusWorldApiClient;
import com.woragis.campusworld.api.dto.PlayerResponse;
import com.woragis.campusworld.config.PluginConfig;
import com.woragis.campusworld.session.PlayerSessionCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final CampusWorldPlugin plugin;
    private final CampusWorldApiClient api;
    private final PluginConfig config;

    public PlayerJoinListener(CampusWorldPlugin plugin, CampusWorldApiClient api, PluginConfig config) {
        this.plugin = plugin;
        this.api = api;
        this.config = config;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> syncPlayer(player));
    }

    private void syncPlayer(Player player) {
        try {
            PlayerResponse response = api.upsertPlayer(player.getUniqueId(), player.getName());
            if (response != null && response.getStatus() != null) {
                PlayerSessionCache.get().put(player.getUniqueId(), response.getStatus());
            }
        } catch (ApiException e) {
            plugin.getLogger().warning("Upsert falhou para " + player.getName() + ": " + e.getMessage());
        }
    }
}
