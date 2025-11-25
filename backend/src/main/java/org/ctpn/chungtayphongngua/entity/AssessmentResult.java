package org.ctpn.chungtayphongngua.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Assessment Result Entity
 * Author: FullStack-Developer-AI (Cursor)
 * Created: [Current session date]
 * Version: 1.0
 * Context: Maps to ctpn_assessment.assessment_results table per Document database schema
 */
@Entity
@Table(name = "assessment_results", schema = "ctpn_assessment")
public class AssessmentResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long resultId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;
    
    @Column(name = "question_id", nullable = false, length = 50)
    private String questionId;
    
    @Column(name = "answer_value", length = 255)
    private String answerValue;
    
    @Column(name = "score_weight", nullable = false)
    private Integer scoreWeight = 0;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
    
    // Constructors
    public AssessmentResult() {
        this.createdAt = LocalDateTime.now();
    }
    
    public AssessmentResult(Assessment assessment, String questionId, String answerValue, Integer scoreWeight) {
        this();
        this.assessment = assessment;
        this.questionId = questionId;
        this.answerValue = answerValue;
        this.scoreWeight = scoreWeight;
    }
    
    // Getters and Setters
    public Long getResultId() {
        return resultId;
    }
    
    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }
    
    public Assessment getAssessment() {
        return assessment;
    }
    
    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }
    
    public String getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
    
    public String getAnswerValue() {
        return answerValue;
    }
    
    public void setAnswerValue(String answerValue) {
        this.answerValue = answerValue;
    }
    
    public Integer getScoreWeight() {
        return scoreWeight;
    }
    
    public void setScoreWeight(Integer scoreWeight) {
        this.scoreWeight = scoreWeight;
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
        return "AssessmentResult{" +
                "resultId=" + resultId +
                ", questionId='" + questionId + '\'' +
                ", answerValue='" + answerValue + '\'' +
                ", scoreWeight=" + scoreWeight +
                ", isDeleted=" + isDeleted +
                '}';
    }
} 