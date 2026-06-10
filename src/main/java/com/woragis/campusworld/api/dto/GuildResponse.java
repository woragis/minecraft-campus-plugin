package com.woragis.campusworld.api.dto;

public class GuildResponse {

    private String id;
    private String slug;
    private String name;
    private int trustScore;
    private int memberCount;

    public String getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getName() {
        return name;
    }

    public int getTrustScore() {
        return trustScore;
    }

    public int getMemberCount() {
        return memberCount;
    }
}
