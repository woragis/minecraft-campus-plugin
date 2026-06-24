package com.woragis.campusworld.commands;

import com.woragis.campusworld.CampusWorldPlugin;
import com.woragis.campusworld.api.ApiException;
import com.woragis.campusworld.api.CampusWorldApiClient;
import com.woragis.campusworld.api.dto.InviteResponse;
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

public class InviteCommand implements CommandExecutor, TabCompleter {

    private static final String AFFILIATION_GUEST = "guest";
    private static final String AFFILIATION_STUDENT = "student";

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
        if (args.length < 1 || args.length > 2) {
            player.sendMessage(config.inviteUsage());
            return true;
        }

        String affiliationType = AFFILIATION_STUDENT;
        String targetUsername;

        if (args.length == 2 && "guest".equalsIgnoreCase(args[0])) {
            affiliationType = AFFILIATION_GUEST;
            targetUsername = args[1].trim();
        } else if (args.length == 1) {
            targetUsername = args[0].trim();
        } else {
            player.sendMessage(config.inviteUsage());
            return true;
        }

        if (targetUsername.isEmpty()) {
            player.sendMessage(config.inviteUsage());
            return true;
        }
        if (PlayerSessionCache.get().isProbation(player.getUniqueId())) {
            player.sendMessage(config.inviteProbationDenied());
            return true;
        }

        String finalAffiliationType = affiliationType;
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> handleInvite(player, targetUsername, finalAffiliationType));
        return true;
    }

    private void handleInvite(Player sponsor, String targetUsername, String affiliationType) {
        try {
            InviteResponse invite = api.createInvite(sponsor.getUniqueId(), targetUsername, affiliationType);
            String message = config.formatInviteCreated(targetUsername, invite.getCode());
            Bukkit.getScheduler().runTask(plugin, () -> sponsor.sendMessage(message));
        } catch (ApiException e) {
            plugin.getLogger().warning("Invite falhou (" + sponsor.getName() + " -> " + targetUsername + "): " + e.getMessage());
            Bukkit.getScheduler().runTask(plugin, () -> sponsor.sendMessage(config.inviteFailed()));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase(Locale.ROOT);
            List<String> matches = new ArrayList<>();
            if ("guest".startsWith(prefix)) {
                matches.add("guest");
            }
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.getName().toLowerCase(Locale.ROOT).startsWith(prefix)) {
                    matches.add(online.getName());
                }
            }
            return matches;
        }
        if (args.length == 2 && "guest".equalsIgnoreCase(args[0])) {
            String prefix = args[1].toLowerCase(Locale.ROOT);
            List<String> matches = new ArrayList<>();
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.getName().toLowerCase(Locale.ROOT).startsWith(prefix)) {
                    matches.add(online.getName());
                }
            }
            return matches;
        }
        return Collections.emptyList();
    }
}
