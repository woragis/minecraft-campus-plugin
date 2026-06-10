package com.woragis.campusworld.commands;

import com.woragis.campusworld.CampusWorldPlugin;
import com.woragis.campusworld.api.ApiException;
import com.woragis.campusworld.api.CampusWorldApiClient;
import com.woragis.campusworld.api.dto.CityResponse;
import com.woragis.campusworld.config.PluginConfig;
import com.woragis.campusworld.session.PlayerSessionCache;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CityCommand implements CommandExecutor, TabCompleter {

    private final CampusWorldPlugin plugin;
    private final CampusWorldApiClient api;
    private final PluginConfig config;

    public CityCommand(CampusWorldPlugin plugin, CampusWorldApiClient api, PluginConfig config) {
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
        if (args.length < 2 || !"create".equalsIgnoreCase(args[0])) {
            player.sendMessage(config.cityUsage());
            return true;
        }
        if (PlayerSessionCache.get().isProbation(player.getUniqueId())) {
            player.sendMessage(config.cityProbationDenied());
            return true;
        }
        String name = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> createCity(player, name));
        return true;
    }

    private void createCity(Player player, String name) {
        try {
            CityResponse city = api.createCity(
                    player.getUniqueId(),
                    name,
                    player.getWorld().getName(),
                    player.getLocation().getBlockX(),
                    player.getLocation().getBlockZ()
            );
            String msg = config.formatCityCreated(city.getName(), city.getSlug(), city.getId());
            Bukkit.getScheduler().runTask(plugin, () -> player.sendMessage(msg));
        } catch (ApiException e) {
            plugin.getLogger().warning("City create failed: " + e.getMessage());
            Bukkit.getScheduler().runTask(plugin, () -> player.sendMessage(config.cityFailed()));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("create");
        }
        return Collections.emptyList();
    }
}
