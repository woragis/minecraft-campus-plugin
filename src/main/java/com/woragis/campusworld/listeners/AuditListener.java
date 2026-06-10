package com.woragis.campusworld.listeners;

import com.woragis.campusworld.api.ApiException;
import com.woragis.campusworld.api.CampusWorldApiClient;
import com.woragis.campusworld.api.dto.AuditEventPayload;
import com.woragis.campusworld.api.dto.ClaimPermissionResponse;
import com.woragis.campusworld.audit.AuditBatchBuffer;
import com.woragis.campusworld.config.PluginConfig;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class AuditListener implements Listener {

    private final CampusWorldApiClient api;
    private final PluginConfig config;
    private final AuditBatchBuffer buffer;

    public AuditListener(CampusWorldApiClient api, PluginConfig config, AuditBatchBuffer buffer) {
        this.api = api;
        this.config = config;
        this.buffer = buffer;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();
        recordBlockEvent(player, block, "block_place", block.getType().name());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material type = block.getType();
        recordBlockEvent(player, block, "block_break", type.name());
    }

    private void recordBlockEvent(Player player, Block block, String eventType, String blockType) {
        ClaimPermissionResponse permission;
        try {
            permission = api.checkClaimPermission(
                    player.getUniqueId(),
                    block.getWorld().getName(),
                    block.getX(),
                    block.getZ()
            );
        } catch (ApiException e) {
            return;
        }
        if (permission.getClaimId() == null || permission.getClaimId().isBlank()) {
            return;
        }
        buffer.enqueue(new AuditEventPayload(
                player.getUniqueId().toString(),
                config.serverSlug(),
                block.getWorld().getName(),
                eventType,
                block.getX(),
                block.getY(),
                block.getZ(),
                blockType,
                permission.getClaimId(),
                AuditBatchBuffer.nowIso()
        ));
    }
}
