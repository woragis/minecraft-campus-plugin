package com.woragis.campusworld.api.dto;

public class HudResponse {

    private String username;
    private String status;
    private String affiliationType;
    private String universitySlug;
    private String universityName;
    private String facultySlug;
    private String facultyName;
    private String facultyAbbr;
    private String courseSlug;
    private String courseName;
    private String courseAbbr;
    private String courseHex;
    private Boolean isGuest;
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

    public String getAffiliationType() {
        return affiliationType;
    }

    public String getUniversitySlug() {
        return universitySlug;
    }

    public String getUniversityName() {
        return universityName;
    }

    public String getFacultySlug() {
        return facultySlug;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public String getFacultyAbbr() {
        return facultyAbbr;
    }

    public String getCourseSlug() {
        return courseSlug;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCourseAbbr() {
        return courseAbbr;
    }

    public String getCourseColorHex() {
        return courseHex;
    }

    public boolean isGuest() {
        if (Boolean.TRUE.equals(isGuest)) {
            return true;
        }
        return "guest".equalsIgnoreCase(affiliationType);
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
