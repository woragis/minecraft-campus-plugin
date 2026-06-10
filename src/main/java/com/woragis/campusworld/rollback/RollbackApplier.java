package com.woragis.campusworld.rollback;

import com.woragis.campusworld.CampusWorldPlugin;
import com.woragis.campusworld.api.ApiException;
import com.woragis.campusworld.api.CampusWorldApiClient;
import com.woragis.campusworld.api.dto.RollbackItemResponse;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class RollbackApplier {

    private final CampusWorldPlugin plugin;
    private final CampusWorldApiClient api;

    public RollbackApplier(CampusWorldPlugin plugin, CampusWorldApiClient api) {
        this.plugin = plugin;
        this.api = api;
    }

    public void createAndApply(CommandSender sender, Player actor, UUID targetUuid, String worldName, int windowMinutes) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                var created = api.createRollback(targetUuid, actor.getUniqueId(), worldName, windowMinutes);
                String rollbackId = created.getRollback().getId();
                int itemCount = created.getRollback().getItemCount();
                List<RollbackItemResponse> items = api.listRollbackItems(rollbackId);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    int applied = applyItems(items);
                    plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                        try {
                            api.completeRollback(rollbackId, applied);
                            String msg = "Rollback aplicado: " + applied + "/" + itemCount + " blocos.";
                            Bukkit.getScheduler().runTask(plugin, () -> sender.sendMessage(msg));
                        } catch (ApiException e) {
                            Bukkit.getScheduler().runTask(plugin, () -> sender.sendMessage("Falha ao finalizar rollback."));
                        }
                    });
                });
            } catch (ApiException e) {
                plugin.getLogger().warning("Rollback failed: " + e.getMessage());
                Bukkit.getScheduler().runTask(plugin, () -> sender.sendMessage("Rollback falhou: " + e.getMessage()));
            }
        });
    }

    private int applyItems(List<RollbackItemResponse> items) {
        int applied = 0;
        for (RollbackItemResponse item : items) {
            World world = Bukkit.getWorld(item.getWorld());
            if (world == null) {
                continue;
            }
            Block block = world.getBlockAt(item.getBlockX(), item.getBlockY(), item.getBlockZ());
            if ("restore".equalsIgnoreCase(item.getAction())) {
                Material material = Material.matchMaterial(item.getBlockType());
                if (material != null && block.getType().isAir()) {
                    block.setType(material, false);
                    applied++;
                }
            } else if ("remove".equalsIgnoreCase(item.getAction())) {
                if (!block.getType().isAir()) {
                    block.setType(Material.AIR, false);
                    applied++;
                }
            }
        }
        return applied;
    }
}
