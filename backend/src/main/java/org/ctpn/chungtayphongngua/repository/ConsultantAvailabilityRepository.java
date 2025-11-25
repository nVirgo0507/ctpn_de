package org.ctpn.chungtayphongngua.repository;

import org.ctpn.chungtayphongngua.entity.ConsultantAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultantAvailabilityRepository extends JpaRepository<ConsultantAvailability, Long> {

    List<ConsultantAvailability> findByUserUserIdOrderByDayOfWeekAscStartTimeAsc(Long userId);

    @Query("SELECT ca FROM ConsultantAvailability ca WHERE ca.user.userId = :consultantId AND ca.dayOfWeek = :dayOfWeek AND ca.startTime <= :time AND ca.endTime > :time AND ca.isAvailable = true")
    Optional<ConsultantAvailability> findConsultantAvailabilityAtTime(@Param("consultantId") Long consultantId, @Param("dayOfWeek") int dayOfWeek, @Param("time") LocalTime time);

    List<ConsultantAvailability> findByUserUserIdAndDayOfWeekAndIsAvailableTrueOrderByStartTimeAsc(Long consultantId, int dayOfWeek);
}
