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
        String guildProbationDenied,
        String claimUsage,
        String claimCorner1,
        String claimCorner2,
        String claimReady,
        String claimNeedCorners,
        String claimCreated,
        String claimDeleted,
        String claimFailed,
        String claimCleared,
        String claimDifferentWorld,
        String claimProbationDenied,
        String claimDenied,
        String cityUsage,
        String cityCreated,
        String cityFailed,
        String cityProbationDenied,
        String rollbackUsage,
        String rollbackDenied,
        String rollbackPlayerNotFound,
        String rollbackStarted,
        boolean auditEnabled,
        boolean claimProtectionEnabled,
        boolean presenceEnabled,
        int presenceHeartbeatIntervalTicks,
        boolean statsEnabled,
        boolean hudEnabled,
        int hudIntervalTicks,
        boolean hudTabPrefixEnabled,
        int claimPermissionCacheTtlTicks,
        int auditBatchIntervalTicks
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
                color(config.getString("messages.campus-usage", "&eUso: /campus status | rollback <jogador> <minutos>")),
                color(config.getString("messages.campus-status-ok", "&aAPI online (&f{url}&a) | servidor &f{slug}")),
                color(config.getString("messages.campus-status-error", "&cAPI offline (&f{url}&c)")),
                color(config.getString("messages.invite-probation-denied", "&cVocê está em probation e não pode convidar.")),
                color(config.getString("messages.guild-usage", "&eUso: /guild create <nome> | join <id> | leave <id>")),
                color(config.getString("messages.guild-created", "&aGuilda &f{name}&a criada (&f{slug}&a). ID: &e{id}")),
                color(config.getString("messages.guild-joined", "&aVocê entrou na guilda.")),
                color(config.getString("messages.guild-left", "&aVocê saiu da guilda.")),
                color(config.getString("messages.guild-failed", "&cOperação de guilda falhou.")),
                color(config.getString("messages.guild-probation-denied", "&cVocê está em probation e não pode usar guildas.")),
                color(config.getString("messages.claim-usage", "&eUso: /claim mark | create [zona] | delete <id> | clear")),
                color(config.getString("messages.claim-corner1", "&aCanto 1 marcado: &f{x}&a, &f{z}")),
                color(config.getString("messages.claim-corner2", "&aCanto 2 marcado: &f{x}&a, &f{z}")),
                color(config.getString("messages.claim-ready", "&aÁrea definida. Use &f/claim create [zona]&a.")),
                color(config.getString("messages.claim-need-corners", "&cMarque dois cantos com &f/claim mark&c.")),
                color(config.getString("messages.claim-created", "&aClaim criado! ID: &e{id}&a | área: &f{area}&a | zona: &f{zone}")),
                color(config.getString("messages.claim-deleted", "&aClaim removido.")),
                color(config.getString("messages.claim-failed", "&cOperação de claim falhou.")),
                color(config.getString("messages.claim-cleared", "&eMarcações de claim limpas.")),
                color(config.getString("messages.claim-different-world", "&cOs dois cantos devem estar no mesmo mundo.")),
                color(config.getString("messages.claim-probation-denied", "&cVocê está em probation e não pode reivindicar terreno.")),
                color(config.getString("messages.claim-denied", "&cVocê não pode alterar blocos neste claim.")),
                color(config.getString("messages.city-usage", "&eUso: /city create <nome>")),
                color(config.getString("messages.city-created", "&aCidade &f{name}&a criada (&f{slug}&a). ID: &e{id}")),
                color(config.getString("messages.city-failed", "&cNão foi possível criar a cidade.")),
                color(config.getString("messages.city-probation-denied", "&cVocê está em probation e não pode criar cidades.")),
                color(config.getString("messages.rollback-usage", "&eUso: /campus rollback <jogador> <minutos>")),
                color(config.getString("messages.rollback-denied", "&cVocê não tem permissão para rollback.")),
                color(config.getString("messages.rollback-player-not-found", "&cJogador não encontrado.")),
                color(config.getString("messages.rollback-started", "&aRollback iniciado para &f{player}&a nos últimos &f{minutes}&a min.")),
                config.getBoolean("features.audit.enabled", true),
                config.getBoolean("features.claim-protection.enabled", true),
                config.getBoolean("features.presence.enabled", true),
                config.getInt("features.presence.heartbeat-interval-ticks", 1200),
                config.getBoolean("features.stats.enabled", true),
                config.getBoolean("features.hud.enabled", true),
                config.getInt("features.hud.interval-ticks", 200),
                config.getBoolean("features.hud.tab-prefix", true),
                config.getInt("features.claim-protection.permission-cache-ttl-ticks", 40),
                config.getInt("features.audit.batch-interval-ticks", 100)
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
                "probation denied",
                "claim usage",
                "corner1 {x} {z}",
                "corner2 {x} {z}",
                "ready",
                "need corners",
                "created {id} {area} {zone}",
                "deleted",
                "failed",
                "cleared",
                "different world",
                "probation denied",
                "denied",
                "city usage",
                "city created",
                "failed",
                "probation denied",
                "rollback usage",
                "denied",
                "not found",
                "started {player} {minutes}",
                true,
                true,
                true,
                1200,
                true,
                true,
                200,
                true,
                40,
                100
        );
    }

    public String rollbackStarted(String player, int minutes) {
        return rollbackStarted.replace("{player}", player).replace("{minutes}", String.valueOf(minutes));
    }

    public String formatClaimCorner1(int x, int z) {
        return claimCorner1.replace("{x}", String.valueOf(x)).replace("{z}", String.valueOf(z));
    }

    public String formatClaimCorner2(int x, int z) {
        return claimCorner2.replace("{x}", String.valueOf(x)).replace("{z}", String.valueOf(z));
    }

    public String formatClaimCreated(String id, int area, String zone) {
        return claimCreated.replace("{id}", id).replace("{area}", String.valueOf(area)).replace("{zone}", zone);
    }

    public String formatCityCreated(String name, String slug, String id) {
        return cityCreated.replace("{name}", name).replace("{slug}", slug).replace("{id}", id);
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
