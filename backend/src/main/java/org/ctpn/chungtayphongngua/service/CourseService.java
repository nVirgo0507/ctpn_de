package org.ctpn.chungtayphongngua.service;

import org.ctpn.chungtayphongngua.entity.Course;
import org.ctpn.chungtayphongngua.entity.User;
import org.ctpn.chungtayphongngua.repository.CourseRepository;
import java.math.BigDecimal;
import org.ctpn.chungtayphongngua.repository.UserRepository;
import org.ctpn.chungtayphongngua.repository.EnrollmentRepository;
import org.ctpn.chungtayphongngua.entity.Enrollment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Arrays;

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

    public void enrollUser(Long courseId, String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Course course = courseRepository.findActiveCourseById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found or inactive: " + courseId));

        // Check if already enrolled
        boolean alreadyEnrolled = user.getEnrollments().stream()
                .anyMatch(e -> e.getCourse().getCourseId().equals(courseId));

        if (alreadyEnrolled) {
            throw new RuntimeException("User already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setUser(user);
        enrollment.setCourse(course);
        enrollment.setStatus("active");

        enrollmentRepository.save(enrollment);
    }

    public List<Object> getStudentStats(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        List<Enrollment> enrollments = enrollmentRepository.findByUserUserId(user.getUserId());

        int totalCourses = enrollments.size();
        double averageProgress = enrollments.stream()
                .mapToInt(Enrollment::getProgress)
                .average()
                .orElse(0.0);
        double averageScore = enrollments.stream()
                .map(Enrollment::getFinalScore)
                .filter(score -> score != null)
                .mapToDouble(BigDecimal::doubleValue)
                .average()
                .orElse(0.0);

        return Arrays.asList(totalCourses, averageProgress, averageScore);
    }

    public void updateCourseProgress(Long courseId, String username, Integer progress) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Enrollment enrollment = enrollmentRepository.findByUserUserIdAndCourseCourseId(user.getUserId(), courseId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        enrollment.setProgress(progress);
        if (progress == 100) {
            enrollment.setStatus("completed");
            enrollment.setCompletedAt(java.time.LocalDateTime.now());
        }

        enrollmentRepository.save(enrollment);
    }

    public List<Enrollment> getEnrollments(String username, String status) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        if (status == null || status.equals("all")) {
            return enrollmentRepository.findByUserUserId(user.getUserId());
        } else {
            return enrollmentRepository.findByUserUserIdAndStatus(user.getUserId(), status);
        }
    }
}
