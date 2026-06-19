package com.woragis.campusworld.api.dto;

public class HudResponse {

    private String username;
    private String status;
    private String guildId;
    private String guildName;
    private String guildSlug;
    private int guildOnlineCount;

    public String getUsername() {
        return username;
    }

    public String getStatus() {
        return status;
    }

    public String getGuildId() {
        return guildId;
    }

    public String getGuildName() {
        return guildName;
    }

    public String getGuildSlug() {
        return guildSlug;
    }

    public int getGuildOnlineCount() {
        return guildOnlineCount;
    }
}
