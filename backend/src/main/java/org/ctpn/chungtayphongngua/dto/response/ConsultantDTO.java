package org.ctpn.chungtayphongngua.dto.response;

import java.util.List;

public class ConsultantDTO {
    private Long id;
    private String fullName;
    private String email;
    private UserProfileDTO userProfile;

    public ConsultantDTO(Long id, String fullName, String email, UserProfileDTO userProfile) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.userProfile = userProfile;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserProfileDTO getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfileDTO userProfile) {
        this.userProfile = userProfile;
    }
}
