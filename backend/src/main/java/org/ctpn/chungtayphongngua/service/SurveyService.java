package org.ctpn.chungtayphongngua.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Survey Service - Public Content Access & Survey Management
 * Author: FullStack-Developer-AI (Cursor)
 * Created: Session 13
 * Version: 1.0
 * Context: Implements FR-002 Public Content Access & FR-017 Survey Management per Document specifications
 */
@Service
@Transactional
public class SurveyService {
    
    /**
     * Get public surveys accessible without authentication (FR-002)
     * Returns surveys available for anonymous participation
     */
    public List<Map<String, Object>> getPublicSurveys() {
        // For Session 13 - Sample data implementation
        List<Map<String, Object>> publicSurveys = new ArrayList<>();
        
        // Survey 1: Drug Prevention Awareness
        Map<String, Object> survey1 = new HashMap<>();
        survey1.put("id", "survey-001");
        survey1.put("title", "Khảo sát nhận thức về tác hại ma túy");
        survey1.put("description", "Khảo sát ẩn danh về nhận thức cộng đồng về tác hại của ma túy và các chất gây nghiện");
        survey1.put("status", "published");
        survey1.put("anonymous", true);
        survey1.put("estimatedTime", "5-7 phút");
        survey1.put("totalQuestions", 12);
        survey1.put("category", "Phòng ngừa ma túy");
        survey1.put("published", LocalDateTime.now().minusDays(3));
        survey1.put("responses", 245);
        survey1.put("publicLink", "/surveys/public/survey-001");
        
        // Survey 2: Youth Support Services
        Map<String, Object> survey2 = new HashMap<>();
        survey2.put("id", "survey-002");
        survey2.put("title", "Đánh giá nhu cầu hỗ trợ thanh thiếu niên");
        survey2.put("description", "Khảo sát ẩn danh về nhu cầu hỗ trợ tâm lý và xã hội của thanh thiếu niên");
        survey2.put("status", "published");
        survey2.put("anonymous", true);
        survey2.put("estimatedTime", "3-5 phút");
        survey2.put("totalQuestions", 8);
        survey2.put("category", "Hỗ trợ thanh thiếu niên");
        survey2.put("published", LocalDateTime.now().minusDays(1));
        survey2.put("responses", 127);
        survey2.put("publicLink", "/surveys/public/survey-002");
        
        // Survey 3: Family Support Assessment
        Map<String, Object> survey3 = new HashMap<>();
        survey3.put("id", "survey-003");
        survey3.put("title", "Khảo sát hỗ trợ gia đình");
        survey3.put("description", "Khảo sát ẩn danh về nhu cầu hỗ trợ và tư vấn gia đình");
        survey3.put("status", "published");
        survey3.put("anonymous", true);
        survey3.put("estimatedTime", "4-6 phút");
        survey3.put("totalQuestions", 10);
        survey3.put("category", "Hỗ trợ gia đình");
        survey3.put("published", LocalDateTime.now().minusDays(5));
        survey3.put("responses", 89);
        survey3.put("publicLink", "/surveys/public/survey-003");
        
        publicSurveys.add(survey1);
        publicSurveys.add(survey2);
        publicSurveys.add(survey3);
        
        return publicSurveys;
    }
    
