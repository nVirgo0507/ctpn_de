package org.ctpn.chungtayphongngua.controller;

import org.ctpn.chungtayphongngua.entity.Course;
import org.ctpn.chungtayphongngua.entity.Enrollment;
import org.ctpn.chungtayphongngua.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/courses") // Corrected: Removed /api prefix
@CrossOrigin(origins = "*")
public class CourseController {

    @Autowired
    private CourseService courseService;

    /**
     * Get all public courses with pagination
     */
    @GetMapping
    public ResponseEntity<Page<Course>> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "rating") String sort) {
        Page<Course> courses = courseService.getAllCourses(page, size, sort);
        return ResponseEntity.ok(courses);
    }

    /**
     * Get a single course by its ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        Optional<Course> course = courseService.getCourseById(id);
        return course.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get courses for the currently logged-in user (My Enrollments)
     */
    /**
     * Get enrollments for the currently logged-in user
     */
    @GetMapping("/my-enrollments")
    public ResponseEntity<List<Enrollment>> getMyEnrollments(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = authentication.getName();
        try {
            List<Enrollment> enrollments = courseService.getEnrollments(username, "all");
            return ResponseEntity.ok(enrollments);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Get enrollments by status
     */
    @GetMapping("/my-enrollments/{status}")
    public ResponseEntity<List<Enrollment>> getMyEnrollmentsByStatus(@PathVariable String status,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = authentication.getName();
        try {
            List<Enrollment> enrollments = courseService.getEnrollments(username, status);
            return ResponseEntity.ok(enrollments);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Get courses for the currently logged-in user (Legacy endpoint alias)
     */
    @GetMapping("/my-courses")
    public ResponseEntity<List<Enrollment>> getMyCourses(Authentication authentication) {
        return getMyEnrollments(authentication);
    }

    /**
     * Get student stats
     */
    @GetMapping("/my-stats")
    public ResponseEntity<List<Object>> getMyStats(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = authentication.getName();
        try {
            List<Object> stats = courseService.getStudentStats(username);
            return ResponseEntity.ok(stats);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Search courses by title
     */
    @GetMapping("/search")
    public ResponseEntity<List<Course>> searchCourses(@RequestParam String title) {
        List<Course> courses = courseService.searchCoursesByTitle(title);
        return ResponseEntity.ok(courses);
    }

    /**
     * Enroll in a course
     */
    @PostMapping("/{id}/enroll")
    public ResponseEntity<?> enrollCourse(@PathVariable Long id, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = authentication.getName();
        try {
            courseService.enrollUser(id, username);
            return ResponseEntity.ok().build();
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Update course progress
     */
    @PutMapping("/{id}/progress")
    public ResponseEntity<?> updateProgress(@PathVariable Long id, @RequestBody java.util.Map<String, Integer> payload,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = authentication.getName();
        Integer progress = payload.get("progress");

        if (progress == null || progress < 0 || progress > 100) {
            return ResponseEntity.badRequest().body("Invalid progress value");
        }

        try {
            courseService.updateCourseProgress(id, username, progress);
            return ResponseEntity.ok().build();
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
