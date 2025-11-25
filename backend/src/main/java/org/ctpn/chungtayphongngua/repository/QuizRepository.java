package org.ctpn.chungtayphongngua.repository;

import org.ctpn.chungtayphongngua.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Quiz Repository
 * Author: FullStack-Developer-AI (Cursor)
 * Created: [Current session date]
 * Version: 1.1
 * Context: Data access layer for Quiz entity, corrected method names.
 */
@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    
    // Find quizzes by course
    List<Quiz> findByCourseCourseId(Long courseId);
    
    // Find quiz by ID (active only)
    @Query("SELECT q FROM Quiz q WHERE q.quizId = :quizId AND q.isActive = true")
    Optional<Quiz> findActiveQuizById(@Param("quizId") Long quizId);
    
    // Count quizzes in course
    long countByCourseCourseId(Long courseId);
}
