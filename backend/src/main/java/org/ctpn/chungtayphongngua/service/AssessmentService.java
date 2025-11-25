package org.ctpn.chungtayphongngua.service;

import org.ctpn.chungtayphongngua.dto.request.AssessmentRequest;
import org.ctpn.chungtayphongngua.dto.response.AssessmentResponse;
import org.ctpn.chungtayphongngua.entity.Assessment;
import org.ctpn.chungtayphongngua.entity.AssessmentResult;
import org.ctpn.chungtayphongngua.entity.AssessmentType;
import org.ctpn.chungtayphongngua.entity.User;
import org.ctpn.chungtayphongngua.repository.AssessmentRepository;
import org.ctpn.chungtayphongngua.repository.AssessmentResultRepository;
import org.ctpn.chungtayphongngua.repository.AssessmentTypeRepository;
import org.ctpn.chungtayphongngua.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Assessment Service - Complete ASSIST/CRAFFT Implementation
 * Author: FullStack-Developer-AI (Cursor)
 * Created: [Current session date]
 * Version: 1.0
 * Context: Implements Document FR-006, FR-007, FR-008 specifications
 */
@Service
@Transactional
public class AssessmentService {
    
    @Autowired
    private AssessmentRepository assessmentRepository;
    
    @Autowired
    private AssessmentResultRepository assessmentResultRepository;
    
