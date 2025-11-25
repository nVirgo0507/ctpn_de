package org.ctpn.chungtayphongngua.repository;

import org.ctpn.chungtayphongngua.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Quiz Attempt Repository
 * Author: FullStack-Developer-AI (Cursor)
 * Created: [Current session date]
 * Version: 1.0
 * Context: Data access layer for QuizAttempt entity per Document requirements
 */
@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    
    // Find attempts by user and quiz
    List<QuizAttempt> findByUserUserIdAndQuizQuizIdAndIsDeletedFalseOrderByAttemptNumberAsc(Long userId, Long quizId);
    
    // Find user's best attempt for a quiz
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.user.userId = :userId " +
           "AND qa.quiz.quizId = :quizId AND qa.isDeleted = false " +
           "ORDER BY qa.score DESC")
    Optional<QuizAttempt> findBestAttempt(@Param("userId") Long userId, @Param("quizId") Long quizId);
    
    // Find user's latest attempt for a quiz
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.user.userId = :userId " +
           "AND qa.quiz.quizId = :quizId AND qa.isDeleted = false " +
           "ORDER BY qa.attemptNumber DESC")
    Optional<QuizAttempt> findLatestAttempt(@Param("userId") Long userId, @Param("quizId") Long quizId);
    
    // Count attempts by user and quiz
    long countByUserUserIdAndQuizQuizIdAndIsDeletedFalse(Long userId, Long quizId);
    
    // Find all attempts by user
    List<QuizAttempt> findByUserUserIdAndIsDeletedFalseOrderByStartedAtDesc(Long userId);
    
    // Get average score for a quiz
    @Query("SELECT AVG(qa.score) FROM QuizAttempt qa WHERE qa.quiz.quizId = :quizId " +
           "AND qa.isDeleted = false AND qa.completedAt IS NOT NULL")
    Double getAverageScoreForQuiz(@Param("quizId") Long quizId);
} 