package org.ctpn.chungtayphongngua.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

import java.util.Map;

/**
 * Assessment Request DTO
 * Author: FullStack-Developer-AI (Cursor)
 * Created: [Current session date]
 * Version: 1.0
 * Context: Handles ASSIST/CRAFFT assessment submissions per Document FR-006
 */
public class AssessmentRequest {
    
    @NotNull(message = "Type ID không được để trống")
    @Min(value = 1, message = "Type ID phải lớn hơn 0")
    @JsonProperty("typeId")
    private Integer typeId;
    
    @NotBlank(message = "Loại đánh giá không được để trống")
    @JsonProperty("assessmentType")
    private String assessmentType;
    
    @NotNull(message = "Câu trả lời không được để trống")
    @JsonProperty("answers")
    private Map<String, String> answers;
    
    @NotNull(message = "Tuổi không được để trống")
    @Min(value = 1, message = "Tuổi phải lớn hơn 0")
    @JsonProperty("age")
    private Integer age;
    
    // Constructors
    public AssessmentRequest() {}
    
    public AssessmentRequest(Integer typeId, String assessmentType, Map<String, String> answers, Integer age) {
        this.typeId = typeId;
        this.assessmentType = assessmentType;
        this.answers = answers;
        this.age = age;
    }
    
    // Getters and Setters
    public Integer getTypeId() {
        return typeId;
    }
    
    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }
    
    public String getAssessmentType() {
        return assessmentType;
    }
    
    public void setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
    }
    
    public Map<String, String> getAnswers() {
        return answers;
    }
    
    public void setAnswers(Map<String, String> answers) {
        this.answers = answers;
    }
    
    public Integer getAge() {
        return age;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
    
    @Override
    public String toString() {
        return "AssessmentRequest{" +
                "typeId=" + typeId +
                ", assessmentType='" + assessmentType + '\'' +
                ", age=" + age +
                ", answersCount=" + (answers != null ? answers.size() : 0) +
                '}';
    }
} 