    /**
     * Get specific public survey by ID without authentication (FR-002)
     */
    public Map<String, Object> getPublicSurvey(String surveyId) {
        Map<String, Object> survey = new HashMap<>();
        
        switch (surveyId) {
            case "survey-001":
                survey.put("id", "survey-001");
                survey.put("title", "Khảo sát nhận thức về tác hại ma túy");
                survey.put("description", "Khảo sát ẩn danh về nhận thức cộng đồng về tác hại của ma túy và các chất gây nghiện");
                survey.put("anonymous", true);
                survey.put("privacyNote", "Khảo sát này hoàn toàn ẩn danh và không thu thập thông tin cá nhân");
                
                List<Map<String, Object>> questions1 = new ArrayList<>();
                
                // Question 1
                Map<String, Object> q1 = new HashMap<>();
                q1.put("id", "q1");
                q1.put("text", "Bạn đã từng nghe về tác hại của ma túy chưa?");
                q1.put("type", "single_choice");
                q1.put("required", true);
                q1.put("options", Arrays.asList("Có", "Không", "Không chắc chắn"));
                questions1.add(q1);
                
                // Question 2
                Map<String, Object> q2 = new HashMap<>();
                q2.put("id", "q2");
                q2.put("text", "Theo bạn, nguồn thông tin nào đáng tin cậy nhất về tác hại ma túy?");
                q2.put("type", "single_choice");
                q2.put("required", true);
                q2.put("options", Arrays.asList("Bác sĩ/Chuyên gia y tế", "Truyền thông/Báo chí", "Trường học", "Gia đình", "Bạn bè", "Internet/Mạng xã hội"));
                questions1.add(q2);
                
                // Question 3
                Map<String, Object> q3 = new HashMap<>();
                q3.put("id", "q3");
                q3.put("text", "Bạn có biết về các dịch vụ hỗ trợ cai nghiện tại địa phương không?");
                q3.put("type", "single_choice");
                q3.put("required", true);
                q3.put("options", Arrays.asList("Biết rõ", "Biết một phần", "Không biết"));
                questions1.add(q3);
                
                survey.put("questions", questions1);
                break;
                
            case "survey-002":
                survey.put("id", "survey-002");
                survey.put("title", "Đánh giá nhu cầu hỗ trợ thanh thiếu niên");
                survey.put("description", "Khảo sát ẩn danh về nhu cầu hỗ trợ tâm lý và xã hội của thanh thiếu niên");
                survey.put("anonymous", true);
                survey.put("privacyNote", "Khảo sát này hoàn toàn ẩn danh và không thu thập thông tin cá nhân");
                
                List<Map<String, Object>> questions2 = new ArrayList<>();
                
                // Question 1
                Map<String, Object> q2_1 = new HashMap<>();
                q2_1.put("id", "q1");
                q2_1.put("text", "Bạn thuộc độ tuổi nào?");
                q2_1.put("type", "single_choice");
                q2_1.put("required", true);
                q2_1.put("options", Arrays.asList("12-15 tuổi", "16-18 tuổi", "19-22 tuổi", "23-25 tuổi", "Trên 25 tuổi"));
                questions2.add(q2_1);
                
                // Question 2
                Map<String, Object> q2_2 = new HashMap<>();
                q2_2.put("id", "q2");
                q2_2.put("text", "Bạn có cần hỗ trợ về vấn đề nào sau đây không?");
                q2_2.put("type", "multiple_choice");
                q2_2.put("required", false);
                q2_2.put("options", Arrays.asList("Tư vấn tâm lý", "Hỗ trợ học tập", "Hướng nghiệp", "Vấn đề gia đình", "Vấn đề bạn bè", "Không cần hỗ trợ"));
                questions2.add(q2_2);
                
                survey.put("questions", questions2);
                break;
                
            case "survey-003":
                survey.put("id", "survey-003");
                survey.put("title", "Khảo sát hỗ trợ gia đình");
                survey.put("description", "Khảo sát ẩn danh về nhu cầu hỗ trợ và tư vấn gia đình");
                survey.put("anonymous", true);
                survey.put("privacyNote", "Khảo sát này hoàn toàn ẩn danh và không thu thập thông tin cá nhân");
                
                List<Map<String, Object>> questions3 = new ArrayList<>();
                
                // Question 1
                Map<String, Object> q3_1 = new HashMap<>();
                q3_1.put("id", "q1");
                q3_1.put("text", "Gia đình bạn có gặp khó khăn nào sau đây không?");
                q3_1.put("type", "multiple_choice");
                q3_1.put("required", false);
                q3_1.put("options", Arrays.asList("Giao tiếp trong gia đình", "Áp lực kinh tế", "Giáo dục con cái", "Chăm sóc người cao tuổi", "Vấn đề sức khỏe", "Không có khó khăn"));
                questions3.add(q3_1);
                
                survey.put("questions", questions3);
                break;
                
            default:
                throw new RuntimeException("Không tìm thấy khảo sát với ID: " + surveyId);
        }
        
        return survey;
    }
    
