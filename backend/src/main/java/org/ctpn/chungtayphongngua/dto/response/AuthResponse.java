package org.ctpn.chungtayphongngua.dto.response;

import java.time.LocalDateTime;

/**
 * Authentication Response DTO per Document security requirements
 * Returns JWT token and user information after successful authentication
 */
public class AuthResponse {
    
    private String token;
    private String tokenType = "Bearer";
    private String email;
    private String fullName;
    private String role;
    private LocalDateTime expiresAt;
    private boolean isVerified;
    
    // Constructors
    public AuthResponse() {}
    
    public AuthResponse(String token, String email, String fullName, String role, LocalDateTime expiresAt, boolean isVerified) {
        this.token = token;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.expiresAt = expiresAt;
        this.isVerified = isVerified;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public boolean isVerified() {
        return isVerified;
    }
    
    public void setVerified(boolean verified) {
        isVerified = verified;
    }
} 