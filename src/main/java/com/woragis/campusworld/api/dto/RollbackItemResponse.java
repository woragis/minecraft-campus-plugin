package com.woragis.campusworld.api.dto;

public class RollbackItemResponse {
    private String action;
    private String blockType;
    private int blockX;
    private int blockY;
    private int blockZ;
    private String world;

    public String getAction() {
        return action;
    }

    public String getBlockType() {
        return blockType;
    }

    public int getBlockX() {
        return blockX;
    }

    public int getBlockY() {
        return blockY;
    }

    public int getBlockZ() {
        return blockZ;
    }

    public String getWorld() {
        return world;
    }
}
