package com.woragis.campusworld.listeners;

import com.woragis.campusworld.api.dto.HudResponse;
import com.woragis.campusworld.config.PluginConfig;
import com.woragis.campusworld.hud.ActionBarTask;
import com.woragis.campusworld.hud.HudCache;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ChatPrefixListener implements Listener {

    private final PluginConfig config;

    public ChatPrefixListener(PluginConfig config) {
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        if (!config.hudChatPrefixEnabled()) {
            return;
        }

        HudResponse hud = HudCache.get().get(event.getPlayer().getUniqueId());
        event.renderer((source, sourceDisplayName, message, viewer) -> {
            Component prefix = ActionBarTask.formatChatPrefix(hud);
            return prefix
                    .append(sourceDisplayName)
                    .append(Component.text(": ", NamedTextColor.DARK_GRAY))
                    .append(message);
        });
    }
}
