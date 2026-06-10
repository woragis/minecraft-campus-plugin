package com.woragis.campusworld.listeners;

import com.woragis.campusworld.CampusWorldPlugin;
import com.woragis.campusworld.api.ApiException;
import com.woragis.campusworld.api.CampusWorldApiClient;
import com.woragis.campusworld.api.dto.WhitelistResponse;
import com.woragis.campusworld.config.PluginConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class WhitelistListener implements Listener {

    private final CampusWorldPlugin plugin;
    private final CampusWorldApiClient api;
    private final PluginConfig config;

    public WhitelistListener(CampusWorldPlugin plugin, CampusWorldApiClient api, PluginConfig config) {
        this.plugin = plugin;
        this.api = api;
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (event.getUniqueId() == null || event.getName() == null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, config.kickApiError());
            return;
        }

        try {
            WhitelistResponse result = api.checkWhitelist(event.getUniqueId(), event.getName());
            if (!result.isAllowed()) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, kickMessage(result.getReason()));
                return;
            }
            if ("probation".equalsIgnoreCase(result.getReason())) {
                plugin.getLogger().info("Jogador em probation entrando: " + event.getName());
            }
        } catch (ApiException e) {
            plugin.getLogger().warning("Whitelist falhou para " + event.getName() + ": " + e.getMessage());
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, config.kickApiError());
        }
    }

    private String kickMessage(String reason) {
        if (reason == null) {
            return config.kickNotInvited();
        }
        return switch (reason.toLowerCase()) {
            case "banned" -> config.kickBanned();
            case "not_invited" -> config.kickNotInvited();
            default -> config.kickNotInvited();
        };
    }
}
