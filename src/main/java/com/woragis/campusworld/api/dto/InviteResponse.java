package com.woragis.campusworld.api.dto;

public class InviteResponse {

    private String id;
    private String code;
    private String targetUsername;
    private String status;

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    public String getStatus() {
        return status;
    }
}
