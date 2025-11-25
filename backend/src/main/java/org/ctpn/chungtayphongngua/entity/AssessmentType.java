package org.ctpn.chungtayphongngua.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Where;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Assessment Type Entity
 * Author: FullStack-Developer-AI (Cursor)
 * Created: [Current session date]
 * Version: 1.0
 * Context: Maps to ctpn_assessment.assessment_types table per Document database schema
 */
@Entity
@Table(name = "assessment_types", schema = "ctpn_assessment")
@Where(clause = "is_deleted = false")
public class AssessmentType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private Long typeId;
    
    @Column(name = "type_name", nullable = false, length = 50)
    private String typeName;
    
    @Column(name = "age_group", nullable = false, length = 20)
    private String ageGroup;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "scoring_method", length = 50)
    private String scoringMethod;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "questions_json", columnDefinition = "jsonb", nullable = false)
    private String questionsJson; // Store as JSON string, will be parsed in service
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
    
    // Constructors
    public AssessmentType() {
        this.createdAt = LocalDateTime.now();
    }
    
    public AssessmentType(String typeName, String ageGroup, String description, String scoringMethod, String questionsJson) {
        this();
        this.typeName = typeName;
        this.ageGroup = ageGroup;
        this.description = description;
        this.scoringMethod = scoringMethod;
        this.questionsJson = questionsJson;
    }
    
    // Getters and Setters
    public Long getTypeId() {
        return typeId;
    }
    
    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }
    
    public String getTypeName() {
        return typeName;
    }
    
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    
    public String getAgeGroup() {
        return ageGroup;
    }
    
    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getScoringMethod() {
        return scoringMethod;
    }
    
    public void setScoringMethod(String scoringMethod) {
        this.scoringMethod = scoringMethod;
    }
    
    public String getQuestionsJson() {
        return questionsJson;
    }
    
    public void setQuestionsJson(String questionsJson) {
        this.questionsJson = questionsJson;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
        return "AssessmentType{" +
                "typeId=" + typeId +
                ", typeName='" + typeName + '\'' +
                ", ageGroup='" + ageGroup + '\'' +
                ", description='" + description + '\'' +
                ", scoringMethod='" + scoringMethod + '\'' +
                ", isActive=" + isActive +
                '}';
    }
} 