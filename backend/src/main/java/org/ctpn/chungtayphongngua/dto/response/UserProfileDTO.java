package org.ctpn.chungtayphongngua.dto.response;

import java.util.List;

public class UserProfileDTO {
    private String bio;
    private List<String> specializations;
    private String qualifications;
    private int experienceYears;
    private double rating;

    public UserProfileDTO(String bio, List<String> specializations, String qualifications, int experienceYears, double rating) {
        this.bio = bio;
        this.specializations = specializations;
        this.qualifications = qualifications;
        this.experienceYears = experienceYears;
        this.rating = rating;
    }

    // Getters and setters
    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<String> getSpecializations() {
        return specializations;
    }

    public void setSpecializations(List<String> specializations) {
        this.specializations = specializations;
    }

    public String getQualifications() {
        return qualifications;
    }

    public void setQualifications(String qualifications) {
        this.qualifications = qualifications;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
