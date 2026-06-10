package com.woragis.campusworld.commands;

import com.woragis.campusworld.api.CampusWorldApiClient;
import com.woragis.campusworld.config.PluginConfig;
import com.woragis.campusworld.rollback.RollbackApplier;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
public class CampusCommand implements CommandExecutor, TabCompleter {

    private final CampusWorldApiClient api;
    private final PluginConfig config;
    private final RollbackApplier rollbackApplier;

    public CampusCommand(CampusWorldApiClient api, PluginConfig config, RollbackApplier rollbackApplier) {
        this.api = api;
        this.config = config;
        this.rollbackApplier = rollbackApplier;
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
        if ("rollback".equalsIgnoreCase(args[0])) {
            if (!sender.hasPermission("campusworld.admin")) {
                sender.sendMessage(config.rollbackDenied());
                return true;
            }
            if (!(sender instanceof Player actor)) {
                sender.sendMessage("Rollback só pode ser executado in-game.");
                return true;
            }
            if (args.length < 3) {
                sender.sendMessage(config.rollbackUsage());
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage(config.rollbackPlayerNotFound());
                return true;
            }
            int minutes;
            try {
                minutes = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(config.rollbackUsage());
                return true;
            }
            if (minutes <= 0 || minutes > 1440) {
                sender.sendMessage(config.rollbackUsage());
                return true;
            }
            rollbackApplier.createAndApply(sender, actor, target.getUniqueId(), actor.getWorld().getName(), minutes);
            sender.sendMessage(config.rollbackStarted(target.getName(), minutes));
            return true;
        }
        sender.sendMessage(config.campusUsage());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("status", "rollback");
        }
        if (args.length == 2 && "rollback".equalsIgnoreCase(args[0])) {
            return null;
        }
        return Collections.emptyList();
    }
}
