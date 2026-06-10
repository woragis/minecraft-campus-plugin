package com.woragis.campusworld.audit;

import com.woragis.campusworld.CampusWorldPlugin;
import com.woragis.campusworld.api.ApiException;
import com.woragis.campusworld.api.CampusWorldApiClient;
import com.woragis.campusworld.api.dto.AuditEventPayload;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class AuditBatchBuffer {

    private static final int MAX_BATCH = 50;

    private final CampusWorldPlugin plugin;
    private final CampusWorldApiClient api;
    private final ConcurrentLinkedQueue<AuditEventPayload> queue = new ConcurrentLinkedQueue<>();

    public AuditBatchBuffer(CampusWorldPlugin plugin, CampusWorldApiClient api) {
        this.plugin = plugin;
        this.api = api;
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this::flush, 100L, 100L);
    }

    public void enqueue(AuditEventPayload event) {
        queue.add(event);
        if (queue.size() >= MAX_BATCH) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, this::flush);
        }
    }

    public void flush() {
        List<AuditEventPayload> batch = new ArrayList<>();
        AuditEventPayload item;
        while ((item = queue.poll()) != null && batch.size() < MAX_BATCH) {
            batch.add(item);
        }
        if (batch.isEmpty()) {
            return;
        }
        try {
            api.ingestAuditEvents(batch);
        } catch (ApiException e) {
            plugin.getLogger().warning("Audit ingest failed (" + batch.size() + " events): " + e.getMessage());
            queue.addAll(batch);
        }
    }

    public static String nowIso() {
        return Instant.now().toString();
    }
}
