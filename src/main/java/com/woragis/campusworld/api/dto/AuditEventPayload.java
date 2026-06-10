package com.woragis.campusworld.api.dto;

public class AuditEventPayload {
    private String minecraftUuid;
    private String serverSlug;
    private String world;
    private String eventType;
    private Integer blockX;
    private Integer blockY;
    private Integer blockZ;
    private String blockType;
    private String claimId;
    private String occurredAt;

    public AuditEventPayload(
            String minecraftUuid,
            String serverSlug,
            String world,
            String eventType,
            Integer blockX,
            Integer blockY,
            Integer blockZ,
            String blockType,
            String claimId,
            String occurredAt
    ) {
        this.minecraftUuid = minecraftUuid;
        this.serverSlug = serverSlug;
        this.world = world;
        this.eventType = eventType;
        this.blockX = blockX;
        this.blockY = blockY;
        this.blockZ = blockZ;
        this.blockType = blockType;
        this.claimId = claimId;
        this.occurredAt = occurredAt;
    }
}
