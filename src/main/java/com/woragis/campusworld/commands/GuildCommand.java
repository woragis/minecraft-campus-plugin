package com.woragis.campusworld.commands;

import com.woragis.campusworld.CampusWorldPlugin;
import com.woragis.campusworld.api.ApiException;
import com.woragis.campusworld.api.CampusWorldApiClient;
import com.woragis.campusworld.api.dto.GuildResponse;
import com.woragis.campusworld.config.PluginConfig;
import com.woragis.campusworld.session.PlayerSessionCache;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class GuildCommand implements CommandExecutor, TabCompleter {

    private final CampusWorldPlugin plugin;
    private final CampusWorldApiClient api;
    private final PluginConfig config;

    public GuildCommand(CampusWorldPlugin plugin, CampusWorldApiClient api, PluginConfig config) {
        this.plugin = plugin;
        this.api = api;
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Este comando só pode ser usado in-game.");
            return true;
        }
        if (args.length < 1) {
            player.sendMessage(config.guildUsage());
            return true;
        }
        String sub = args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "create" -> {
                if (args.length < 2) {
                    player.sendMessage(config.guildUsage());
                    return true;
                }
                if (PlayerSessionCache.get().isProbation(player.getUniqueId())) {
                    player.sendMessage(config.guildProbationDenied());
                    return true;
                }
                String name = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> createGuild(player, name));
            }
            case "join" -> {
                if (args.length < 2) {
                    player.sendMessage(config.guildUsage());
                    return true;
                }
                if (PlayerSessionCache.get().isProbation(player.getUniqueId())) {
                    player.sendMessage(config.guildProbationDenied());
                    return true;
                }
                String guildId = args[1];
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> joinGuild(player, guildId));
            }
            case "leave" -> {
                if (args.length < 2) {
                    player.sendMessage(config.guildUsage());
                    return true;
                }
                String guildId = args[1];
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> leaveGuild(player, guildId));
            }
            default -> player.sendMessage(config.guildUsage());
        }
        return true;
    }

    private void createGuild(Player player, String name) {
        try {
            GuildResponse guild = api.createGuild(player.getUniqueId(), name);
            String msg = config.formatGuildCreated(guild.getName(), guild.getSlug(), guild.getId());
            Bukkit.getScheduler().runTask(plugin, () -> player.sendMessage(msg));
        } catch (ApiException e) {
            plugin.getLogger().warning("Guild create failed: " + e.getMessage());
            Bukkit.getScheduler().runTask(plugin, () -> player.sendMessage(config.guildFailed()));
        }
    }

    private void joinGuild(Player player, String guildId) {
        try {
            api.joinGuild(guildId, player.getUniqueId());
            Bukkit.getScheduler().runTask(plugin, () -> player.sendMessage(config.guildJoined()));
        } catch (ApiException e) {
            Bukkit.getScheduler().runTask(plugin, () -> player.sendMessage(config.guildFailed()));
        }
    }

    private void leaveGuild(Player player, String guildId) {
        try {
            api.leaveGuild(guildId, player.getUniqueId());
            Bukkit.getScheduler().runTask(plugin, () -> player.sendMessage(config.guildLeft()));
        } catch (ApiException e) {
            Bukkit.getScheduler().runTask(plugin, () -> player.sendMessage(config.guildFailed()));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("create", "join", "leave");
        }
        return Collections.emptyList();
    }
}
