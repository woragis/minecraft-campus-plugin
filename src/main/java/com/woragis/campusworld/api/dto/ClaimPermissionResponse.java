package com.woragis.campusworld.api.dto;

public class ClaimPermissionResponse {
    private boolean allowed;
    private String reason;
    private String claimId;

    public boolean isAllowed() {
        return allowed;
    }

    public String getReason() {
        return reason;
    }

    public String getClaimId() {
        return claimId;
    }
}
