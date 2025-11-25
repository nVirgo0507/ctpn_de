package org.ctpn.chungtayphongngua.service;

import org.ctpn.chungtayphongngua.entity.Course;
import org.ctpn.chungtayphongngua.entity.Enrollment;
import org.ctpn.chungtayphongngua.entity.User;
import org.ctpn.chungtayphongngua.repository.CourseRepository;
import org.ctpn.chungtayphongngua.repository.EnrollmentRepository;
import org.ctpn.chungtayphongngua.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CourseService {
    
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;
    
    public Page<Course> getAllCourses(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        return courseRepository.findByIsActiveTrue(pageable);
    }
    
    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findActiveCourseById(id);
    }
    
    public List<Course> findCoursesByUsername(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return courseRepository.findEnrolledCoursesByUserId(user.getUserId());
    }

    public Map<String, Object> getUserCourseStats(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        long activeCourses = enrollmentRepository.countByUserAndStatus(user, "active");
        long completedCourses = enrollmentRepository.countByUserAndStatus(user, "completed");
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("activeCourses", activeCourses);
        stats.put("completedCourses", completedCourses);
        return stats;
    }

    public List<Enrollment> getActiveEnrollmentsForUser(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return enrollmentRepository.findByUserAndStatus(user, "active");
    }

    public List<Course> searchCoursesByTitle(String title) {
        return courseRepository.findByTitleContainingIgnoreCaseAndIsActiveTrue(title);
    }
    
    public List<Course> getCourseRecommendations(String riskLevel) {
        Pageable top3 = PageRequest.of(0, 3);
        Pageable top2 = PageRequest.of(0, 2);
        
        switch (riskLevel.toLowerCase()) {
            case "high":
                return courseRepository.findByIsActiveTrue();
            case "medium":
                return courseRepository.findTop3ByIsActiveTrueOrderByRatingDesc(top3);
            case "low":
                return courseRepository.findTop2ByIsActiveTrueOrderByRatingDesc(top2);
            default:
                return courseRepository.findTop3ByIsActiveTrueOrderByRatingDesc(top3);
        }
    }
    
    public List<Course> getCourseRecommendationsByCategories(String riskLevel) {
        List<String> categories;
        
        switch (riskLevel.toLowerCase()) {
            case "high":
                categories = Arrays.asList("Tư vấn chuyên sâu", "Điều trị và phục hồi", "Hỗ trợ tâm lý");
                break;
            case "medium":
                categories = Arrays.asList("Giáo dục phòng ngừa", "Kỹ năng sống", "Tư vấn cơ bản");
                break;
            case "low":
            default:
                categories = Arrays.asList("Giáo dục tổng quát", "Nâng cao nhận thức", "Hoạt động cộng đồng");
                break;
        }
        
        return courseRepository.findRecommendedCourses(categories);
    }
}
