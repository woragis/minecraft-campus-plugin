package com.woragis.campusworld.api.dto;

public class WhitelistResponse {

    private boolean allowed;
    private String reason;

    public boolean isAllowed() {
        return allowed;
    }

    public String getReason() {
        return reason;
    }
}