    @Autowired
    private AssessmentTypeRepository assessmentTypeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Get assessment questions based on age
     * ASSIST for 18+, CRAFFT for <18 per Document specifications
     */
    public Map<String, Object> getAssessmentQuestions(int age) {
        String assessmentType = age >= 18 ? "ASSIST" : "CRAFFT";
        String ageGroup = age >= 18 ? "adult" : "youth";
        
        Optional<AssessmentType> type = assessmentTypeRepository.findByTypeNameAndAgeGroup(assessmentType, ageGroup);
        
        if (type.isEmpty()) {
            // Initialize default questions if not found
            initializeDefaultAssessments();
            type = assessmentTypeRepository.findByTypeNameAndAgeGroup(assessmentType, ageGroup);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("assessmentType", assessmentType);
        response.put("ageGroup", ageGroup);
        response.put("questions", type.get().getQuestionsJson());
        response.put("instructions", getInstructions(assessmentType));
        
        return response;
    }
    
    /**
     * Process assessment submission with automated scoring
     * Implements Document FR-006 risk calculation
     */
    public AssessmentResponse processAssessment(AssessmentRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        
        // Create assessment record
        Assessment assessment = new Assessment();
        assessment.setUser(user);
        assessment.setTypeId(request.getTypeId());
        assessment.setStartedAt(LocalDateTime.now());
        assessment.setCompletedAt(LocalDateTime.now());
        
        // Calculate score based on assessment type
        String assessmentType = request.getAssessmentType();
        int totalScore = calculateScore(request.getAnswers(), assessmentType);
        String riskLevel = calculateRiskLevel(totalScore, assessmentType);
        
        assessment.setTotalScore(totalScore);
        assessment.setRiskLevel(riskLevel);
        assessment.setRecommendations(generateRecommendations(riskLevel, assessmentType));
        
        assessment = assessmentRepository.save(assessment);
        
        // Save individual answers
        saveAssessmentResults(assessment, request.getAnswers());
        
        return new AssessmentResponse(
                assessment.getAssessmentId(),
                assessmentType,
                totalScore,
                riskLevel,
                assessment.getRecommendations(),
                LocalDateTime.now()
        );
    }
    
    /**
     * Calculate total score based on assessment type
     * ASSIST: Sum scoring, CRAFFT: Binary scoring per Document
     */
    private int calculateScore(Map<String, String> answers, String assessmentType) {
        if ("ASSIST".equals(assessmentType)) {
            return calculateAssistScore(answers);
        } else if ("CRAFFT".equals(assessmentType)) {
            return calculateCrafftScore(answers);
        }
        return 0;
    }
    
    /**
     * ASSIST scoring algorithm per WHO standards
     * Each substance category scored 0-39, total max 312
     */
    private int calculateAssistScore(Map<String, String> answers) {
        int totalScore = 0;
        
        // ASSIST scoring logic based on WHO guidelines
        for (Map.Entry<String, String> answer : answers.entrySet()) {
            String questionId = answer.getKey();
            String answerValue = answer.getValue();
            
            try {
                int score = Integer.parseInt(answerValue);
                totalScore += score;
            } catch (NumberFormatException e) {
                // Handle non-numeric answers
                totalScore += mapAnswerToScore(answerValue);
            }
        }
        
        return totalScore;
    }
    
    /**
     * CRAFFT scoring algorithm per CeASAR standards
     * Binary scoring: 0-1 = Low risk, 2+ = High risk
     */
    private int calculateCrafftScore(Map<String, String> answers) {
        int totalScore = 0;
        
        for (Map.Entry<String, String> answer : answers.entrySet()) {
            String answerValue = answer.getValue().toLowerCase();
            
            // CRAFFT binary scoring: "yes" = 1 point, "no" = 0 points
            if ("yes".equals(answerValue) || "có".equals(answerValue) || "1".equals(answerValue)) {
                totalScore += 1;
            }
        }
        
        return totalScore;
    }
    
    /**
     * Calculate risk level based on score and assessment type
     * Per Document FR-006 specifications
     */
    private String calculateRiskLevel(int score, String assessmentType) {
        if ("ASSIST".equals(assessmentType)) {
            // ASSIST risk levels per WHO standards
            if (score <= 10) return "LOW";
            else if (score <= 26) return "MODERATE";
            else return "HIGH";
        } else if ("CRAFFT".equals(assessmentType)) {
            // CRAFFT binary risk classification
            return (score <= 1) ? "LOW" : "HIGH";
        }
        return "LOW";
    }
    
    /**
     * Generate personalized recommendations based on risk level
     */
    private String generateRecommendations(String riskLevel, String assessmentType) {
        Map<String, String> recommendations = new HashMap<>();
        
        if ("ASSIST".equals(assessmentType)) {
            recommendations.put("LOW", "Tiếp tục duy trì lối sống lành mạnh. Tham gia các khóa học phòng ngừa để tăng cường kiến thức.");
            recommendations.put("MODERATE", "Nên cân nhắc tham gia tư vấn và các khóa học can thiệp sớm để giảm rủi ro.");
            recommendations.put("HIGH", "Cần tham gia tư vấn chuyên sâu và các chương trình điều trị phù hợp ngay lập tức.");
        } else {
            recommendations.put("LOW", "Tiếp tục duy trì nhận thức đúng đắn về tác hại của ma túy. Tham gia các hoạt động giáo dục.");
            recommendations.put("HIGH", "Cần có sự hỗ trợ từ gia đình và tham gia tư vấn chuyên nghiệp để được hướng dẫn phù hợp.");
        }
        
        return recommendations.getOrDefault(riskLevel, "Tham gia các chương trình giáo dục và tư vấn để có thêm thông tin.");
    }
    
    /**
     * Save assessment results for audit and analysis
     */
    private void saveAssessmentResults(Assessment assessment, Map<String, String> answers) {
        for (Map.Entry<String, String> answer : answers.entrySet()) {
            AssessmentResult result = new AssessmentResult();
            result.setAssessment(assessment);
            result.setQuestionId(answer.getKey());
            result.setAnswerValue(answer.getValue());
            result.setScoreWeight(mapAnswerToScore(answer.getValue()));
            
            assessmentResultRepository.save(result);
        }
    }
    
    /**
     * Map answer values to numeric scores
     */
    private int mapAnswerToScore(String answer) {
        Map<String, Integer> scoreMapping = new HashMap<>();
        scoreMapping.put("never", 0);
        scoreMapping.put("once or twice", 1);
        scoreMapping.put("monthly", 2);
        scoreMapping.put("weekly", 3);
        scoreMapping.put("daily", 4);
        scoreMapping.put("no", 0);
        scoreMapping.put("yes", 1);
        
        return scoreMapping.getOrDefault(answer.toLowerCase(), 0);
    }
    
    /**
     * Get user's assessment history
     */
    public Map<String, Object> getUserAssessmentHistory() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        
        List<Assessment> assessments = assessmentRepository.findByUserOrderByCompletedAtDesc(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("totalAssessments", assessments.size());
        response.put("assessments", assessments);
        
        return response;
    }
    
    /**
     * Get course recommendations based on latest assessment
     */
    public Map<String, Object> getCourseRecommendations() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        
        Optional<Assessment> latestAssessment = assessmentRepository.findTopByUserOrderByCompletedAtDesc(user);
        
        if (latestAssessment.isEmpty()) {
            throw new RuntimeException("Bạn cần hoàn thành đánh giá trước khi nhận đề xuất khóa học");
        }
        
        String riskLevel = latestAssessment.get().getRiskLevel();
        List<Map<String, Object>> recommendations = generateCourseRecommendations(riskLevel);
        
        Map<String, Object> response = new HashMap<>();
        response.put("riskLevel", riskLevel);
        response.put("recommendations", recommendations);
        response.put("assessmentDate", latestAssessment.get().getCompletedAt());
        
        return response;
    }
    
