package com.woragis.campusworld.api.dto;

public class PlayerResponse {

    private String id;
    private String minecraftUuid;
    private String username;
    private String status;

    public String getId() {
        return id;
    }

    public String getMinecraftUuid() {
        return minecraftUuid;
    }

    public String getUsername() {
        return username;
    }

    public String getStatus() {
        return status;
    }
}
