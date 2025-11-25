package org.ctpn.chungtayphongngua.controller;

import org.ctpn.chungtayphongngua.entity.Course;
import org.ctpn.chungtayphongngua.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

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
     * Get courses for the currently logged-in user
     */
    @GetMapping("/my-courses")
    public ResponseEntity<List<Course>> getMyCourses(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        String username = authentication.getName();
        try {
            List<Course> courses = courseService.findCoursesByUsername(username);
            return ResponseEntity.ok(courses);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(404).build();
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
}
