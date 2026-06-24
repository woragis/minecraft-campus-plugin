package com.woragis.campusworld.listeners;

import com.woragis.campusworld.CampusWorldPlugin;
import com.woragis.campusworld.api.ApiException;
import com.woragis.campusworld.api.CampusWorldApiClient;
import com.woragis.campusworld.config.PluginConfig;
import com.woragis.campusworld.hud.HudCache;
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
        Player player = event.getPlayer();
        HudCache.get().remove(player.getUniqueId());
        PlayerSessionCache.SessionEntry session = PlayerSessionCache.get().take(player.getUniqueId());
        if (session == null) {
            return;
        }
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> flushSession(player.getName(), session));
    }

    private void flushSession(String username, PlayerSessionCache.SessionEntry session) {
        if (config.statsEnabled()) {
            long sessionSeconds = PlayerSessionCache.get().sessionSeconds(session);
            long mobKills = session.mobKills().get();
            if (sessionSeconds > 0 || mobKills > 0) {
                try {
                    api.statsIngest(session.campusPlayerId(), config.serverSlug(), sessionSeconds, mobKills);
                } catch (ApiException e) {
                    plugin.getLogger().fine("Stats ingest falhou para " + username + ": " + e.getMessage());
                }
            }
        }
        if (config.presenceEnabled()) {
            try {
                api.presenceOffline(session.campusPlayerId(), config.serverSlug());
            } catch (ApiException e) {
                plugin.getLogger().fine("Presence offline falhou para " + username + ": " + e.getMessage());
            }
        }
    }
}
