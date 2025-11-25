package org.ctpn.chungtayphongngua.controller;

import org.ctpn.chungtayphongngua.dto.request.AssessmentRequest;
import org.ctpn.chungtayphongngua.dto.response.AssessmentResponse;
import org.ctpn.chungtayphongngua.service.AssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * Assessment Controller - ASSIST/CRAFFT Implementation
 * Author: FullStack-Developer-AI (Cursor)
 * Created: [Current session date]
 * Version: 1.0
 * Context: Implements FR-006 Drug Risk Self-Assessment per Document specifications
 */
@RestController
@RequestMapping("/assessment")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AssessmentController {
    
    @Autowired
    private AssessmentService assessmentService;
    
    /**
     * Get assessment questions based on user age
     * ASSIST for 18+, CRAFFT for <18 per Document requirements
     */
    @GetMapping("/questions")
    @PreAuthorize("hasRole('MEMBER') or hasRole('STAFF') or hasRole('CONSULTANT') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getAssessmentQuestions(@RequestParam("age") int age) {
        try {
            Map<String, Object> questions = assessmentService.getAssessmentQuestions(age);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy câu hỏi đánh giá thành công");
            response.put("data", questions);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy câu hỏi đánh giá: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Submit assessment answers and get risk calculation
     * Implements automated scoring per Document FR-006
     */
    @PostMapping("/submit")
    @PreAuthorize("hasRole('MEMBER') or hasRole('STAFF') or hasRole('CONSULTANT') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> submitAssessment(@Valid @RequestBody AssessmentRequest request) {
        try {
            AssessmentResponse assessmentResult = assessmentService.processAssessment(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đánh giá đã được xử lý thành công");
            response.put("data", assessmentResult);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi xử lý đánh giá: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Get user's assessment history
     * Implements historical tracking per Document FR-007
     */
    @GetMapping("/history")
    @PreAuthorize("hasRole('MEMBER') or hasRole('STAFF') or hasRole('CONSULTANT') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getAssessmentHistory() {
        try {
            Map<String, Object> history = assessmentService.getUserAssessmentHistory();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy lịch sử đánh giá thành công");
            response.put("data", history);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy lịch sử đánh giá: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Get course recommendations based on assessment results
     * Implements FR-008 Course Recommendation Engine
     */
    @GetMapping("/recommendations")
    @PreAuthorize("hasRole('MEMBER') or hasRole('STAFF') or hasRole('CONSULTANT') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getCourseRecommendations() {
        try {
            Map<String, Object> recommendations = assessmentService.getCourseRecommendations();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy đề xuất khóa học thành công");
            response.put("data", recommendations);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy đề xuất khóa học: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
} 