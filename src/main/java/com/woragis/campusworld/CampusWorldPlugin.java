package com.woragis.campusworld;

import com.woragis.campusworld.api.CampusWorldApiClient;
import com.woragis.campusworld.commands.InviteCommand;
import com.woragis.campusworld.config.PluginConfig;
import com.woragis.campusworld.listeners.PlayerJoinListener;
import com.woragis.campusworld.listeners.WhitelistListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class CampusWorldPlugin extends JavaPlugin {

    private PluginConfig pluginConfig;
    private CampusWorldApiClient apiClient;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        pluginConfig = PluginConfig.load(getConfig());
        apiClient = new CampusWorldApiClient(pluginConfig);

        getServer().getPluginManager().registerEvents(new WhitelistListener(this, apiClient, pluginConfig), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this, apiClient, pluginConfig), this);

        var inviteCommand = getCommand("invite");
        if (inviteCommand != null) {
            var executor = new InviteCommand(this, apiClient, pluginConfig);
            inviteCommand.setExecutor(executor);
            inviteCommand.setTabCompleter(executor);
        } else {
            getLogger().warning("Comando /invite não registrado — verifique plugin.yml.");
        }

        getLogger().info("CampusWorld ativo. API: " + pluginConfig.apiBaseUrl() + " | servidor: " + pluginConfig.serverSlug());
    }

    @Override
    public void onDisable() {
        if (apiClient != null) {
            apiClient.close();
        }
    }

    public PluginConfig pluginConfig() {
        return pluginConfig;
    }
}
