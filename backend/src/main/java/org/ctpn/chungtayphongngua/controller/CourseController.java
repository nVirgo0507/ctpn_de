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

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/courses")
@CrossOrigin(origins = "*")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping
    public ResponseEntity<Page<Course>> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "rating") String sort) {
        Page<Course> courses = courseService.getAllCourses(page, size, sort);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        Optional<Course> course = courseService.getCourseById(id);
        return course.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/my-courses")
    public ResponseEntity<List<Course>> getMyCourses(Authentication authentication) {
        String username = authentication.getName();
        List<Course> courses = courseService.findCoursesByUsername(username);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/my-stats")
    public ResponseEntity<Map<String, Object>> getMyCourseStats(Authentication authentication) {
        String username = authentication.getName();
        Map<String, Object> stats = courseService.getUserCourseStats(username);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/my-enrollments/active")
    public ResponseEntity<List<Enrollment>> getMyActiveEnrollments(Authentication authentication) {
        String username = authentication.getName();
        List<Enrollment> enrollments = courseService.getActiveEnrollmentsForUser(username);
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Course>> searchCourses(@RequestParam String title) {
        List<Course> courses = courseService.searchCoursesByTitle(title);
        return ResponseEntity.ok(courses);
    }
}