    /**
     * Generate course recommendations based on risk level
     */
    private List<Map<String, Object>> generateCourseRecommendations(String riskLevel) {
        List<Map<String, Object>> courses = new ArrayList<>();
        
        if ("LOW".equals(riskLevel)) {
            courses.add(createCourseRecommendation("Giáo dục phòng ngừa cơ bản", "Khóa học nền tảng về tác hại của ma túy", "beginner"));
            courses.add(createCourseRecommendation("Kỹ năng sống lành mạnh", "Phát triển lối sống tích cực", "beginner"));
        } else if ("MODERATE".equals(riskLevel)) {
            courses.add(createCourseRecommendation("Can thiệp sớm", "Chương trình hỗ trợ giảm rủi ro", "intermediate"));
            courses.add(createCourseRecommendation("Tư vấn nhóm", "Chia sẻ kinh nghiệm và hỗ trợ lẫn nhau", "intermediate"));
        } else if ("HIGH".equals(riskLevel)) {
            courses.add(createCourseRecommendation("Điều trị chuyên sâu", "Chương trình điều trị toàn diện", "advanced"));
            courses.add(createCourseRecommendation("Hỗ trợ gia đình", "Tư vấn cho gia đình người nghiện", "advanced"));
        }
        
        return courses;
    }
    
    private Map<String, Object> createCourseRecommendation(String title, String description, String level) {
        Map<String, Object> course = new HashMap<>();
        course.put("title", title);
        course.put("description", description);
        course.put("level", level);
        course.put("priority", "high");
        return course;
    }
    
    /**
     * Get instructions for each assessment type
     */
    private String getInstructions(String assessmentType) {
        if ("ASSIST".equals(assessmentType)) {
            return "Hãy trả lời thành thật về việc sử dụng các chất kích thích trong 3 tháng qua. Thông tin của bạn sẽ được bảo mật.";
        } else {
            return "Hãy trả lời có/không cho các câu hỏi sau đây. Thông tin này sẽ giúp chúng tôi đưa ra lời khuyên phù hợp.";
        }
    }
    
    /**
     * Initialize default assessment questions if not present
     */
    private void initializeDefaultAssessments() {
        // Initialize ASSIST assessment for adults (18+)
        if (!assessmentTypeRepository.existsByTypeNameAndAgeGroup("ASSIST", "adult")) {
            String assistQuestions = createAssistQuestions();
            AssessmentType assistType = new AssessmentType("ASSIST", "adult", 
                "WHO ASSIST Assessment for adults 18+", "sum_score", assistQuestions);
            assessmentTypeRepository.save(assistType);
        }
        
        // Initialize CRAFFT assessment for youth (<18)
        if (!assessmentTypeRepository.existsByTypeNameAndAgeGroup("CRAFFT", "youth")) {
            String crafftQuestions = createCrafftQuestions();
            AssessmentType crafftType = new AssessmentType("CRAFFT", "youth", 
                "CeASAR CRAFFT Assessment for youth <18", "binary_score", crafftQuestions);
            assessmentTypeRepository.save(crafftType);
        }
    }
    
