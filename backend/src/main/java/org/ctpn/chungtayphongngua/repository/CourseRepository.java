package org.ctpn.chungtayphongngua.repository;

import org.ctpn.chungtayphongngua.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByTitle(String title);

    Page<Course> findByIsActiveTrue(Pageable pageable);

    @Query("SELECT c FROM Course c WHERE c.courseId = :id AND c.isActive = true")
    Optional<Course> findActiveCourseById(@Param("id") Long id);

    List<Course> findByIsActiveTrue();

    @Query("SELECT c FROM Course c JOIN c.enrollments e WHERE e.user.userId = :userId")
    List<Course> findEnrolledCoursesByUserId(@Param("userId") Long userId);

    List<Course> findTop3ByIsActiveTrueOrderByRatingDesc(Pageable pageable);
    
    List<Course> findTop2ByIsActiveTrueOrderByRatingDesc(Pageable pageable);

    List<Course> findByTitleContainingIgnoreCaseAndIsActiveTrue(String title);

    @Query("SELECT c FROM Course c WHERE c.category IN :categories AND c.isActive = true")
    List<Course> findRecommendedCourses(@Param("categories") List<String> categories);
}
