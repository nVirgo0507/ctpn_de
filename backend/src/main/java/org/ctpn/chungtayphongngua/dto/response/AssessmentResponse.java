package org.ctpn.chungtayphongngua.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

/**
 * Assessment Response DTO
 * Author: FullStack-Developer-AI (Cursor)
 * Created: [Current session date]
 * Version: 1.0
 * Context: Returns assessment results per Document FR-006, FR-007
 */
public class AssessmentResponse {
    
    @JsonProperty("assessmentId")
    private Long assessmentId;
    
    @JsonProperty("assessmentType")
    private String assessmentType;
    
    @JsonProperty("totalScore")
    private Integer totalScore;
    
    @JsonProperty("riskLevel")
    private String riskLevel;
    
    @JsonProperty("recommendations")
    private String recommendations;
    
    @JsonProperty("completedAt")
    private LocalDateTime completedAt;
    
    @JsonProperty("riskLevelDescription")
    private String riskLevelDescription;
    
    @JsonProperty("nextSteps")
    private String nextSteps;
    
    // Constructors
    public AssessmentResponse() {}
    
    public AssessmentResponse(Long assessmentId, String assessmentType, Integer totalScore, 
                             String riskLevel, String recommendations, LocalDateTime completedAt) {
        this.assessmentId = assessmentId;
        this.assessmentType = assessmentType;
        this.totalScore = totalScore;
        this.riskLevel = riskLevel;
        this.recommendations = recommendations;
        this.completedAt = completedAt;
        this.riskLevelDescription = generateRiskLevelDescription(riskLevel, assessmentType);
        this.nextSteps = generateNextSteps(riskLevel);
    }
    
    // Getters and Setters
    public Long getAssessmentId() {
        return assessmentId;
    }
    
    public void setAssessmentId(Long assessmentId) {
        this.assessmentId = assessmentId;
    }
    
    public String getAssessmentType() {
        return assessmentType;
    }
    
    public void setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
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
        this.riskLevelDescription = generateRiskLevelDescription(riskLevel, this.assessmentType);
    }
    
    public String getRecommendations() {
        return recommendations;
    }
    
    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    public String getRiskLevelDescription() {
        return riskLevelDescription;
    }
    
    public void setRiskLevelDescription(String riskLevelDescription) {
        this.riskLevelDescription = riskLevelDescription;
    }
    
    public String getNextSteps() {
        return nextSteps;
    }
    
    public void setNextSteps(String nextSteps) {
        this.nextSteps = nextSteps;
    }
    
    /**
     * Generate risk level description in Vietnamese
     */
    private String generateRiskLevelDescription(String riskLevel, String assessmentType) {
        if (riskLevel == null) return "";
        
        switch (riskLevel.toUpperCase()) {
            case "LOW":
                return "Mức độ rủi ro thấp - Bạn có lối sống lành mạnh";
            case "MODERATE":
                return "Mức độ rủi ro trung bình - Cần quan tâm và theo dõi";
            case "HIGH":
                return "Mức độ rủi ro cao - Cần hỗ trợ chuyên nghiệp";
            default:
                return "Mức độ rủi ro chưa xác định";
        }
    }
    
    /**
     * Generate next steps recommendations
     */
    private String generateNextSteps(String riskLevel) {
        if (riskLevel == null) return "";
        
        switch (riskLevel.toUpperCase()) {
            case "LOW":
                return "Tiếp tục duy trì lối sống lành mạnh và tham gia các hoạt động giáo dục";
            case "MODERATE":
                return "Tham gia tư vấn và các khóa học can thiệp sớm";
            case "HIGH":
                return "Liên hệ ngay với chuyên gia tư vấn để được hỗ trợ";
            default:
                return "Tham gia đánh giá để được tư vấn phù hợp";
        }
    }
    
    @Override
    public String toString() {
        return "AssessmentResponse{" +
                "assessmentId=" + assessmentId +
                ", assessmentType='" + assessmentType + '\'' +
                ", totalScore=" + totalScore +
                ", riskLevel='" + riskLevel + '\'' +
                ", completedAt=" + completedAt +
                '}';
    }
} 