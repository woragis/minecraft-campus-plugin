package com.woragis.campusworld.api.dto;

public class PlayerResponse {

    private String id;
    private String minecraftUuid;
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
}
