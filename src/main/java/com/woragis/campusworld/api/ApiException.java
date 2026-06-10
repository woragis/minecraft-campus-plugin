package com.woragis.campusworld.api;

public class ApiException extends Exception {

    private final int statusCode;
    private final String errorCode;

    public ApiException(String message) {
        super(message);
        this.statusCode = 0;
        this.errorCode = null;
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 0;
        this.errorCode = null;
    }

    public ApiException(int statusCode, String errorCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    public int statusCode() {
        return statusCode;
    }

    public String errorCode() {
        return errorCode;
    }
}
