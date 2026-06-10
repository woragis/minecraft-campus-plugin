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
        String inviteUsage,
        String campusUsage,
        String campusStatusOk,
        String campusStatusError,
        String inviteProbationDenied,
        String guildUsage,
        String guildCreated,
        String guildJoined,
        String guildLeft,
        String guildFailed,
        String guildProbationDenied
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
                color(config.getString("messages.invite-usage", "&eUso: /invite <jogador>")),
                color(config.getString("messages.campus-usage", "&eUso: /campus status")),
                color(config.getString("messages.campus-status-ok", "&aAPI online (&f{url}&a) | servidor &f{slug}")),
                color(config.getString("messages.campus-status-error", "&cAPI offline (&f{url}&c)")),
                color(config.getString("messages.invite-probation-denied", "&cVocê está em probation e não pode convidar.")),
                color(config.getString("messages.guild-usage", "&eUso: /guild create <nome> | join <id> | leave <id>")),
                color(config.getString("messages.guild-created", "&aGuilda &f{name}&a criada (&f{slug}&a). ID: &e{id}")),
                color(config.getString("messages.guild-joined", "&aVocê entrou na guilda.")),
                color(config.getString("messages.guild-left", "&aVocê saiu da guilda.")),
                color(config.getString("messages.guild-failed", "&cOperação de guilda falhou.")),
                color(config.getString("messages.guild-probation-denied", "&cVocê está em probation e não pode usar guildas."))
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
                "usage",
                "campus usage",
                "ok {url} {slug}",
                "error {url}",
                "probation denied",
                "guild usage",
                "guild created",
                "joined",
                "left",
                "failed",
                "probation denied"
        );
    }

    public String formatGuildCreated(String name, String slug, String id) {
        return guildCreated.replace("{name}", name).replace("{slug}", slug).replace("{id}", id);
    }

    public String campusStatusOk(String apiUrl, String slug) {
        return campusStatusOk.replace("{url}", apiUrl).replace("{slug}", slug);
    }

    public String campusStatusError(String apiUrl) {
        return campusStatusError.replace("{url}", apiUrl);
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
