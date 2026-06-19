package com.woragis.campusworld.hud;

import com.woragis.campusworld.CampusWorldPlugin;
import com.woragis.campusworld.api.ApiException;
import com.woragis.campusworld.api.CampusWorldApiClient;
import com.woragis.campusworld.api.dto.HudResponse;
import com.woragis.campusworld.config.PluginConfig;
import com.woragis.campusworld.session.PlayerSessionCache;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionBarTask extends BukkitRunnable {

    private final CampusWorldPlugin plugin;
    private final CampusWorldApiClient api;
    private final PluginConfig config;

    public ActionBarTask(CampusWorldPlugin plugin, CampusWorldApiClient api, PluginConfig config) {
        this.plugin = plugin;
        this.api = api;
        this.config = config;
    }

    public void start() {
        long interval = Math.max(40L, config.hudIntervalTicks());
        runTaskTimer(plugin, interval, interval);
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            String campusPlayerId = PlayerSessionCache.get().campusPlayerId(player.getUniqueId());
            if (campusPlayerId == null) {
                continue;
            }
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> refreshHud(player, campusPlayerId));
        }
    }

    private void refreshHud(Player player, String campusPlayerId) {
        try {
            HudResponse hud = api.fetchHud(campusPlayerId);
            if (hud == null) {
                return;
            }
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (!player.isOnline()) {
                    return;
                }
                player.sendActionBar(formatActionBar(hud));
                if (config.hudTabPrefixEnabled()) {
                    player.playerListName(formatTabName(player, hud));
                }
            });
        } catch (ApiException e) {
            plugin.getLogger().fine("HUD fetch falhou para " + player.getName() + ": " + e.getMessage());
        }
    }

    static Component formatActionBar(HudResponse hud) {
        Component line = Component.text(hud.getUsername(), NamedTextColor.WHITE);
        if (hud.getStatus() != null && !hud.getStatus().isBlank()) {
            line = line.append(Component.text(" · ", NamedTextColor.DARK_GRAY))
                    .append(Component.text(hud.getStatus(), NamedTextColor.GRAY));
        }
        if (hud.getGuildName() != null && !hud.getGuildName().isBlank()) {
            line = line.append(Component.text(" · ", NamedTextColor.DARK_GRAY))
                    .append(Component.text(hud.getGuildName(), NamedTextColor.AQUA));
            if (hud.getGuildOnlineCount() > 0) {
                line = line.append(Component.text(" (" + hud.getGuildOnlineCount() + " online)", NamedTextColor.DARK_AQUA));
            }
        }
        return line;
    }

    static Component formatTabName(Player player, HudResponse hud) {
        if (hud.getGuildName() == null || hud.getGuildName().isBlank()) {
            return Component.text(player.getName(), NamedTextColor.WHITE);
        }
        return Component.text("[" + hud.getGuildName() + "] ", NamedTextColor.GRAY)
                .append(Component.text(player.getName(), NamedTextColor.WHITE));
    }
}
