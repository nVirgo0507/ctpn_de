package org.ctpn.chungtayphongngua.repository;

import org.ctpn.chungtayphongngua.entity.Assessment;
import org.ctpn.chungtayphongngua.entity.AssessmentResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Assessment Result Repository
 * Author: FullStack-Developer-AI (Cursor)
 * Created: [Current session date]
 * Version: 1.0
 * Context: Database operations for ctpn_assessment.assessment_results table
 */
@Repository
public interface AssessmentResultRepository extends JpaRepository<AssessmentResult, Long> {
    
    /**
     * Find results by assessment
     */
    List<AssessmentResult> findByAssessmentAndIsDeletedFalse(Assessment assessment);
    
    /**
     * Find results by assessment ordered by question ID
     */
    List<AssessmentResult> findByAssessmentAndIsDeletedFalseOrderByQuestionId(Assessment assessment);
    
    /**
     * Find specific result by assessment and question ID
     */
    Optional<AssessmentResult> findByAssessmentAndQuestionIdAndIsDeletedFalse(Assessment assessment, String questionId);
    
    /**
     * Count results for an assessment
     */
    long countByAssessmentAndIsDeletedFalse(Assessment assessment);
    
    /**
     * Find results with specific answer value
     */
    List<AssessmentResult> findByAssessmentAndAnswerValueAndIsDeletedFalse(Assessment assessment, String answerValue);
    
    /**
     * Find results by score weight range
     */
    @Query("SELECT ar FROM AssessmentResult ar WHERE ar.assessment = :assessment " +
           "AND ar.scoreWeight BETWEEN :minScore AND :maxScore AND ar.isDeleted = false")
    List<AssessmentResult> findByAssessmentAndScoreRange(@Param("assessment") Assessment assessment,
                                                        @Param("minScore") Integer minScore,
                                                        @Param("maxScore") Integer maxScore);
    
    /**
     * Get total score for an assessment
     */
    @Query("SELECT SUM(ar.scoreWeight) FROM AssessmentResult ar WHERE ar.assessment = :assessment " +
           "AND ar.isDeleted = false")
    Integer getTotalScoreForAssessment(@Param("assessment") Assessment assessment);
    
    /**
     * Find results by multiple assessments (for batch operations)
     */
    List<AssessmentResult> findByAssessmentInAndIsDeletedFalse(List<Assessment> assessments);
    
    /**
     * Find results with high scores (for analysis)
     */
    @Query("SELECT ar FROM AssessmentResult ar WHERE ar.scoreWeight >= :threshold " +
           "AND ar.isDeleted = false ORDER BY ar.scoreWeight DESC")
    List<AssessmentResult> findHighScoreResults(@Param("threshold") Integer threshold);
    
    /**
     * Get answer distribution for a question across all assessments
     */
    @Query("SELECT ar.answerValue, COUNT(ar) FROM AssessmentResult ar " +
           "WHERE ar.questionId = :questionId AND ar.isDeleted = false GROUP BY ar.answerValue")
    List<Object[]> getAnswerDistributionForQuestion(@Param("questionId") String questionId);
} 