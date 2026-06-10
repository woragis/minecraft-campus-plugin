package com.woragis.campusworld.commands;

import com.woragis.campusworld.CampusWorldPlugin;
import com.woragis.campusworld.api.ApiException;
import com.woragis.campusworld.api.CampusWorldApiClient;
import com.woragis.campusworld.api.dto.ClaimResponse;
import com.woragis.campusworld.config.PluginConfig;
import com.woragis.campusworld.session.ClaimCornerCache;
import com.woragis.campusworld.session.PlayerSessionCache;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ClaimCommand implements CommandExecutor, TabCompleter {

    private final CampusWorldPlugin plugin;
    private final CampusWorldApiClient api;
    private final PluginConfig config;

    public ClaimCommand(CampusWorldPlugin plugin, CampusWorldApiClient api, PluginConfig config) {
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
            player.sendMessage(config.claimUsage());
            return true;
        }
        String sub = args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "mark" -> markCorner(player);
            case "create" -> {
                if (PlayerSessionCache.get().isProbation(player.getUniqueId())) {
                    player.sendMessage(config.claimProbationDenied());
                    return true;
                }
                String zone = args.length >= 2 ? args[1].toLowerCase(Locale.ROOT) : "urban";
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> createClaim(player, zone));
            }
            case "delete" -> {
                if (args.length < 2) {
                    player.sendMessage(config.claimUsage());
                    return true;
                }
                String claimId = args[1];
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> deleteClaim(player, claimId));
            }
            case "clear" -> {
                ClaimCornerCache.get().clear(player.getUniqueId());
                player.sendMessage(config.claimCleared());
            }
            default -> player.sendMessage(config.claimUsage());
        }
        return true;
    }

    private void markCorner(Player player) {
        Location loc = player.getLocation();
        ClaimCornerCache cache = ClaimCornerCache.get();
        if (cache.getCorner1(player.getUniqueId()) == null) {
            cache.setCorner1(player.getUniqueId(), loc);
            player.sendMessage(config.formatClaimCorner1(loc.getBlockX(), loc.getBlockZ()));
            return;
        }
        if (cache.getCorner2(player.getUniqueId()) == null) {
            if (!loc.getWorld().equals(cache.getCorner1(player.getUniqueId()).getWorld())) {
                player.sendMessage(config.claimDifferentWorld());
                return;
            }
            cache.setCorner2(player.getUniqueId(), loc);
            player.sendMessage(config.formatClaimCorner2(loc.getBlockX(), loc.getBlockZ()));
            player.sendMessage(config.claimReady());
            return;
        }
        cache.clear(player.getUniqueId());
        cache.setCorner1(player.getUniqueId(), loc);
        player.sendMessage(config.formatClaimCorner1(loc.getBlockX(), loc.getBlockZ()));
    }

    private void createClaim(Player player, String zone) {
        ClaimCornerCache cache = ClaimCornerCache.get();
        Location c1 = cache.getCorner1(player.getUniqueId());
        Location c2 = cache.getCorner2(player.getUniqueId());
        if (c1 == null || c2 == null) {
            Bukkit.getScheduler().runTask(plugin, () -> player.sendMessage(config.claimNeedCorners()));
            return;
        }
        try {
            ClaimResponse claim = api.createClaim(
                    player.getUniqueId(),
                    c1.getWorld().getName(),
                    c1.getBlockX(),
                    c2.getBlockX(),
                    c1.getBlockZ(),
                    c2.getBlockZ(),
                    zone
            );
            cache.clear(player.getUniqueId());
            String msg = config.formatClaimCreated(claim.getId(), claim.getAreaBlocks(), claim.getZoneType());
            Bukkit.getScheduler().runTask(plugin, () -> player.sendMessage(msg));
        } catch (ApiException e) {
            plugin.getLogger().warning("Claim create failed: " + e.getMessage());
            Bukkit.getScheduler().runTask(plugin, () -> player.sendMessage(config.claimFailed()));
        }
    }

    private void deleteClaim(Player player, String claimId) {
        try {
            api.deleteClaim(claimId, player.getUniqueId());
            Bukkit.getScheduler().runTask(plugin, () -> player.sendMessage(config.claimDeleted()));
        } catch (ApiException e) {
            Bukkit.getScheduler().runTask(plugin, () -> player.sendMessage(config.claimFailed()));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("mark", "create", "delete", "clear");
        }
        if (args.length == 2 && "create".equalsIgnoreCase(args[0])) {
            return List.of("urban", "rural", "industrial", "historic");
        }
        return Collections.emptyList();
    }
}
