package com.woragis.campusworld.commands;

import com.woragis.campusworld.api.CampusWorldApiClient;
import com.woragis.campusworld.config.PluginConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CampusCommand implements CommandExecutor, TabCompleter {

    private final CampusWorldApiClient api;
    private final PluginConfig config;

    public CampusCommand(CampusWorldApiClient api, PluginConfig config) {
        this.api = api;
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(config.campusUsage());
            return true;
        }
        if ("status".equalsIgnoreCase(args[0])) {
            boolean healthy = api.isApiHealthy();
            if (healthy) {
                sender.sendMessage(config.campusStatusOk(config.apiBaseUrl(), config.serverSlug()));
            } else {
                sender.sendMessage(config.campusStatusError(config.apiBaseUrl()));
            }
            return true;
        }
        sender.sendMessage(config.campusUsage());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("status");
        }
        return Collections.emptyList();
    }
}
