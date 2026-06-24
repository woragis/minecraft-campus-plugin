package com.woragis.campusworld.hud;

import com.woragis.campusworld.CampusWorldPlugin;
import com.woragis.campusworld.api.ApiException;
import com.woragis.campusworld.api.CampusWorldApiClient;
import com.woragis.campusworld.api.dto.HudResponse;
import com.woragis.campusworld.config.PluginConfig;
import com.woragis.campusworld.session.PlayerSessionCache;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
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
                HudCache.get().put(player.getUniqueId(), hud);
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
        Component line = Component.empty();
        boolean hasContent = false;

        if (hud.isGuest()) {
            line = line.append(Component.text("[Visit] ", NamedTextColor.GOLD));
            hasContent = true;
        }

        String courseAbbr = hud.getCourseAbbr();
        if (courseAbbr != null && !courseAbbr.isBlank()) {
            if (hasContent) {
                line = line.append(separator());
            }
            line = line.append(Component.text(courseAbbr, parseHex(hud.getCourseColorHex(), NamedTextColor.YELLOW)));
            hasContent = true;
        }

        if (hud.getGuildName() != null && !hud.getGuildName().isBlank()) {
            if (hasContent) {
                line = line.append(separator());
            }
            line = line.append(Component.text(hud.getGuildName(), NamedTextColor.AQUA));
            if (hud.getGuildOnlineCount() > 0) {
                line = line.append(Component.text(" (" + hud.getGuildOnlineCount() + " online)", NamedTextColor.DARK_AQUA));
            }
            hasContent = true;
        }

        if (!hasContent) {
            line = Component.text(hud.getUsername(), NamedTextColor.WHITE);
            if (hud.getStatus() != null && !hud.getStatus().isBlank()) {
                line = line.append(separator()).append(Component.text(hud.getStatus(), NamedTextColor.GRAY));
            }
        }

        return line;
    }

    static Component formatTabName(Player player, HudResponse hud) {
        Component line = Component.empty();

        if (hud.isGuest()) {
            line = line.append(Component.text("[Visit] ", NamedTextColor.GOLD));
        }

        String courseAbbr = hud.getCourseAbbr();
        if (courseAbbr != null && !courseAbbr.isBlank()) {
            line = line.append(Component.text("[" + courseAbbr + "] ", parseHex(hud.getCourseColorHex(), NamedTextColor.YELLOW)));
        }

        if (hud.getGuildName() != null && !hud.getGuildName().isBlank()) {
            line = line.append(Component.text("[" + hud.getGuildName() + "] ", NamedTextColor.GRAY));
        }

        return line.append(Component.text(player.getName(), NamedTextColor.WHITE));
    }

    public static Component formatChatPrefix(HudResponse hud) {
        Component prefix = Component.empty();

        if (hud != null && hud.isGuest()) {
            prefix = prefix.append(Component.text("[Visitante] ", NamedTextColor.GOLD));
        }

        if (hud != null && hud.getGuildName() != null && !hud.getGuildName().isBlank()) {
            prefix = prefix.append(Component.text("[" + hud.getGuildName() + "] ", NamedTextColor.AQUA));
        }

        return prefix;
    }

    private static Component separator() {
        return Component.text(" · ", NamedTextColor.DARK_GRAY);
    }

    static TextColor parseHex(String hex, TextColor fallback) {
        if (hex == null || hex.isBlank()) {
            return fallback;
        }
        String normalized = hex.startsWith("#") ? hex : "#" + hex;
        TextColor parsed = TextColor.fromHexString(normalized);
        return parsed != null ? parsed : fallback;
    }
}
