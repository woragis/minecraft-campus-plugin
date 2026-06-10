package com.woragis.campusworld.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public record PluginConfig(
        String apiBaseUrl,
        String pluginKey,
        int timeoutMs,
        String serverSlug,
        String kickNotInvited,
        String kickBanned,
        String kickProbation,
        String kickApiError,
        String inviteCreated,
        String inviteFailed,
        String inviteUsage
) {
    public static PluginConfig load(FileConfiguration config) {
        return new PluginConfig(
                trimTrailingSlash(config.getString("api.base-url", "http://127.0.0.1:8080")),
                config.getString("api.plugin-key", "dev-plugin-key"),
                config.getInt("api.timeout-ms", 5000),
                config.getString("server.slug", "vanilla"),
                color(config.getString("messages.kick-not-invited", "&cVocê precisa de um convite.")),
                color(config.getString("messages.kick-banned", "&cVocê está banido.")),
                color(config.getString("messages.kick-probation", "&ePeríodo de avaliação.")),
                color(config.getString("messages.kick-api-error", "&cCampusWorld indisponível.")),
                color(config.getString("messages.invite-created", "&aConvite criado: &e{code}")),
                color(config.getString("messages.invite-failed", "&cFalha ao criar convite.")),
                color(config.getString("messages.invite-usage", "&eUso: /invite <jogador>"))
        );
    }

    public static PluginConfig forTest(String apiBaseUrl, String pluginKey) {
        return new PluginConfig(
                trimTrailingSlash(apiBaseUrl),
                pluginKey,
                3000,
                "vanilla",
                "not invited",
                "banned",
                "probation",
                "api error",
                "invite {code}",
                "invite failed",
                "usage"
        );
    }

    public String formatInviteCreated(String player, String code) {
        return inviteCreated.replace("{player}", player).replace("{code}", code);
    }

    private static String trimTrailingSlash(String url) {
        if (url == null || url.isBlank()) {
            return "http://127.0.0.1:8080";
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private static String color(String message) {
        if (message == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