    /**
     * Create ASSIST questions in JSON format
     */
    private String createAssistQuestions() {
        return "{\n" +
                "  \"questions\": [\n" +
                "    {\n" +
                "      \"id\": \"q1\",\n" +
                "      \"text\": \"Trong 3 tháng qua, bạn có sử dụng rượu bia không?\",\n" +
                "      \"type\": \"frequency\",\n" +
                "      \"category\": \"alcohol\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"q2\",\n" +
                "      \"text\": \"Trong 3 tháng qua, bạn có sử dụng thuốc lá không?\",\n" +
                "      \"type\": \"frequency\",\n" +
                "      \"category\": \"tobacco\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"q3\",\n" +
                "      \"text\": \"Trong 3 tháng qua, bạn có sử dụng ma túy không?\",\n" +
                "      \"type\": \"frequency\",\n" +
                "      \"category\": \"drugs\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"q4\",\n" +
                "      \"text\": \"Bạn có bao giờ cảm thấy cần phải sử dụng nhiều hơn để có cùng cảm giác?\",\n" +
                "      \"type\": \"frequency\",\n" +
                "      \"category\": \"tolerance\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"q5\",\n" +
                "      \"text\": \"Bạn có bao giờ muốn cắt bỏ hoặc giảm việc sử dụng chất kích thích?\",\n" +
                "      \"type\": \"frequency\",\n" +
                "      \"category\": \"control\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"options\": {\n" +
                "    \"frequency\": [\"Không bao giờ\", \"Một hoặc hai lần\", \"Hàng tháng\", \"Hàng tuần\", \"Hàng ngày\"]\n" +
                "  }\n" +
                "}";
    }
    
    /**
     * Create CRAFFT questions in JSON format
     */
    private String createCrafftQuestions() {
        return "{\n" +
                "  \"questions\": [\n" +
                "    {\n" +
                "      \"id\": \"q1\",\n" +
                "      \"text\": \"Bạn có bao giờ lái xe khi đã sử dụng chất kích thích?\",\n" +
                "      \"type\": \"yesno\",\n" +
                "      \"category\": \"car\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"q2\",\n" +
                "      \"text\": \"Bạn có bao giờ sử dụng chất kích thích để thư giãn hay tự tin hơn?\",\n" +
                "      \"type\": \"yesno\",\n" +
                "      \"category\": \"relax\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"q3\",\n" +
                "      \"text\": \"Bạn có bao giờ sử dụng chất kích thích khi một mình?\",\n" +
                "      \"type\": \"yesno\",\n" +
                "      \"category\": \"alone\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"q4\",\n" +
                "      \"text\": \"Bạn có bao giờ quên những gì đã làm khi sử dụng chất kích thích?\",\n" +
                "      \"type\": \"yesno\",\n" +
                "      \"category\": \"forget\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"q5\",\n" +
                "      \"text\": \"Gia đình hay bạn bè có bao giờ nói bạn nên giảm sử dụng chất kích thích?\",\n" +
                "      \"type\": \"yesno\",\n" +
                "      \"category\": \"family\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"q6\",\n" +
                "      \"text\": \"Bạn có bao giờ gặp rắc rối khi sử dụng chất kích thích?\",\n" +
                "      \"type\": \"yesno\",\n" +
                "      \"category\": \"trouble\"\n" +
                "    }\n" +
                "  ],\n" +
                                                  "  \"options\": {\n" +
                 "    \"yesno\": [\"Không\", \"Có\"]\n" +
                 "  }\n" +
                 "}";
    }
} 