package org.ctpn.chungtayphongngua.controller;

import org.ctpn.chungtayphongngua.service.BlogService;
import org.ctpn.chungtayphongngua.service.CourseService;
import org.ctpn.chungtayphongngua.entity.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Homepage Controller - Public Homepage Implementation
 * Author: FullStack-Developer-AI (Cursor)
 * Created: Session 12
 * Version: 1.1 - Added Course endpoints
 * Context: Implements FR-001 Public Homepage + FR-009/FR-010 Course Management
 */
@RestController
@RequestMapping("/homepage")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HomepageController {
    
    @Autowired
    private BlogService blogService;
    
    @Autowired
    private CourseService courseService;
    
    /**
     * Get homepage data including organization info, mission, and latest blog posts
     * Implements FR-001: Public Homepage requirements
     */
    @GetMapping("/data")
    public ResponseEntity<?> getHomepageData() {
        try {
            Map<String, Object> homepageData = new HashMap<>();
            
            // Organization Information (FR-001 requirement)
            Map<String, Object> organizationInfo = new HashMap<>();
            organizationInfo.put("name", "Chung Tay Phòng Ngừa");
            organizationInfo.put("shortName", "CTPN");
            organizationInfo.put("tagline", "Cùng nhau xây dựng cộng đồng lành mạnh, an toàn");
            
            // Mission Statement (FR-001 requirement)
            Map<String, Object> mission = new HashMap<>();
            mission.put("title", "Sứ mệnh của chúng tôi");
            mission.put("content", "Chung Tay Phòng Ngừa cam kết tạo ra một cộng đồng an toàn và lành mạnh thông qua việc " +
                    "giáo dục, tư vấn và hỗ trợ những người gặp khó khăn với các vấn đề về chất kích thích. " +
                    "Chúng tôi tin rằng mọi người đều xứng đáng có cơ hội để sống một cuộc sống tốt đẹp và khỏe mạnh.");
            mission.put("values", List.of(
                "Tôn trọng và không phán xét",
                "Hỗ trợ dựa trên bằng chứng khoa học",
                "Tiếp cận cộng đồng toàn diện",
                "Bảo mật thông tin tuyệt đối",
                "Hợp tác với tình nguyện viên chuyên nghiệp"
            ));
            
            // Contact Information (FR-001 requirement - exact as specified)
            Map<String, Object> contact = new HashMap<>();
            contact.put("phone", "0337315535");
            contact.put("email", "chungtay.adm@gmail.com");
            contact.put("address", "Tp. Hồ Chí Minh, Việt Nam");
            contact.put("workingHours", "Thứ 2 - Chủ nhật: 8:00 - 22:00");
            contact.put("emergencyNote", "Dịch vụ tư vấn khẩn cấp 24/7 qua email");
            
            // Services Overview
            Map<String, Object> services = new HashMap<>();
            services.put("title", "Dịch vụ của chúng tôi");
            services.put("description", "Chúng tôi cung cấp các dịch vụ hỗ trợ toàn diện và miễn phí");
            services.put("serviceList", List.of(
                Map.of(
                    "name", "Đánh giá rủi ro cá nhân",
                    "description", "Công cụ đánh giá WHO ASSIST và CeASAR CRAFFT",
                    "icon", "clipboard-check",
                    "available", true
                ),
                Map.of(
                    "name", "Tư vấn chuyên nghiệp",
                    "description", "Tư vấn 1-1 với các chuyên gia tình nguyện",
                    "icon", "heart",
                    "available", true
                ),
                Map.of(
                    "name", "Khóa học trực tuyến",
                    "description", "Chương trình học phòng ngừa và can thiệp",
                    "icon", "book-open",
                    "available", true
                ),
                Map.of(
                    "name", "Hỗ trợ cộng đồng",
                    "description", "Diễn đàn chia sẻ và hỗ trợ lẫn nhau",
                    "icon", "users",
                    "available", false
                )
            ));
            
            // Latest Blog Posts (FR-001 requirement: latest 5 blog posts)
            List<Map<String, Object>> latestBlogPosts = blogService.getLatestBlogPosts(5);
            
            // Statistics (dynamic or static for demo)
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("title", "Tác động của chúng tôi");
            statistics.put("stats", List.of(
                Map.of("label", "Người được hỗ trợ", "value", "1,200+", "icon", "users"),
                Map.of("label", "Tình nguyện viên", "value", "50+", "icon", "heart"),
                Map.of("label", "Khóa học hoàn thành", "value", "800+", "icon", "book"),
                Map.of("label", "Tỷ lệ thành công", "value", "85%", "icon", "chart-bar")
            ));
            
            // Build complete homepage response
            homepageData.put("organization", organizationInfo);
            homepageData.put("mission", mission);
            homepageData.put("contact", contact);
            homepageData.put("services", services);
            homepageData.put("latestBlogs", latestBlogPosts);
            homepageData.put("statistics", statistics);
            homepageData.put("loadTime", System.currentTimeMillis());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy dữ liệu trang chủ thành công");
            response.put("data", homepageData);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy dữ liệu trang chủ: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * Get organization information only
     */
    @GetMapping("/about")
    public ResponseEntity<?> getAboutInfo() {
        try {
            Map<String, Object> aboutInfo = new HashMap<>();
            
            // Detailed About Us Information
            aboutInfo.put("name", "Chung Tay Phòng Ngừa");
            aboutInfo.put("foundedYear", "2024");
            aboutInfo.put("description", "Tổ chức phi lợi nhuận chuyên về phòng ngừa và hỗ trợ những người " +
                    "gặp khó khăn với các vấn đề liên quan đến chất kích thích.");
            
            aboutInfo.put("vision", "Tạo ra một cộng đồng Việt Nam an toàn, lành mạnh, " +
                    "nơi mọi người đều có cơ hội sống một cuộc sống tốt đẹp và ý nghĩa.");
            
            aboutInfo.put("approach", List.of(
                "Tiếp cận dựa trên bằng chứng khoa học",
                "Tôn trọng phẩm giá con người",
                "Hỗ trợ toàn diện và liên tục",
                "Hợp tác với cộng đồng địa phương",
                "Minh bạch và có trách nhiệm"
            ));
            
            aboutInfo.put("partnerships", List.of(
                "Tổ chức Y tế Thế giới (WHO)",
                "Mạng lưới CeASAR",
                "Các trường đại học y khoa",
                "Tình nguyện viên chuyên nghiệp"
            ));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy thông tin tổ chức thành công");
            response.put("data", aboutInfo);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy thông tin tổ chức: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * Get contact information only
     */
    @GetMapping("/contact")
    public ResponseEntity<?> getContactInfo() {
        try {
            Map<String, Object> contactInfo = new HashMap<>();
            
            // Primary Contact (as specified in FR-001)
            contactInfo.put("phone", "0337315535");
            contactInfo.put("email", "chungtay.adm@gmail.com");
            contactInfo.put("address", "Tp. Hồ Chí Minh, Việt Nam");
            
            // Additional Contact Details
            contactInfo.put("workingHours", Map.of(
                "weekdays", "Thứ 2 - Thứ 6: 8:00 - 20:00",
                "weekends", "Thứ 7 - Chủ nhật: 9:00 - 18:00",
                "emergency", "Hỗ trợ khẩn cấp 24/7 qua email"
            ));
            
            contactInfo.put("socialMedia", Map.of(
                "facebook", "ChungTayPhongNgua",
                "zalo", "0337315535",
                "website", "https://chungtayphongngua.org"
            ));
            
            contactInfo.put("emergencyInfo", Map.of(
                "hotline", "113 (Cảnh sát)",
                "medical", "115 (Cấp cứu y tế)",
                "support", "chungtay.adm@gmail.com (Hỗ trợ tâm lý)"
            ));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy thông tin liên hệ thành công");
            response.put("data", contactInfo);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy thông tin liên hệ: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * Health check for homepage controller
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Homepage Controller");
        health.put("timestamp", System.currentTimeMillis());
        health.put("version", "1.0");
        
        return ResponseEntity.ok(health);
    }
    
    /**
     * Simple test endpoint to verify controller functionality
     */
    @GetMapping("/test-endpoint")
    public ResponseEntity<?> testEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "HomepageController test endpoint working");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Course health check endpoint (temporary workaround)
     * GET /api/homepage/courses/health
     */
    @GetMapping("/courses/health")
    public ResponseEntity<?> courseHealthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "Course functionality is working");
        response.put("message", "Course endpoints with real database integration");
        response.put("timestamp", System.currentTimeMillis());
        response.put("serviceAvailable", courseService != null);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get all courses - real database implementation (PUBLIC ACCESS)
     * GET /api/homepage/courses
     */
    @GetMapping("/courses")
    public ResponseEntity<?> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "rating") String sort) {
        
        try {
            System.out.println("DEBUG: Getting courses - page=" + page + ", size=" + size + ", sort=" + sort);
            
            Page<Course> coursePage = courseService.getAllCourses(page, size, sort);
            
            System.out.println("DEBUG: Found " + coursePage.getTotalElements() + " courses");
            
            // Convert to simplified DTOs to avoid lazy loading issues
            List<Map<String, Object>> simplifiedCourses = coursePage.getContent().stream()
                .map(course -> {
                    Map<String, Object> courseDto = new HashMap<>();
                    courseDto.put("courseId", course.getCourseId());
                    courseDto.put("title", course.getTitle());
                    courseDto.put("description", course.getDescription());
                    courseDto.put("category", course.getCategory());
                    courseDto.put("level", course.getLevel());
                    courseDto.put("durationHours", course.getDurationHours());
                    courseDto.put("maxStudents", course.getMaxStudents());
                    courseDto.put("rating", course.getRating());
                    courseDto.put("totalReviews", course.getTotalReviews());
                    courseDto.put("price", course.getPrice());
                    courseDto.put("isActive", course.getIsActive());
                    courseDto.put("imageUrl", course.getImageUrl());
                    
                    // Add instructor info if available (handle null instructor)
                    if (course.getInstructor() != null) {
                        Map<String, Object> instructorInfo = new HashMap<>();
                        instructorInfo.put("userId", course.getInstructor().getUserId());
                        instructorInfo.put("fullName", course.getInstructor().getFullName());
                        courseDto.put("instructor", instructorInfo);
                    } else {
                        courseDto.put("instructor", null);
                    }
                    
                    return courseDto;
                })
                .toList();
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", simplifiedCourses);
            response.put("totalElements", coursePage.getTotalElements());
            response.put("totalPages", coursePage.getTotalPages());
            response.put("page", page);
            response.put("size", size);
            response.put("sort", sort);
            response.put("success", true);
            response.put("message", "Courses retrieved successfully from database");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("ERROR in getAllCourses: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error retrieving courses: " + e.getMessage());
            errorResponse.put("error", e.getClass().getSimpleName());
            errorResponse.put("cause", e.getCause() != null ? e.getCause().getMessage() : "No cause");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * Get course recommendations (PUBLIC ACCESS)
     * GET /api/homepage/courses/recommendations?riskLevel=high
     */
    @GetMapping("/courses/recommendations")
    public ResponseEntity<?> getCourseRecommendations(@RequestParam String riskLevel) {
        try {
            List<Course> recommendations = courseService.getCourseRecommendations(riskLevel);
            
            // Convert to simplified DTOs to avoid lazy loading issues
            List<Map<String, Object>> simplifiedCourses = recommendations.stream()
                .map(course -> {
                    Map<String, Object> courseDto = new HashMap<>();
                    courseDto.put("courseId", course.getCourseId());
                    courseDto.put("title", course.getTitle());
                    courseDto.put("description", course.getDescription());
                    courseDto.put("category", course.getCategory());
                    courseDto.put("level", course.getLevel());
                    courseDto.put("durationHours", course.getDurationHours());
                    courseDto.put("maxStudents", course.getMaxStudents());
                    courseDto.put("rating", course.getRating());
                    courseDto.put("totalReviews", course.getTotalReviews());
                    courseDto.put("price", course.getPrice());
                    courseDto.put("isActive", course.getIsActive());
                    courseDto.put("imageUrl", course.getImageUrl());
                    
                    // Add instructor info if available (handle null instructor)
                    if (course.getInstructor() != null) {
                        Map<String, Object> instructorInfo = new HashMap<>();
                        instructorInfo.put("userId", course.getInstructor().getUserId());
                        instructorInfo.put("fullName", course.getInstructor().getFullName());
                        courseDto.put("instructor", instructorInfo);
                    } else {
                        courseDto.put("instructor", null);
                    }
                    
                    return courseDto;
                })
                .toList();
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", simplifiedCourses);
            response.put("riskLevel", riskLevel);
            response.put("count", simplifiedCourses.size());
            response.put("success", true);
            response.put("message", "Course recommendations retrieved successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error retrieving recommendations: " + e.getMessage());
            errorResponse.put("error", e.getClass().getSimpleName());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
} 