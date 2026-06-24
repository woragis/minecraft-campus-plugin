package com.woragis.campusworld;

import com.woragis.campusworld.api.CampusWorldApiClient;
import com.woragis.campusworld.commands.CampusCommand;
import com.woragis.campusworld.commands.CityCommand;
import com.woragis.campusworld.commands.ClaimCommand;
import com.woragis.campusworld.commands.GuildCommand;
import com.woragis.campusworld.commands.InviteCommand;
import com.woragis.campusworld.audit.AuditBatchBuffer;
import com.woragis.campusworld.config.PluginConfig;
import com.woragis.campusworld.listeners.AuditListener;
import com.woragis.campusworld.listeners.ChatPrefixListener;
import com.woragis.campusworld.listeners.ClaimProtectionListener;
import com.woragis.campusworld.rollback.RollbackApplier;
import com.woragis.campusworld.listeners.MobKillListener;
import com.woragis.campusworld.listeners.PlayerJoinListener;
import com.woragis.campusworld.listeners.PlayerQuitListener;
import com.woragis.campusworld.listeners.WhitelistListener;
import com.woragis.campusworld.hud.ActionBarTask;
import com.woragis.campusworld.presence.PresenceHeartbeatTask;
import org.bukkit.plugin.java.JavaPlugin;

public final class CampusWorldPlugin extends JavaPlugin {

    private PluginConfig pluginConfig;
    private CampusWorldApiClient apiClient;
    private AuditBatchBuffer auditBatchBuffer;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        pluginConfig = PluginConfig.load(getConfig());
        apiClient = new CampusWorldApiClient(pluginConfig);
        auditBatchBuffer = pluginConfig.auditEnabled() ? new AuditBatchBuffer(this, apiClient, pluginConfig) : null;
        RollbackApplier rollbackApplier = new RollbackApplier(this, apiClient);

        getServer().getPluginManager().registerEvents(new WhitelistListener(this, apiClient, pluginConfig), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this, apiClient, pluginConfig), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this, apiClient, pluginConfig), this);
        if (pluginConfig.presenceEnabled()) {
            new PresenceHeartbeatTask(this, apiClient, pluginConfig).start();
        }
        if (pluginConfig.statsEnabled()) {
            getServer().getPluginManager().registerEvents(new MobKillListener(pluginConfig), this);
        }
        if (pluginConfig.hudEnabled()) {
            new ActionBarTask(this, apiClient, pluginConfig).start();
            if (pluginConfig.hudChatPrefixEnabled()) {
                getServer().getPluginManager().registerEvents(new ChatPrefixListener(pluginConfig), this);
            }
        }
        if (pluginConfig.claimProtectionEnabled()) {
            getServer().getPluginManager().registerEvents(new ClaimProtectionListener(this, apiClient, pluginConfig), this);
        }
        if (pluginConfig.auditEnabled() && auditBatchBuffer != null) {
            getServer().getPluginManager().registerEvents(new AuditListener(apiClient, pluginConfig, auditBatchBuffer), this);
        }

        var inviteCommand = getCommand("invite");
        if (inviteCommand != null) {
            var executor = new InviteCommand(this, apiClient, pluginConfig);
            inviteCommand.setExecutor(executor);
            inviteCommand.setTabCompleter(executor);
        } else {
            getLogger().warning("Comando /invite não registrado — verifique plugin.yml.");
        }

        var campusCommand = getCommand("campus");
        if (campusCommand != null) {
            var campus = new CampusCommand(this, apiClient, pluginConfig, rollbackApplier);
            campusCommand.setExecutor(campus);
            campusCommand.setTabCompleter(campus);
        }

        var guildCommand = getCommand("guild");
        if (guildCommand != null) {
            var guild = new GuildCommand(this, apiClient, pluginConfig);
            guildCommand.setExecutor(guild);
            guildCommand.setTabCompleter(guild);
        }

        var claimCommand = getCommand("claim");
        if (claimCommand != null) {
            var claim = new ClaimCommand(this, apiClient, pluginConfig);
            claimCommand.setExecutor(claim);
            claimCommand.setTabCompleter(claim);
        }

        var cityCommand = getCommand("city");
        if (cityCommand != null) {
            var city = new CityCommand(this, apiClient, pluginConfig);
            cityCommand.setExecutor(city);
            cityCommand.setTabCompleter(city);
        }

        getLogger().info("CampusWorld ativo. API: " + pluginConfig.apiBaseUrl() + " | servidor: " + pluginConfig.serverSlug());
    }

    @Override
    public void onDisable() {
        if (auditBatchBuffer != null) {
            auditBatchBuffer.flush();
        }
        if (apiClient != null) {
            apiClient.close();
        }
    }

    public PluginConfig pluginConfig() {
        return pluginConfig;
    }
}
