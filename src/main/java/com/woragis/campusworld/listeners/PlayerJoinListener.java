package com.woragis.campusworld.listeners;

import com.woragis.campusworld.CampusWorldPlugin;
import com.woragis.campusworld.api.ApiException;
import com.woragis.campusworld.api.CampusWorldApiClient;
import com.woragis.campusworld.api.dto.PlayerResponse;
import com.woragis.campusworld.config.PluginConfig;
import com.woragis.campusworld.session.PlayerSessionCache;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Duration;

public class PlayerJoinListener implements Listener {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

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
            if (response != null && response.getId() != null && response.getStatus() != null) {
                PlayerSessionCache.get().put(player.getUniqueId(), response.getId(), response.getStatus());
            }
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (player.isOnline() && response != null) {
                    showJoinTitle(player, response);
                }
            });
        } catch (ApiException e) {
            plugin.getLogger().warning("Upsert falhou para " + player.getName() + ": " + e.getMessage());
        }
    }

    private void showJoinTitle(Player player, PlayerResponse response) {
        Component title;
        Component subtitle;

        if (response.isGuest()) {
            title = legacy(config.joinTitleGuest());
            subtitle = legacy(config.joinSubtitleGuest());
        } else {
            title = legacy(config.formatJoinTitleStudent(
                    response.getUniversityName(),
                    response.getCourseAbbr(),
                    response.getCourseName()
            ));
            if ("probation".equalsIgnoreCase(response.getStatus())) {
                subtitle = legacy(config.joinSubtitleStudentProbation());
            } else {
                subtitle = legacy(config.formatJoinSubtitleStudentActive(
                        response.getCourseName(),
                        response.getCourseAbbr()
                ));
            }
        }

        player.showTitle(Title.title(
                title,
                subtitle,
                Title.Times.times(
                        Duration.ofMillis(500),
                        Duration.ofMillis(3500),
                        Duration.ofMillis(1000)
                )
        ));
    }

    private static Component legacy(String message) {
        if (message == null || message.isBlank()) {
            return Component.empty();
        }
        return LEGACY.deserialize(message);
    }
}
