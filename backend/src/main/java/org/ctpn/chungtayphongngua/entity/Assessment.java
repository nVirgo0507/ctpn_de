package org.ctpn.chungtayphongngua.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Assessment Entity
 * Author: FullStack-Developer-AI (Cursor)
 * Created: [Current session date]
 * Version: 1.0
 * Context: Maps to ctpn_assessment.assessments table per Document database schema
 */
@Entity
@Table(name = "assessments", schema = "ctpn_assessment")
public class Assessment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assessment_id")
    private Long assessmentId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "type_id", nullable = false)
    private Integer typeId;
    
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "total_score", nullable = false)
    private Integer totalScore = 0;
    
    @Column(name = "risk_level", length = 20)
    private String riskLevel;
    
    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
    
    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AssessmentResult> results;
    
    // Constructors
    public Assessment() {
        this.createdAt = LocalDateTime.now();
        this.startedAt = LocalDateTime.now();
    }
    
    public Assessment(User user, Integer typeId) {
        this();
        this.user = user;
        this.typeId = typeId;
    }
    
    // Getters and Setters
    public Long getAssessmentId() {
        return assessmentId;
    }
    
    public void setAssessmentId(Long assessmentId) {
        this.assessmentId = assessmentId;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Integer getTypeId() {
        return typeId;
    }
    
    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }
    
    public LocalDateTime getStartedAt() {
        return startedAt;
    }
    
    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    public Integer getTotalScore() {
        return totalScore;
    }
    
    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }
    
    public String getRiskLevel() {
        return riskLevel;
    }
    
    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }
    
    public String getRecommendations() {
        return recommendations;
    }
    
    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
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
    
    public List<AssessmentResult> getResults() {
        return results;
    }
    
    public void setResults(List<AssessmentResult> results) {
        this.results = results;
    }
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.startedAt == null) {
            this.startedAt = LocalDateTime.now();
        }
    }
    
    @Override
    public String toString() {
        return "Assessment{" +
                "assessmentId=" + assessmentId +
                ", typeId=" + typeId +
                ", totalScore=" + totalScore +
                ", riskLevel='" + riskLevel + '\'' +
                ", completedAt=" + completedAt +
                ", isDeleted=" + isDeleted +
                '}';
    }
} 