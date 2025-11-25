package org.ctpn.chungtayphongngua.controller;

import org.ctpn.chungtayphongngua.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Survey Controller - Public Content Access Implementation
 * Author: FullStack-Developer-AI (Cursor)
 * Created: Session 13
 * Version: 1.0
 * Context: Implements FR-002 Public Content Access & FR-017 Survey Management per Document specifications
 */
@RestController
@RequestMapping("/surveys")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SurveyController {
    
    @Autowired
    private SurveyService surveyService;
    
    /**
     * Get public surveys accessible without authentication (FR-002 requirement)
     * Anonymous access with no personal data collection
     */
    @CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
    @GetMapping("/public")
    public ResponseEntity<?> getPublicSurveys() {
        try {
            List<Map<String, Object>> publicSurveys = surveyService.getPublicSurveys();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy danh sách khảo sát công khai thành công");
            response.put("data", publicSurveys);
            response.put("note", "Các khảo sát này hoàn toàn ẩn danh và không thu thập thông tin cá nhân");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy danh sách khảo sát: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Get survey by public link without authentication (FR-002 requirement)
     * Anonymous access for public surveys
     */
    @GetMapping("/public/{surveyId}")
    public ResponseEntity<?> getPublicSurvey(@PathVariable String surveyId) {
        try {
            Map<String, Object> survey = surveyService.getPublicSurvey(surveyId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy nội dung khảo sát thành công");
            response.put("data", survey);
            response.put("anonymous", true);
            response.put("privacyNote", "Khảo sát này hoàn toàn ẩn danh và không lưu trữ thông tin định danh");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy khảo sát: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Submit anonymous survey response (FR-002 requirement)
     * No authentication required, no personal data collection
     */
    @PostMapping("/submit/{surveyId}")
    public ResponseEntity<?> submitAnonymousSurvey(
            @PathVariable String surveyId,
            @RequestBody Map<String, Object> surveyResponse) {
        try {
            // Ensure no personal data is collected in anonymous submissions
            surveyResponse.remove("email");
            surveyResponse.remove("phone");
            surveyResponse.remove("name");
            surveyResponse.remove("userId");
            
            Map<String, Object> result = surveyService.submitAnonymousSurvey(surveyId, surveyResponse);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Gửi phản hồi khảo sát thành công. Cảm ơn bạn đã tham gia!");
            response.put("data", result);
            response.put("anonymous", true);
            response.put("dataRetention", "Phản hồi được lưu trữ ẩn danh không có thông tin định danh");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi gửi phản hồi khảo sát: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Create new survey (Staff+ only per FR-017)
     * Survey builder with multiple question types
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('STAFF') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> createSurvey(
            @RequestBody Map<String, Object> surveyData,
            Authentication authentication) {
        try {
            Map<String, Object> createdSurvey = surveyService.createSurvey(surveyData, authentication.getName());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tạo khảo sát thành công");
            response.put("data", createdSurvey);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi tạo khảo sát: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Get survey management dashboard (Staff+ only)
     */
    @GetMapping("/manage")
    @PreAuthorize("hasRole('STAFF') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getSurveyManagement(Authentication authentication) {
        try {
            List<Map<String, Object>> surveys = surveyService.getSurveysForManagement(authentication.getName());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy danh sách khảo sát quản lý thành công");
            response.put("data", surveys);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy danh sách khảo sát: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Get survey analytics and responses (Manager+ only)
     */
    @GetMapping("/{surveyId}/analytics")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getSurveyAnalytics(@PathVariable String surveyId) {
        try {
            Map<String, Object> analytics = surveyService.getSurveyAnalytics(surveyId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy phân tích khảo sát thành công");
            response.put("data", analytics);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy phân tích khảo sát: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Publish survey for public access (Manager+ only)
     */
    @PutMapping("/{surveyId}/publish")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> publishSurvey(@PathVariable String surveyId) {
        try {
            Map<String, Object> publishedSurvey = surveyService.publishSurvey(surveyId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Xuất bản khảo sát thành công");
            response.put("data", publishedSurvey);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi xuất bản khảo sát: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Get survey templates (Staff+ only per FR-017)
     */
    @GetMapping("/templates")
    @PreAuthorize("hasRole('STAFF') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getSurveyTemplates() {
        try {
            List<Map<String, Object>> templates = surveyService.getSurveyTemplates();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy mẫu khảo sát thành công");
            response.put("data", templates);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy mẫu khảo sát: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Health check for survey controller - Public endpoint per FR-002
     * Following Spring.io CORS guide: https://spring.io/guides/gs/rest-service-cors
     */
    @CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Survey Controller");
        health.put("anonymousAccess", true);
        health.put("publicSurveys", true);
        health.put("timestamp", System.currentTimeMillis());
        health.put("cors", "enabled");
        
        return ResponseEntity.ok(health);
    }
    
    /**
     * OPTIONS handler for CORS preflight requests
     * Explicit OPTIONS handling per Spring.io CORS guide
     */
    @CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
    @RequestMapping(value = "/health", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> healthCheckOptions() {
        return ResponseEntity.ok().build();
    }
} 