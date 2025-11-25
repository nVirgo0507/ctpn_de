package org.ctpn.chungtayphongngua.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Consultant Availability Exception Entity
 * Author: FullStack-Developer-AI (Cursor)
 * Created: [Current session date]
 * Version: 1.0
 * Context: Maps to ctpn_consultation.consultant_availability_exceptions table per Document database schema
 */
@Entity
@Table(name = "consultant_availability_exceptions", schema = "ctpn_consultation")
public class ConsultantAvailabilityException {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exception_id")
    private Long exceptionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "exception_date", nullable = false)
    private LocalDate exceptionDate;
    
    @Column(name = "exception_type", nullable = false, length = 20)
    private String exceptionType; // 'unavailable', 'custom_hours', 'busy'
    
    @Column(name = "start_time")
    private LocalTime startTime; // NULL for full day unavailable
    
    @Column(name = "end_time")
    private LocalTime endTime;   // NULL for full day unavailable
    
    @Column(name = "reason", length = 255)
    private String reason; // 'holiday', 'personal', 'meeting', etc.
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
    
    // Constructors
    public ConsultantAvailabilityException() {
        this.createdAt = LocalDateTime.now();
    }
    
    public ConsultantAvailabilityException(User user, LocalDate exceptionDate, String exceptionType) {
        this();
        this.user = user;
        this.exceptionDate = exceptionDate;
        this.exceptionType = exceptionType;
    }
    
    // Getters and Setters
    public Long getExceptionId() {
        return exceptionId;
    }
    
    public void setExceptionId(Long exceptionId) {
        this.exceptionId = exceptionId;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public LocalDate getExceptionDate() {
        return exceptionDate;
    }
    
    public void setExceptionDate(LocalDate exceptionDate) {
        this.exceptionDate = exceptionDate;
    }
    
    public String getExceptionType() {
        return exceptionType;
    }
    
    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }
    
    public LocalTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Boolean getIsDeleted() {
        return isDeleted;
    }
    
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "ConsultantAvailabilityException{" +
                "exceptionId=" + exceptionId +
                ", exceptionDate=" + exceptionDate +
                ", exceptionType='" + exceptionType + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", reason='" + reason + '\'' +
                ", isDeleted=" + isDeleted +
                '}';
    }
} 