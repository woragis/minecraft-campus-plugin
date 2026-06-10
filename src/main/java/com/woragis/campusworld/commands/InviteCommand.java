package com.woragis.campusworld.commands;

import com.woragis.campusworld.CampusWorldPlugin;
import com.woragis.campusworld.api.ApiException;
import com.woragis.campusworld.api.CampusWorldApiClient;
import com.woragis.campusworld.api.dto.InviteResponse;
import com.woragis.campusworld.config.PluginConfig;
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

public class InviteCommand implements CommandExecutor, TabCompleter {

    private final CampusWorldPlugin plugin;
    private final CampusWorldApiClient api;
    private final PluginConfig config;

    public InviteCommand(CampusWorldPlugin plugin, CampusWorldApiClient api, PluginConfig config) {
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
        if (args.length != 1) {
            player.sendMessage(config.inviteUsage());
            return true;
        }

        String targetUsername = args[0].trim();
        if (targetUsername.isEmpty()) {
            player.sendMessage(config.inviteUsage());
            return true;
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> handleInvite(player, targetUsername));
        return true;
    }

    private void handleInvite(Player sponsor, String targetUsername) {
        try {
            InviteResponse invite = api.createInvite(sponsor.getUniqueId(), targetUsername);
            String message = config.formatInviteCreated(targetUsername, invite.getCode());
            Bukkit.getScheduler().runTask(plugin, () -> sponsor.sendMessage(message));
        } catch (ApiException e) {
            plugin.getLogger().warning("Invite falhou (" + sponsor.getName() + " -> " + targetUsername + "): " + e.getMessage());
            Bukkit.getScheduler().runTask(plugin, () -> sponsor.sendMessage(config.inviteFailed()));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 1) {
            return Collections.emptyList();
        }
        String prefix = args[0].toLowerCase(Locale.ROOT);
        List<String> matches = new ArrayList<>();
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.getName().toLowerCase(Locale.ROOT).startsWith(prefix)) {
                matches.add(online.getName());
            }
        }
        return matches;
    }
}
