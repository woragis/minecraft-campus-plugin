package com.woragis.campusworld.api.dto;

public class WebLinkCodeResponse {

    private String code;
    private int expiresInSeconds;

    public String getCode() {
        return code;
    }

    public int getExpiresInSeconds() {
        return expiresInSeconds;
    }
}