    /**
     * Submit anonymous survey response (FR-002)
     * No personal data collection, completely anonymous
     */
    public Map<String, Object> submitAnonymousSurvey(String surveyId, Map<String, Object> surveyResponse) {
        // Ensure complete anonymity - remove any potential identifying information
        surveyResponse.remove("email");
        surveyResponse.remove("phone");
        surveyResponse.remove("name");
        surveyResponse.remove("userId");
        surveyResponse.remove("ipAddress");
        surveyResponse.remove("userAgent");
        
        // Store anonymous response (in real implementation, this would go to database)
        Map<String, Object> result = new HashMap<>();
        result.put("surveyId", surveyId);
        result.put("submissionId", "anon-" + System.currentTimeMillis());
        result.put("submitted", LocalDateTime.now());
        result.put("anonymous", true);
        result.put("status", "submitted");
        result.put("message", "Phản hồi đã được ghi nhận ẩn danh");
        
        // Log for analytics (no personal data)
        System.out.println("Anonymous survey response submitted for survey: " + surveyId);
        
        return result;
    }
    
    /**
     * Create new survey (Staff+ only per FR-017)
     */
    public Map<String, Object> createSurvey(Map<String, Object> surveyData, String createdBy) {
        Map<String, Object> createdSurvey = new HashMap<>();
        createdSurvey.put("id", "survey-" + System.currentTimeMillis());
        createdSurvey.put("title", surveyData.get("title"));
        createdSurvey.put("description", surveyData.get("description"));
        createdSurvey.put("questions", surveyData.get("questions"));
        createdSurvey.put("status", "draft");
        createdSurvey.put("createdBy", createdBy);
        createdSurvey.put("created", LocalDateTime.now());
        createdSurvey.put("anonymous", surveyData.getOrDefault("anonymous", true));
        
        return createdSurvey;
    }
    
    /**
     * Get surveys for management (Staff+ only)
     */
    public List<Map<String, Object>> getSurveysForManagement(String username) {
        List<Map<String, Object>> surveys = new ArrayList<>();
        
        Map<String, Object> survey = new HashMap<>();
        survey.put("id", "survey-001");
        survey.put("title", "Khảo sát nhận thức về tác hại ma túy");
        survey.put("status", "published");
        survey.put("responses", 245);
        survey.put("created", LocalDateTime.now().minusDays(7));
        survey.put("createdBy", "admin");
        surveys.add(survey);
        
        return surveys;
    }
    
    /**
     * Get survey analytics (Manager+ only)
     */
    public Map<String, Object> getSurveyAnalytics(String surveyId) {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("surveyId", surveyId);
        analytics.put("totalResponses", 245);
        analytics.put("completionRate", 87.5);
        analytics.put("averageTime", "4.2 phút");
        
        Map<String, Object> demographics = new HashMap<>();
        demographics.put("ageGroups", Map.of(
            "12-15", 15,
            "16-18", 35,
            "19-22", 30,
            "23-25", 20
        ));
        analytics.put("demographics", demographics);
        
        return analytics;
    }
    
    /**
     * Publish survey for public access (Manager+ only)
     */
    public Map<String, Object> publishSurvey(String surveyId) {
        Map<String, Object> publishedSurvey = new HashMap<>();
        publishedSurvey.put("id", surveyId);
        publishedSurvey.put("status", "published");
        publishedSurvey.put("published", LocalDateTime.now());
        publishedSurvey.put("publicLink", "/surveys/public/" + surveyId);
        
        return publishedSurvey;
    }
    
    /**
     * Get survey templates (Staff+ only per FR-017)
     */
    public List<Map<String, Object>> getSurveyTemplates() {
        List<Map<String, Object>> templates = new ArrayList<>();
        
        Map<String, Object> template1 = new HashMap<>();
        template1.put("id", "template-001");
        template1.put("name", "Khảo sát nhận thức cộng đồng");
        template1.put("description", "Mẫu khảo sát đánh giá nhận thức cộng đồng về vấn đề xã hội");
        template1.put("questions", 10);
        template1.put("category", "Cộng đồng");
        templates.add(template1);
        
        Map<String, Object> template2 = new HashMap<>();
        template2.put("id", "template-002");
        template2.put("name", "Đánh giá nhu cầu hỗ trợ");
        template2.put("description", "Mẫu khảo sát đánh giá nhu cầu hỗ trợ và dịch vụ");
        template2.put("questions", 8);
        template2.put("category", "Hỗ trợ");
        templates.add(template2);
        
        return templates;
    }
} 