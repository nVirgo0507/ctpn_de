package org.ctpn.chungtayphongngua.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite Key class for UserRole entity
 * Fixes Hibernate composite-id warnings for proper equals/hashCode implementation
 */
public class UserRoleId implements Serializable {
    
    private Long user;
    private Long role;
    
    // Constructors
    public UserRoleId() {}
    
    public UserRoleId(Long user, Long role) {
        this.user = user;
        this.role = role;
    }
    
    // Getters and Setters
    public Long getUser() {
        return user;
    }
    
    public void setUser(Long user) {
        this.user = user;
    }
    
    public Long getRole() {
        return role;
    }
    
    public void setRole(Long role) {
        this.role = role;
    }
    
    // equals and hashCode implementation
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserRoleId that = (UserRoleId) obj;
        return Objects.equals(user, that.user) && Objects.equals(role, that.role);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(user, role);
    }
} 