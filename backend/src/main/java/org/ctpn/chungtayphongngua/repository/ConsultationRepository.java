package org.ctpn.chungtayphongngua.repository;

import org.ctpn.chungtayphongngua.entity.Consultation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

    @Query("SELECT c FROM Consultation c WHERE c.consultant.userId = :consultantId AND c.scheduledAt < :endTime AND c.scheduledAt > :startTime")
    List<Consultation> findConflictingConsultations(@Param("consultantId") Long consultantId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    List<Consultation> findByMemberUserIdOrderByScheduledAtDesc(Long userId);

    List<Consultation> findByConsultantUserIdOrderByScheduledAtDesc(Long userId);

    @Query("SELECT c FROM Consultation c WHERE c.member.userId = :userId AND c.scheduledAt > :now")
    List<Consultation> findUpcomingConsultationsForMember(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("SELECT c FROM Consultation c WHERE c.consultant.userId = :userId AND c.scheduledAt > :now")
    List<Consultation> findUpcomingConsultationsForConsultant(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("SELECT c FROM Consultation c WHERE c.member.userId = :memberId AND c.status = 'completed' AND c.rating IS NULL")
    List<Consultation> findUnratedCompletedConsultations(@Param("memberId") Long memberId);

    @Query("SELECT COUNT(c), AVG(c.rating) FROM Consultation c WHERE c.consultant.userId = :consultantId AND c.status = 'completed'")
    Object[] getConsultantStats(@Param("consultantId") Long consultantId);

    long countByStatus(String status);
}
